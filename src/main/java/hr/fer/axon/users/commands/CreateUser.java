package hr.fer.axon.users.commands;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import hr.fer.axon.users.aggregates.internal.UniqueUserJmbag;
import hr.fer.axon.users.aggregates.internal.UniqueUserLogin;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotNull;

@RecordBuilder
public record CreateUser(
    @UniqueUserLogin
    @TargetAggregateIdentifier
    String login,
    @UniqueUserJmbag
    @NotNull
    String jmbag,
    @NotNull
    String firstName,
    @NotNull
    String lastName,
    @NotNull
    String email,
    double durationFactor,
    @NotNull
    List<String> roles
    ) {
}
