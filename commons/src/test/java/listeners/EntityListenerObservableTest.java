package listeners;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class EntityListenerObservableTest {
    private EntityListenerObservable observable;
    private EntityListenerObservable.EntityListener<Object> listener;
    private EntityListenerObservable.EntityListener<Object> secondListener;

    @BeforeEach
    public void setUp() {
        observable = new EntityListenerObservable();
        listener = mock(EntityListenerObservable.EntityListener.class);
        secondListener = mock(EntityListenerObservable.EntityListener.class);
    }

    @Test
    public void testRegisterListener() {
        observable.registerListener(listener);
        observable.prePersist(new Object());
        verify(listener, times(1)).prePersist(any());
    }

    @Test
    public void testRemoveListener() {
        observable.registerListener(listener);
        observable.removeListener(listener);
        observable.prePersist(new Object());
        verify(listener, times(0)).prePersist(any());
    }

    @Test
    public void testPrePersistNotification() {
        observable.registerListener(listener);
        Object entity = new Object();
        observable.prePersist(entity);
        verify(listener, times(1)).prePersist(entity);
    }

    @Test
    public void testPreUpdateNotification() {
        observable.registerListener(listener);
        Object entity = new Object();
        observable.preUpdate(entity);
        verify(listener, times(1)).preUpdate(entity);
    }

    @Test
    public void testPreRemoveNotification() {
        observable.registerListener(listener);
        Object entity = new Object();
        observable.preRemove(entity);
        verify(listener, times(1)).preRemove(entity);
    }

    @Test
    public void testPostLoadNotification() {
        observable.registerListener(listener);
        Object entity = new Object();
        observable.postLoad(entity);
        verify(listener, times(1)).postLoad(entity);
    }

    @Test
    public void testPostRemoveNotification() {
        observable.registerListener(listener);
        Object entity = new Object();
        observable.postRemove(entity);
        verify(listener, times(1)).postRemove(entity);
    }

    @Test
    public void testPostUpdateNotification() {
        observable.registerListener(listener);
        Object entity = new Object();
        observable.postUpdate(entity);
        verify(listener, times(1)).postUpdate(entity);
    }

    @Test
    public void testPostPersistNotification() {
        observable.registerListener(listener);
        Object entity = new Object();
        observable.postPersist(entity);
        verify(listener, times(1)).postPersist(entity);
    }

    // Testing multiple listeners receiving notifications
    @Test
    public void testMultipleListenersNotification() {
        observable.registerListener(listener);
        observable.registerListener(secondListener);
        Object entity = new Object();

        observable.prePersist(entity);
        verify(listener, times(1)).prePersist(entity);
        verify(secondListener, times(1)).prePersist(entity);
    }


    // Test remaining listeners still receive notification after one is removed
    @Test
    public void testNotificationAfterListenerRemoval() {
        EntityListenerObservable observable = new EntityListenerObservable();
        EntityListenerObservable.EntityListener<Object> listener1 = mock(EntityListenerObservable.EntityListener.class);
        EntityListenerObservable.EntityListener<Object> listener2 = mock(EntityListenerObservable.EntityListener.class);

        observable.registerListener(listener1);
        observable.registerListener(listener2);
        observable.removeListener(listener1);

        Object entity = new Object();
        observable.prePersist(entity);
        verify(listener1, times(0)).prePersist(entity);
        verify(listener2, times(1)).prePersist(entity);
    }

}
