package com.care.project.admin;

import java.util.List;
import com.care.project.main.dto.MovieDTO;

public interface AdminService {
	List<MovieDTO> getPopularBoxOfficeMovies();
}