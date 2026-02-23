package com.assignment.assignment_backend.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class CreateDeviceRequest {

    @NotBlank 
    private String deviceName;
    @NotBlank 
    private String partNumber;
    @NotBlank 
    private String buildingName;
    @NotBlank 
    private String deviceType;
    @Min(1)   
    private int numberOfShelfPositions;

}
