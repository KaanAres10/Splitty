package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static String password;

    public static void setPassword(String serverPassword) {
        password = serverPassword;
    }

    @GetMapping("/password/{enteredString}")
    @ResponseBody
    public ResponseEntity<Boolean> checkPassword(@PathVariable String enteredString) {
        if (enteredString == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(Objects.equals(enteredString, password));
    }

    public static String getPassword(){
        return password;
    }
}
