package com.assignment.assignment_backend.entity;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shelf {

    private String shelfId;
    private String shelfName;
    private String partNumber;
    private boolean isDeleted;

}
