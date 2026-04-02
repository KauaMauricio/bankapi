package com.example.bankapi.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventRequest {
    String type;
    String origin;
    String destination;
    int amount;

}