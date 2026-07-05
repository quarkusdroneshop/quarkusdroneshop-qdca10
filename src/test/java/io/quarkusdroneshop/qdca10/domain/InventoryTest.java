package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryTest.class);

    @Inject
    Inventory inventory;

    @Test @Order(1)
    public void testStockIsPopulated() {

        Map<Item, Integer> inStock = inventory.getStock();
        assertNotNull(inStock);
        inStock.forEach((k,v) -> {
            LOGGER.info(k + " " + v);
        });
    }

    @Test @Order(2)
    public void testDecrementDrone() {

        Integer totalDrone = inventory.getTotalDrone();
        LOGGER.info("total drone: {}", totalDrone);
        assertTrue(inventory.decrementItem(Item.QDC_A102));
        Integer updatedDrone = inventory.getTotalDrone();
        LOGGER.info("total drone after decrementing: {}", updatedDrone);
        assertTrue(updatedDrone == totalDrone - 1);
    }

    @Test @Order(3)
    public void testEightySixDrone() {

        Integer totalDrone = inventory.getTotalDrone();
        for (int i = 0; i < totalDrone; i++) {
            assertTrue(inventory.decrementItem(Item.QDC_A101));
        }
        assertFalse(inventory.decrementItem(Item.QDC_A101));
    }
}
