package com.homie.endersgame.api.events.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CancelCreatingCommandEvent extends Event {

	private Player player;
	private static final HandlerList handlers = new HandlerList();
	
	public CancelCreatingCommandEvent(Player player) {
		this.player = player;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	public Player getPlayer() {
		return player;
	}
	
	public void call() {
		Bukkit.getServer().getPluginManager().callEvent(this);
	}
}
