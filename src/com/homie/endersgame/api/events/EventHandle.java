package com.homie.endersgame.api.events;

import org.bukkit.entity.Player;

import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.events.game.PlayerJoinEndersGameEvent;
import com.homie.endersgame.api.events.game.PlayerLeaveEndersGameEvent;

public class EventHandle {

	public static PlayerJoinEndersGameEvent callPlayerJoinEndersGameEvent(Game game, Player player) {
		PlayerJoinEndersGameEvent event = new PlayerJoinEndersGameEvent(game, player);
		event.call();
		return event;
	}
	
	public static PlayerLeaveEndersGameEvent callPlayerLeaveEndersGameEvent(int gameid, Player player) {
		PlayerLeaveEndersGameEvent event = new PlayerLeaveEndersGameEvent(gameid, player);
		event.call();
		return event;
	}
}
