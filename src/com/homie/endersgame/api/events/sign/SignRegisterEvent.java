package com.homie.endersgame.api.events.sign;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SignRegisterEvent extends Event {
	
	private Sign sign;
	private static final HandlerList handlers = new HandlerList();
	
	public SignRegisterEvent(Sign sign) {
		this.sign = sign;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public Sign getSign() {
		return sign;
	}
	
	public void call() {
		Bukkit.getPluginManager().callEvent(this);
	}
}
