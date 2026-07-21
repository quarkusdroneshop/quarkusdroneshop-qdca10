package io.quarkusdroneshop.qdca10.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.qdca10.domain.Inventory;
import io.quarkusdroneshop.qdca10.domain.Qdca10;
import io.quarkusdroneshop.domain.valueobjects.ComponentStockDecrement;
import io.quarkusdroneshop.domain.valueobjects.EightySixMessage;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import io.quarkusdroneshop.qdca10.domain.valueobjects.ComponentStockUpdate;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@RegisterForReflection
public class KafkaService {

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Inject
    Qdca10 qdca10;

    @Inject
    Inventory inventory;

    @Inject
    @Channel("orders-up")
    Emitter<OrderUp> orderUpEmitter;

    @Inject
    @Channel("eighty-six")
    Emitter<EightySixMessage> eightySixEmitter;

    @Inject
    @Channel("component-stock-decrement")
    Emitter<ComponentStockDecrement> componentStockDecrementEmitter;

    @Incoming("component-stock-quantity")
    public void onComponentStockUpdate(final ComponentStockUpdate update) {
        if (update == null) {
            // QDCA10pro 専用品目など、この Item enum に存在しないレコードは
            // デシリアライザ側で既に null を返している。
            return;
        }
        inventory.applyStockUpdate(update.getItem(), (int) update.getQuantity());
    }

    @Incoming("orders-in")
    public CompletableFuture<Void> onOrderIn(final OrderIn orderIn) {

        // dataproduct-order-events は QDCA10 以外宛ての ORDER_PLACED や、
        // LINE_ITEM_STATUS_CHANGED / ORDER_CANCELLED も同じトピックに流れてくる。
        // OrderEventOrderInDeserializer はそれらを null として返す (フィルタ) ため、
        // ここで null チェックしてスキップする必要がある。
        if (orderIn == null) {
            logger.debug("Skipping non-QDCA10 dataproduct-order-events message");
            return CompletableFuture.completedFuture(null);
        }

        logger.debug("OrderTicket received: {}", orderIn);

        return CompletableFuture
            .supplyAsync(new Qdca10Task(qdca10, orderIn))
            .thenAccept(result -> {
                if (result.isEightySixed()) {
                    logger.debug("Item is eighty-sixed, sending to topic: {}", orderIn.getItem());
                    eightySixEmitter.send(new EightySixMessage(orderIn.getOrderId(), orderIn.getLineItemId(), orderIn.getItem()))
                        .whenComplete((res, ex) -> {
                            if (ex != null) {
                                logger.error("Failed to send to eighty-six topic", ex);
                            } else {
                                logger.debug("Sent to eighty-six topic successfully");
                            }
                        });
                } else {
                    OrderUp orderUp = result.getOrderUp();
                    logger.debug("OrderUp: {}", orderUp);
                    orderUpEmitter.send(orderUp)
                        .whenComplete((res, ex) -> {
                            if (ex != null) {
                                logger.error("Failed to send OrderUp to Kafka", ex);
                            } else {
                                logger.debug("OrderUp sent successfully to Kafka");
                            }
                        });

                    Integer remaining = inventory.getRawBaseline(orderIn.getItem());
                    if (remaining != null) {
                        componentStockDecrementEmitter.send(new ComponentStockDecrement(orderIn.getItem(), remaining))
                            .whenComplete((res, ex) -> {
                                if (ex != null) {
                                    logger.error("Failed to send ComponentStockDecrement to Kafka", ex);
                                }
                            });
                    }
                }
            });
    }
}