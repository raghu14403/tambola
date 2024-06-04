package com.example.Tambola;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@Document(collection="taskInfo")
public class Room {

    @Id
    String id;
    String name;
    Boolean gameStarted=false;
    ArrayList<String>players;
    ArrayList<Integer> generatedNumbers = new ArrayList<>();
    HashMap<String,Integer>allotments;


    public Room() {
        this.players = new ArrayList<>();
        this.allotments=new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(Boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(String playerId) {
        this.players.add(playerId);
    }

    public void setPlayersToInitial(){
        players=new ArrayList<>();
    }

    public void setGeneratedNumbers() {
        for(int i=1;i<=90;i++){
            this.generatedNumbers.add(i);
        }
        Collections.shuffle(this.generatedNumbers);
    }

    public void setGeneratedNumbersToInitial(){
        this.generatedNumbers=new ArrayList<>();
    }
    public ArrayList<Integer> getGeneratedNumbers() {
        return generatedNumbers;
    }

    public HashMap<String, Integer> getAllotments() {
        return allotments;
    }

    public void setAllotments(String id, Integer tickets) {
        this.allotments.put(id,tickets);
    }

    public void setAllotmentsToIntial(){
        this.allotments=new HashMap<>();
    }

}
