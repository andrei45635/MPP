package com.example.repo.participants;


import com.example.Participant;
import com.example.enums.AgeGroup;
import com.example.enums.Type;
import com.example.repo.IRepository;

import java.util.List;

public interface ParticipantsRepository extends IRepository<Integer, Participant> {
    Participant findByID(int id);
    List<Participant> filterByAgeType(Type type, AgeGroup ageGroup);
}
