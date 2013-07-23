package com.homie.endersgame.api.events.sign;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SignRegisterEvent extends Event {
	
	private int gameid;
	private Sign sign;
	private static final HandlerList handlers = new HandlerList();
	
	public SignRegisterEvent(int gameid, Sign sign) {
		this.gameid = gameid;
		this.sign = sign;
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
	
	public Sign getSign() {
		return sign;
	}
	
	public void call() {
		Bukkit.getPluginManager().callEvent(this);
	}
}
