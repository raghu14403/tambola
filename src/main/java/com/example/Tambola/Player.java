package com.example.Tambola;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection="playerInfo")
public class Player {
    @Id
    String identity;
    String roomId;
    String name;
    Integer numOfTickets;
    Integer wins;
    ArrayList<ArrayList<Integer>>tickets;

    public Player() {
        this.tickets=new ArrayList<>();
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String id) {
        identity= id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumOfTickets() {
        return numOfTickets;
    }

    public void setNumOfTickets(Integer tickets) {
        this.numOfTickets = tickets;
    }

    public void setNumOfTicketsToInitial(){
        this.numOfTickets=null;
    }

    public Integer getWins() {
        return wins;
    }

    public ArrayList<ArrayList<Integer>> getTickets() {
        return tickets;
    }

    public void setTickets(List<Integer> tickets) {
        this.tickets.add((ArrayList<Integer>) tickets);
    }

    public void setTicketsToInitial(){
        this.tickets=new ArrayList<>();
    }
}
