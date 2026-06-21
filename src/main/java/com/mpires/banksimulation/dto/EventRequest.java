package com.mpires.banksimulation.dto;

import com.mpires.banksimulation.enums.EventType;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {
    private EventType type;
    private String destination;
    private String origin;
    private int amount;
}
