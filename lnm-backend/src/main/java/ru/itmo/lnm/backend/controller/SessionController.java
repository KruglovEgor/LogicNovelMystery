package ru.itmo.lnm.backend.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.itmo.lnm.backend.dto.SessionDto;
import ru.itmo.lnm.backend.messages.LnmSessionListResponse;
import ru.itmo.lnm.backend.service.SessionService;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    public ResponseEntity<String> createSession(@RequestBody @Valid SessionDto sessionDto, Authentication authentication) {
        var response = sessionService.createSession(sessionDto, authentication.getName());
        return response;
    }

    @GetMapping
    public ResponseEntity<LnmSessionListResponse> getWaitingSession(Authentication authentication){
        var response = sessionService.getWaitingSession(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
