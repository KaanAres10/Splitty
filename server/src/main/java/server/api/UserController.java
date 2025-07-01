package server.api;

import commons.Event;
import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getById(@PathVariable long id){return userService.getById(id);}


    @PostMapping("/create")
    public ResponseEntity<User> createUser() {
        User savedUser = userService.createUser();
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (user == null || user.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<Event>> getEventsByUser(@PathVariable Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Event> events = userService.getEventsByUser(userId);
        return ResponseEntity.ok(events);
    }
}
