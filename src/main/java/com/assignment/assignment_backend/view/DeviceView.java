package com.assignment.assignment_backend.view;

import java.util.List;

import com.assignment.assignment_backend.entity.Device;
import com.assignment.assignment_backend.entity.Shelf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceView {
	private Device device;
	private List<PositionView> positions;
}
