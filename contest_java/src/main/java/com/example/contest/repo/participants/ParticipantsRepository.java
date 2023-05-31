package com.example.contest.repo.participants;


import com.example.contest.domain.Participant;
import com.example.contest.domain.enums.AgeGroup;
import com.example.contest.domain.enums.Type;
import com.example.contest.repo.IRepository;

import java.util.List;

public interface ParticipantsRepository extends IRepository<Integer, Participant> {
    Participant findByID(int id);
    List<Participant> filterByAgeType(Type type, AgeGroup ageGroup);
}
