package com.example.org.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@EqualsAndHashCode
public class Address {
    private String zipCode;
    private String street;
    private String country;
}
