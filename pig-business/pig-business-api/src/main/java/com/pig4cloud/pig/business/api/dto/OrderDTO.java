package com.pig4cloud.pig.business.api.dto;

import java.io.Serializable;

public class OrderDTO implements Serializable {
    private Long id;
    private String name;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

