package com.assignment.assignment_backend.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.assignment.assignment_backend.service.ShelfPositionService;
import com.assignment.assignment_backend.view.PositionView;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


@RestController
@RequestMapping("/api/positions")
@Validated
public class ShelfPositionController {
	
	private final ShelfPositionService shelfPositionService;

	public ShelfPositionController(ShelfPositionService shelfPositionService) {
		this.shelfPositionService = shelfPositionService;
	}

	public static class CreateShelfPositionRequest {
	    @NotBlank
	    public String deviceId;

	    @Min(1)
	    public int index;
	}

//	    @PostMapping
//	    public ResponseEntity<PositionView> create(@Valid @RequestBody CreateShelfPositionRequest req) {
//	        PositionView created = shelfPositionService.createShelfPosition(req.deviceId, req.index);
//	        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//	    }



	 @GetMapping("/{shelfPositionId}")
	 public ResponseEntity<PositionView> get(@PathVariable String shelfPositionId) {
	      return ResponseEntity.ok(shelfPositionService.getShelfPosition(shelfPositionId));
	 }


	 @GetMapping("/by-device/{deviceId}")
	 public ResponseEntity<List<PositionView>> listByDevice(@PathVariable String deviceId) {
	    return ResponseEntity.ok(shelfPositionService.listPositionsByDevice(deviceId));
	  }



	@DeleteMapping("/{shelfPositionId}")
	public ResponseEntity<Void> softDelete(@PathVariable String shelfPositionId) {
        shelfPositionService.softDeleteShelfPosition(shelfPositionId);
        return ResponseEntity.noContent().build();
    }

}
