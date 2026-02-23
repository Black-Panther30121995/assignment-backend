package com.assignment.assignment_backend.entity;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelfPostion {

    private String shelfPositionId;
    private int index;                      
    private String deviceId;                
    private boolean isDeleted;

}
