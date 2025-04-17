package io.quarkuscoffeeshop.barista.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkuscoffeeshop.barista.domain.Barista;
import io.quarkuscoffeeshop.domain.valueobjects.OrderIn;
import io.quarkuscoffeeshop.domain.valueobjects.OrderUp;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@RegisterForReflection
public class KafkaService {

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Inject
    Barista barista;

    @Inject
    @Channel("orders-up")
    Emitter<OrderUp> orderUpEmitter;

    @Inject
    @Channel("eighty-six")
    Emitter<String> eightySixEmitter;

    @Incoming("orders-in")
    public CompletableFuture onOrderIn(final OrderIn orderIn) {

        logger.debug("OrderTicket received: {}", orderIn);

        return CompletableFuture.supplyAsync(() -> {

            return barista.make(orderIn);
        }).thenApply(baristaResult -> {

            if (baristaResult.isEightySixed()) {

                eightySixEmitter.send(orderIn.getItem().toString());
            }else{

                logger.debug( "OrderUp: {}", baristaResult.getOrderUp());
                orderUpEmitter.send(baristaResult.getOrderUp());
            }

            return null;
        });
    }
}
