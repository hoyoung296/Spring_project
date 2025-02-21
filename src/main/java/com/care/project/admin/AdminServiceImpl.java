package com.care.project.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
				fetchAndUpdateMovies();
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
		MovieDTO kmdbMovie = getMovieFromKmdb(movie.getTitle(), movie.getOpenDt());
		if (kmdbMovie != null) {
			System.out.println("서비스임플 포스터 url 확인 : " + kmdbMovie.getPosterUrl());

			// KMDB에서 가져온 값이 유효할 때만 업데이트
			movie.setPosterUrl(MovieUtils.getFirstPosterUrl(kmdbMovie.getPosterUrl()));
			movie.setMovieSynopsis(MovieUtils.getValidSynopsis(kmdbMovie.getMovieSynopsis()));

			// 한글 제목 유지
			if ((movie.getTitle() == null || movie.getTitle().trim().isEmpty()) && kmdbMovie.getTitle() != null
					&& !kmdbMovie.getTitle().trim().isEmpty()) {
				movie.setTitle(kmdbMovie.getTitle());
			}

			// 영어 제목 유지 (NuLL 체크 추가)
			if (movie.getEntitle() == null || movie.getEntitle().trim().isEmpty()
					|| movie.getEntitle().equals("데이터없음")) {
				movie.setEntitle(kmdbMovie.getEntitle() != null && !kmdbMovie.getEntitle().trim().isEmpty()
						? kmdbMovie.getEntitle()
						: "데이터없음");
			}

			// 감독 정보 유지 (NuLL 체크 추가)
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

			// 개봉일자 유지 (NuLL 체크 추가)
			if (movie.getOpenDt() == null || movie.getOpenDt().trim().isEmpty() || movie.getOpenDt().equals("데이터없음")) {
				movie.setOpenDt(
						kmdbMovie.getOpenDt() != null && !kmdbMovie.getOpenDt().trim().isEmpty() ? kmdbMovie.getOpenDt()
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

	private MovieDTO getMovieFromKmdb(String title, String releaseDts) {
		try {
			String urlString = KmdbApiClient.BASE_URL + "?collection=kmdb_new2&ServiceKey=" + KmdbApiClient.API_KEY
					+ "&query=" + MovieUtils.cleanTitle(title) + "&releaseDts=" + releaseDts.replace("-", "")
					+ "&listCount=1";

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

	@Scheduled(fixedRate = 600000) // 100분
	public void scheduledFetchAndUpdateMovies() {
		System.out.println("자동 업데이트 시작");
		((AdminServiceImpl) AopContext.currentProxy()).fetchAndUpdateMovies(); // 자기 자신을 Proxy로 호출
		System.out.println("자동 업데이트 완료.");
	}

	@Transactional
	public void fetchAndUpdateMovies() {
		List<MovieDTO> newMovies = fetchUniqueBoxOfficeMovies();
		List<MovieDTO> enhancedMovies = enhanceMovieDetails(newMovies); // 상세정보 보강

		for (MovieDTO movie : enhancedMovies) { // enhancedMovies로 반복
			MovieDTO existingMovie = adminMapper.findByMovieId(movie.getMovieId());

			if (existingMovie != null) {
				// 기존 데이터 업데이트
				existingMovie.setTitle(movie.getTitle());
				existingMovie.setDirectorName(movie.getDirectorName());
				existingMovie.setActors(movie.getActors());
				existingMovie.setPosterUrl(movie.getPosterUrl());
				existingMovie.setMovieSynopsis(movie.getMovieSynopsis());

				adminMapper.updateMovie(existingMovie); // 명시적 업데이트
			} else {
				// 새로운 데이터 저장
				adminMapper.insertMovie(movie);
			}
		}
	}
}