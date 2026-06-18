package com.mpires.banksimulation.dto;

import lombok.Data;

@Data
public class EventRequest {
    private String type;
    private String destination;
    private String origin;
    private int amount;
}
