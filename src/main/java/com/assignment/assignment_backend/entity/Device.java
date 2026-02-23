package com.assignment.assignment_backend.entity;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String deviceId;
    private String deviceName;
    private String partNumber;
    private String buildingName;
    private String deviceType;              
    private int numberOfShelfPositions;     
    private boolean isDeleted;

}
