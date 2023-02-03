package gklijs.tech.sticker.axon.query;

import gklijs.tech.sticker.axon.queries.MemberCountQuery;
import gklijs.tech.sticker.axon.queries.MemberDetailsQuery;
import gklijs.tech.sticker.event_api.MemberRegistered;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("member")
public class MemberProjection {

    private final MemberRepository memberRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    public MemberProjection(
            MemberRepository memberRepository,
            QueryUpdateEmitter queryUpdateEmitter
    ) {
        this.memberRepository = memberRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    public void on(MemberRegistered event) {
        MemberDocument member = new MemberDocument(event);
        memberRepository.save(member);
        queryUpdateEmitter.emit(MemberDetailsQuery.class,
                                query -> query.registrationId().equals(event.registrationId()),
                                new MemberDetails(member));
    }

    @QueryHandler
    public long handle(MemberCountQuery query) {
        return memberRepository.count();
    }

    @QueryHandler
    public Optional<MemberDetails> handle(MemberDetailsQuery query) {
        return memberRepository
                .findById(query.registrationId())
                .map(MemberDetails::new);
    }
}
