package com.care.project.utils;

public class MovieUtils {
	public static String getValidSynopsis(String synopsis) {
		return (synopsis == null || synopsis.trim().isEmpty()) ? "데이터없음" : trimSynopsis(synopsis, 300);
	}
	
	public static String getValidRunntime(String runtime) {
		return (runtime == null || runtime.trim().isEmpty()) ? "데이터없음" : runtime;
	}
	
	public static String trimSynopsis(String synopsis, int maxLength) {
		return synopsis.length() > maxLength ? synopsis.substring(0, maxLength) + "..." : synopsis;
	}
	
	public static String getFirstPosterUrl(String posterUrls) {
	    if (posterUrls == null || posterUrls.trim().isEmpty()) {
	        return "데이터없음";
	    }
	    String[] urls = posterUrls.split("\\|");
	    return urls.length > 0 ? urls[0].trim() : "데이터없음";
	}
}