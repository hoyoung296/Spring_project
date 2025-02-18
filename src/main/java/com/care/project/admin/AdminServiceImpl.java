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
            System.out.println("영화목록 호출 오류" + e);
            return Collections.emptyList();
        }
    }

    private List<MovieDTO> fetchUniqueBoxOfficeMovies() {
        Set<String> seenTitles = new HashSet<>();
        List<MovieDTO> allMovies = new ArrayList<>();
        int daysAgo = 0;

        while (allMovies.size() < 50) {
            try {
                List<MovieDTO> kobisMovies = kobisApiClient.getBoxOfficeMovies(getDateNDaysAgo(daysAgo));
                if (kobisMovies != null) {
                    kobisMovies.stream().filter(movie -> seenTitles.add(movie.getTitle())).forEach(allMovies::add);
                }
            } catch (Exception e) {
                System.out.println("kobis API 호출 오류" + e);
            }
            daysAgo++;
        }

        return allMovies.stream().distinct().collect(Collectors.toList());
    }

    private List<MovieDTO> enhanceMovieDetails(List<MovieDTO> movies) {
        return movies.stream().map(this::enhanceMovie).collect(Collectors.toList());
    }

    private MovieDTO enhanceMovie(MovieDTO movie) {
        // KMDB에서 영화 정보 가져오기
        MovieDTO kmdbMovie = getMovieFromKmdb(movie.getTitle());

        if (kmdbMovie != null) {
            movie.setPosterUrl(kmdbMovie.getPosterUrl());  // KMDB에서 가져온 포스터 URL
            movie.setMovieSynopsis(kmdbMovie.getMovieSynopsis());  // KMDB에서 가져온 시놉시스
            movie.setDirectorName(MovieUtils.getValidDirector(kmdbMovie.getDirectorName()));  // 감독 이름
            movie.setActors(MovieUtils.getValidActors(kmdbMovie.getActors()));  // 배우들
        } else {
            movie.setPosterUrl("데이터없음");
            movie.setMovieSynopsis("데이터없음");
            movie.setDirectorName("데이터없음");
            movie.setActors("데이터없음");
        }

        return movie;
    }

    private String getDateNDaysAgo(int daysAgo) {
        return LocalDate.now().minusDays(daysAgo).format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    private MovieDTO getMovieFromKmdb(String title) {
        try {
            String urlString = KmdbApiClient.BASE_URL + "?collection=kmdb_new2&ServiceKey=" + KmdbApiClient.API_KEY
                    + "&query=" + MovieUtils.cleanTitle(title) + "&listCount=1";
            return kmdbApiClient.getMovieByTitle(urlString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertMoviesInDB(List<MovieDTO> movies) {
        movies.forEach(adminMapper::insertMovie);
    }
}