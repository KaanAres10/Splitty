package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HandshakeController {

    @RequestMapping("/handshake")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handshake() {}

}
