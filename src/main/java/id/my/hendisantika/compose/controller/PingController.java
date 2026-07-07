package id.my.hendisantika.compose.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-docker-compose-mysql
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Visit: https://s.id/hendisantika
 * Date: 21/05/24
 * Time: 07.11
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/api/ping")
public class PingController {
    private static final Logger logger = LoggerFactory.getLogger(PingController.class);

    @GetMapping
    public String ping() {
        logger.info("Ping endpoint was calleddddd");
        return "Pong >>> It workingggg!";
    }
}
