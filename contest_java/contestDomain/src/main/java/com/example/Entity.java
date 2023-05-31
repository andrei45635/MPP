package com.example;

public interface Entity<ID> {
    void setId(ID id);
    ID getId();
}