package com.soeunkk.weather.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {
	List<DateWeather> findAllByDate(LocalDate date);
}
