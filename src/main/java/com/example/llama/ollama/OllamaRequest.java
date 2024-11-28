package com.example.llama.ollama;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OllamaRequest {
    private String model;
    private List<Message> messages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    public static OllamaRequest create(String model, String userMessage) {
        return new OllamaRequest(model, List.of(new Message("user", userMessage)));
    }
}
