package server;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import listeners.EntityListenerObservable.EntityListener;
import server.database.EventRepository;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public class EventLastActivityListener implements EntityListener {
    private final Map<Class, EntityListener> entityListenerMap = new HashMap<>();
    private final EventRepository eventRepository;

    @Autowired
    public EventLastActivityListener(EventRepository eventRepository) {
        this.eventRepository = eventRepository;

        // register parameterized entity listeners
        DebtEntityListener debtEntityListener = new DebtEntityListener();
        ExpenseEntityListener expenseEntityListener = new ExpenseEntityListener();
        ParticipantEntityListener participantEntityListener = new ParticipantEntityListener();
        TagEntityListener tagEntityListener = new TagEntityListener();
        entityListenerMap.put(Debt.class, debtEntityListener);
        entityListenerMap.put(Expense.class, expenseEntityListener);
        entityListenerMap.put(Participant.class, participantEntityListener);
        entityListenerMap.put(Tag.class, tagEntityListener);

        // register this as an entity listener
        // actually don't because that doesn't work for some reason
//        EntityListenerObservable entityListenerObservable = new EntityListenerObservable();
//        entityListenerObservable.registerListener(this);
    }

    private void updateEventLastActivity(Event event) {
        // if the event is already in the database calling save will trigger the @PreUpdate
        // method that exists in the Event class.
        eventRepository.findById(event.getId()).ifPresent(e -> {
            e.setLastActivityDate();
            eventRepository.save(e);
        });
    }

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
        @Override
        public void postRemove(Debt debt) {
            updateEventLastActivity(debt.getEvent());
        }

        @Override
        public void postUpdate(Debt debt) {
            updateEventLastActivity(debt.getEvent());
        }

        @Override
        public void postPersist(Debt debt) {
            updateEventLastActivity(debt.getEvent());
        }
    }

    private class ExpenseEntityListener implements EntityListener<Expense> {
        @Override
        public void postRemove(Expense expense) {
            updateEventLastActivity(expense.getEvent());
        }

        @Override
        public void postUpdate(Expense expense) {
            updateEventLastActivity(expense.getEvent());
        }

        @Override
        public void postPersist(Expense expense) {
            updateEventLastActivity(expense.getEvent());
        }
    }

    private class ParticipantEntityListener implements EntityListener<Participant> {
        @Override
        public void postRemove(Participant participant) {
            updateEventLastActivity(participant.getEvent());
        }

        @Override
        public void postUpdate(Participant participant) {
            updateEventLastActivity(participant.getEvent());
        }

        @Override
        public void postPersist(Participant participant) {
            updateEventLastActivity(participant.getEvent());
        }
    }

    private class TagEntityListener implements EntityListener<Tag> {
        @Override
        public void postRemove(Tag tag) {
            updateEventLastActivity(tag.getEvent());
        }

        @Override
        public void postUpdate(Tag tag) {
            updateEventLastActivity(tag.getEvent());
        }

        @Override
        public void postPersist(Tag tag) {
            updateEventLastActivity(tag.getEvent());
        }
    }
}
