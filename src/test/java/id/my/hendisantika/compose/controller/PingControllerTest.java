package id.my.hendisantika.compose.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PingControllerTest {

    @Test
    void pingReturnsExpectedMessage() {
        PingController controller = new PingController();

        String response = controller.ping();

        assertEquals("Pong >>> It works!", response);
    }
}
