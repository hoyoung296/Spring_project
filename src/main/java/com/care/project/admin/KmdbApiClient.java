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

		System.out.println("KMDB API 응답: " + response.getBody());

		JsonNode movieListNode = rootNode.path("Data").path(0).path("Result");

		// 응답이 없을 경우 처리
		if (!movieListNode.isArray() || movieListNode.size() == 0) {
			System.out.println("No matching movie found in KMDB for URL: " + urlString);
			return null;
		}

		MovieDTO movie = new MovieDTO();
		JsonNode firstMovie = movieListNode.get(0);
		movie.setTitle(firstMovie.path("title").asText());

		// 포스터 URL 처리 (Kobis에서 제공하는 포스터 URL 사용, 없으면 '데이터없음')
		if (movie.getPosterUrl() == null || "데이터없음".equals(movie.getPosterUrl())) {
			movie.setPosterUrl(getFirstNonEmptyValue(firstMovie.path("posters")));
		}
		System.out.println("포스터 url 확인 : " + movie.getPosterUrl());

		// 스틸 URL 처리 (KMDb에서 제공하는 스틸컷 URL 사용, 없으면 '데이터없음')
		if (movie.getStillUrl() == null || "데이터없음".equals(movie.getStillUrl())) {
			movie.setStillUrl(getFirstNonEmptyValue(firstMovie.path("stlls")));
		}
		System.out.println("스틸 url 확인 : " + movie.getStillUrl());

		// 시놉시스 처리 (기존 방식 유지, '데이터없음'으로 기본값 설정)
		if (movie.getMovieSynopsis() == null || "데이터없음".equals(movie.getMovieSynopsis())) {
			movie.setMovieSynopsis(
					getFirstNonEmptyValue(firstMovie.path("plots").path("plot").path(0).path("plotText")));
		}
		System.out.println("시놉 확인 : " + movie.getMovieSynopsis());

		return movie;
	}

	// 비어있지 않은 첫 번째 값을 가져오는 함수
	public String getFirstNonEmptyValue(JsonNode node) {
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

		// 디버깅을 위해 출력
		System.out.println("No valid value found, returning '데이터없음'");
		return "데이터없음";
	}
}