package hr.fer.axon.utils;

import java.util.List;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class AxonValidationConfig {

  @Bean
  LocalValidatorFactoryBean localValidatorFactoryBean() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  BeanValidationInterceptor<?> beanValidationInterceptor(LocalValidatorFactoryBean validatorFactory) {
    return new BeanValidationInterceptor<CommandMessage<?>>(validatorFactory);
  }

  @Bean
  CommandGateway commandGateway(
      CommandBus commandBus,
      List<MessageDispatchInterceptor<? super CommandMessage<?>>> dispatchInterceptors,
      List<MessageHandlerInterceptor<? super CommandMessage<?>>> handlerInterceptor)
  {
      handlerInterceptor.forEach(it -> commandBus.registerHandlerInterceptor(it));
      return DefaultCommandGateway.builder()
          .commandBus(commandBus)
          .dispatchInterceptors(dispatchInterceptors)
          .build();
  }
}