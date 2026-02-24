package com.assignment.assignment_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.assignment_backend.entity.Shelf;
import com.assignment.assignment_backend.requests.CreateShelfRequest;
import com.assignment.assignment_backend.requests.UpdateShelfRequest;
import com.assignment.assignment_backend.service.ShelfService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shelves")
@Validated
public class ShelfController {
	private final ShelfService shelfService;
	
	public ShelfController(ShelfService shelfService)
	{
		this.shelfService=shelfService;
	}
	
	@GetMapping
	public ResponseEntity<List<Shelf>> getAll() {
	    return new ResponseEntity<>(shelfService.getAllShelves(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Shelf> create(@Valid @RequestBody CreateShelfRequest req)
	{
		Shelf shelf=shelfService.createShelf(req);
		return new ResponseEntity<>(shelf,HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{shelfId}")
	public ResponseEntity<Shelf> get(@PathVariable String shelfId)
	{
		return new ResponseEntity<>(shelfService.getShelf(shelfId),HttpStatus.OK);
	}
	
	@PutMapping("/{shelfId}")
	public ResponseEntity<Shelf> update(@PathVariable String shelfId, @Valid @RequestBody UpdateShelfRequest req)
	{
		return new ResponseEntity<>(shelfService.updateShelf(shelfId, req),HttpStatus.OK);
	}
	

    @DeleteMapping("/{shelfId}")
    public ResponseEntity<?> softDelete(@PathVariable String shelfId) {
        shelfService.softDeleteShelf(shelfId);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    

    @PostMapping("/attach")
    public ResponseEntity<?> attach(@RequestParam String shelfPositionId, @RequestParam String shelfId) {
        shelfService.attachShelfToPosition(shelfPositionId, shelfId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    

    @DeleteMapping("/detach")
    public ResponseEntity<?> detach(@RequestParam String shelfPositionId) {
         shelfService.detachShelfFromPosition(shelfPositionId);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


	
}
