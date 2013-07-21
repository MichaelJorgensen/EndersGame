package com.homie.endersgame.api.events.lobby;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.homie.endersgame.api.Lobby;

public class LobbyCreateEvent extends Event {
	
	private Lobby lobby;
	private static final HandlerList handlers = new HandlerList();
	
	public LobbyCreateEvent(Lobby lobby) {
		this.lobby = lobby;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public Lobby getLobby() {
		return lobby;
	}
	
	public void call() {
		Bukkit.getPluginManager().callEvent(this);
	}

}
