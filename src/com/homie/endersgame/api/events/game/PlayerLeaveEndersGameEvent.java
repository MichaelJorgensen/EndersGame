package com.homie.endersgame.api.events.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveEndersGameEvent extends Event {

	private int gameid;
	private Player player;
	private static final HandlerList handlers = new HandlerList();
	
	public PlayerLeaveEndersGameEvent(int gameid, Player player) {
		this.gameid = gameid;
		this.player = player;
	}
	
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public int getGameId() {
		return gameid;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void call() {
		Bukkit.getServer().getPluginManager().callEvent(this);
	}
}