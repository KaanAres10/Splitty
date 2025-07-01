package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class AdminControllerTest {

    private AdminController adminController;

    @BeforeEach
    public void setUp() {
        adminController = new AdminController();
        AdminController.setPassword("adminPassword"); // Set a predefined password for testing
    }

    @Test
    public void testCheckPasswordValidPassword() {
        ResponseEntity<Boolean> response = adminController.checkPassword("adminPassword");
        assertTrue(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCheckPasswordInvalidPassword() {
        ResponseEntity<Boolean> response = adminController.checkPassword("wrongPassword");
        assertFalse(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testCheckPasswordNullInput() {
        ResponseEntity<Boolean> response = adminController.checkPassword(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testSetPassword() {
        String newPassword = "a";
        AdminController.setPassword(newPassword);
        assertEquals(newPassword, AdminController.getPassword());
    }
}

