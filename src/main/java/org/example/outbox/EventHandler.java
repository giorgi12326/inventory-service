package org.example.outbox;

import org.example.dto.Event;

import java.util.concurrent.CompletionStage;

public interface EventHandler {
    CompletionStage<Void> process(Event payload);
    void compensate(Event payload);
}