package com.github.projectrake.injector.events;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * Created on 22.11.2017.
 */
public class EventSystem {
    private static EventSystem instance = new EventSystem();
    private final ConcurrentMap<Class<?>, Set<Consumer<Event>>> eventListeners = new ConcurrentHashMap<>();

    public static EventSystem getInstance() {
        return instance;
    }

    public <T extends Event> EventSystem addListener(Class<T> eventType, Consumer<T> listener) {
        eventListeners.computeIfAbsent(eventType, (cl) -> new ConcurrentSkipListSet<>((o1, o2) -> Integer.compareUnsigned(o1.hashCode(), o2.hashCode())));
        eventListeners.get(eventType).add((Consumer<Event>) listener);

        return this;
    }

    public <T extends Event> EventSystem removeListener(Consumer<T> listener) {
        eventListeners.forEach((a, b) -> b.remove(listener));

        return this;
    }

    public EventSystem call(Event event) {
        for (Consumer<Event> listener : eventListeners.getOrDefault(event.getClass(), Collections.emptySet())) {
            listener.accept(event);
        }

        return this;
    }
}
