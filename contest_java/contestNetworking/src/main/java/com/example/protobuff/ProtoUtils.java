package com.example.protobuff;

import com.example.Participant;
import com.example.ParticipantTask;
import com.example.User;
import com.example.Task;
import com.example.dto.TaskDTO;
import com.example.dto.ParticipantDTO;
import com.example.enums.Type;
import com.example.enums.AgeGroup;

import java.util.ArrayList;
import java.util.List;

public class ProtoUtils {
    public static ContestProto.ContestRequest createLoginRequest(User user) {
        ContestProto.User loggedInUser = ContestProto.User.newBuilder().setId(user.getId()).setUsername(user.getUsername()).setPassword(user.getPassword()).build();
        return ContestProto.ContestRequest.newBuilder().setReq(ContestProto.ContestRequest.Req.Login).setUser(loggedInUser).build();
    }

    public static ContestProto.ContestRequest createLogoutRequest(User user) {
        ContestProto.User loggedOutUser = ContestProto.User.newBuilder().setId(user.getId()).setUsername(user.getUsername()).setPassword(user.getPassword()).build();
        return ContestProto.ContestRequest.newBuilder().setReq(ContestProto.ContestRequest.Req.Logout).setUser(loggedOutUser).build();
    }

    public static ContestProto.ContestRequest createGetTasksEnrolledRequest() {
        return ContestProto.ContestRequest.newBuilder().setReq(ContestProto.ContestRequest.Req.GET_ALL_TASKS_EXPERIMENTAL).build();
    }

    public static ContestProto.ContestRequest createGetFilteredParticipantsRequest(Type type, AgeGroup ageGroup) {
        String[] filters = new String[2];
        filters[0] = String.valueOf(type);
        filters[1] = String.valueOf(ageGroup);
        return ContestProto.ContestRequest.newBuilder().setReq(ContestProto.ContestRequest.Req.FILTER).setType(filters[0]).setAge(filters[1]).build();
    }

    public static ContestProto.ContestRequest createRegisterRequest(String name, int age, Type task1, Type task2) {
        String type1 = String.valueOf(task1);
        String type2 = String.valueOf(task2);
        ContestProto.ParticipantTask participantTask = ContestProto.ParticipantTask.newBuilder().setName(name).setAge(age).setType1(type1).setType2(type2).build();
        return ContestProto.ContestRequest.newBuilder().setReq(ContestProto.ContestRequest.Req.ADD_PARTICIPANT_EXPERIMENTAL).setPt(participantTask).build();
    }

    public static ContestProto.ContestResponse createOKResponse() {
        return ContestProto.ContestResponse.newBuilder().setRep(ContestProto.ContestResponse.Reply.OK).build();
    }

    public static ContestProto.ContestResponse createErrorResponse(String text) {
        return ContestProto.ContestResponse.newBuilder().setRep(ContestProto.ContestResponse.Reply.Error).setError(text).build();
    }

    public static ContestProto.ContestResponse createGetTasksEnrolledResponse(List<ContestProto.TaskDTO> tasks) {
        ContestProto.ContestResponse.Builder response = ContestProto.ContestResponse.newBuilder().setRep(ContestProto.ContestResponse.Reply.GET_ALL_TASKS_EXPERIMENTAL);
        for (var task : tasks) {
            ContestProto.TaskDTO taskDTO = ContestProto.TaskDTO.newBuilder().setTaskID(task.getTaskID()).setType(task.getType()).setAge(task.getAge()).setEnrolled(task.getEnrolled()).build();
            response.addTasks(taskDTO);
        }
        return response.build();
    }

    public static ContestProto.ContestResponse createGetFilteredParticipantsResponse(List<ContestProto.ParticipantDTO> participantDTOS) {
        ContestProto.ContestResponse.Builder response = ContestProto.ContestResponse.newBuilder().setRep(ContestProto.ContestResponse.Reply.FILTER);
        for (var pd : participantDTOS) {
            ContestProto.ParticipantDTO pdto = ContestProto.ParticipantDTO.newBuilder().setName(pd.getName()).setAge(pd.getAge()).build();
            response.addParticipants(pdto);
        }
        return response.build();
    }

    public static ContestProto.ContestResponse createRegisterResponse() {
        return ContestProto.ContestResponse.newBuilder().setRep(ContestProto.ContestResponse.Reply.ADD_PARTICIPANT_EXPERIMENTAL).build();
    }

    public static ContestProto.ContestResponse createUpdateResponse(){
        return ContestProto.ContestResponse.newBuilder().setRep(ContestProto.ContestResponse.Reply.UPDATE).build();
    }

    public static String getError(ContestProto.ContestResponse response) {
        return response.getError();
    }

    public static User getUser(ContestProto.ContestRequest request){
        return new User(request.getUser().getUsername(), request.getUser().getPassword());
    }

    public static ParticipantTask getPT(ContestProto.ContestRequest request){
        return new ParticipantTask(request.getPt().getName(), request.getPt().getAge(), Type.valueOf(request.getPt().getType1()), Type.valueOf(request.getPt().getType2()));
    }

    public static String[] getFilters(ContestProto.ContestRequest request){
        String[] filters = new String[2];
        filters[0] = request.getType();
        filters[1] = request.getAge();
        return filters;
    }

    public static List<ContestProto.TaskDTO> getEnrolledTasks(ContestProto.ContestResponse response) {
        List<ContestProto.TaskDTO> tasks = new ArrayList<>();
        for(int i = 0; i < response.getTasksCount(); i++){
            tasks.add(response.getTasks(i));
        }
        return tasks;
    }

    public static List<ContestProto.TaskDTO> getEnrolledTasksForResponse(List<TaskDTO> tasks){
        List<ContestProto.TaskDTO> res = new ArrayList<>();
        for(var tk: tasks){
            ContestProto.TaskDTO td = ContestProto.TaskDTO.newBuilder().setTaskID(tk.getTaskID()).setType(ContestProto.Type.valueOf(tk.getType().toString())).setAge(ContestProto.AgeGroup.valueOf(tk.getAgeGroup().toString())).setEnrolled(tk.getEnrolled()).build();
            res.add(td);
        }
        return res;
    }

    public static List<ContestProto.Participant> getFilteredPartiticipants(List<Participant> parts){
        List<ContestProto.Participant> res = new ArrayList<>();
        for(var p: parts){
            ContestProto.Participant pt = ContestProto.Participant.newBuilder().setId(p.getId()).setName(p.getName()).setAge(p.getAge()).build();
            res.add(pt);
        }
        return res;
    }

    public static List<ParticipantDTO> part2dto(List<Participant> pts){
        List<ParticipantDTO> res = new ArrayList<>();
        for(var p: pts){
            res.add(new ParticipantDTO(p.getName(), p.getAge()));
        }
        return res;
    }

    public static List<ContestProto.ParticipantDTO> getFilteredPartsDTOMapper(List<ContestProto.Participant> pts) {
        List<ContestProto.ParticipantDTO> parts = new ArrayList<>();
        for(var p: pts){
            parts.add(ContestProto.ParticipantDTO.newBuilder().setName(p.getName()).setAge(p.getAge()).build());
        }
        return parts;
    }

    public static List<ContestProto.ParticipantDTO> getFilteredPartsDTO(ContestProto.ContestResponse response) {
        List<ContestProto.ParticipantDTO> parts = new ArrayList<>();
        for(int i = 0; i < response.getParticipantsCount(); i++){
            parts.add(response.getParticipants(i));
        }
        return parts;
    }
}
