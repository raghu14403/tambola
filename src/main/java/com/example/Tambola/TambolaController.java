package com.example.Tambola;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class TambolaController {
    @Autowired
    public RoomRepository roomRepository;

    @Autowired
    public PlayerRepository playerRepository;

    @Autowired
    public NumberService numberService;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/check")
    public String hello(){
        return "Hello";
    }

    IdCreator idObj=new IdCreator();
    @PostMapping("/room")
    public Room createRoom(@RequestBody Room room){
            String roomName=room.getName();
            Room newRoom=new Room();
            newRoom.setName(roomName);
            String uuidRoom = idObj.idGenerator();
            System.out.println(uuidRoom);
            newRoom.setId(uuidRoom);
            newRoom.setGameStarted(false);
            roomRepository.save(newRoom);
            return newRoom;
    }

    @PostMapping("/player")
    public Player registerPLayer(@RequestBody(required = true) Player player){
        String uuidPlayer = idObj.idGenerator();
        Player newPlayer=new Player();
        String name=player.getName();
        newPlayer.setIdentity(uuidPlayer);
        newPlayer.setName(name);
        playerRepository.save(newPlayer);
        System.out.println(newPlayer);
        return newPlayer;
    }

    @PostMapping("players/{identity}/room/{id}")
    public Room joinRoom(@PathVariable String id ,@PathVariable String identity, @RequestBody Integer ticket){
        System.out.println(identity);
        Player newPlayer;
        Optional <Player> player=playerRepository.findById(identity);
        if(player.isPresent()){
            newPlayer=player.get();
            if(newPlayer.roomId != null){
                throw new AlreadyInRoom("You are already in room ->"+ newPlayer.roomId+" Hence, cannot join ->"+id);
            }
            System.out.println(newPlayer);
        }
        else{
            throw new PlayerNotFoundException("Enter into the room as a valid player.");
        }
        Optional<Room> room=roomRepository.findById(id);
        if(room.isPresent()){
            Room roomDetails=room.get();
            if (roomDetails.isGameStarted()) {
                throw new GameAlreadyStartedException("Cannot join the room as the game has already started.");
            }
            ArrayList<String>playersInRoom=roomDetails.getPlayers();
            for(int i=0;i<playersInRoom.size();i++){
                Optional<Player>players=playerRepository.findById(playersInRoom.get(i));
                if(!players.isPresent()){
                    playersInRoom.remove(playersInRoom.get(i));
                }
            }
            if(!playersInRoom.contains(newPlayer.identity)){
                roomDetails.setPlayers(newPlayer.identity);
                roomDetails.setAllotments(identity,ticket);
                newPlayer.setRoomId(roomDetails.id);
                newPlayer.setNumOfTickets(ticket);
                playerRepository.save(newPlayer);
                roomRepository.save(roomDetails);
            }
            else{
                throw new AlreadyInRoom("You are already present in the room");
            }
            return room.get();
        }
        else{
            throw new RoomNotFoundException("Room not found with id: " + id);
        }
    }

    @GetMapping("/total-players")
    public List<Player> getAlLPlayers(){
        return playerRepository.findAll();
    }

    @GetMapping("/room/{roomId}/players")
    public ObjectNode getRoomDetails(@PathVariable("roomId") String id){
        ArrayList<Optional<String>> players=new ArrayList<>();
        Optional<Room> room=roomRepository.findById(id);
        ObjectNode response = new ObjectMapper().createObjectNode();
        if(room.isPresent()){
            Room roomDetails=room.get();
            List<String>playerId=roomDetails.players;
            for(int i=0;i<playerId.size();i++){
                Optional <Player> player=playerRepository.findById(playerId.get(i));
                if(player.isPresent()){
                    players.add(Optional.of(player.get().identity));
                }
            }
            response.putPOJO("players",players);
            response.put("roomName",roomDetails.getName());
            ArrayList<String>details=new ArrayList<>();
            details.add(roomDetails.getName());
            for(int i=0;i<roomDetails.players.size();i++){
                details.add(roomDetails.players.get(i));
            }
            return response;
        }
        else{
            throw new RoomNotFoundException("Room not found with id: " + id);
        }
    }

    @GetMapping("/player/{playerIdentity}")
    public Optional<Player> getPlayerDetails(@PathVariable String playerIdentity){
        Optional<Player> playerDetails=playerRepository.findById(playerIdentity);
        if(playerDetails.isPresent()){
            Player player=new Player();
            player=playerDetails.get();
            return Optional.of(player);
        }
        else{
            throw new PlayerNotFoundException("No Details found for the Player");
        }

    }

    @GetMapping("/rooms/{roomId}/player/{playerId}")
    public Object getParticularPlayerDetail(@PathVariable String roomId,@PathVariable String playerId){
        Optional<Room>roomDetails=roomRepository.findById(roomId);
        if(roomDetails.isPresent()){
            Room room=roomDetails.get();
            if(room.players.contains(playerId)) {
                if (room.getGameStarted()) {
                    Optional<Player>playerDetails=playerRepository.findById(playerId);
                    Player player=playerDetails.get();
                    ObjectNode response=new ObjectMapper().createObjectNode();
                    response.put("Room Name",room.getName());
                    response.put("Player Name",player.getName());
                    response.put("ticket", String.valueOf(player.tickets));
                    return response;
                } else {
                    throw new OtherExceptions("Tickets are not yet generated yet");
                }
            }
            else{
                throw new PlayerNotFoundException("Not in this room");
            }
        }
        else{
            throw new RoomNotFoundException("Please enter a valid room Id");
        }
    }

    @PostMapping("/game/{roomId}")
    public String startGame(@PathVariable String roomId) {
        numberService.startGame(roomId);
        return "Game Started in "+roomId;
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<String> deleteGame(@PathVariable String roomId){
        Optional<Room>roomDetails=roomRepository.findById(roomId);
        if(roomDetails.isPresent()){
            Room room=new Room();
            room=roomDetails.get();
            if(room.gameStarted){
//                throw new GameAlreadyStartedException("Game has already been started in the room ->"+room.name);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Game has already been started in the room ->"+room.name);
            }
            else{
                ArrayList<String>players=room.getPlayers();
                for(String id:players){
                    Optional<Player> playerDetails =playerRepository.findById(id);
                    Player player=playerDetails.get();
                    player.roomId=null;
                    player.setTicketsToInitial();
                    player.setNumOfTicketsToInitial();
                    playerRepository.save(player);
                }
                roomRepository.deleteById(roomId);
            }
        }
        else{
            throw new RoomNotFoundException("Please enter a valid room id to delete");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("The room with Id -> "+roomId+" will be deleted");
    }

    @DeleteMapping("/player/{playerId}")
    public String deletePlayer(@PathVariable String playerId){
        Optional<Player>playerDetails=playerRepository.findById(playerId);
        if(playerDetails.isPresent()){
            Player player=playerDetails.get();
            Optional<Room>roomDetails=roomRepository.findById(player.roomId);
            if(roomDetails.isPresent()){
                Room room=roomDetails.get();
                room.players.remove(player.identity);
                roomRepository.save(room);
            }
            playerRepository.deleteById(playerId);
        }
        else{
            throw new PlayerNotFoundException("Enter a valid player id to delete");
        }
        return "The player with id -> "+playerId+" will be deleted";
    }
}



@ResponseStatus(value = HttpStatus.NOT_FOUND)
class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class GameAlreadyStartedException extends RuntimeException {
    public GameAlreadyStartedException(String message) {
        super(message);
    }
}

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class AlreadyInRoom extends RuntimeException {
    public AlreadyInRoom(String message) {
        super(message);
    }
}

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
class OtherExceptions extends RuntimeException{
    public OtherExceptions(String message){
        super(message);
    }
}