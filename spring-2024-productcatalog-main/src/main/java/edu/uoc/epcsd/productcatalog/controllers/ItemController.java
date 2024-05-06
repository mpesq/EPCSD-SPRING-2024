package edu.uoc.epcsd.productcatalog.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateItemRequest;
import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.services.ItemService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/items")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@GetMapping("/")
	@ResponseStatus(HttpStatus.OK)
	public List<Item> getAllItems() {
		log.trace("getAllItems");

		return itemService.findAll();
	}

	@GetMapping("/{serialNumber}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Item> getItemById(@PathVariable @NotNull String serialNumber) {
		log.trace("getItemById");

		return itemService.findBySerialNumber(serialNumber).map(item -> ResponseEntity.ok().body(item))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<String> createItem(@RequestBody CreateItemRequest createItemRequest) {
		log.trace("createItem");

		log.trace("Creating item " + createItemRequest);
		String serialNumber = itemService
				.createItem(createItemRequest.getProductId(), createItemRequest.getSerialNumber()).getSerialNumber();
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{serialNumber}").buildAndExpand(serialNumber)
				.toUri();

		return ResponseEntity.created(uri).body(serialNumber);
	}

	// TODO: add the code for the missing system operations here:
	// 1. setOperational
	// * use the correct HTTP verb
	// * must ensure the item exists
	// * if the new status is OPERATIONAL, must send a UNIT_AVAILABLE message to the
	// kafka message queue (see ItemService.createItem method)

	@PatchMapping("/{serialNumber}/status")
	public ResponseEntity<Void> setOperational(@PathVariable @NotNull String serialNumber,
			@RequestBody Boolean operational) {
		
		itemService.setOperational(serialNumber, operational);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

	}

}
