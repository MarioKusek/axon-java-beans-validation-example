package hr.fer.axon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateNotFoundException;

public class ProjectionFixtureConfiguration<T> {
    private Callable<Aggregate<T>> aggregateFactory;
    private final List<Object> given = new ArrayList<>();

    private ProjectionFixtureConfiguration<T> newInstance(Callable<Aggregate<T>> factory) {
        this.aggregateFactory = factory;
        return this;
    }

    public void reset() {
        this.given.clear();
    }

    public void given(Object... events) {
        for (Object event : events) {
            this.given.add(event);
        }
    }

    public void apply() throws Exception {
        DefaultUnitOfWork<?> unitOfWork = DefaultUnitOfWork.startAndGet(null);
        Aggregate<T> aggregate;

        try {
            aggregate = aggregateFactory.call();
        } catch (AggregateNotFoundException e) {
            // Wait for 1 second and retry
            Thread.sleep(1000);
            aggregate = aggregateFactory.call();
        }

        aggregate.execute(t -> {
            for (Object event : given) {
                AggregateLifecycle.apply(event);
            }
        });

        unitOfWork.commit();
    }

    public static <T> ProjectionFixtureConfiguration<T> aggregateInstance(Callable<Aggregate<T>> factory) {
        ProjectionFixtureConfiguration<T> projectionFixtureConfiguration = new ProjectionFixtureConfiguration<>();
        return projectionFixtureConfiguration.newInstance(factory);
    }
}
