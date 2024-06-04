package com.example.Tambola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NumberService {
    Timer timer;

    ArrayList<Integer>nums;
    Integer index;

    NumberService(){
        index=0;
        nums=new ArrayList<>();
    }

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    PlayerRepository playerRepository;

    public void startGame(String roomId){
        Optional<Room> roomDetails=roomRepository.findById(roomId);
        List<List<Integer>> totalTickets=new ArrayList<>();
        if(roomDetails.isPresent()){
            Room room=roomDetails.get();
            Integer numOfTickets=0;
            for(Integer value:room.allotments.values()){
                numOfTickets+=value;
            }
            TambolaTicket obj=new TambolaTicket(numOfTickets);
            totalTickets=obj.generateAndPrintTickets();
            Integer count=0;
            for(String id:room.allotments.keySet()){
                Optional<Player>players=playerRepository.findById(id);
                if(players.isPresent()){
                    Player player=players.get();
                    Integer nums=room.allotments.get(id);
                    while(nums>0){
                        player.setTickets(totalTickets.get(count));
                        count++;
                        nums--;
                    }
                    playerRepository.save(player);
                }
            }

            if(!room.getGameStarted()){
                room.setGeneratedNumbers();
                room.setGameStarted(true);
                roomRepository.save(room);
                nums=room.getGeneratedNumbers();
                timer=new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        displayNextNumber(index,roomDetails);
                        index++;
                    }
                },15000,1000);
                System.out.println(room.getGeneratedNumbers());
            }
            else{
                throw new GameAlreadyStartedException("The game has already been started in this room");
            }
        }
        else{
            throw new RoomNotFoundException("Please Enter a valid room to start the game");
        }
    }
    public void displayNextNumber(Integer index,Optional<Room> roomDetails){

        if(nums != null && !nums.isEmpty() && index<nums.size()){
            System.out.println("Next Value -->"+nums.get(index));
        }
        else{
            System.out.println("All the numbers have been displayed...!!");
            Room room=roomDetails.get();
            for(String id:room.allotments.keySet()){
                Optional<Player>playerDetails=playerRepository.findById(id);
                Player player=playerDetails.get();
                player.setRoomId(null);
                player.setNumOfTicketsToInitial();
                player.setTicketsToInitial();
                playerRepository.save(player);
            }
            room.setPlayersToInitial();
            room.setAllotmentsToIntial();
            room.setGeneratedNumbersToInitial();
            room.setGameStarted(false);
            roomRepository.save(room);
            if(timer != null){
                timer.cancel();
            }
        }
    }
}
