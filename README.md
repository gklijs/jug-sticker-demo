# Sticker Demo Application

## Introduction

This repository will eventually contain multiple solutions for the same problem. The important thing to keep in mind is
that all components need to be possibly scaled up. This means different simultaneous calls might hit different
applications, which might in turn hit different middleware.

## The problem

A fictional Java User Group want to create a platform for their members. If they register they get a certain about of
credits (50), and with that they can oder stickers (30). It must be impossible for someone just registered to order
multiple sheets of stickers. Please note this is highly simplified. The email is never checked, it doesn't need to be
unique, and there is almost no other validation than a check for the current credits of a member.

## Solutions

All solutions will likely be build using Spring Boot 3 for the application. In most cases the middleware can be started
by using docker, from the docker file in the solution, like [here](/axon/docker-compose.yml). As Sping Boot requires
Java 17, that is needed to run the application.

Graphql is used for the API. As this is also common, you can go to [graphiql](http://localhost:8080/graphiql), if it's
enabled by the `spring.graphql.graphiql.enabled` property. You can find the schema
used [here](common/src/main/resources/graphql/member.graphqls).