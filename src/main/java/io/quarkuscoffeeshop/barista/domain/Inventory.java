package io.quarkuscoffeeshop.barista.domain;

import io.quarkuscoffeeshop.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@ApplicationScoped
public class Inventory {

    static Map<Item, Integer> stock;
    Logger LOGGER = LoggerFactory.getLogger(Inventory.class.getName());

    public Inventory() {
        super();
    }

    /*
        COFFEE_BLACK and COFFEE_WITH_ROOM are simply tracked as COFFEE_BLACK
     */
    @PostConstruct
    private void createStock() {
        stock = new HashMap<>();
        Stream.of(Item.values()).forEach(v -> {
            stock.put(v, ThreadLocalRandom.current().nextInt(55, 99));
        });
        stock.entrySet().stream().forEach(entrySet -> {
            LOGGER.debug(entrySet.getKey() + " " + entrySet.getValue());
        });

        // Account for coffee
        Integer totalCoffee = stock.get(Item.COFFEE_BLACK).intValue() + stock.get(Item.COFFEE_WITH_ROOM).intValue();
        stock.remove(Item.COFFEE_BLACK);
        stock.remove(Item.COFFEE_WITH_ROOM);
        stock.put(Item.COFFEE_BLACK, totalCoffee);
    }

    public boolean decrementItem(Item item) {

        LOGGER.debug("decrementing {}", item);

        if (item == Item.COFFEE_WITH_ROOM) item = Item.COFFEE_BLACK;

        Integer itemCount = stock.get(item);
        LOGGER.debug("current inventory for {} is {}", item, itemCount);

        if (itemCount <= 0) return false;

        itemCount--;
        stock.replace(item, itemCount);
        LOGGER.debug("updated inventory for {} is {}", item, stock.get(item));
        return true;
    }

    public Map<Item, Integer> getStock() {
        return stock;
    }

    public Integer getTotalCoffee() {
        return stock.get(Item.COFFEE_BLACK);
    }

    public void restock(Item item) {
        stock.put(item, ThreadLocalRandom.current().nextInt(55, 99));
    }
}
