package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * qdca10 ドメインオブジェクトの純粋ユニットテスト
 */
public class DomainTest {

    // ── EightySixEvent ────────────────────────────────────────────────────────

    @Test
    void testEightySixEvent_defaultConstructor() {
        EightySixEvent event = new EightySixEvent();
        assertEquals(EventType.EIGHTY_SIX, event.getEventType());
    }

    @Test
    void testEightySixEvent_withItem() {
        EightySixEvent event = new EightySixEvent(Item.QDC_A101);
        assertEquals(EventType.EIGHTY_SIX, event.getEventType());
    }

    // ── EightySixException ────────────────────────────────────────────────────

    @Test
    void testEightySixException_singleItem() {
        EightySixException ex = new EightySixException(Item.QDC_A102);
        assertNotNull(ex.getItems());
        assertEquals(1, ex.getItems().size());
        assertEquals(Item.QDC_A102, ex.getItems().get(0));
    }

    @Test
    void testEightySixException_multipleItems() {
        List<Item> items = Arrays.asList(Item.QDC_A101, Item.QDC_A102);
        EightySixException ex = new EightySixException(items);
        assertEquals(2, ex.getItems().size());
    }

    // ── EightySixDroneException ──────────────────────────────────────────────

    @Test
    void testEightySixDroneException_getEvents() {
        EightySixDroneException ex = new EightySixDroneException();
        Collection<EightySixEvent> events = ex.getEvents();
        assertNotNull(events);
        assertEquals(2, events.size());
    }

    // ── EventType enum ────────────────────────────────────────────────────────

    @Test
    void testEventType_allValues() {
        EventType[] values = EventType.values();
        assertTrue(values.length > 0);
        assertEquals(EventType.EIGHTY_SIX, EventType.valueOf("EIGHTY_SIX"));
        assertEquals(EventType.QDCA10_ORDER_IN, EventType.valueOf("QDCA10_ORDER_IN"));
        assertEquals(EventType.QDCA10_ORDER_UP, EventType.valueOf("QDCA10_ORDER_UP"));
        assertEquals(EventType.QDCA10Pro_ORDER_IN, EventType.valueOf("QDCA10Pro_ORDER_IN"));
        assertEquals(EventType.QDCA10Pro_ORDER_UP, EventType.valueOf("QDCA10Pro_ORDER_UP"));
        assertEquals(EventType.ORDER_PLACED, EventType.valueOf("ORDER_PLACED"));
        assertEquals(EventType.RESTOCK, EventType.valueOf("RESTOCK"));
        assertEquals(EventType.NEW_ORDER, EventType.valueOf("NEW_ORDER"));
    }

    // ── Item enum ─────────────────────────────────────────────────────────────

    @Test
    void testItem_allValues() {
        Item[] values = Item.values();
        assertEquals(9, values.length);
        assertNotNull(Item.valueOf("QDC_A101"));
    }

    // ── OrderIn ───────────────────────────────────────────────────────────────

    @Test
    void testOrderIn_constructorAndGetters() {
        String orderId = UUID.randomUUID().toString();
        String lineItemId = UUID.randomUUID().toString();
        OrderIn orderIn = new OrderIn(orderId, lineItemId, Item.QDC_A101, "testName");

        assertEquals(orderId, orderIn.getOrderId());
        assertEquals(lineItemId, orderIn.getLineItemId());
        assertEquals(Item.QDC_A101, orderIn.getItem());
        assertEquals("testName", orderIn.getName());
        assertNotNull(orderIn.getTimestamp());
    }

    @Test
    void testOrderIn_equalsAndHashCode() {
        String orderId = UUID.randomUUID().toString();
        String lineItemId = UUID.randomUUID().toString();
        OrderIn a = new OrderIn(orderId, lineItemId, Item.QDC_A101, "name");
        // 同一オブジェクトの等価性
        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "other");
        // hashCodeが一定であること
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    void testOrderIn_toString() {
        OrderIn orderIn = new OrderIn("id1", "li1", Item.QDC_A101, "name");
        String s = orderIn.toString();
        assertTrue(s.contains("OrderIn"));
        assertTrue(s.contains("QDC_A101"));
    }

    // ── OrderUp ───────────────────────────────────────────────────────────────

    @Test
    void testOrderUp_constructorAndFields() {
        String orderId = UUID.randomUUID().toString();
        String lineItemId = UUID.randomUUID().toString();
        Instant ts = Instant.now();
        OrderUp orderUp = new OrderUp(orderId, lineItemId, Item.QDC_A102, "name", ts, "worker1");

        assertEquals(orderId, orderUp.orderId);
        assertEquals(lineItemId, orderUp.lineItemId);
        assertEquals(Item.QDC_A102, orderUp.item);
        assertEquals("name", orderUp.name);
        assertEquals(ts, orderUp.timestamp);
        assertEquals("worker1", orderUp.madeBy);
    }

    @Test
    void testOrderUp_equalsAndHashCode() {
        Instant ts = Instant.now();
        OrderUp a = new OrderUp("o1", "l1", Item.QDC_A101, "n", ts, "w");
        OrderUp b = new OrderUp("o1", "l1", Item.QDC_A101, "n", ts, "w");
        OrderUp c = new OrderUp("o2", "l1", Item.QDC_A101, "n", ts, "w");
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "other");
    }

    @Test
    void testOrderUp_toString() {
        OrderUp orderUp = new OrderUp("o1", "l1", Item.QDC_A101, "n", Instant.now(), "w");
        assertTrue(orderUp.toString().contains("OrderUp"));
    }

    // ── Qdca10Result ──────────────────────────────────────────────────────────

    @Test
    void testQdca10Result_withOrderUp() {
        OrderUp orderUp = new OrderUp("o1", "l1", Item.QDC_A101, "n", Instant.now(), "w");
        Qdca10Result result = new Qdca10Result(orderUp);
        assertFalse(result.isEightySixed());
        assertEquals(orderUp, result.getOrderUp());
        assertNull(result.getEightySixEvent());
    }

    @Test
    void testQdca10Result_withEightySix() {
        EightySixEvent event = new EightySixEvent(Item.QDC_A101);
        Qdca10Result result = new Qdca10Result(event);
        assertTrue(result.isEightySixed());
        assertEquals(event, result.getEightySixEvent());
        assertNull(result.getOrderUp());
    }

    @Test
    void testQdca10Result_setters() {
        Qdca10Result result = new Qdca10Result(new EightySixEvent(Item.QDC_A101));
        OrderUp orderUp = new OrderUp("o2", "l2", Item.QDC_A102, "n2", Instant.now(), "w2");
        result.setOrderUp(orderUp);
        result.setEightySixed(false);
        assertEquals(orderUp, result.getOrderUp());
        assertFalse(result.isEightySixed());

        EightySixEvent newEvent = new EightySixEvent(Item.QDC_A103);
        result.setEightySixEvent(newEvent);
        assertEquals(newEvent, result.getEightySixEvent());
    }
}
