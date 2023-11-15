package com.example.cashcard;

import org.springframework.data.annotation.Id;
import org.springframework.hateoas.RepresentationModel;

public class CashCard {

    @Id Long id;
    Double amount;
    String owner;

    public CashCard(Long id, Double amount, String owner) {
        this.id = id;
        this.amount = amount;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public String getOwner() {
        return owner;
    }
}
