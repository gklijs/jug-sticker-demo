package gklijs.tech.sticker.axon.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    @Nullable
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof CommandExecutionException) {
            return GraphqlErrorBuilder.newError()
                                      .errorType(ErrorType.BAD_REQUEST)
                                      .message(ex.getMessage())
                                      .path(env.getExecutionStepInfo().getPath())
                                      .location(env.getField().getSourceLocation())
                                      .build();
        } else {
            return null;
        }
    }
}
