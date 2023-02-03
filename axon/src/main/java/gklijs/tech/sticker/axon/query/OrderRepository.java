package gklijs.tech.sticker.axon.query;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.support.QuerydslMongoPredicateExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends MongoRepository<OrderDocument, UUID> {

    List<OrderDocument> findByRegistrationIdOrderByDateOrdered(UUID registrationId);
}
