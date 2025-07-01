package server.api;

import commons.Debt;
import commons.Event;
import commons.Expense;
import listeners.EntityListenerObservable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import listeners.EntityListenerObservable.EntityListener;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class WebSocketUtil implements EntityListener {

    private final Map<Class, EntityListener> entityListenerMap = new HashMap<>();
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public WebSocketUtil(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;

        // register parameterized entity listeners
        DebtEntityListener debtEntityListener = new DebtEntityListener();
        EventEntityListener eventEntityListener = new EventEntityListener();
        ExpenseEntityListener expenseEntityListener = new ExpenseEntityListener();
        entityListenerMap.put(Debt.class, debtEntityListener);
        entityListenerMap.put(Event.class, eventEntityListener);
        entityListenerMap.put(Expense.class, expenseEntityListener);

        // registers this as an EntityListener
        EntityListenerObservable elb = new EntityListenerObservable();
        elb.registerListener(this);
    }

    // insane amount of boilerplate
    @Override
    public void prePersist(Object object) {
        if (entityListenerMap.containsKey(object.getClass())) {
            entityListenerMap.get(object.getClass()).prePersist(object);
        }
    }

    @Override
    public void preUpdate(Object object) {
        if (entityListenerMap.containsKey(object.getClass())) {
            entityListenerMap.get(object.getClass()).preUpdate(object);
        }
    }

    @Override
    public void preRemove(Object object) {
        if (entityListenerMap.containsKey(object.getClass())) {
            entityListenerMap.get(object.getClass()).preRemove(object);
        }
    }

    @Override
    public void postLoad(Object object) {
        if (entityListenerMap.containsKey(object.getClass())) {
            entityListenerMap.get(object.getClass()).postLoad(object);
        }
    }

    @Override
    public void postRemove(Object object) {
        if (entityListenerMap.containsKey(object.getClass())) {
            entityListenerMap.get(object.getClass()).postRemove(object);
        }
    }

    @Override
    public void postUpdate(Object object) {
        if (entityListenerMap.containsKey(object.getClass())) {
            entityListenerMap.get(object.getClass()).postUpdate(object);
        }
    }

    @Override
    public void postPersist(Object object) {
        if (entityListenerMap.containsKey(object.getClass())) {
            entityListenerMap.get(object.getClass()).postPersist(object);
        }
    }

    private class DebtEntityListener implements EntityListener<Debt> {
        private void sendRemove(Debt debt) {
            final String destination = "/topic/" + debt.getEvent().getId() + "/debts/delete";
            simpMessagingTemplate.convertAndSend(destination, debt);
        }

        private void sendUpdate(Debt debt) {
            final String destination = "/topic/" + debt.getEvent().getId() + "/debts";
            simpMessagingTemplate.convertAndSend(destination, debt);
        }

        @Override
        public void postRemove(Debt debt) {
            sendRemove(debt);
        }

        @Override
        public void postUpdate(Debt debt) {
            sendUpdate(debt);
        }

        @Override
        public void postPersist(Debt debt) {
            sendUpdate(debt);
        }
    }

    private class EventEntityListener implements EntityListener<Event> {
        private void sendRemove(Event event) {
            final String destination = "/topic/" + event.getId() + "/event";
            simpMessagingTemplate.convertAndSend(destination, event);
        }

        private void sendUpdate(Event event) {
            final String destination = "/topic/" + event.getId() + "/event/delete";
            simpMessagingTemplate.convertAndSend(destination, event);
        }

        @Override
        public void postRemove(Event event) {
            sendRemove(event);
        }

        @Override
        public void postUpdate(Event event) {
            sendUpdate(event);
        }

        @Override
        public void postPersist(Event event) {
            sendUpdate(event);
        }
    }

    private class ExpenseEntityListener implements EntityListener<Expense> {
        private void sendRemove(Expense expense) {
            final String destination = "/topic/" + expense.getEvent().getId() + "/expenses/delete";
            simpMessagingTemplate.convertAndSend(destination, expense);
        }

        private void sendUpdate(Expense expense) {
            final String destination = "/topic/" + expense.getEvent().getId() + "/expenses";
            simpMessagingTemplate.convertAndSend(destination, expense);
        }

        @Override
        public void postRemove(Expense expense) {
            sendRemove(expense);
        }

        @Override
        public void postUpdate(Expense expense) {
            sendUpdate(expense);
        }

        @Override
        public void postPersist(Expense expense) {
            sendUpdate(expense);
        }
    }
}
