package gklijs.tech.sticker.axon.query;

import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.Person;

import java.util.UUID;

public record MemberDetails(
        UUID registrationId,
        Person person,
        Address address
) {

    MemberDetails(MemberDocument member) {
        this(member.registrationId(), member.person(), member.address());
    }
}
