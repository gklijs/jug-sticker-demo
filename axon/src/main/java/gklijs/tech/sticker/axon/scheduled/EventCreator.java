package gklijs.tech.sticker.axon.scheduled;

import gklijs.tech.sticker.axon.commands.OrderStickerCommand;
import gklijs.tech.sticker.axon.commands.RegisterMemberCommand;
import gklijs.tech.sticker.event_api.Address;
import gklijs.tech.sticker.event_api.Person;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Component
public class EventCreator {

    private final CommandGateway commandGateway;
    private final AtomicInteger counter;

    public EventCreator(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
        counter = new AtomicInteger(0);
    }

    @Scheduled(initialDelay = 3000, fixedRate = 40)
    private void orderStickerForBla() {
        int run = counter.incrementAndGet();
        UUID registrationId = UUID.randomUUID();
        Person person = new Person("Bla", "Blabla", "bla" + run + "@bla.com");
        Address address = new Address("Blastraat " + run, "1234AB", "The Netherlands");
        commandGateway.sendAndWait(new RegisterMemberCommand(registrationId, person, address));
        IntStream.range(1,6).forEach(
                amount -> commandGateway.send(new OrderStickerCommand(registrationId, UUID.randomUUID(), amount))
        );
    }
}
