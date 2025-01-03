package hr.fer.axon.users.infrastructure;

import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import hr.fer.axon.users.commands.CreateUser;
import hr.fer.axon.users.user_list.UserListRepository;

@Component
public class AdminUserInitializer {
  private String adminUser;
  private CommandGateway commandGateway;
  private UserListRepository userListRepository;

  public AdminUserInitializer(
      @Value("${admin.user}") String adminUser,
      CommandGateway commandGateway,
      UserListRepository userListRepository)
  {
    this.adminUser = adminUser;
    this.commandGateway = commandGateway;
    this.userListRepository = userListRepository;
  }

  @EventListener(ApplicationStartedEvent.class)
  void initAdminUser(ApplicationStartedEvent event) {
    initAdminUser();
  }

  @Transactional
  void initAdminUser() {
    final UUID randomUUID = UUID.randomUUID();
    userListRepository.findById(adminUser)
      .ifPresentOrElse(user -> {
          user.getRoleNames().add("admin");
          userListRepository.saveAndFlush(user);
        },
        () -> {
          try {
            commandGateway.sendAndWait(new CreateUser(adminUser, randomUUID.toString(), "Admin", "Admin", "admin@fepa.fer.hr", 1.0, List.of("admin")));
          } catch (JSR303ViolationException e) {
            userListRepository.findById(adminUser)
              .ifPresent(user -> {
                user.getRoleNames().add("admin");
                userListRepository.saveAndFlush(user);
              });
          }
        });
  }
}
