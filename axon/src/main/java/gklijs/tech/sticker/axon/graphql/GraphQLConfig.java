package gklijs.tech.sticker.axon.graphql;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.UUID;
import java.util.regex.Pattern;

@Configuration
public class GraphQLConfig {

    private final static Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {

        return wiringBuilder -> wiringBuilder
                .scalar(UUID_SCALAR);
    }

    public static final GraphQLScalarType UUID_SCALAR =
            GraphQLScalarType.newScalar()
                             .name("UUID")
                             .description("A custom scalar that handles uuids")
                             .coercing(new Coercing() {
                                 @Override
                                 public Object serialize(Object dataFetcherResult) {
                                     return serializeUuid(dataFetcherResult);
                                 }

                                 @Override
                                 public Object parseValue(Object input) {
                                     return parseUuidFromVariable(input);
                                 }

                                 @Override
                                 public Object parseLiteral(Object input) {
                                     return parseUuidFromAstLiteral(input);
                                 }
                             })
                             .build();


    private static boolean isValidUuid(String possibleUuid) {
        // ps.  I am not trying to replicate RFC-3696 clearly
        return UUID_REGEX.matcher(possibleUuid).matches();
    }

    private static Object serializeUuid(Object dataFetcherResult) {
        String possibleUuidValue = String.valueOf(dataFetcherResult);
        if (isValidUuid(possibleUuidValue)) {
            return possibleUuidValue;
        } else {
            throw new CoercingSerializeException("Unable to serialize " + possibleUuidValue + " as an uuid");
        }
    }

    private static Object parseUuidFromVariable(Object input) {
        if (input instanceof String) {
            String possibleUuidValue = input.toString();
            if (isValidUuid(possibleUuidValue)) {
                return UUID.fromString(possibleUuidValue);
            }
        }
        throw new CoercingParseValueException("Unable to parse variable value " + input + " as an uuid");
    }

    private static Object parseUuidFromAstLiteral(Object input) {
        if (input instanceof StringValue) {
            String possibleUuidValue = ((StringValue) input).getValue();
            if (isValidUuid(possibleUuidValue)) {
                return UUID.fromString(possibleUuidValue);
            }
        }
        throw new CoercingParseLiteralException(
                "Value is not a uuid : '" + input + "'"
        );
    }
}
