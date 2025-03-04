package com.care.project.main.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.care.project.main.dto.MovieDTO;
import com.care.project.main.dto.ReviewDTO;
import com.care.project.main.mapper.ReviewMapper;

@Service
public class ReviewServiceImpl implements ReviewService {
	@Autowired
	ReviewMapper rev;

	@JsonIgnoreProperties(ignoreUnknown = true)
	private abstract class IgnoreUnknownMixin {
	}

	public List<MovieDTO> getList(String id) {
		List<MovieDTO> list = null;
		try {
			list = rev.getList(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> searchInfo(int id) {
		List<Map<String, Object>> list = null;
		try {
			list = rev.searchInfo(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ObjectMapper 설정
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

		List<Map<String, Object>> formattedList = list.stream().map(map -> {
			try {
				// ✅ ReviewDTO 변환 (불필요한 필드 무시)
				ReviewDTO reviewDTO = objectMapper.addMixIn(ReviewDTO.class, IgnoreUnknownMixin.class) // 불필요한 필드 무시
						.convertValue(map, ReviewDTO.class);

				if (reviewDTO.getReviewDate() != null) {
					// ✅ Timestamp → yyyy-MM-dd String 변환
					String formattedDate = dateFormat.format(reviewDTO.getReviewDate());
					map.put("reviewDate", formattedDate); // 🔥 변경된 날짜 적용
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}).collect(Collectors.toList());

		return formattedList;
	}

	public List<Map<String, Object>> getInfo(String id, int start) {
		List<Map<String, Object>> list = null;
		start = (start - 1) * 5;
		try {
			list = rev.getInfo(id, start);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int getCount(String id) {
		int count = 0;
		try {
			count = rev.getCount(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int page = count / 5;
		if (count % 5 != 0) {
			page += 1;
		}

		return page;
	}

	public List<Map<String, Object>> getReserve(String id, int start) {
	    List<Map<String, Object>> list = null;
	    start = (start - 1) * 5;
	    try {
	        list = rev.getReserve(id, start);  // 예약 내역 가져오기
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    // ObjectMapper 설정
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");  // 최종 출력 형식

	    List<Map<String, Object>> formattedList = list.stream().map(map -> {
	        try {
	            // start_dt 변환
	            if (map.containsKey("startDateTime")) {
	                String startDateTime = (String) map.get("startDateTime");  // start_dt는 String 형식으로 제공됨
	                if (startDateTime != null && !startDateTime.isEmpty()) {
	                    // `startDateTime`이 "yyyy-MM-dd HH:mm:ss" 형식일 경우에만 처리
	                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // 기존 형식
	                    Date date = originalFormat.parse(startDateTime);  // String을 Date로 변환
	                    String formattedStartDate = dateFormat.format(date);  // Date를 "yyyy.MM.dd" 형식으로 변환
	                    map.put("startDateTime", formattedStartDate);  // 변환된 start_dt를 맵에 업데이트
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return map;
	    }).collect(Collectors.toList());

	    return formattedList;
	}

	public int getReserveCount(String id) {
		int count = 0;
		try {
			count = rev.getReserveCount(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int page = count / 5;
		if (count % 5 != 0) {
			page += 1;
		}

		return page;
	}

	public int reviewCheck(String id, int movieid) {
		int result = 0;
		try {
			result = rev.reviewCheck(id, movieid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int writeReview(ReviewDTO dto) {
		int result = 0;
		try {
			result = rev.writeReview(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int delReserve(int id) {
		int result = 0;
		try {
			result = rev.delReserve(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}