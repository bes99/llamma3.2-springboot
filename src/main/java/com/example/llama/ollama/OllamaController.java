package com.example.llama.ollama;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
public class OllamaController {
    private final OllamaService ollamaService;
    @GetMapping("/chat")
    public String chatWithOllama(@RequestParam String message) {
        return ollamaService.getFinalSentence("llama3.2-korean", message);
    }
}
