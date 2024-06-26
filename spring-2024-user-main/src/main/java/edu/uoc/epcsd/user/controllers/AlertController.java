package edu.uoc.epcsd.user.controllers;

import edu.uoc.epcsd.user.controllers.dtos.CreateAlertRequest;
import edu.uoc.epcsd.user.controllers.dtos.InputGetAlertsByProductAndDate;
import edu.uoc.epcsd.user.controllers.dtos.InputGetAlertsByUserAndInterval;
import edu.uoc.epcsd.user.entities.Alert;
import edu.uoc.epcsd.user.services.AlertService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/alerts")
public class AlertController {

	@Autowired
	private AlertService alertService;

	@GetMapping("/")
	@ResponseStatus(HttpStatus.OK)
	public List<Alert> getAllAlerts() {
		log.trace("getAllAlerts");

		return alertService.findAll();
	}

	@GetMapping("/{alertId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Alert> getAlertById(@PathVariable @NotNull Long alertId) {
		log.trace("getAlertById");

		return alertService.findById(alertId).map(alert -> ResponseEntity.ok().body(alert))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Long> createAlert(@RequestBody CreateAlertRequest createAlertRequest) {
		log.trace("createAlert");

		try {
			log.trace("Creating alert " + createAlertRequest);
			Long alertId = alertService.createAlert(createAlertRequest.getProductId(), createAlertRequest.getUserId(),
					createAlertRequest.getFrom(), createAlertRequest.getTo()).getId();
			URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(alertId).toUri();

			return ResponseEntity.created(uri).body(alertId);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}

	// TODO: add the code for the missing system operations here:
	// 1. query alerts by product and date
	@GetMapping("/product/date")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<Alert>> getAlertsByProductAndDate(@RequestParam @NotNull Long productId,
			@RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		log.trace("getAlertsByProductAndDate");

		List<Alert> alertList = alertService.findAlertsByProductAndDate(productId, date);

		if (alertList != null && !alertList.isEmpty()) {
			return ResponseEntity.ok().body(alertList);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 2. query alerts by user and date interval (all the alerts for the specified
	// user where any day in the interval defined in the parameters is between
	// Alert.from and Alert.to)
	@GetMapping("/users/date")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<Alert>> getAlertsByUserAndInterval(@RequestParam @NotNull Long userId,
			@RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

		log.trace("getAlertsByUserAndInterval");
		List<Alert> alertsList = alertService.findAlertsByUserAndInterval(userId, from, to);

		if (alertsList != null && !alertsList.isEmpty()) {
			return ResponseEntity.ok().body(alertsList);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

}
