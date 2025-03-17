package com.care.project.main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.care.project.main.mapper.ReserveMapper;

@Service
public class ReserveServiceImpl implements ReserveService {
	@Autowired
	private ReserveMapper reserveMapper;

	// 예매 내역 추가 메세드
	@Override
	@Transactional
	public Long createReservation(String userId, Integer scheduleid, Integer totalAmount) {
		// 유니크한 예약 ID 생성
		Long reservationId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
		int reservationStatusId = 1; // 기본 예약 상태 (1: 대기)

		// DB에 예약 정보 저장
		reserveMapper.insertReservation(reservationId, userId, scheduleid, reservationStatusId, totalAmount);
		return reservationId; // 생성된 예약 ID 반환
	}

	// 좌석상태id 구하는 메서드
	@Override
	public List<Integer> seatStatus(Integer scheduleid, List<String> seatIds) {
		List<Integer> seatStatusIds = new ArrayList<>();
		System.out.println("✅ 조회 대상 좌석 ID: " + seatIds + "스케줄 아이디 " + scheduleid);

		for (String seatId : seatIds) {
			Integer statusId = reserveMapper.getSeatStatusId(scheduleid, seatId);
			System.out.println(
					"🔍 조회 결과 - Schedule ID: " + scheduleid + ", Seat ID: " + seatId + ", SeatStatusId: " + statusId);

			if (statusId != null) {
				seatStatusIds.add(statusId);
			} else {
				System.out.println("⚠️ 조회 실패 - DB에 해당 좌석 상태가 없습니다: " + seatId);
			}
		}

		System.out.println("🎯 최종 좌석 상태 ID 목록: " + seatStatusIds);
		return seatStatusIds;
	}

	// 예매좌석 db 추가
	@Override
	@Transactional
	public boolean reserveSeats(Long reservationId, List<Integer> seatStatusIds) {
		int successCount = 0; // ✅ 성공한 행 수 카운트

		for (Integer seatStatusId : seatStatusIds) {
			int result = reserveMapper.insertReservationSeat(reservationId, seatStatusId);
			if (result > 0) {
				successCount++; // ✅ 성공하면 증가
			} else {
				return false; // ✅ 하나라도 실패하면 전체 실패 반환
			}
		}

		return successCount == seatStatusIds.size(); // ✅ 모든 좌석이 성공하면 true 반환
	}

	// 해당하는 좌석상태id의 좌석상태아이디를 예매 중 으로 변경
	@Override
	@Transactional
	public boolean updateSeatStatusType(List<Integer> seatStatusIds) {
		int successCount = 0; // ✅ 성공한 행 수 카운트

		for (Integer seatStatusId : seatStatusIds) {
			int result = reserveMapper.updateSeatStatusType(seatStatusId, 3);
			if (result > 0) {
				successCount++; // ✅ 성공하면 증가
			} else {
				return false; // ✅ 하나라도 실패하면 전체 실패 반환
			}
		}

		return successCount == seatStatusIds.size(); // ✅
	}

	// 해당하는 좌석상태id의 좌석상태아이디를 예매 완료로 변경
	@Override
	@Transactional
	public boolean updateSeatStatusType2(List<Integer> seatStatusIds) {
		int successCount = 0; // ✅ 성공한 행 수 카운트

		for (Integer seatStatusId : seatStatusIds) {
			int result = reserveMapper.updateSeatStatusType(seatStatusId, 2);
			if (result > 0) {
				successCount++; // ✅ 성공하면 증가
			} else {
				return false; // ✅ 하나라도 실패하면 전체 실패 반환
			}
		}

		return successCount == seatStatusIds.size(); // ✅
	}

	@Override
	public boolean cancelReservation(Long reservationId, Integer scheduleid, List<Integer> seatStatusIds) {
		try {
			// 1. 예약 좌석 삭제
			if (reserveMapper.deleteReservationSeats(reservationId) <= 0) {
				System.out.println("@@@reservationId : " + reservationId);
				System.out.println("@@@service부분 : " + reserveMapper.deleteReservationSeats(reservationId));
				System.err.println("❌ 예매 좌석 삭제 실패.");
				return false;
			}

			// 2. 좌석 상태 예매 가능으로 변경
			for (Integer seatStatusId : seatStatusIds) {
				if (reserveMapper.updateSeatStatusType(seatStatusId, 1) <= 0) {
					System.err.println("❌ 좌석 상태 변경 실패. SeatStatusId: " + seatStatusId);
					return false;
				}
			}

			// 3. 예약 상태 변경
			if (reserveMapper.updateReservation(reservationId, 3) <= 0) {
				System.err.println("❌ 예매 상태 변경 실패 또는 이미 처리됨.");
				return false;
			}

			return true; // 모든 작업 성공 시 true 반환
		} catch (Exception e) {
			System.err.println("❌ 예매 취소 실패: " + e.getMessage());
			return false;
		}
	}

	// 스케줄 아이디 구하기
	@Override
	public int getSchedulId(Long reservationId) {

		return reserveMapper.getSchedulId(reservationId);
	}

	// 예매된 좌석아이디 구하기
	@Override
	public List<Integer> reserveSeatStatus(Long reservationId) {
		System.out.println("예매된 좌석 아이디 : " + reserveMapper.getReserveSeatStatusId(reservationId));

		return reserveMapper.getReserveSeatStatusId(reservationId);
	}

	// 만료된 예약 조회: Reservation 테이블에서 created_at이 지정된 시각 이전이고, 예약 상태가 대기(1)인 예약 ID 목록
	// 반환
	@Override
	public List<Long> findExpiredReservations(int reservationStatusId) {
//	    	System.out.println("createdBefore : " + createdBefore);
//	    	System.out.println("reservationStatusId : " + reservationStatusId);

		return reserveMapper.findExpiredReservations(reservationStatusId);
	}

	// ========================
	// 예약 자동 취소 스케줄러 메서드: 1분마다 실행하여 10분 이상 미결제(대기 상태)인 예약 자동 취소
	// ========================
	@Override
	@Scheduled(fixedRate = 60000) // 1분마다 실행
	public void cancelExpiredReservations() {
		System.out.println("예약 자동 취소 실행");
//	        long tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000);
		// 예약 상태 1: 대기 상태
		List<Long> expiredReservationIds = findExpiredReservations(1);
		System.out.println("expiredReservationIds : " + expiredReservationIds);
		if (expiredReservationIds != null && !expiredReservationIds.isEmpty()) {
			for (Long reservationId : expiredReservationIds) {
				try {
					int scheduleId = getSchedulId(reservationId);
					List<Integer> seatStatusIds = reserveSeatStatus(reservationId);
					boolean canceled = cancelReservation(reservationId, scheduleId, seatStatusIds);
					if (canceled) {
						System.out.println("예약 자동 취소 성공: " + reservationId);
					} else {
						System.err.println("예약 자동 취소 실패: " + reservationId);
					}
				} catch (Exception e) {
					System.err.println("예약 자동 취소 처리 중 오류 발생: " + e.getMessage());
				}
			}
		}
	}
}