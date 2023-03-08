package gklijs.tech.sticker.axon.transformation;

import com.google.protobuf.ByteString;
import io.axoniq.axonserver.connector.AxonServerConnection;
import io.axoniq.axonserver.connector.AxonServerConnectionFactory;
import io.axoniq.axonserver.connector.event.EventChannel;
import io.axoniq.axonserver.connector.event.EventStream;
import io.axoniq.axonserver.connector.event.transformation.ActiveTransformation;
import io.axoniq.axonserver.connector.event.transformation.EventTransformation;
import io.axoniq.axonserver.connector.event.transformation.EventTransformationChannel;
import io.axoniq.axonserver.connector.impl.ServerAddress;
import io.axoniq.axonserver.grpc.SerializedObject;
import io.axoniq.axonserver.grpc.event.Event;
import io.axoniq.axonserver.grpc.event.EventWithToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Component
public class TransFormer {

    private static final Logger logger = LoggerFactory.getLogger(TransFormer.class);

    private AxonServerConnection connection;
    private String big;
    private long eventToReplace = 0L;

    @Scheduled(initialDelay = 10_000, fixedRate = 30_000)
    private void transform() throws ExecutionException, InterruptedException {
        EventTransformationChannel channel = getConnection().eventTransformationChannel();
        logger.info("starting for {}", eventToReplace);
        CompletableFuture<ActiveTransformation> transformation =
                channel.newTransformation("Add some bytes" + eventToReplace);
        transformation = setReplaceActions(transformation);
        String id = transformation.thenCompose(ActiveTransformation::startApplying).get().id();
        while (getState(channel, id) == EventTransformation.State.APPLYING){
            logger.info("Still busy applying");
            Thread.sleep(2500L);
        }
        logger.info("ended applying for {}", eventToReplace);
        channel.startCompacting().get();
        logger.info("ended compacting for {}", eventToReplace);
        eventToReplace = eventToReplace + 10;
    }

    private EventTransformation.State getState(EventTransformationChannel channel, String id)
            throws ExecutionException, InterruptedException {
        AtomicReference<EventTransformation.State> result = new AtomicReference<>(EventTransformation.State.APPLIED);
        channel.transformations().get().forEach(t -> {
            if (t.id().equals(id)){
                result.set(t.state());
            }
        });
        return result.get();
    }

    private CompletableFuture<ActiveTransformation> setReplaceActions(CompletableFuture<ActiveTransformation> transformation)
            throws InterruptedException {
        CompletableFuture<ActiveTransformation> result = transformation;
        EventChannel channel = getConnection().eventChannel();
        EventStream eventStream = channel.openStream(eventToReplace - 1, 10);
        for(int i = 0; i < 10; i++){
            long token = eventToReplace + i;
            EventWithToken eventWithToken = eventStream.next();
            result = result.thenCompose(et -> et.transform(executor -> executor.replaceEvent(token, bigEvent(eventWithToken.getEvent()))));
        }
        return result;
    }

    private AxonServerConnection getConnection() {
        if (connection == null) {
            AxonServerConnectionFactory factory = AxonServerConnectionFactory
                    .forClient("event-transformer")
                    .routingServers(new ServerAddress("localhost", 8124))
                    .build();
            connection = factory.connect("default");
        }
        return connection;
    }

    private Event bigEvent(Event event) {
        return Event.newBuilder(event)
                .setPayload(
                SerializedObject.newBuilder().setData(event.getPayload().getData().concat(bigString())).build()
        ).build();
    }

    private ByteString bigString() {
        if (big == null) {
            StringBuilder builder = new StringBuilder();
            IntStream.range(0, 1000).forEach(builder::append);
            big = builder.toString();
        }
        return ByteString.copyFromUtf8(big);
    }
}
