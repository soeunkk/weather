package com.soeunkk.weather.service;

import com.soeunkk.weather.WeatherApplication;
import com.soeunkk.weather.domain.DateWeather;
import com.soeunkk.weather.domain.DateWeatherRepository;
import com.soeunkk.weather.domain.Diary;
import com.soeunkk.weather.domain.DiaryRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiaryService {
	private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

	private final DiaryRepository diaryRepository;
	private final DateWeatherRepository dateWeatherRepository;
	@Value("${openweathermap.key}")
	private String openWeatherMapApiKey;

	@Transactional
	@Scheduled(cron = "0 0 1 * * *")
	public void saveWeatherDate() throws Exception {
		logger.info("started to load weather api");
		dateWeatherRepository.save(getWeatherFromApi());
		logger.info("end to load weather api");
	}

	private DateWeather getWeatherFromApi() throws Exception {
		String result = getWeatherString();
		Map<String, Object> parsedWeather = parseWeather(result);
		DateWeather dateWeather = new DateWeather();
		dateWeather.setDate(LocalDate.now());
		dateWeather.setWeather(parsedWeather.get("main").toString());
		dateWeather.setIcon(parsedWeather.get("icon").toString());
		dateWeather.setTemperature((Double) parsedWeather.get("temp"));
		return dateWeather;
	}

	private String getWeatherString() throws Exception {
		String apiUrl =
			"https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + openWeatherMapApiKey;

		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();

		BufferedReader br;
		if (responseCode == 200) {
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		}

		String inputLine;
		StringBuilder response = new StringBuilder();
		while ((inputLine = br.readLine()) != null) {
			response.append(inputLine);
		}
		br.close();

		return response.toString();
	}

	private Map<String, Object> parseWeather(String jsonString) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);

		Map<String, Object> resultMap = new HashMap<>();

		JSONObject mainData = (JSONObject) jsonObject.get("main");
		resultMap.put("temp", mainData.get("temp"));

		JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
		JSONObject weatherData = (JSONObject) weatherArray.get(0);
		resultMap.put("main", weatherData.get("main"));
		resultMap.put("icon", weatherData.get("icon"));

		return resultMap;
	}

	@Transactional(readOnly = true)
	public List<Diary> readDiary(LocalDate date) {
		logger.debug("read diary");
		return diaryRepository.findAllByDate(date);
	}

	@Transactional(readOnly = true)
	public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
		return diaryRepository.findAllByDateBetween(startDate, endDate);
	}

	@Transactional
	public void createDiary(LocalDate date, String text) throws Exception {
		logger.info("started to create diary");
		DateWeather dateWeather = getDateWeather(date);
		Diary nowDiary = Diary.builder()
			.text(text)
			.date(date)
			.build();
		nowDiary.setDateWeather(dateWeather);
		diaryRepository.save(nowDiary);
		logger.info("end to create diary");
	}

	private DateWeather getDateWeather(LocalDate date) throws Exception {
		List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
		if (dateWeatherListFromDB.size() == 0) {
			return getWeatherFromApi();
		} else {
			return dateWeatherListFromDB.get(0);
		}

	}

	@Transactional
	public void updateDiary(LocalDate date, String text) {
		Diary nowDiary = diaryRepository.getFirstByDate(date);
		nowDiary.updateText(text);
		diaryRepository.save(nowDiary);
	}

	@Transactional
	public void deleteDiary(LocalDate date) {
		diaryRepository.deleteAllByDate(date);
	}
}
