package com.healthcore.appointmentservice.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphqlExceptionHandler {

    @GraphQlExceptionHandler(DataNotFoundException.class)
    public GraphQLError handleNotFound(DataNotFoundException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(org.springframework.graphql.execution.ErrorType.NOT_FOUND)
                .extensions(java.util.Map.of("code", "NOT_FOUND"))
                .build();
    }

    @GraphQlExceptionHandler(AccessDeniedException.class)
    public GraphQLError handleAccessDenied(AccessDeniedException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(org.springframework.graphql.execution.ErrorType.FORBIDDEN)
                .extensions(java.util.Map.of("code", "FORBIDDEN"))
                .build();
    }

    @GraphQlExceptionHandler(ArgumentException.class)
    public GraphQLError handleBadRequest(ArgumentException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(org.springframework.graphql.execution.ErrorType.BAD_REQUEST)
                .extensions(java.util.Map.of("code", "BAD_REQUEST"))
                .build();
    }
}
