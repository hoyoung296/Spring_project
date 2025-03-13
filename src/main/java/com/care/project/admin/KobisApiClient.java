package com.care.project.admin;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.care.project.main.dto.MovieDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class KobisApiClient {
	private static final String API_KEY = "99886be483ee7d1b0a2da2c733bbe6bf";
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
			// Kobis API에서 영화 기본 정보 가져오기
			String url = MOVIE_INFO_URL + "?key=" + API_KEY + "&movieCd=" + movieCd;
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			JsonNode rootNode = objectMapper.readTree(response.getBody());
			JsonNode movieInfoNode = rootNode.path("movieInfoResult").path("movieInfo");

			// 영화 제목 (한글) 가져오기
			String movieTitle = movieInfoNode.path("movieNm").asText();
			if (movie.getTitle() == null || "데이터없음".equals(movie.getTitle())) {
				movie.setTitle(movieTitle); // 기존 값이 없다면 최신 값으로 설정
			}

			// 영화 제목 (영문) 가져오기
			String movieEntitle = movieInfoNode.path("movieNmEn").asText();
			if (movie.getEntitle() == null || "데이터없음".equals(movie.getEntitle())) {
				movie.setEntitle(movieEntitle.isEmpty() ? "데이터없음" : movieEntitle);
			}

			// 감독 정보 가져오기
			JsonNode directorsNode = movieInfoNode.path("directors");
			String director = Optional.ofNullable(directorsNode).filter(JsonNode::isArray)
					.filter(node -> node.size() > 0).map(node -> node.get(0).path("peopleNm").asText().trim())
					.filter(text -> !text.isEmpty()).orElse("데이터없음");
			if (movie.getDirectorName() == null || "데이터없음".equals(movie.getDirectorName())) {
				movie.setDirectorName(director);
			}

			// 배우 정보 가져오기
			JsonNode actorsNode = movieInfoNode.path("actors");
			String actors = Optional.ofNullable(actorsNode).filter(JsonNode::isArray).filter(node -> node.size() > 0)
					.map(node -> {
						List<String> actorList = new ArrayList<>();
						for (JsonNode actorNode : node) {
							if (actorList.size() >= 3)
								break;
							if (actorNode != null && actorNode.has("peopleNm")) {
								String actorName = actorNode.path("peopleNm").asText().trim();
								if (!actorName.isEmpty()) {
									actorList.add(actorName);
								}
							}
						}
						return String.join(", ", actorList);
					}).filter(text -> !text.isEmpty()).orElse("데이터없음");
			if (movie.getActors() == null || "데이터없음".equals(movie.getActors())) {
				movie.setActors(actors);
			}

			// 시놉시스 가져오기 (KMDb에서 가져온 시놉시스를 덮어씌우지 않음)
			String synopsis = movieInfoNode.path("synopsis").asText();
			if (movie.getMovieSynopsis() == null || "데이터없음".equals(movie.getMovieSynopsis())) {
				movie.setMovieSynopsis(synopsis.isEmpty() ? "데이터없음" : synopsis);
			}

			// 포스터 URL 가져오기 (Kobis에서 제공하는 포스터 URL)
			String posterUrl = movieInfoNode.path("poster").asText();
			if (movie.getPosterUrl() == null || "데이터없음".equals(movie.getPosterUrl())) {
				movie.setPosterUrl(posterUrl.isEmpty() ? "데이터없음" : posterUrl);
			}

		} catch (Exception e) {
			System.out.println("KOBIS 상세정보 API 호출 오류: " + e);
		}
	}
}