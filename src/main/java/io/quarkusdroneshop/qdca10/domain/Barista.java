package io.quarkusdroneshop.barista.domain;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.BaristaResult;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;

@ApplicationScoped
public class Barista {

    static final Logger logger = LoggerFactory.getLogger(Barista.class);
    @Inject
    Inventory inventory;
    private String madeBy;

    @PostConstruct
    void setHostName() {
        try {
            madeBy = InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            logger.debug("unable to get hostname");
            madeBy = "unknown";
        }
    }

    public BaristaResult make(final OrderIn orderIn) {

        logger.debug("making: {}" + orderIn.getItem());

        if (inventory.decrementItem(orderIn.getItem())) {

            sleepyTimeTime(orderIn.getItem());

            return new BaristaResult(new OrderUp(
                    orderIn.getOrderId(),
                    orderIn.getLineItemId(),
                    orderIn.getItem(),
                    orderIn.getName(),
                    Instant.now(),
                    madeBy));
        } else {

            return new BaristaResult(new EightySixEvent(orderIn.getItem()));
        }

    }

    private void sleepyTimeTime(final Item item) {
        int i = calculateDelay(item);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int calculateDelay(final Item item) {
        int delay;
        switch (item) {
            case COFFEE_BLACK:
                delay = 5000;
                break;
            case COFFEE_WITH_ROOM:
                delay = 5000;
                break;
            case ESPRESSO:
                delay = 7000;
                break;
            case ESPRESSO_DOUBLE:
                delay = 7000;
                break;
            case CAPPUCCINO:
                delay = 10000;
                break;
            default:
                delay = 3000;
                break;
        }
        ;
        return delay;
    }


    public void restockItem(Item item) {
        inventory.restock(item);
    }
}
