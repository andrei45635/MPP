package com.example.contest.domain;

public interface Entity<ID> {
    void setId(ID id);
    ID getId();
}