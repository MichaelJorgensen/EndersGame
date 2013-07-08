package com.homie.endersgame.api;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Game {

	private int gameid;
	private int lobbyid;
	private Location l1;
	private Location l2;
	private HashMap<Player, Boolean> list;
	private GameStage gamestage;
	
	public Game(int gameid, int lobbyid, Location l1, Location l2, HashMap<Player, Boolean> list) {
		this.gameid = gameid;
		this.lobbyid = lobbyid;
		this.l1 = l1;
		this.l2 = l2;
		this.list = list;
		this.gamestage = GameStage.Lobby;
	}
	
	public enum GameStage {
		Lobby, Ingame, Endgame;
	}
	
	public int getGameId() {
		return gameid;
	}
	
	public int getLobbyId() {
		return lobbyid;
	}
	
	public void setLobbyId(int lobbyid) {
		this.lobbyid = lobbyid;
	}
	
	public Location getLocationOne() {
		return l1;
	}
	
	public void setLocationOne(Location l1) {
		this.l1 = l1;
	}
	
	public Location getLocationTwo() {
		return l2;
	}
	
	public void setLocationTwo(Location l2) {
		this.l2 = l2;
	}
	
	public HashMap<Player, Boolean> getPlayerList() {
		return list;
	}
	
	public void setPlayerList(HashMap<Player, Boolean> list) {
		this.list = list;
	}
	
	public GameStage getGameStage() {
		return gamestage;
	}
	
	public void setGameStage(GameStage gamestage) {
		this.gamestage = gamestage;
	}
}
