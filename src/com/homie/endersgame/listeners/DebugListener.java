package com.homie.endersgame.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.events.game.GameCreateEvent;
import com.homie.endersgame.api.events.game.GameDeleteEvent;
import com.homie.endersgame.api.events.game.PlayerJoinEndersGameEvent;
import com.homie.endersgame.api.events.game.PlayerLeaveEndersGameEvent;
import com.homie.endersgame.api.events.lobby.LobbyCreateEvent;
import com.homie.endersgame.api.events.lobby.LobbyDeleteEvent;

public class DebugListener implements Listener {

	public DebugListener() {}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEndersGame(PlayerJoinEndersGameEvent event) {
		EndersGame.debug("Player '" + event.getPlayer().getName() + "' has joined Arena " + event.getGame().getGameId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeaveEndersGame(PlayerLeaveEndersGameEvent event) {
		EndersGame.debug("Player '" + event.getPlayer().getName() + "' has left Arena " + event.getGameId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameCreate(GameCreateEvent event) {
		EndersGame.debug("A new game has been created and added to the database, ID: " + event.getGame().getGameId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameDelete(GameDeleteEvent event) {
		EndersGame.debug("A game has been delete from the database, ID: " + event.getGame().getGameId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLobbyCreate(LobbyCreateEvent event) {
		EndersGame.debug("A lobby has been created and added to the database, ID: " + event.getLobby().getLobbyId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLobbyDelte(LobbyDeleteEvent event) {
		EndersGame.debug("A lobby has been deleted from the database, ID: " + event.getLobby().getLobbyId());
	}
}
