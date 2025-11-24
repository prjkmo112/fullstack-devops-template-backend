package io.github.prjkmo112.eventlogger.service;

import io.github.prjkmo112.eventlogger.dto.EventMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRegisterService {

    private final KafkaSender<String, Object> sender;

    @Value("${kafka.topic.events}")
    private String eventTopic;

    public Mono<Void> send(List<EventMessageDto> messages) {
        Flux<SenderRecord<String, Object, Object>> flux = Flux.fromIterable(messages)
                .map(msg ->
                        SenderRecord.create(
                                eventTopic,
                                null,
                                null,
                                msg.getKey(),
                                msg,
                                msg.getKey()
                        )
                );

        return sender.createOutbound()
                .send(flux)
                .then()
                .doOnError(e -> log.error("Failed to send event messages to Kafka", e));
    }
}
