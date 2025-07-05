package io.quarkusdroneshop.barista;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;

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
