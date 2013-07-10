package com.homie.endersgame.listeners;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.Game.GameStage;
import com.homie.endersgame.api.GameManager;
import com.homie.endersgame.api.Lobby;
import com.homie.endersgame.api.events.game.PlayerJoinEndersGameEvent;

public class EndersGameListener implements Listener {

	private GameManager gm;
	
	public EndersGameListener(EndersGame plugin) {
		this.gm = plugin.getGameManager();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinEndersGame(PlayerJoinEndersGameEvent event) {
		Player player = event.getPlayer();
		Game game = event.getGame();
		try {
			Lobby l = gm.getLobby(game.getLobbyId());
			if (!EndersGame.playing_players_inventory.containsKey(player.getName())) {
				if (game.getGameStage() != GameStage.Lobby) {
					player.sendMessage(ChatColor.RED + "Game is already in session");
					return;
				}
				EndersGame.playing_players_inventory.put(player.getName(), player.getInventory().getContents());
				player.teleport(gm.getLobbySpawn(l.getLobbyId()));
				player.getInventory().clear();
				player.updateInventory();
				HashMap<String, Boolean> playerlist = game.getPlayerList();
				playerlist.put(player.getName(), false);
				gm.updateGamePlayers(game.getGameId(), playerlist);
				player.sendMessage(ChatColor.GREEN + "You have joined Arena " + game.getGameId() + ". You are in the lobby.");
				return;
			} else {
				player.sendMessage(ChatColor.RED + "You are already playing");
				return;
			}
		} catch (SQLException e) {
			player.sendMessage(ChatColor.RED + "There has been an error with the database: " + e.getMessage());
			EndersGame.sendErr("SQLException while trying to get a lobby and game from the database, error: " + e.getErrorCode() + ", message: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
