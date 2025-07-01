package server.api;

import commons.Event;
import commons.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getByIdExistingUser() {
        long userId = 1L;
        User user = new User("name", "John");
        user.setId(userId);
        when(userService.getById(userId)).thenReturn(user);

        User response = userController.getById(userId);

        assertEquals(user, response);
    }

    @Test
    void getByIdNonExistingUser() {
        long userId = 2;
        when(userService.getById(userId)).thenReturn(null);

        User response = userController.getById(userId);

        assertEquals(response, null);
    }

    @Test
    void createUser() {
        User newUser = new User("name", "Alice");
        newUser.setId(1L);
        when(userService.createUser()).thenReturn(newUser);

        ResponseEntity<User> response = userController.createUser();

        assertEquals(newUser, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateUserValidUser() {
        User user = new User("name", "Bob");
        user.setId(1L);
        when(userService.saveUser(user)).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser(user);

        assertEquals(user, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).saveUser(user);
    }

    @Test
    void updateUserInvalidUser() {
        User user = null;

        ResponseEntity<User> response = userController.updateUser(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(userService);
    }

    @Test
    void getEventsByUserValidUserId() {
        long userId = 1;
        List<Event> events = new ArrayList<>();
        when(userService.getEventsByUser(userId)).thenReturn(events);

        ResponseEntity<List<Event>> response = userController.getEventsByUser(userId);

        assertEquals(events, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getEventsByUserInvalidUserId() {
        Long userId = null;

        ResponseEntity<List<Event>> response = userController.getEventsByUser(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(userService);
    }
}
