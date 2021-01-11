package io.quarkuscoffeeshop.barista.infrastructure;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkuscoffeeshop.domain.valueobjects.OrderTicket;

public class OrderTicketDeserializer extends ObjectMapperDeserializer<OrderTicket> {

    public OrderTicketDeserializer() {
        super(OrderTicket.class);
    }
}
