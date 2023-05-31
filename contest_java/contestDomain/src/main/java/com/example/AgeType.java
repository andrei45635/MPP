package com.example;

import com.example.enums.AgeGroup;
import com.example.enums.Type;

public class AgeType {
    private Type type;
    private AgeGroup ageGroup;

    public AgeType(Type type, AgeGroup ageGroup) {
        this.type = type;
        this.ageGroup = ageGroup;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }
}
