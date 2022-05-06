package io.quarkuscoffeeshop.barista.domain;

import io.quarkuscoffeeshop.barista.TestUtil;
import io.quarkuscoffeeshop.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkuscoffeeshop.domain.valueobjects.BaristaResult;
import io.quarkuscoffeeshop.domain.valueobjects.OrderIn;
import io.quarkuscoffeeshop.domain.valueobjects.OrderUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.Duration;
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

        OrderIn orderIn = TestUtil.getOrderTicket();

        BaristaResult baristaResult = barista.make(orderIn);

        OrderUp orderUp = baristaResult.getOrderUp();

        await().atLeast(Duration.ofSeconds(5000));

        assertNotNull(orderUp);
        assertEquals(orderUp.orderId, orderIn.getOrderId());
        assertEquals(orderUp.lineItemId, orderIn.getLineItemId());
        assertEquals(orderUp.item, orderIn.getItem());
        assertEquals(orderUp.name, orderIn.getName());

    }


}
