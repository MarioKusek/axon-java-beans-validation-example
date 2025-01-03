package hr.fer.axon.users.user_list;

import java.util.ArrayList;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import hr.fer.axon.users.UserDto;
import hr.fer.axon.users.UsersPage;
import hr.fer.axon.users.queries.FindAllUsers;
import hr.fer.axon.users.queries.FindUserByJmbag;
import hr.fer.axon.users.queries.FindUserByLogin;
import hr.fer.axon.users.queries.FindUserByOauth2sub;
import hr.fer.axon.users.queries.SearchUsers;

@Component
class UserListQueryHandler {
  private UserListRepository repo;

  public static final Sort SORT = Sort.by(Direction.ASC, "lastName")
      .and(Sort.by(Direction.ASC, "firstName"))
      .and(Sort.by(Direction.ASC, "jmbag"));

  public UserListQueryHandler(UserListRepository repo) {
    this.repo = repo;
  }

  @QueryHandler
  UsersPage allUsers(FindAllUsers query) {
    final Page<UserListEntity> users = repo.findAll(query.page());
    return new UsersPage(
        users.getContent().stream().map(UserListQueryHandler::toDto).toList(),
        query.page(),
        users.getTotalElements());
  }

  @QueryHandler
  UsersPage searchUsers(SearchUsers query) {
    Page<UserListEntity> users;

    if(query.searchText().startsWith("!")) {
      final String searchText = query.searchText().substring(1).toLowerCase();
      users = repo.findByNotSearchText(query.page(), "%" + searchText + "%", searchText );
    } else {
      final String searchText = query.searchText().toLowerCase();
      users = repo.findBySearchText(query.page(), "%" + searchText + "%", searchText);
    }

    return new UsersPage(
        users.getContent().stream().map(UserListQueryHandler::toDto).toList(),
        query.page(),
        users.getTotalElements());
  }

  @QueryHandler
  UserDto findByLogin(FindUserByLogin query) {
    return repo.findByLogin(query.login())
        .map(UserListQueryHandler::toDto)
        .orElseGet(() -> null);
  }

  @QueryHandler
  UserDto findByJmbag(FindUserByJmbag query) {
    return repo.findByJmbag(query.jmbag())
        .map(UserListQueryHandler::toDto)
        .orElseGet(() -> null);
  }

  @QueryHandler
  UserDto findByOauth2sub(FindUserByOauth2sub query) {
    return repo.findByOauth2sub(query.oauth2sub())
        .map(UserListQueryHandler::toDto)
        .orElseGet(() -> null);
  }

  private static UserDto toDto(UserListEntity e) {
    return new UserDto(
        e.getLogin(),
        e.getOauth2sub(),
        e.getJmbag(),
        e.getFirstName(),
        e.getLastName(),
        e.getEmail(),
        e.getDurationFactor(),
        new ArrayList<>(e.getRoleNames()));
  }
}
