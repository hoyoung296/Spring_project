package com.care.project.admin;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.care.project.main.dto.MovieDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class KobisApiClient {
    private static final String API_KEY = "835b2a2e897258d2bc07966ac81c0970";
    private static final String BASE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private static final String MOVIE_INFO_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MovieDTO> getBoxOfficeMovies(String targetDate) throws Exception {
        List<MovieDTO> movies = new ArrayList<>();
        String urlString = BASE_URL + "?key=" + API_KEY + "&targetDt=" + targetDate;

        ResponseEntity<String> response = restTemplate.getForEntity(urlString, String.class);
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        JsonNode boxOfficeListNode = rootNode.path("boxOfficeResult").path("dailyBoxOfficeList");

        for (JsonNode node : boxOfficeListNode) {
            MovieDTO movie = new MovieDTO();
            movie.setMovieId(node.path("movieCd").asInt());
            movie.setTitle(node.path("movieNm").asText());

            // 추가: 영화 상세정보 가져오기
            addMovieDetailsFromKobis(movie, node.path("movieCd").asText());

            movies.add(movie);
        }

        return movies;
    }

    private void addMovieDetailsFromKobis(MovieDTO movie, String movieCd) {
        try {
            String url = MOVIE_INFO_URL + "?key=" + API_KEY + "&movieCd=" + movieCd;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode movieInfoNode = rootNode.path("movieInfoResult").path("movieInfo");

            // 시놉시스 추가
            String synopsis = movieInfoNode.path("synopsis").asText("");
            movie.setMovieSynopsis(synopsis.isEmpty() ? "데이터없음" : synopsis);

            // 포스터 URL 추가
            String posterUrl = movieInfoNode.path("posters").asText("");
            movie.setPosterUrl(posterUrl.isEmpty() ? "데이터없음" : posterUrl);
        } catch (Exception e) {
            System.out.println("KOBIS 상세정보 API 호출 오류: " + e);
            movie.setMovieSynopsis("데이터없음");
            movie.setPosterUrl("데이터없음");
        }
    }
}