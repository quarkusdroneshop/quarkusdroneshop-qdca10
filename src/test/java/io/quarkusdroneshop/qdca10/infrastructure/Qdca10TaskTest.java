package io.quarkusdroneshop.qdca10.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;
import io.quarkusdroneshop.qdca10.domain.Inventory;
import io.quarkusdroneshop.qdca10.domain.Qdca10;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class Qdca10TaskTest {

    @Inject
    Qdca10 qdca10;

    @Inject
    Inventory inventory;

    @Test
    void testQdca10Task_get_orderUp() {
        inventory.restock(Item.QDC_A101);
        OrderIn orderIn = new OrderIn(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Item.QDC_A101,
                "task-test");
        Qdca10Task task = new Qdca10Task(qdca10, orderIn);
        Qdca10Result result = task.get();
        assertNotNull(result);
        assertFalse(result.isEightySixed());
        assertNotNull(result.getOrderUp());
    }

    @Test
    void testQdca10Task_get_eightySix() {
        // A104_AT の在庫を枯渇させて 86 パスを確認
        Integer count = inventory.getStock().get(Item.QDC_A104_AT);
        if (count == null) count = 0;
        for (int i = 0; i < count; i++) {
            inventory.decrementItem(Item.QDC_A104_AT);
        }
        OrderIn orderIn = new OrderIn(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Item.QDC_A104_AT,
                "task-test-86");
        Qdca10Task task = new Qdca10Task(qdca10, orderIn);
        Qdca10Result result = task.get();
        assertNotNull(result);
        assertTrue(result.isEightySixed());
    }
}
