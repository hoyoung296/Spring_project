package com.care.project.admin;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.care.project.main.dto.MovieDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KmdbApiClient {
    public static final String API_KEY = "C991I3J9JS89X40H7NL7";
    public static final String BASE_URL = "https://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp";

    // KMDB에서 영화 정보 가져오기
    public MovieDTO getMovieByTitle(String urlString) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(urlString, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode movieListNode = rootNode.path("Data").path(0).path("Result");

        // 응답이 없을 경우 처리
        if (!movieListNode.isArray() || movieListNode.size() == 0) {
            System.out.println("No matching movie found in KMDB for URL: " + urlString);
            return null;
        }

        MovieDTO movie = new MovieDTO();
        JsonNode firstMovie = movieListNode.get(0);
        movie.setTitle(firstMovie.path("title").asText());

        // 포스터 URL 처리
        movie.setPosterUrl(getFirstNonEmptyValue(firstMovie.path("posters")));

        // 시놉시스 처리 (기존 방식 유지)
        movie.setMovieSynopsis(getFirstNonEmptyValue(firstMovie.path("plots").path("plot").path(0).path("plotText")));

        return movie;
    }

    private String getFirstNonEmptyValue(JsonNode node) {
        if (node.isArray()) {
            for (JsonNode item : node) {
                String textValue = item.asText().trim();
                if (!textValue.isEmpty()) {
                    return textValue;
                }
            }
        } else {
            String textValue = node.asText().trim();
            if (!textValue.isEmpty()) {
                return textValue;
            }
        }
        return "데이터없음";
    }
}