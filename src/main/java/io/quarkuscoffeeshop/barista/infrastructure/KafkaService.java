package io.quarkuscoffeeshop.barista.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkuscoffeeshop.barista.domain.Barista;
import io.quarkuscoffeeshop.barista.domain.EightySixException;
import io.quarkuscoffeeshop.domain.valueobjects.OrderTicket;
import io.quarkuscoffeeshop.domain.valueobjects.TicketUp;
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
    @Channel("orders-out")
    Emitter<TicketUp> orderUpEmitter;

    @Inject
    @Channel("eighty-six")
    Emitter<String> eightySixEmitter;

    @Incoming("orders-in")
    public CompletableFuture handleOrderIn(OrderTicket orderTicket) {

        logger.debug("OrderTicket received: {}", orderTicket);

        return CompletableFuture.supplyAsync(() -> {
            return barista.make(orderTicket);
        }).thenApply(orderUp -> {
            logger.debug( "OrderUp: {}", orderUp);
            orderUpEmitter.send(orderUp);
            return null;
        })
        .exceptionally(exception -> {
            logger.debug( "EightySixException: {}", exception.getMessage());
            ((EightySixException) exception).getItems().forEach(item -> {
                eightySixEmitter.send(item.toString());
            });
            return null;
        });
    }
}
