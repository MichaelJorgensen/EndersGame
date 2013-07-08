package com.homie.endersgame.api.events;

import org.bukkit.entity.Player;

import com.homie.endersgame.api.events.game.PlayerJoinEndersGameEvent;
import com.homie.endersgame.api.events.game.PlayerLeaveEndersGameEvent;

public class EventHandle {

	public static PlayerJoinEndersGameEvent callPlayerJoinEndersGameEvent(int gameid, Player player) {
		PlayerJoinEndersGameEvent event = new PlayerJoinEndersGameEvent(gameid, player);
		event.call();
		return event;
	}
	
	public static PlayerLeaveEndersGameEvent callPlayerLeaveEndersGameEvent(int gameid, Player player) {
		PlayerLeaveEndersGameEvent event = new PlayerLeaveEndersGameEvent(gameid, player);
		event.call();
		return event;
	}
}
