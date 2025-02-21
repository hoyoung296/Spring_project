package com.care.project.utils;

public class MovieUtils {
	public static String getValidSynopsis(String synopsis) {
		return (synopsis == null || synopsis.trim().isEmpty()) ? "데이터없음" : trimSynopsis(synopsis, 300);
	}
	
	private static String trimSynopsis(String synopsis, int maxLength) {
		return synopsis.length() > maxLength ? synopsis.substring(0, maxLength) + "..." : synopsis;
	}

	public static String cleanTitle(String title) {
	    return (title == null) ? "" : title.replaceAll("[!HS!HE\\\\\\\\s]", "").trim();
	}
	
	public static String getFirstPosterUrl(String posterUrls) {
	    if (posterUrls == null || posterUrls.trim().isEmpty()) {
	        return "데이터없음";
	    }
	    String[] urls = posterUrls.split("\\|");
	    return urls.length > 0 ? urls[0].trim() : "데이터없음";
	}
	
	public static String openDt(String openDt) {
		
		return openDt;
	}
}