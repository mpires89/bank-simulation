package com.mpires.banksimulation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mpires.banksimulation.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse {
    private Account origin;
    private Account destination;
}
