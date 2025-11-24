package io.github.prjkmo112.eventlogger.controller;

import io.github.prjkmo112.eventlogger.dto.EventMessageDto;
import io.github.prjkmo112.eventlogger.service.EventRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventRegisterController {

    private final EventRegisterService eventRegisterService;

    @PostMapping("message")
    public Mono<Void> messageEvent(@Valid @RequestBody EventMessageDto eventMessageDto) {
        return eventRegisterService.send(List.of(eventMessageDto));
    }
}
