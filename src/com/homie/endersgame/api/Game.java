package com.homie.endersgame.api;

import java.util.HashMap;

import org.bukkit.Location;

public class Game {

	private int gameid;
	private int lobbyid;
	private Location l1;
	private Location l2;
	private HashMap<String, Boolean> list;
	private GameStage gamestage;
	
	public Game(int gameid, int lobbyid, Location l1, Location l2, HashMap<String, Boolean> list, GameStage gamestage) {
		this.gameid = gameid;
		this.lobbyid = lobbyid;
		this.l1 = l1;
		this.l2 = l2;
		this.list = list;
		this.gamestage = gamestage;
	}
	
	public enum GameStage {
		Lobby, Ingame, Endgame;
		
		public static GameStage getFrom(String args) {
			switch(args.toLowerCase()) {
			case "lobby": return GameStage.Lobby;
			case "ingame": return GameStage.Ingame;
			case "endgame": return GameStage.Endgame;
			default: return GameStage.Lobby;
			}
		}
		
		@Override
		public String toString() {
			switch(this) {
			case Endgame: return "Endgame";
			case Ingame: return "Ingame";
			case Lobby: return "Lobby";
			default: return null;
			}
		}
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
	
	public HashMap<String, Boolean> getPlayerList() {
		return list;
	}
	
	public void setPlayerList(HashMap<String, Boolean> list) {
		this.list = list;
	}
	
	public GameStage getGameStage() {
		return gamestage;
	}
	
	public void setGameStage(GameStage gamestage) {
		this.gamestage = gamestage;
	}
}
