package gklijs.tech.sticker.axon.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record OrderStickerCommand(
        @TargetAggregateIdentifier
        UUID registrationId,

        UUID orderId,
        int amount
) {

}
