package io.quarkuscoffeeshop.barista.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkuscoffeeshop.barista.TestUtil;
import io.quarkuscoffeeshop.domain.valueobjects.OrderIn;
import io.quarkuscoffeeshop.domain.valueobjects.OrderUp;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
public class KafkaServiceOrderUpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaServiceOrderUpTest.class);

    @Inject
    KafkaService kafkaService;

    @Inject
    @Any
    InMemoryConnector ordersInConnector;

    InMemorySource<OrderIn> ordersIn;

    @Inject
    @Any
    InMemoryConnector ordersOutConnector;

    InMemorySink<OrderUp> ordersOut;

    @BeforeEach
    public void setUp(){

        ordersIn = ordersInConnector.source("orders-in");
        ordersOut = ordersOutConnector.sink("orders-out");
    }

    @Test
    public void testSingleBlackCoffee() {

        OrderIn orderIn = TestUtil.getOrderTicket();
        ordersIn.send(orderIn);

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<?> ordersUp = ordersOut.received();
        assertNotNull(ordersUp);
        assertEquals(1, ordersUp.size());
        LOGGER.debug(ordersUp.get(0).toString());
//        TicketUp ticketUp = (TicketUp) ordersUp.get(0);
//        assertNotNull(ticketUp);
//        assertEquals(ticketUp.orderId, orderTicket.getOrderId());
//        assertEquals(ticketUp.lineItemId, orderTicket.getLineItemId());
//        assertEquals(ticketUp.item, orderTicket.getItem());
//        assertEquals(ticketUp.name, orderTicket.getName());
    }
}
