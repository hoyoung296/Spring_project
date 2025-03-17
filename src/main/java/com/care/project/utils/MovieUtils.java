package com.care.project.utils;

public class MovieUtils {
	public static String getValidSynopsis(String currentSynopsis, String newSynopsis) {
		if (currentSynopsis != null && !currentSynopsis.equals("데이터없음")) {
			return currentSynopsis;
		}
		return (newSynopsis == null || newSynopsis.trim().isEmpty()) ? "데이터없음" : trimSynopsis(newSynopsis, 300);
	}

	public static String getValidData(String currentData, String newData) {
		if (currentData != null && !currentData.equals("데이터없음")) {
			return currentData; // 기존에 직접 수정한 값 유지
		}
		return (newData == null || newData.trim().isEmpty()) ? "데이터없음" : newData;
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