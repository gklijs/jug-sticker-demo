package gklijs.tech.sticker.axon.query;

import gklijs.tech.sticker.axon.queries.MemberCountQuery;
import gklijs.tech.sticker.axon.queries.MemberDetailsQuery;
import gklijs.tech.sticker.axon.queries.Order;
import gklijs.tech.sticker.axon.queries.OrderDetailsQuery;
import gklijs.tech.sticker.axon.queries.OrderHistoryForMemberQuery;
import gklijs.tech.sticker.event_api.MemberRegistered;
import gklijs.tech.sticker.event_api.StickersOrdered;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@ProcessingGroup("order")
public class OrderProjection {

    private final OrderRepository orderRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    public OrderProjection(
            OrderRepository orderRepository,
            QueryUpdateEmitter queryUpdateEmitter
    ) {
        this.orderRepository = orderRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    public void on(StickersOrdered event, @Timestamp Instant timestamp) {
        Order order = new Order(
                event.orderId(), "sticker", event.amount(), timestamp, event.person(), event.address()
        );
        orderRepository.save(new OrderDocument(order, event.registrationId()));
        queryUpdateEmitter.emit(OrderDetailsQuery.class,
                                query -> query.orderId().equals(event.orderId()),
                                order);
    }

    @QueryHandler
    public List<Order> handle(OrderHistoryForMemberQuery query) {
        return orderRepository
                .findByRegistrationIdOrderByDateOrdered(query.registrationId())
                .stream()
                .map(OrderDocument::toOrder)
                .toList();
    }

    @QueryHandler
    public Optional<Order> handle(OrderDetailsQuery query) {
        UUID orderId = query.orderId();
        return orderRepository
                .findById(orderId)
                .map(OrderDocument::toOrder);
    }
}
