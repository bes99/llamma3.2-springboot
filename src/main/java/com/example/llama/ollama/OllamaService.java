package com.example.llama.ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
public class OllamaService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(OllamaService.class.getName());

    public OllamaService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String getFinalSentence(String model, String userMessage) {
        try {
            // 요청 본문(JSON) 생성
            String requestBody = createRequestBody(model, userMessage);

            // HTTP POST 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:11434/api/chat"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/x-ndjson")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // HTTP 응답 스트리밍 처리
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // 응답 상태 코드 확인
            if (response.statusCode() != 200) {
                logger.warning("HTTP 요청 실패: 상태 코드 " + response.statusCode());
                throw new RuntimeException("API 요청 실패: " + response.statusCode());
            }

            logger.info("HTTP 요청 성공: 상태 코드 " + response.statusCode());

            StringBuilder finalSentenceBuilder = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                String line;

                // NDJSON 데이터를 줄 단위로 읽어서 content 값 조합
                while ((line = reader.readLine()) != null) {

                    JsonNode jsonNode = parseJson(line);
                    boolean isDone = jsonNode.path("done").asBoolean(false);
                    String content = jsonNode.path("message").path("content").asText("");

                    if (!content.isEmpty()) {
                        finalSentenceBuilder.append(content); // content를 누적
                    }

                    if (isDone) {
                        logger.info("done 상태 확인: 종료");
                        break; // done 상태면 반복 종료
                    }
                }
            }

            String finalSentence = finalSentenceBuilder.toString().trim();
            logger.info("최종 문장: " + finalSentence);
            return finalSentence; // 최종 조합된 문장 반환
        } catch (Exception e) {
            logger.severe("NDJSON 처리 오류: " + e.getMessage());
            throw new RuntimeException("Error during HTTP request or response processing", e);
        }
    }

    private String createRequestBody(String model, String userMessage) {
        // 요청 JSON 문자열 생성
        return String.format("{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", model, userMessage);
    }

    private JsonNode parseJson(String jsonLine) {
        try {
            return objectMapper.readTree(jsonLine);
        } catch (Exception e) {
            logger.warning("JSON 파싱 오류: " + jsonLine);
            throw new RuntimeException("Error parsing JSON line: " + jsonLine, e);
        }
    }
}
