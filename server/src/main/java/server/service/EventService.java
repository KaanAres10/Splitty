package server.service;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.*;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private DebtRepository debtRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private TransferRepository transferRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event createEvent(Event event, User user) {
        Event savedEvent = eventRepository.save(event);
        savedEvent = eventRepository.save(savedEvent);
        return savedEvent;
    }

    public Event changeTitle(Long eventId, String newTitle) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setTitle(newTitle);
            return eventRepository.save(event);
        } else {
            return null;
        }
    }
    public boolean deleteEvent(Long eventId) {
        List<Transfer> transfers = transferRepository.findByEventId(eventId);
        //delete transfers
        for(Transfer t:transfers)
            transferRepository.deleteById(t.getId());
        List<Debt> debts = debtRepository.findByEventId(eventId);
        //delete debts
        for(Debt d: debts)
            debtRepository.deleteById(d.getId());
        List<Tag> tags = tagRepository.findTagsByEventId(eventId);
        List<Expense> expenses = expenseRepository.findByEventId(eventId);
        for(Expense e: expenses) {
            e.setTags(null);
            expenseRepository.save(e);
        }
        for(Tag t: tags)
            tagRepository.deleteById(t.getId());
        for(Expense e: expenses)
            expenseRepository.deleteById(e.getId());

        List<Participant> participants = participantRepository.findAllByEventId(eventId);
        for(Participant p: participants){
            participantRepository.deleteById(p.getId());
        }
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isPresent()) {
            eventRepository.deleteById(eventId);
            return true;
        } else {
            return false;
        }
    }

    public List<Transfer> getAllTransfersByEventId(Long eventId){
        return transferRepository.findByEventId(eventId);
    }

    public List<Debt> getAllDebtsByEventId(Long eventId){
        return debtRepository.findByEventId(eventId);
    }
    public List<Tag> getAllTagsByEventId(Long eventId){
        return tagRepository.findTagsByEventId(eventId);
    }

    public List<Expense> getAllExpensesByEventId(Long eventId){
        return expenseRepository.findByEventId(eventId);
    }


    public Event findById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public Event findByInviteCode(String inviteCode) {
        return eventRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public List<Event> getAllEvents(){
        return eventRepository.getAllEvents();
    }

}