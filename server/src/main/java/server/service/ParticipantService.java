package server.service;

import commons.Event;
import commons.Participant;
import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.EventLastActivityListener;
import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.database.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventLastActivityListener eventLastActivityListener;

    public Participant addParticipantToEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        Participant participant = new Participant(user, event);
        Participant ret = participantRepository.save(participant);
        eventLastActivityListener.postPersist(ret);
        return ret;
    }

    public Participant addParticipantToEvent(Participant participant) {
        Long userId = participant.getUser().getId();
        Long eventId = participant.getEvent().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        boolean isOwner = participant.isOwner();
        participant = new Participant(user, event, participant.getName(), participant.getIban(), participant.getBic(), participant.getMail());
        participant.setOwner(isOwner);
        Participant ret = participantRepository.save(participant);
        eventLastActivityListener.postPersist(ret);
        return ret;
    }

    public Participant updateParticipant(Participant participant) {
        Participant existingParticipant = participantRepository.findById(participant.getId())
                .orElseThrow(() -> new RuntimeException("Participant not found"));
        existingParticipant.setName(participant.getName());
        existingParticipant.setIban(participant.getIban());
        existingParticipant.setBic(participant.getBic());
        existingParticipant.setMail(participant.getMail());

        Participant ret = participantRepository.save(participant);
        eventLastActivityListener.postUpdate(ret);
        return ret;
    }

    public List<Participant> getAllParticipantsByEventId(Long eventId) {
        return participantRepository.findAllByEventId(eventId);
    }

    public boolean deleteParticipant(Long participantId) {
        Optional<Participant> optionalParticipant = participantRepository.findById(participantId);
        if (optionalParticipant.isPresent()) {
            participantRepository.deleteById(participantId);
            eventLastActivityListener.postRemove(optionalParticipant.get());
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteParticipantByUserId(Long eventId, Long userId) {
        List<Participant> list = participantRepository.findAllByEventId(eventId);
        for (Participant p : list) {
            if (p.getUser().getId().equals(userId)) {
                participantRepository.deleteById(p.getId());
                eventLastActivityListener.postRemove(p);
                return true;
            }
        }
        return false;
    }

    public Participant getParticipantByName(Long eventId, String name) {
        List<Participant> list = participantRepository.findAllByEventId(eventId);
        for (Participant p : list) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }
}