package com.care.project.utils;

public class MovieUtils {
    public static String getValidSynopsis(String synopsis) {
        return (synopsis == null || synopsis.trim().isEmpty()) ? "데이터없음" : trimSynopsis(synopsis, 300);
    }

    public static String getValidActors(String actors) {
        return (actors == null || actors.trim().isEmpty()) ? "데이터없음" : trimSynopsis(cleanActors(actors), 300);
    }

    public static String getValidDirector(String director) {
        return (director == null || director.trim().isEmpty()) ? "데이터없음" : director;
    }

    public static String cleanActors(String actors) {
        if (actors == null)
            return "데이터없음";
        return actors.replaceAll("[!HS!HE]", "").replaceAll("[^a-zA-Z0-9가-힣, ]", "").trim();
    }

    public static String trimSynopsis(String text, int maxLength) {
        if (text != null && text.length() > maxLength) {
            String trimmed = text.substring(0, maxLength);
            int lastPeriodIndex = trimmed.lastIndexOf(".");
            return (lastPeriodIndex != -1) ? trimmed.substring(0, lastPeriodIndex + 1) : trimmed;
        }
        return (text != null) ? text : "데이터없음";
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
}