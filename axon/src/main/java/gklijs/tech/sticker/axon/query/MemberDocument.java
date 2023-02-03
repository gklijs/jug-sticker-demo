package gklijs.tech.sticker.axon.query;

import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.MemberRegistered;
import gklijs.tech.sticker.event_api.Person;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document
public record MemberDocument(
        @Id
        UUID registrationId,
        Person person,
        Address address
) {

    MemberDocument(MemberRegistered memberRegistered) {
        this(
                memberRegistered.registrationId(),
                memberRegistered.person(),
                memberRegistered.address()
        );
    }
}
