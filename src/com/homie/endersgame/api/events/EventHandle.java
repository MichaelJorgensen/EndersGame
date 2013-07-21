package com.homie.endersgame.api.events;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.Lobby;
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

public class EventHandle {

	/**
	 * Called when a player joins a game. This is for debug purposes
	 * @param game that the player is joining
	 * @param player that is joining the game
	 * @return PlayerJoinEndersGameEvent
	 */
	public static PlayerJoinEndersGameEvent callPlayerJoinEndersGameEvent(Game game, Player player) {
		PlayerJoinEndersGameEvent event = new PlayerJoinEndersGameEvent(game, player);
		event.call();
		return event;
	}
	
	/**
	 * Called when a player leaves a game. This is for debug purposes
	 * @param gameid of the game the player is leaving
	 * @param player that is leaving the game
	 * @return PlayerLeaveEndersGameEvent
	 */
	public static PlayerLeaveEndersGameEvent callPlayerLeaveEndersGameEvent(int gameid, Player player) {
		PlayerLeaveEndersGameEvent event = new PlayerLeaveEndersGameEvent(gameid, player);
		event.call();
		return event;
	}
	
	/**
	 * Called when a game is created and added to the database. This is for debug purposes
	 * @param game that is being added to the database
	 * @return GameCreateEvent
	 */
	public static GameCreateEvent callGameCreateEvent(Game game) {
		GameCreateEvent event = new GameCreateEvent(game);
		event.call();
		return event;
	}
	
	/**
	 * Called when a game is deleted from the database. This is for debug purposes
	 * @param game that is being deleted
	 * @return GameDeleteEvent
	 */
	public static GameDeleteEvent callGameDeleteEvent(Game game) {
		GameDeleteEvent event = new GameDeleteEvent(game);
		event.call();
		return event;
	}
	
	/**
	 * Called when a game begins
	 * @param game that begun
	 * @return GameStartEvent
	 */
	public static GameStartEvent callGameStartEvent(Game game) {
		GameStartEvent event = new GameStartEvent(game);
		event.call();
		return event;
	}
	
	/**
	 * Called when a game ends
	 * @param game that ended
	 * @param reason for the game ending
	 * @return GameEndEvent
	 */
	public static GameEndEvent callGameEndEvent(Game game, GameEndReason reason) {
		GameEndEvent event = new GameEndEvent(game, reason);
		event.call();
		return event;
	}
	
	/**
	 * Called when a lobby is added to the database. This is for debug purposes
	 * @param lobby that is being added to the database
	 * @return LobbyCreateEvent
	 */
	public static LobbyCreateEvent callLobbyCreateEvent(Lobby lobby) {
		LobbyCreateEvent event = new LobbyCreateEvent(lobby);
		event.call();
		return event;
	}
	
	/**
	 * Called when a lobby is removed from the database. This is for debug purposes
	 * @param lobby that is being deleted from the database
	 * @return LobbyDeleteEvent
	 */
	public static LobbyDeleteEvent callLobbyDeleteEvent(Lobby lobby) {
		LobbyDeleteEvent event = new LobbyDeleteEvent(lobby);
		event.call();
		return event;
	}
	
	/**
	 * Called when a sign is added to the database. This is for debug purposes
	 * @param sign that is added to the database
	 * @return SignRegisterEvent
	 */
	public static SignRegisterEvent callSignRegisterEvent(Sign sign) {
		SignRegisterEvent event = new SignRegisterEvent(sign);
		event.call();
		return event;
	}
	
	/**
	 * Called when a sign is removed from the database. This is for debug purposes
	 * @param gameid of the sign that was removed from the database
	 * @return SignUnregisterEvent
	 */
	public static SignUnregisterEvent callSignUnregisterEvent(int gameid) {
		SignUnregisterEvent event = new SignUnregisterEvent(gameid);
		event.call();
		return event;
	}
}
