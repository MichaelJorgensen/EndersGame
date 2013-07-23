package com.homie.endersgame.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.events.game.GameCreateEvent;
import com.homie.endersgame.api.events.game.GameDeleteEvent;
import com.homie.endersgame.api.events.game.GameEndEvent;
import com.homie.endersgame.api.events.game.GameEndEvent.GameEndReason;
import com.homie.endersgame.api.events.game.GameStartEvent;
import com.homie.endersgame.api.events.game.PlayerJoinEndersGameEvent;
import com.homie.endersgame.api.events.game.PlayerLeaveEndersGameEvent;
import com.homie.endersgame.api.events.lobby.LobbyCreateEvent;
import com.homie.endersgame.api.events.lobby.LobbyDeleteEvent;
import com.homie.endersgame.api.events.sign.SignRegisterEvent;
import com.homie.endersgame.api.events.sign.SignUnregisterEvent;

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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameStart(GameStartEvent event) {
		EndersGame.debug("Game " + event.getGame().getGameId() + " has begun");
		Bukkit.broadcastMessage(ChatColor.GOLD + "[EndersGame] " + ChatColor.GREEN + "Arena " + event.getGame().getGameId() + " has begun");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameEnd(GameEndEvent event) {
		event.getGame().resetDoors();
		EndersGame.debug("Game " + event.getGame().getGameId() + " has ended, reason: " + event.getReasonForGameEnding().toString().replaceAll("%arena%", String.valueOf(event.getGame().getGameId())));
		if (event.getReasonForGameEnding() == GameEndReason.NoPlayersLeft) return;
		Bukkit.broadcastMessage(ChatColor.GOLD + "[EndersGame] " + ChatColor.GREEN + event.getReasonForGameEnding().toString().replaceAll("%arena%", String.valueOf(event.getGame().getGameId())));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignRegister(SignRegisterEvent event) {
		EndersGame.debug("A sign has been registered for game " + event.getGameId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignUnregister(SignUnregisterEvent event) {
		EndersGame.debug("A sign has been unregistered for game " + event.getGameIdOfSign());
	}
}
