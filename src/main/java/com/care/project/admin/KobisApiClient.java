package com.care.project.admin;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.care.project.main.dto.MovieDTO;
import com.care.project.utils.MovieUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class KobisApiClient {
    private static final String API_KEY = "009d110a56e4baee34f673082a74bd8b";
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
            movie.setMovieRank(targetDate + "-" + node.path("rank").asInt());
            System.out.println("랭크 확인 : " + movie.getMovieRank());
            
            movie.setOpenDt(node.path("openDt").asText());
            System.out.println("개봉일자 확인 : " + movie.getOpenDt());

            addMovieDetailsFromKobis(movie, node.path("movieCd").asText());

            movies.add(movie);
        }

        return movies;
    }

    public void addMovieDetailsFromKobis(MovieDTO movie, String movieCd) {
        try {
            String url = MOVIE_INFO_URL + "?key=" + API_KEY + "&movieCd=" + movieCd;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode movieInfoNode = rootNode.path("movieInfoResult").path("movieInfo");
            
            // 상세정보에서 영화 제목(한글)을 가져오기
            String movieTitle = movieInfoNode.path("movieNm").asText();
            System.out.println("한글 제목 확인 : " + movieTitle);
            movie.setTitle(movieTitle);
            
            // 상세정보에서 영화 제목(영문)을 가져오기
            String movieEntitle = movieInfoNode.path("movieNmEn").asText();
            System.out.println("영문 제목 확인 : " + movieEntitle);
            movie.setEntitle(movieEntitle);
            
            // 감독 정보 가져오기
            JsonNode directorsNode = movieInfoNode.path("directors");
            if (directorsNode.isArray() && directorsNode.size() > 0) {
                String director = directorsNode.get(0).path("peopleNm").asText();
                movie.setDirectorName(director);  // 감독 정보 설정
            }
            System.out.println("감독 확인 : " + movie.getDirectorName());

            // 배우 정보 가져오기 (상위 3명만)
            JsonNode actorsNode = movieInfoNode.path("actors");
            StringBuilder actors = new StringBuilder();
            int count = 0;
            for (JsonNode actorNode : actorsNode) {
                if (count >= 3) break;
                String actorName = actorNode.path("peopleNm").asText();
                if (actors.length() > 0) {
                    actors.append(", ");
                }
                actors.append(actorName);
                count++;
            }
            movie.setActors(actors.toString());  // 배우 정보 설정
            
            System.out.println("배우 확인 : " + movie.getActors());
            
            movie.setMovieSynopsis(MovieUtils.getValidSynopsis(movieInfoNode.path("synopsis").asText("")));
            
            movie.setPosterUrl(movieInfoNode.path("posters").asText("").isEmpty() ? "데이터없음" : movieInfoNode.path("posters").asText());
        } catch (Exception e) {
            System.out.println("KOBIS 상세정보 API 호출 오류: " + e);
            movie.setMovieSynopsis("데이터없음");
            movie.setPosterUrl("데이터없음");
        }
    }
}