package com.soeunkk.weather.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

	Diary getFirstByDate(LocalDate date);

	List<Diary> findAllByDate(LocalDate date);
	List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

	@Transactional
	void deleteAllByDate(LocalDate date);
}
