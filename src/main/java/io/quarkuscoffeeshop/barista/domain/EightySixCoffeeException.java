package io.quarkuscoffeeshop.barista.domain;

import io.quarkuscoffeeshop.domain.Item;

import java.util.Arrays;
import java.util.Collection;

public class EightySixCoffeeException extends Exception {

    public Collection<EightySixEvent> getEvents() {
        return Arrays.asList(new EightySixEvent(Item.COFFEE_BLACK), new EightySixEvent(Item.COFFEE_WITH_ROOM));
    }

}
