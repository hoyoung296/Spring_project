package com.care.project.admin;

import java.util.List;
import com.care.project.main.dto.MovieDTO;

public interface AdminService {
	public List<MovieDTO> getPopularBoxOfficeMovies();
	public void scheduledFetchAndUpdateMovies();
}