package com.homie.endersgame.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class Game {

	private int gameid;
	private int lobbyid;
	private Location l1;
	private Location l2;
	private HashMap<String, GameTeam> list;
	private GameStage gamestage;
	
	public Game(int gameid, int lobbyid, Location l1, Location l2, HashMap<String, GameTeam> list, GameStage gamestage) {
		this.gameid = gameid;
		this.lobbyid = lobbyid;
		this.l1 = l1;
		this.l2 = l2;
		this.list = list;
		this.gamestage = gamestage;
	}
	
	public enum GameStage {
		Lobby, PreGame, Ingame;
		
		public static GameStage getFrom(String args) {
			switch(args.toLowerCase()) {
			case "lobby": return GameStage.Lobby;
			case "pregame": return GameStage.PreGame;
			case "ingame": return GameStage.Ingame;
			default: return GameStage.Lobby;
			}
		}
		
		@Override
		public String toString() {
			switch(this) {
			case PreGame: return "PreGame";
			case Ingame: return "Ingame";
			case Lobby: return "Lobby";
			default: return null;
			}
		}
	}
	
	public enum GameTeam {
		Team1, Team1Leader, Team2, Team2Leader, TeamUnknown;
		
		public static GameTeam getFrom(String args) {
			switch(args.toLowerCase()) {
			case "team1": return GameTeam.Team1;
			case "team1leader": return GameTeam.Team1Leader;
			case "team2": return GameTeam.Team2;
			case "team2leader": return GameTeam.Team2Leader;
			default: return GameTeam.TeamUnknown;
			}
		}
		
		@Override
		public String toString() {
			switch(this) {
			case Team1: return "Team1";
			case Team1Leader: return "Team1Leader";
			case Team2: return "Team2";
			case Team2Leader: return "Team2Leader";
			default: return "Unknown";
			}
		}
		
		public String toNiceString() {
			switch(this) {
			case Team1: return "Team 1";
			case Team1Leader: return "Team 1 Leader";
			case Team2: return "Team 2";
			case Team2Leader: return "Team 2 Leader";
			default: return "Unknown";
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
	
	public HashMap<String, GameTeam> getPlayerList() {
		return list;
	}
	
	public ArrayList<String> getArrayListofPlayers() {
		ArrayList<String> players = new ArrayList<String>();
		for (Map.Entry<String, GameTeam> en : list.entrySet()) {
			players.add(en.getKey());
		}
		return players;
	}
	
	public void setPlayerList(HashMap<String, GameTeam> list) {
		this.list = list;
	}
	
	public GameStage getGameStage() {
		return gamestage;
	}
	
	public void setGameStage(GameStage gamestage) {
		this.gamestage = gamestage;
	}
}
