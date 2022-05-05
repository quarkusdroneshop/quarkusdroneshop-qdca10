package io.quarkuscoffeeshop.barista.domain;

import io.quarkuscoffeeshop.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkuscoffeeshop.domain.valueobjects.OrderTicket;
import io.quarkuscoffeeshop.domain.valueobjects.TicketUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class BaristaTest {

    @Inject
    Barista barista;

    Jsonb jsonb = JsonbBuilder.create();

    @BeforeEach
    public void restock() {
        barista.restockItem(Item.COFFEE_BLACK);
    }

    @Test
    public void testBlackCoffeeOrder() throws ExecutionException, InterruptedException {

        OrderTicket orderTicket = new OrderTicket(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Item.COFFEE_BLACK,
                "Lemmy"
        );

        TicketUp ticketUp = barista.make(orderTicket);

        await().atLeast(Duration.ofSeconds(5000));

        assertNotNull(ticketUp);
        assertEquals(ticketUp.orderId, orderTicket.getOrderId());
        assertEquals(ticketUp.lineItemId, orderTicket.getLineItemId());
        assertEquals(ticketUp.item, orderTicket.getItem());
        assertEquals(ticketUp.name, orderTicket.getName());

    }

}
