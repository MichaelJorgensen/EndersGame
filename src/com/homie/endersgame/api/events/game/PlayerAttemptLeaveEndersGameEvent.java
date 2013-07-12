package com.homie.endersgame.api.events.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAttemptLeaveEndersGameEvent extends Event {

	private Player player;
	private boolean success;
	private boolean shouldMessage;
	private static final HandlerList handlers = new HandlerList();
	
	public PlayerAttemptLeaveEndersGameEvent(Player player, boolean shouldMessage) {
		this.player = player;
		this.success = false;
		this.shouldMessage = shouldMessage;
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
	
	public boolean getSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public boolean shouldMessage() {
		return shouldMessage;
	}
	
	public void setShouldMessage(boolean shouldMessage) {
		this.shouldMessage = shouldMessage;
	}
	
	public void call() {
		Bukkit.getServer().getPluginManager().callEvent(this);
	}
}