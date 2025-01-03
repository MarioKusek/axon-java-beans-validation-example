package hr.fer.axon.users.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import hr.fer.axon.users.aggregates.internal.UniqueUserOauth2sub;
import jakarta.validation.constraints.NotNull;

public record UpdateUserOauth2sub(
    @NotNull
    @TargetAggregateIdentifier
    String login,
    @UniqueUserOauth2sub
    @NotNull
    String oauth2sub) {
}
