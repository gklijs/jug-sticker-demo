package gklijs.tech.sticker.axon.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                                 .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                                 .findAndRegisterModules();
    }

    @Bean
    @Primary
    public Serializer serializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                                .objectMapper(objectMapper.copy())
                                .defaultTyping()
                                .build();
    }
}
