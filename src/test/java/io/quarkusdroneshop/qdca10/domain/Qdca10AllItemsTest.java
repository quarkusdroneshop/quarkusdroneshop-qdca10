package io.quarkusdroneshop.qdca10.domain;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;
import org.junit.jupiter.api.*;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * calculateDelay() の全分岐をカバーする Qdca10 テスト
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Qdca10AllItemsTest {

    @Inject
    Qdca10 qdca10;

    @Inject
    Inventory inventory;

    private OrderIn makeOrder(Item item) {
        return new OrderIn(
                java.util.UUID.randomUUID().toString(),
                java.util.UUID.randomUUID().toString(),
                item,
                "test-" + item.name());
    }

    @Test @Order(1)
    void testMake_QDC_A101_success() {
        inventory.restock(Item.QDC_A101);
        Qdca10Result result = qdca10.make(makeOrder(Item.QDC_A101));
        assertFalse(result.isEightySixed());
        assertNotNull(result.getOrderUp());
    }

    @Test @Order(2)
    void testMake_QDC_A102_success() {
        inventory.restock(Item.QDC_A101); // A102 は A101 として管理
        Qdca10Result result = qdca10.make(makeOrder(Item.QDC_A102));
        assertFalse(result.isEightySixed());
        assertNotNull(result.getOrderUp());
    }

    @Test @Order(3)
    void testMake_QDC_A103_success() {
        inventory.restock(Item.QDC_A103);
        Qdca10Result result = qdca10.make(makeOrder(Item.QDC_A103));
        assertFalse(result.isEightySixed());
        assertNotNull(result.getOrderUp());
    }

    @Test @Order(4)
    void testMake_QDC_A104_AC_success() {
        inventory.restock(Item.QDC_A104_AC);
        Qdca10Result result = qdca10.make(makeOrder(Item.QDC_A104_AC));
        assertFalse(result.isEightySixed());
        assertNotNull(result.getOrderUp());
    }

    @Test @Order(5)
    void testMake_QDC_A104_AT_success() {
        inventory.restock(Item.QDC_A104_AT);
        Qdca10Result result = qdca10.make(makeOrder(Item.QDC_A104_AT));
        assertFalse(result.isEightySixed());
        assertNotNull(result.getOrderUp());
    }

    @Test @Order(6)
    void testMake_default_success() {
        // QDC_A105_Pro01 は default ブランチ（10000ms delay）
        inventory.restock(Item.QDC_A105_Pro01);
        Qdca10Result result = qdca10.make(makeOrder(Item.QDC_A105_Pro01));
        assertFalse(result.isEightySixed());
        assertNotNull(result.getOrderUp());
    }

    @Test @Order(7)
    void testMake_eightySix_path() {
        // 在庫を 0 にして 86 パスをテスト
        // QDC_A103 の在庫を枯渇させる
        Integer current = inventory.getStock().get(Item.QDC_A103);
        if (current == null) current = 0;
        for (int i = 0; i < current; i++) {
            inventory.decrementItem(Item.QDC_A103);
        }
        Qdca10Result result = qdca10.make(makeOrder(Item.QDC_A103));
        assertTrue(result.isEightySixed());
        assertNotNull(result.getEightySixEvent());
        assertNull(result.getOrderUp());
    }

    @Test @Order(8)
    void testRestockItem() {
        inventory.restock(Item.QDC_A104_AC);
        Integer before = inventory.getStock().get(Item.QDC_A104_AC);
        qdca10.restockItem(Item.QDC_A104_AC);
        Integer after = inventory.getStock().get(Item.QDC_A104_AC);
        assertNotNull(after);
        assertTrue(after >= 55 && after <= 99);
    }
}
