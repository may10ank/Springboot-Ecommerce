package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ProductListDTO;
import com.example.EcommerceWeb.DTO.RatingSummaryDTO;
import com.example.EcommerceWeb.DTO.SearchFilters;
import com.example.EcommerceWeb.Repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.speech.v1.*;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.protobuf.ByteString;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Service
public class VoiceSearchService {
    @Autowired
    private SpeechToTextService speechToTextService;
    @Autowired
    private  QueryParserService queryParserService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;

    public List<ProductListDTO> searchByVoice(MultipartFile audioFile) throws Exception {
        String query = speechToTextService.transcribe(audioFile);
        SearchFilters filters = queryParserService.parse(query);
        return productService.searchProducts(
                        filters.getName(),
                        filters.getCategory(),
                        filters.getBrand(),
                        filters.getMinPrice(),
                        filters.getMaxPrice(),
                        "id",
                        "asc",
                        0,
                        10
                ).getContent();
    }
}

@Service
class QueryParser{
    private final ChatClient chatClient;
    public QueryParser(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public SearchFilters parse(String query) {
        String prompt = """
           Extract filters from the user query.
            Output strictly as JSON in the following format:
            {
              "name": "keyword for product",
              "category": "category if mentioned",
              "brand": "brand if mentioned",
              "minPrice": number or null,
              "maxPrice": number or null
            }

            Query: "{query}"
            """;

        String Prompt = prompt.replace("{query}", query);

        String response = chatClient.prompt(Prompt)
                .call()
                .content();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, SearchFilters.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse query: " + response, e);
        }
    }
}


@Service
class SpeechToTextService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public SpeechToTextService(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    public String transcribe(MultipartFile audioFile) throws Exception {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(
                audioFile.getResource(),
                OpenAiAudioTranscriptionOptions.builder()
                        .model("whisper-1")
                        .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                        .language("en")
                        .build()
        );
        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        return response.getResult().getOutput();
    }
}

@Service
class QueryParserService {

    private final ChatClient chatClient;

    public QueryParserService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public SearchFilters parse(String query) {
        String prompt = """
           Extract filters from the user query.
           Output strictly as JSON in the following format:
           {
             "name": "keyword for product",
             "category": "category if mentioned",
             "brand": "brand if mentioned",
             "minPrice": number or null,
             "maxPrice": number or null
           }
           Query: "{query}"
           """;

        String formattedPrompt = prompt.replace("{query}", query);

        String response = chatClient.prompt(formattedPrompt)
                .call()
                .content();

        // Extract JSON from the response safely
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start < 0 || end <= start) {
            throw new RuntimeException("No JSON found in Ollama response: " + response);
        }
        String json = response.substring(start, end + 1);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, SearchFilters.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + json, e);
        }
    }
}







