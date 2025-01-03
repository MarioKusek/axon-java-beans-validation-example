package hr.fer.axon.users.events;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record UserCreated(
    @TargetAggregateIdentifier
    String login,
    String jmbag,
    String firstName,
    String lastName,
    String email,
    double durationFactor,
    List<String> roles
    ) {
}
