package gklijs.tech.sticker.axon.graphql;

import gklijs.tech.sticker.axon.commands.OrderStickerCommand;
import gklijs.tech.sticker.axon.commands.RegisterMemberCommand;
import gklijs.tech.sticker.axon.queries.MemberCountQuery;
import gklijs.tech.sticker.axon.queries.MemberDetailsQuery;
import gklijs.tech.sticker.axon.queries.Order;
import gklijs.tech.sticker.axon.queries.OrderDetailsQuery;
import gklijs.tech.sticker.axon.queries.OrderHistoryForMemberQuery;
import gklijs.tech.sticker.axon.query.MemberDetails;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
public class GraphQLController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public GraphQLController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @QueryMapping
    public CompletableFuture<Long> memberCount() {
        return queryGateway.query(
                new MemberCountQuery(),
                ResponseTypes.instanceOf(Long.class)
        );
    }

    @QueryMapping
    public CompletableFuture<Optional<MemberDetails>> memberDetails(@Argument UUID registrationId) {
        return queryGateway.query(
                new MemberDetailsQuery(registrationId),
                ResponseTypes.optionalInstanceOf(MemberDetails.class)
        );
    }

    @QueryMapping
    public CompletableFuture<List<Order>> orderHistory(@Argument UUID registrationId) {
        return queryGateway.query(
                new OrderHistoryForMemberQuery(registrationId),
                ResponseTypes.multipleInstancesOf(Order.class)
        );
    }

    @MutationMapping
    public Mono<MemberDetails> register(@Argument RegistrationInput input) {
        UUID registrationId = UUID.randomUUID();
        return Mono.fromFuture(
                commandGateway.send(new RegisterMemberCommand(registrationId, input.person(), input.address()))
        ).then(firstFromQuery(new MemberDetailsQuery(registrationId), MemberDetails.class));
    }

    @MutationMapping
    public Mono<Order> orderStickers(@Argument UUID registrationId, @Argument int amount) {
        UUID orderId = UUID.randomUUID();
        return Mono.fromFuture(commandGateway.send(new OrderStickerCommand(registrationId, orderId, amount))
        ).then(firstFromQuery(new OrderDetailsQuery(orderId), Order.class));
    }

    private <Q, R> Mono<R> firstFromQuery(Q query, Class<R> c) {
        var subscription = queryGateway.subscriptionQuery(
                query,
                ResponseTypes.optionalInstanceOf(c),
                ResponseTypes.instanceOf(c)
        );
        return subscription.initialResult()
                           .flatMap(Mono::justOrEmpty)
                           .concatWith(subscription.updates())
                           .next()
                           .timeout(Duration.ofSeconds(5L))
                           .doFinally(signal -> subscription.close());
    }
}
