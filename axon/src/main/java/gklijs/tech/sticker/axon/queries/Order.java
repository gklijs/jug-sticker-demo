package gklijs.tech.sticker.axon.queries;

import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.Person;

import java.time.Instant;
import java.util.UUID;

public record Order(
        UUID orderId,
        String itemOrdered,
        int amount,
        Instant dateOrdered,
        Person person,
        Address address
) {

}
