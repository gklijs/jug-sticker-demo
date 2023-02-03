package gklijs.tech.sticker.axon.command;

import gklijs.tech.sticker.axon.commands.OrderStickerCommand;
import gklijs.tech.sticker.axon.commands.RegisterMemberCommand;
import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.MemberRegistered;
import gklijs.tech.sticker.event_api.Person;
import gklijs.tech.sticker.event_api.StickersOrdered;
import gklijs.tech.sticker.exceptions.NotEnoughCreditsException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static gklijs.tech.sticker.util.Constants.DEFAULT_START_CREDITS;
import static gklijs.tech.sticker.util.Constants.STICKER_PRICE;
import static org.axonframework.modelling.command.AggregateCreationPolicy.CREATE_IF_MISSING;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Registration {

    @AggregateIdentifier
    private UUID registrationId;

    private Person person;
    private Address address;
    private int credits;

    @CommandHandler
    @CreationPolicy(CREATE_IF_MISSING)
    public void handle(RegisterMemberCommand command) {
        apply(new MemberRegistered(command.registrationId(), command.person(), command.address()));
    }

    @CommandHandler
    public void handle(OrderStickerCommand command) {
        if (credits - command.amount() * STICKER_PRICE < 0) {
            throw new NotEnoughCreditsException(credits, command.amount(), "stickers");
        }
        apply(new StickersOrdered(command.registrationId(), command.orderId(), command.amount(), person, address));
    }

    @EventSourcingHandler
    public void on(MemberRegistered event) {
        this.registrationId = event.registrationId();
        this.person = event.person();
        this.address = event.address();
        this.credits = DEFAULT_START_CREDITS;
    }

    @EventSourcingHandler
    public void on(StickersOrdered event) {
        credits = credits - event.amount() * STICKER_PRICE;
    }

    public Registration() {
        //required by axon
    }
}
