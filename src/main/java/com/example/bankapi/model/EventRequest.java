package com.example.bankapi.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventRequest {
    private String type;
    private String origin;
    private String destination;
    private int amount;

}