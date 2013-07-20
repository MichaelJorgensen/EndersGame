package com.homie.endersgame.api.events.sign;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SignUnregisterEvent extends Event {

	private int gameid;
	private static final HandlerList handlers = new HandlerList();
	
	public SignUnregisterEvent(int gameid) {
		this.gameid = gameid;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public int getGameIdOfSign() {
		return gameid;
	}
	
	public void call() {
		Bukkit.getPluginManager().callEvent(this);
	}
}
