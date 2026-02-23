package com.assignment.assignment_backend.view;

import com.assignment.assignment_backend.entity.Shelf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShelfView {
	private Shelf shelf;
	private String deviceId;
	private String shelfPositionId;
	private Integer positionIndex;
}
