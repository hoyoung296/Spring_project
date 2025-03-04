package com.care.project.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care.project.main.dto.MemberDTO;
import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ScheduleDTO;
import com.care.project.main.dto.ScreenDTO;
import com.care.project.main.mapper.MemberMapper;
import com.care.project.main.mapper.ScheduleMapper;
import com.care.project.utils.MovieUtils;

@Service
@Primary
public class AdminServiceImpl implements AdminService {
	@Autowired
	MemberMapper memberMapper;

	@Autowired
	ScheduleMapper ScheduleMapper;

	@Autowired
	KobisApiClient kobisApiClient;

	@Autowired
	KmdbApiClient kmdbApiClient;

	@Autowired
	AdminMapper adminMapper;

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

	public List<MovieDTO> fetchUniqueBoxOfficeMovies() {
		Set<String> seenTitles = new HashSet<>();
		List<MovieDTO> allMovies = new ArrayList<>();
		int daysAgo = 1;

		while (allMovies.size() < 10) {
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

	public List<MovieDTO> enhanceMovieDetails(List<MovieDTO> movies) {
		return movies.stream().map(this::enhanceMovie).collect(Collectors.toList());
	}

	public MovieDTO enhanceMovie(MovieDTO movie) {
		// 1. KOBIS에서 가져온 영화 정보로 상세정보 추가
		enhanceMovieDetailsFromKobis(movie);

		// 2. KMDB에서 영화 제목을 기반으로 시놉시스와 포스터 URL 추가
		MovieDTO kmdbMovie = getMovieFromKmdb(movie.getTitle(), movie.getOpenDt());
		if (kmdbMovie != null) {
			System.out.println("서비스임플 포스터 url 확인 : " + kmdbMovie.getPosterUrl());

			// KMDB에서 가져온 값이 유효할 때만 업데이트
			movie.setPosterUrl(MovieUtils.getFirstPosterUrl(kmdbMovie.getPosterUrl()));
			movie.setStillUrl(MovieUtils.getFirstPosterUrl(kmdbMovie.getStillUrl()));
			movie.setMovieSynopsis(MovieUtils.getValidSynopsis(kmdbMovie.getMovieSynopsis()));
			movie.setRuntime(MovieUtils.getValidRunntime(kmdbMovie.getRuntime()));

			// 한글 제목 유지
			if ((movie.getTitle() == null || movie.getTitle().trim().isEmpty()) && kmdbMovie.getTitle() != null
					&& !kmdbMovie.getTitle().trim().isEmpty()) {
				movie.setTitle(kmdbMovie.getTitle());
			}

			// 영어 제목 유지 (NULL 체크 추가)
			if (movie.getEntitle() == null || movie.getEntitle().trim().isEmpty()
					|| movie.getEntitle().equals("데이터없음")) {
				movie.setEntitle(kmdbMovie.getEntitle() != null && !kmdbMovie.getEntitle().trim().isEmpty()
						? kmdbMovie.getEntitle()
								: "데이터없음");
			}

			// 감독 정보 유지 (NULL 체크 추가)
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
			movie.setStillUrl("데이터없음");
			movie.setPosterUrl("데이터없음");
			movie.setMovieSynopsis("데이터없음");
			movie.setRuntime("데이터없음");
		}
		return movie;
	}

	public void enhanceMovieDetailsFromKobis(MovieDTO movie) {
		try {
			String movieCd = String.valueOf(movie.getMovieId());
			kobisApiClient.addMovieDetailsFromKobis(movie, movieCd);
		} catch (Exception e) {
			System.out.println("KOBIS API 상세정보 가져오기 오류: " + e);
		}
	}

	public String getDateNDaysAgo(int daysAgo) {
		return LocalDate.now().minusDays(daysAgo).format(DateTimeFormatter.BASIC_ISO_DATE);
	}

	public MovieDTO getMovieFromKmdb(String title, String releaseDts) {
		try {
			String urlString = KmdbApiClient.BASE_URL + "?collection=kmdb_new2&ServiceKey=" + KmdbApiClient.API_KEY
					+ "&query=" + title.replaceAll("[!HS!HE\\\\\\\\s]", "").trim() + "&releaseDts="
					+ releaseDts.replace("-", "") + "&listCount=1";

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

	@Override
	public MovieDTO getMovieById(int movieID) {
		// 기존 영화 정보 가져오기
		return adminMapper.findByMovieId(movieID);
	}

	public int editMovie(MovieDTO movie) {
		return adminMapper.editMovie(movie); // 수정 가능한 영화정보 8개 업데이트
	}

	@Scheduled(cron = "0 0 23 * * *") // 매일 밤 11시에 실행
	public void scheduledFetchAndUpdateMovies() {
		System.out.println("자동 업데이트 시작");
		fetchAndUpdateMovies();
		System.out.println("자동 업데이트 완료.");
	}

	@Transactional
	public void fetchAndUpdateMovies() {
		List<MovieDTO> newMovies = fetchUniqueBoxOfficeMovies();
		List<MovieDTO> enhancedMovies = enhanceMovieDetails(newMovies); // 상세정보 보강

		for (MovieDTO movie : enhancedMovies) { // enhancedMovies로 반복
			MovieDTO existingMovie = adminMapper.findByMovieId(movie.getMovieId());

			if (existingMovie != null) {
				existingMovie.setTitle(movie.getTitle());
				existingMovie.setEntitle(movie.getEntitle());
				existingMovie.setPosterUrl(movie.getPosterUrl());
				existingMovie.setStillUrl(movie.getStillUrl());
				existingMovie.setMovieSynopsis(movie.getMovieSynopsis());
				existingMovie.setDirectorName(movie.getDirectorName());
				existingMovie.setActors(movie.getActors());
				existingMovie.setMovieRank(movie.getMovieRank());
				existingMovie.setOpenDt(movie.getOpenDt());
				existingMovie.setRuntime(movie.getRuntime());

				adminMapper.updateMovie(existingMovie);
			} else {
				adminMapper.insertMovie(movie);
			}
		}
	}

	@Override
	public List<ScreenDTO> getAllScreens() {
		return ScheduleMapper.getAllScreens();
	}

	@Override
	public List<Map<String, Object>> getAllSchedules() {
		return ScheduleMapper.getAllSchedules();
	}

	@Override
	public int insertSchedule(List<ScheduleDTO> scheduleList) {
		int result = 0;
		for (ScheduleDTO schedule : scheduleList) {
			result += ScheduleMapper.insertSchedule(schedule);
		}
		return result;
	}

	@Override
	public int deleteSchedule(int scheduleId) {
		return ScheduleMapper.deleteSchedule(scheduleId);
	}

	@Override
	public List<MemberDTO> getUserList() {
		return memberMapper.userData(); // userData SQL 호출
	}
	
	public int insertMovie(MovieDTO dto) {
		return adminMapper.insertMovie(dto);
	}
}