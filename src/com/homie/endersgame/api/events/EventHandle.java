package com.homie.endersgame.api.events;

import org.bukkit.entity.Player;

import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.events.game.CancelCreatingCommandEvent;
import com.homie.endersgame.api.events.game.PlayerAttemptLeaveEndersGameEvent;
import com.homie.endersgame.api.events.game.PlayerJoinEndersGameEvent;
import com.homie.endersgame.api.events.game.PlayerLeaveEndersGameEvent;

public class EventHandle {

	public static PlayerJoinEndersGameEvent callPlayerJoinEndersGameEvent(Game game, Player player) {
		PlayerJoinEndersGameEvent event = new PlayerJoinEndersGameEvent(game, player);
		event.call();
		return event;
	}
	
	public static PlayerAttemptLeaveEndersGameEvent callPlayerAttemptLeaveEndersGameEvent(Player player, boolean shouldMessage) {
		PlayerAttemptLeaveEndersGameEvent event = new PlayerAttemptLeaveEndersGameEvent(player, shouldMessage);
		event.call();
		return event;
	}
	
	public static PlayerLeaveEndersGameEvent callPlayerLeaveEndersGameEvent(int gameid, Player player) {
		PlayerLeaveEndersGameEvent event = new PlayerLeaveEndersGameEvent(gameid, player);
		event.call();
		return event;
	}
	
	public static CancelCreatingCommandEvent callCancelCreatingCommandEvent(Player player) {
		CancelCreatingCommandEvent event = new CancelCreatingCommandEvent(player);
		event.call();
		return event;
	}
}
