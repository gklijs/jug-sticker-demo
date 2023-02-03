package gklijs.tech.sticker.axon.query;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MemberRepository extends MongoRepository<MemberDocument, UUID> {

}
