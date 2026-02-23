package com.assignment.assignment_backend.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class AttachShelfRequest {
	@NotBlank 
	private String shelfPositionId;
	@NotBlank 
	private String shelfId;

}
