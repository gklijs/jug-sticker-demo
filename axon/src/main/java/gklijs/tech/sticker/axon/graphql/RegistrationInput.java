package gklijs.tech.sticker.axon.graphql;

import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.Person;

public record RegistrationInput(
        Person person,
        Address address
) {

}
