package io.quarkusdroneshop.barista.domain;

import io.quarkusdroneshop.domain.Item;

import java.util.Arrays;
import java.util.Collection;

public class EightySixCoffeeException extends Exception {

    public Collection<EightySixEvent> getEvents() {
        return Arrays.asList(new EightySixEvent(Item.COFFEE_BLACK), new EightySixEvent(Item.COFFEE_WITH_ROOM));
    }

}
