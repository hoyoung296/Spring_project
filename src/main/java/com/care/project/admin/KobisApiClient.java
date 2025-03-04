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
import java.util.Optional;

@Component
public class KobisApiClient {
	private static final String API_KEY = "fea36a1138ff9ae109f6e95653aef1b8";
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
			if (movieEntitle == null || movieEntitle.trim().isEmpty()) {
				movieEntitle = "데이터없음"; // 기본값 설정
			}
			movie.setEntitle(movieEntitle);
			System.out.println("영문 제목 확인 : " + movieEntitle);

			// 감독 정보 가져오기 (Optional 활용)
			JsonNode directorsNode = movieInfoNode.path("directors");

			String director = Optional.ofNullable(directorsNode).filter(JsonNode::isArray)
					.filter(node -> node.size() > 0).map(node -> node.get(0).path("peopleNm").asText().trim())
					.filter(text -> !text.isEmpty()) // 빈 문자열 방지
					.orElse("데이터없음"); // 기본값 설정

			movie.setDirectorName(director);
			System.out.println("감독 확인 : " + movie.getDirectorName());

			// 배우 정보 가져오기 (상위 3명만, Optional 사용)
			JsonNode actorsNode = movieInfoNode.path("actors");

			String actors = Optional.ofNullable(actorsNode).filter(JsonNode::isArray).filter(node -> node.size() > 0)
					.map(node -> {
						List<String> actorList = new ArrayList<>();
						for (JsonNode actorNode : node) {
							if (actorList.size() >= 3)
								break;
							if (actorNode != null && actorNode.has("peopleNm")) { // Null 체크
								String actorName = actorNode.path("peopleNm").asText().trim();
								if (!actorName.isEmpty()) {
									actorList.add(actorName);
								}
							}
						}
						return String.join(", ", actorList);
					}).filter(text -> !text.isEmpty()) // 빈 문자열 방지
					.orElse("데이터없음"); // 기본값 설정

			movie.setActors(actors);
			System.out.println("배우 확인 : " + movie.getActors());

			movie.setMovieSynopsis(MovieUtils.getValidSynopsis(movieInfoNode.path("synopsis").asText("")));

			movie.setPosterUrl(movieInfoNode.path("posters").asText("").isEmpty() ? "데이터없음"
					: movieInfoNode.path("posters").asText());
		} catch (Exception e) {
			System.out.println("KOBIS 상세정보 API 호출 오류: " + e);
			movie.setMovieSynopsis("데이터없음");
			movie.setPosterUrl("데이터없음");
		}
	}
}