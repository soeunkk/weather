package com.soeunkk.weather.controller;

import com.soeunkk.weather.domain.Diary;
import com.soeunkk.weather.service.DiaryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiaryController {
	private final DiaryService diaryService;

	@GetMapping("/read/diary")
	public List<Diary> getDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
		// 일기 List 형태로 반환
		return diaryService.readDiary(date);
	}

	@GetMapping("/read/diaries")
	public List<Diary> getDiaries(
		@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
		@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDate) {
		// 해당 기간의 일기를 List 형태로 반환
		return diaryService.readDiaries(startDate, endDate);
	}

	@PostMapping("/create/diary")
	public void createDiary(
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
		@RequestBody String text) throws Exception {
		// 외부 API에서 받아온 날씨 데이터와 함께 DB에 저장
		diaryService.createDiary(date, text);
	}

	@PutMapping("/update/diary")
	public void updateDiary(
		@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date,
		@RequestBody String text) {
		// 해당 날짜의 첫 번째 일기 글을 새로 받아온 일기글로 수정
		diaryService.updateDiary(date, text);
	}

	@DeleteMapping("/delete/diary")
	public void deleteDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
		// 해당 날짜의 모든 일기 삭제
		diaryService.deleteDiary(date);
	}
}
