package gklijs.tech.sticker.axon.commands;

import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.Person;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record RegisterMemberCommand(
        @TargetAggregateIdentifier
        UUID registrationId,
        Person person,
        Address address
) {

}
