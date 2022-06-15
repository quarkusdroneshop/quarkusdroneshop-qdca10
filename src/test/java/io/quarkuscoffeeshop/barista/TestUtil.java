package io.quarkuscoffeeshop.barista;

import io.quarkuscoffeeshop.domain.Item;
import io.quarkuscoffeeshop.domain.valueobjects.OrderIn;

import java.util.UUID;

public class TestUtil {

    public static OrderIn getOrderTicket() {

        return new OrderIn(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Item.COFFEE_BLACK,
                "Lemmy"
        );
    }

}
