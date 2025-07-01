package server.service;

import commons.Event;
import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.UserRepository;

import java.util.List;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User createUser() {
        User user = new User();
        return userRepository.save(user);
    }
    public User getById(long id) {
        return userRepository.findById(id).get();
    }

    public List<Event> getEventsByUser(Long userId) {
        return eventRepository.findByParticipantsContains(userId);
    }
}