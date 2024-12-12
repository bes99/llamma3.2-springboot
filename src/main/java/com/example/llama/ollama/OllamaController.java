package com.example.llama.ollama;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class OllamaController {
    private final OllamaService ollamaService;
    @GetMapping("/chat")
    public String chatWithOllama(@RequestParam String message) {
        return ollamaService.getFinalSentence("llama3.2-korean", message);
    }
    @PostMapping("/embeddings")
    public ResponseEntity<String> getEmbeddings(@RequestBody EmbeddingRequest request) {
        String response = ollamaService.fetchEmbeddings(request);
        return ResponseEntity.ok(response);
    }

}
