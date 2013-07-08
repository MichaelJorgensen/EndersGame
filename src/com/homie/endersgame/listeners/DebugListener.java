package com.homie.endersgame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.events.game.PlayerJoinEndersGameEvent;
import com.homie.endersgame.api.events.game.PlayerLeaveEndersGameEvent;

public class DebugListener implements Listener {

	public DebugListener() {}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEndersGame(PlayerJoinEndersGameEvent event) {
		EndersGame.debug("Player '" + event.getPlayer().getName() + "' has joined Arena " + event.getGameId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeaveEndersGame(PlayerLeaveEndersGameEvent event) {
		EndersGame.debug("Player '" + event.getPlayer().getName() + "' has left Arena " + event.getGameId());
	}
}
