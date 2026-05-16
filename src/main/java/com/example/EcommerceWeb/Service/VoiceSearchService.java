package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.DTO.ProductListDTO;
import com.example.EcommerceWeb.DTO.SearchFilters;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
public class VoiceSearchService {

    private final ProductService productService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String PYTHON_VOICE_SEARCH_URL="http://localhost:8000/voice-search";

    public VoiceSearchService(ProductService productService,
                              RestTemplate restTemplate,
                              ObjectMapper objectMapper) {
        this.productService = productService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<ProductListDTO> searchByVoice(MultipartFile audioFile) throws Exception {

        SearchFilters filters = callPythonVoiceSearch(audioFile);

        return productService.searchProducts(
                filters.getName(),
                null,
                filters.getBrand(),
                filters.getMinPrice(),
                filters.getMaxPrice(),
                "productId",
                "asc",
                0,
                10
        ).getContent();
    }

    private SearchFilters callPythonVoiceSearch(MultipartFile audioFile) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("audio", new ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename();
            }
        });

        HttpEntity<?> entity = new HttpEntity<>(builder.build(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    PYTHON_VOICE_SEARCH_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            return objectMapper.readValue(response.getBody(), SearchFilters.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get filters from Python voice search service: " + e.getMessage());
        }
    }
}






