package hr.fer.axon.users.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import jakarta.validation.constraints.NotNull;

public record DeleteUser(
    @NotNull
    @TargetAggregateIdentifier
    String login) {
}
