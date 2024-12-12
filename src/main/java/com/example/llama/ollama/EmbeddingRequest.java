package com.example.llama.ollama;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class EmbeddingRequest {
    private String model;
    private String prompt;
}
