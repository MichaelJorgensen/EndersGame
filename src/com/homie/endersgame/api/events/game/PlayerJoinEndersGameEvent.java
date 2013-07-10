package com.homie.endersgame.api.events.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.homie.endersgame.api.Game;

public class PlayerJoinEndersGameEvent extends Event {

	private Game game;
	private Player player;
	private static final HandlerList handlers = new HandlerList();
	
	public PlayerJoinEndersGameEvent(Game game, Player player) {
		this.game = game;
		this.player = player;
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
	
	public Player getPlayer() {
		return player;
	}
	
	public void call() {
		Bukkit.getServer().getPluginManager().callEvent(this);
	}
}
