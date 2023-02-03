package gklijs.tech.sticker.axon.query;

import gklijs.tech.sticker.axon.queries.Order;
import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.Person;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document
public record OrderDocument(
        @Id
        UUID orderId,
        UUID registrationId,
        String itemOrdered,
        int amount,
        Instant dateOrdered,
        Person person,
        Address address
) {

    OrderDocument(Order order, UUID registrationId) {
        this(order.orderId(),
             registrationId,
             order.itemOrdered(),
             order.amount(),
             order.dateOrdered(),
             order.person(),
             order.address());
    }

    Order toOrder() {
        return new Order(orderId, itemOrdered, amount, dateOrdered, person, address);
    }
}
