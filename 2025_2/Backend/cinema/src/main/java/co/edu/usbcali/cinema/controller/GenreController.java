package co.edu.usbcali.cinema.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/genre")
@RestController
public class GenreController {

    @GetMapping("/ping")
    String ping() {
        return "pong";
    }
}
