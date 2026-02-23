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
import org.springframework.web.bind.annotation.RestController;

import com.assignment.assignment_backend.entity.Device;
import com.assignment.assignment_backend.requests.CreateDeviceRequest;
import com.assignment.assignment_backend.requests.UpdateDeviceRequest;
import com.assignment.assignment_backend.service.DeviceService;
import com.assignment.assignment_backend.view.DeviceView;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/devices")
@Validated
public class DeviceController {
	public final DeviceService deviceService;
	
	public DeviceController(DeviceService deviceService)
	{
		this.deviceService=	deviceService;
	}
	
	@PostMapping
	public ResponseEntity<Device> create(@Valid @RequestBody CreateDeviceRequest req)
	{
		Device device=deviceService.createDevice(req);
		return new ResponseEntity<>(device,HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<List<Device>> getAll()
	{
		return new ResponseEntity<>(deviceService.getAllDevices(),HttpStatus.OK);
	}
	
	@GetMapping("/{deviceId}/view")
	public ResponseEntity<DeviceView> getView(@PathVariable String deviceId)
	{
		return new ResponseEntity<>(deviceService.getDeviceView(deviceId),HttpStatus.OK);
	}
	
	@PutMapping("/{deviceId}")
	public ResponseEntity<Device> update(@PathVariable String deviceId, @Valid @RequestBody UpdateDeviceRequest req)
	{
		return new ResponseEntity<>(deviceService.updateDevice(deviceId, req),HttpStatus.OK);
	}
	
	@DeleteMapping("/{deviceId}")
	public ResponseEntity<?> softDelete(@PathVariable String deviceId)
	{
		deviceService.softDeleteDevice(deviceId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
