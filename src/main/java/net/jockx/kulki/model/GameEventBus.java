package net.jockx.kulki.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class GameEventBus {

    private final Map<GameEvent, List<Consumer<StateTransition>>> listeners = new ConcurrentHashMap<>();

    public void subscribe(GameEvent event, Consumer<StateTransition> handler) {
        listeners.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    public void unsubscribe(GameEvent event, Consumer<StateTransition> handler) {
        List<Consumer<StateTransition>> handlers = listeners.get(event);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    public void publish(GameEvent event, StateTransition data) {
        List<Consumer<StateTransition>> handlers = listeners.get(event);
        if (handlers != null) {
            for (Consumer<StateTransition> h : handlers) {
                h.accept(data);
            }
        }
    }
}
