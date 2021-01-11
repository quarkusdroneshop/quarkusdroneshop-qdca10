package io.quarkuscoffeeshop.barista.domain;

import io.quarkuscoffeeshop.domain.Item;
import io.quarkuscoffeeshop.domain.valueobjects.OrderTicket;
import io.quarkuscoffeeshop.domain.valueobjects.TicketUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.util.ArrayList;

@ApplicationScoped
public class Barista {

    static final Logger logger = LoggerFactory.getLogger(Barista.class);

    private String madeBy;

    @Inject
    Inventory inventory;

    @PostConstruct
    void setHostName() {
        try {
            madeBy = InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            logger.debug("unable to get hostname");
            madeBy = "unknown";
        }
    }

    public TicketUp make(final OrderTicket orderTicket){

        logger.debug("making: {}" + orderTicket.getItem());

            int delay;
            switch (orderTicket.getItem()) {
                case COFFEE_BLACK:
                    delay = 5;
                    break;
                case COFFEE_WITH_ROOM:
                    delay = 5;
                    break;
                case ESPRESSO:
                    delay = 7;
                    break;
                case ESPRESSO_DOUBLE:
                    delay = 7;
                    break;
                case CAPPUCCINO:
                    delay = 10;
                    break;
                default:
                    delay = 3;
                    break;
            };
            return prepare(orderTicket, delay);
    }

    /*
        Delay for the specified time and then return the completed TicketUp
        @throws RuntimeException for 86'd items
     */
    private TicketUp prepare(final OrderTicket orderTicket, int seconds) {

        // decrement the item in inventory
        try {

            inventory.decrementItem(orderTicket.getItem());
            logger.debug("inventory decremented 1 {}", orderTicket.getItem());
        } catch (EightySixException e) {

            logger.debug(orderTicket.getItem() + " is 86'd");
            throw new EightySixException(orderTicket.getItem());
        } catch (EightySixCoffeeException e) {

            logger.debug("coffee is 86'd");
            // 86 both coffee items
            throw new EightySixException(
                    new ArrayList<Item>(){{
                        add(Item.COFFEE_BLACK);
                        add(Item.COFFEE_WITH_ROOM);
                    }}
            );
        }

        // model the barista's time making the drink
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // return the completed drink
        return new TicketUp(
                orderTicket.getOrderId(),
                orderTicket.getLineItemId(),
                orderTicket.getItem(),
                orderTicket.getName(),
                Instant.now(),
                madeBy);
    }

    public void restockItem(Item item) {
        inventory.restock(item);
    }
}
