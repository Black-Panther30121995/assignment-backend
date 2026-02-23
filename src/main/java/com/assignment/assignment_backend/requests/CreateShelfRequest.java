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
public class CreateShelfRequest {
		@NotBlank 
		private String shelfName;
	    @NotBlank 
	    private String partNumber;
	
}
