package com.homie.endersgame.api.events.game;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.homie.endersgame.api.Game;

public class GameDeleteEvent extends Event {

	private Game game;
	private static final HandlerList handlers = new HandlerList();
	
	public GameDeleteEvent(Game game) {
		this.game = game;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void call() {
		Bukkit.getPluginManager().callEvent(this);
	}
}
