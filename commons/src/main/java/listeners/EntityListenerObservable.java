package listeners;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

public class EntityListenerObservable {

    public interface EntityListener<T> {
        default void prePersist(T t) {};
        default void preUpdate(T t) {};
        default void preRemove(T t) {};
        default void postLoad(T t) {};
        default void postRemove(T t) {};
        default void postUpdate(T t) {};
        default void postPersist(T t) {};
    }

    private static final Set<EntityListener> LISTENERS = new HashSet<>();

    public void registerListener(EntityListener listener) {
        LISTENERS.add(listener);
    }

    public void removeListener(EntityListener listener) {
        LISTENERS.remove(listener);
    }

    @PrePersist
    public void prePersist(Object o) {
        LISTENERS.forEach(listener -> {
            try {
                listener.prePersist(o);
            } catch (Exception e) {
                // just continue with the next EntityListener
            }
        });
    }

    @PreUpdate
    public void preUpdate(Object o) {
        LISTENERS.forEach(listener -> {
            try {
                listener.preUpdate(o);
            } catch (Exception e) {
                // just continue with the next EntityListener
            }
        });
    }

    @PreRemove
    public void preRemove(Object o) {
        LISTENERS.forEach(listener -> {
            try {
                listener.preRemove(o);
            } catch (Exception e) {
                // just continue with the next EntityListener
            }
        });
    }

    @PostLoad
    public void postLoad(Object o) {
        LISTENERS.forEach(listener -> {
            try {
                listener.postLoad(o);
            } catch (Exception e) {
                // just continue with the next EntityListener
            }
        });
    }

    @PostRemove
    public void postRemove(Object o) {
        LISTENERS.forEach(listener -> {
            try {
                listener.postRemove(o);
            } catch (Exception e) {
                // just continue with the next EntityListener
            }
        });
    }

    @PostUpdate
    public void postUpdate(Object o) {
        LISTENERS.forEach(listener -> {
            try {
                listener.postUpdate(o);
            } catch (Exception e) {
                // just continue with the next EntityListener
            }
        });
    }

    @PostPersist
    public void postPersist(Object o) {
        LISTENERS.forEach(listener -> {
            try {
                listener.postPersist(o);
            } catch (Exception e) {
                // just continue with the next EntityListener
            }
        });
    }
}
