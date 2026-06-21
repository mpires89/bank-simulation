package com.mpires.banksimulation.dto;

import com.mpires.banksimulation.enums.EventType;
import lombok.Data;

@Data
public class EventRequest {
    private EventType type;
    private String destination;
    private String origin;
    private int amount;
}
