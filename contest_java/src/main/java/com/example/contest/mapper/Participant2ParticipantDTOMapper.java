package com.example.contest.mapper;

import com.example.contest.domain.Participant;
import com.example.contest.dto.ParticipantDTO;

import java.util.ArrayList;
import java.util.List;

public class Participant2ParticipantDTOMapper {
    public ParticipantDTO convert(Participant participant){
        return new ParticipantDTO(participant.getName(), participant.getAge());
    }

    public List<ParticipantDTO> convert(List<Participant> participants){
        List<ParticipantDTO> participantDTOS = new ArrayList<>();
        for(Participant participant: participants){
            participantDTOS.add(convert(participant));
        }
        return participantDTOS;
    }
}
