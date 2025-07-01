package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class WebSocketUtilTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    private WebSocketUtil webSocketUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        webSocketUtil = new WebSocketUtil(simpMessagingTemplate);
    }

}
