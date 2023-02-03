package gklijs.tech.sticker.event_api;

import java.util.UUID;

public record StickersOrdered(
        UUID registrationId,
        UUID orderId,
        int amount,
        Person person,
        Address address
) {

}
