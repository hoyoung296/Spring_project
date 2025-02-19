package com.care.project.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.care.project.main.dto.MovieDTO;
import com.care.project.utils.MovieUtils;

@Service
@Primary
public class AdminServiceImpl implements AdminService {
	@Autowired
	private KobisApiClient kobisApiClient;

	@Autowired
	private KmdbApiClient kmdbApiClient;

	@Autowired
	private AdminMapper adminMapper;

	@Override
	public List<MovieDTO> getPopularBoxOfficeMovies() {
		try {
			List<MovieDTO> allMovies = fetchUniqueBoxOfficeMovies();
			List<MovieDTO> enhancedMovies = enhanceMovieDetails(allMovies);

			if (!enhancedMovies.isEmpty()) {
				insertMoviesInDB(enhancedMovies);
			}

			return enhancedMovies;
		} catch (Exception e) {
			System.out.println("영화목록 호출 오류: " + e);
			return Collections.emptyList();
		}
	}

	private List<MovieDTO> fetchUniqueBoxOfficeMovies() {
		Set<String> seenTitles = new HashSet<>();
		List<MovieDTO> allMovies = new ArrayList<>();
		int daysAgo = 1;

		while (allMovies.size() < 50) {
			try {
				List<MovieDTO> kobisMovies = kobisApiClient.getBoxOfficeMovies(getDateNDaysAgo(daysAgo));
				if (kobisMovies != null) {
					kobisMovies.stream().filter(movie -> seenTitles.add(movie.getTitle())).forEach(allMovies::add);
				}
			} catch (Exception e) {
				System.out.println("KOBIS API 호출 오류: " + e);
			}
			daysAgo++;
		}

		return allMovies.stream().distinct().collect(Collectors.toList());
	}

	private List<MovieDTO> enhanceMovieDetails(List<MovieDTO> movies) {
		return movies.stream().map(this::enhanceMovie).collect(Collectors.toList());
	}

	private MovieDTO enhanceMovie(MovieDTO movie) {
		// 1. KOBIS에서 가져온 영화 정보로 상세정보 추가
		enhanceMovieDetailsFromKobis(movie);

		// 2. KMDB에서 영화 제목을 기반으로 시놉시스와 포스터 URL 추가
		MovieDTO kmdbMovie = getMovieFromKmdb(movie.getTitle());
		if (kmdbMovie != null) {
			System.out.println("서비스임플 포스터 url 확인 : " + kmdbMovie.getPosterUrl());

			// KMDB에서 가져온 값이 유효할 때만 업데이트
			movie.setPosterUrl(MovieUtils.getFirstPosterUrl(kmdbMovie.getPosterUrl()));
			movie.setMovieSynopsis(MovieUtils.getValidSynopsis(kmdbMovie.getMovieSynopsis()));

			// KOBIS에서 제목이 이미 존재하면 KMDB 제목으로 덮어쓰지 않음
			if ((movie.getTitle() == null || movie.getTitle().trim().isEmpty()) && kmdbMovie.getTitle() != null
					&& !kmdbMovie.getTitle().trim().isEmpty()) {
				movie.setTitle(kmdbMovie.getTitle());
			}

			// 감독 정보 유지
			if (movie.getDirectorName() == null || movie.getDirectorName().trim().isEmpty()
					|| movie.getDirectorName().equals("데이터없음")) {
				movie.setDirectorName(
						kmdbMovie.getDirectorName() != null && !kmdbMovie.getDirectorName().trim().isEmpty()
								? kmdbMovie.getDirectorName()
								: "데이터없음");
			}

			// 배우 정보 유지 (NULL 체크 추가)
			if (movie.getActors() == null || movie.getActors().trim().isEmpty() || movie.getActors().equals("데이터없음")) {
				movie.setActors(
						kmdbMovie.getActors() != null && !kmdbMovie.getActors().trim().isEmpty() ? kmdbMovie.getActors()
								: "데이터없음");
			}
		} else {
			movie.setPosterUrl("데이터없음");
			movie.setMovieSynopsis("데이터없음");
		}

		return movie;
	}

	private void enhanceMovieDetailsFromKobis(MovieDTO movie) {
		try {
			String movieCd = String.valueOf(movie.getMovieId());
			kobisApiClient.addMovieDetailsFromKobis(movie, movieCd);
		} catch (Exception e) {
			System.out.println("KOBIS API 상세정보 가져오기 오류: " + e);
		}
	}

	private String getDateNDaysAgo(int daysAgo) {
		return LocalDate.now().minusDays(daysAgo).format(DateTimeFormatter.BASIC_ISO_DATE);
	}

	private MovieDTO getMovieFromKmdb(String title) {
		try {
			String urlString = KmdbApiClient.BASE_URL + "?collection=kmdb_new2&ServiceKey=" + KmdbApiClient.API_KEY
					+ "&query=" + MovieUtils.cleanTitle(title) + "&listCount=1";

			System.out.println("주소 확인 : " + urlString);

			MovieDTO kmdbMovie = kmdbApiClient.getMovieByTitle(urlString);

			if (kmdbMovie != null && kmdbMovie.getTitle() != null) {
				return kmdbMovie;
			}

		} catch (Exception e) {
			System.out.println("KMDB API 호출 오류: " + e);
		}

		return null;
	}

	private void insertMoviesInDB(List<MovieDTO> movies) {
		movies.forEach(adminMapper::insertMovie);
	}
}