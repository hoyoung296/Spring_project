package com.care.project.main.dto;

public class MovieDTO {
    private int movieId;  // movie_ID -> movieId
    private String title;
    private String entitle; // entitle -> entitle
    private String posterUrl;  // poster_URL -> posterUrl
    private String stillUrl; // still_url -> stillUrl
    private String movieSynopsis;  // synopsis -> movieSynopsis
    private String directorName;  // director -> directorName
    private String actors;  // actors -> actors
    private String movieRank; // movie_rank - > movieRank (영화데이터 api를 위한 추가, kobis에서 가져오는 rank의 자료형은 int이지만 날짜구별을 위해 앞에
    // "날짜 - 순위" 이런식으로 만들어서 데이터베이스 넣을거라서 String으로 작성
    private String openDt; // open_dt -> openDt (개봉일자까지 조건에 추가하여 정확한 영화데이터 가져오기)
    private String runtime;

	// Getters and Setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
	public String getEntitle() {
		return entitle;
	}

	public void setEntitle(String entitle) {
		this.entitle = entitle;
	}

	public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    
    public String getStillUrl() {
		return stillUrl;
	}

	public void setStillUrl(String stillUrl) {
		this.stillUrl = stillUrl;
	}

	public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public void setMovieSynopsis(String movieSynopsis) {
        this.movieSynopsis = movieSynopsis;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

	public String getMovieRank() {
		return movieRank;
	}

	public void setMovieRank(String movieRank) {
		this.movieRank = movieRank;
	}

	public String getOpenDt() {
		return openDt;
	}

	public void setOpenDt(String openDt) {
		this.openDt = openDt;
	}

	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
}