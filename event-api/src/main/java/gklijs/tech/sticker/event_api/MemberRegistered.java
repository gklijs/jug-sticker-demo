package gklijs.tech.sticker.event_api;

import java.util.UUID;

public record MemberRegistered(
        UUID registrationId,
        Person person,
        Address address
) {

}
