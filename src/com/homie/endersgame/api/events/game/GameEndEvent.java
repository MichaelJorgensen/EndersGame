package com.homie.endersgame.api.events.game;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.homie.endersgame.api.Game;

public class GameEndEvent extends Event {

	private Game game;
	private GameEndReason reason;
	private static final HandlerList handlers = new HandlerList();
	
	public GameEndEvent(Game game, GameEndReason reason) {
		this.game = game;
		this.reason = reason;
	}
	
	public enum GameEndReason {
		Team1Victory, Team2Victory, TimeLimitReached, NoPlayersLeft;
		
		@Override
		public String toString() {
			switch(this) {
			case Team1Victory: return "Team 1 has won on Arena %arena%";
			case Team2Victory: return "Team 2 has won on Arena %arena%";
			case TimeLimitReached: return "Time limit has been reached on Arena %arena%";
			case NoPlayersLeft: return "Arena %arena% has ended as there aren't any players left on one or more teams";
			default: return "No one won on Arena %arena%";
			}
		}
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
	
	public GameEndReason getReasonForGameEnding() {
		return reason;
	}
	
	public void call() {
		Bukkit.getPluginManager().callEvent(this);
	}
}
