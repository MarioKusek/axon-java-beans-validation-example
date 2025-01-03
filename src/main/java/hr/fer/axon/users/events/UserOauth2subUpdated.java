package hr.fer.axon.users.events;

public record UserOauth2subUpdated(
    String login,
    String oauth2sub) {
}
