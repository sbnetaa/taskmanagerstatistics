package ru.terentyev.taskmanagerstatisticsmain.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.terentyev.taskmanagerstatisticsmain.entities.StatisticsResponse;
import ru.terentyev.taskmanagerstatisticsmain.services.StatisticsService;


@RestController
@RequestMapping(value = "/api/v1/statistics", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
, headers = "Accept=application/json")
public class StatisticsController extends AbstractController {
		
	private StatisticsService statisticsService;

	@Autowired
	public StatisticsController(StatisticsService statisticsService) {
		super();
		this.statisticsService = statisticsService;
	}
	
	@GetMapping
	public ResponseEntity<StatisticsResponse> showCommonStatistics() {
		StatisticsResponse response = new StatisticsResponse();
		response.setCommonStatistics(statisticsService.getCommonStatistics());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<StatisticsResponse> showPersonalStatistics(@PathVariable("id") Long id) {
		StatisticsResponse response = new StatisticsResponse();
		response.setAuthorStatistics(statisticsService.getPersonalStatistics(id, true));
		response.setExecutorStatistics(statisticsService.getPersonalStatistics(id, false));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
