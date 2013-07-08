package com.homie.endersgame.api;

import org.bukkit.Location;

public class Lobby {

	private int lobbyid;
	private Location l1;
	private Location l2;
	
	public Lobby(int lobbyid, Location l1, Location l2) {
		this.lobbyid = lobbyid;
		this.l1 = l1;
		this.l2 = l2;
	}
	
	public int getLobbyId() {
		return lobbyid;
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
}
