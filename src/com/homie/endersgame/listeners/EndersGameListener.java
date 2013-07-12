package com.homie.endersgame.listeners;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.Game.GameStage;
import com.homie.endersgame.api.Game.GameTeam;
import com.homie.endersgame.api.GameManager;
import com.homie.endersgame.api.Lobby;
import com.homie.endersgame.api.events.EventHandle;
import com.homie.endersgame.api.events.game.CancelCreatingCommandEvent;
import com.homie.endersgame.api.events.game.PlayerAttemptLeaveEndersGameEvent;
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
				HashMap<String, GameTeam> playerlist = game.getPlayerList();
				
				int team1 = gm.getPlayersOnTeam(game.getGameId(), GameTeam.Team1).size();
				int team2 = gm.getPlayersOnTeam(game.getGameId(), GameTeam.Team2).size();
				
				if (team1 > team2) {
					playerlist.put(player.getName(), GameTeam.Team2);
				}
				else if (team1 < team2) {
					playerlist.put(player.getName(), GameTeam.Team1);
				} else {
					playerlist.put(player.getName(), GameTeam.Team1);
				}
				
				EndersGame.playing_players_gamemode.put(player.getName(), player.getGameMode());
				gm.updateGamePlayers(game.getGameId(), playerlist);
				player.sendMessage(ChatColor.GREEN + "You have joined Arena " + game.getGameId() + ". You are on team " + playerlist.get(player.getName()).toNiceString() + ", and you are in the lobby");
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
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeaveAttemptEndersGame(PlayerAttemptLeaveEndersGameEvent event) {
		Player player = event.getPlayer();
		try {
			for (Integer i : gm.getAllGamesFromDatabase()) {
				HashMap<String, GameTeam> players = gm.getGamePlayers(i);
				if (players.containsKey(player.getName())) {
					player.teleport(player.getLocation().getWorld().getSpawnLocation());
					player.getInventory().clear();
					player.setGameMode(EndersGame.playing_players_gamemode.get(player.getName()));
					player.getInventory().setContents(EndersGame.playing_players_inventory.get(player.getName()));
					player.updateInventory();
					EndersGame.playing_players_gamemode.remove(player.getName());
					EndersGame.playing_players_inventory.remove(player.getName());
					players.remove(player.getName());
					gm.updateGamePlayers(i, players);
					player.sendMessage(ChatColor.GREEN + "You have left arena " + i + " and your inventory has been reset.");
					EventHandle.callPlayerLeaveEndersGameEvent(i, player);
					event.setSuccess(true);
					return;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCancelCreatingCommandEvent(CancelCreatingCommandEvent event) {
		Player player = event.getPlayer();
		
		if (EndersGame.creating_game_players.contains(player.getName())) {
			EndersGame.creating_game_players.remove(player.getName());
			EndersGame.creating_game_ids.remove(player.getName());
			player.getInventory().remove(Material.WOOD_SPADE);
			player.sendMessage(ChatColor.GOLD + "You have canceled creating an arena");
			return;
		}
		else if (EndersGame.creating_lobby_players.contains(player.getName())) {
			EndersGame.creating_lobby_players.remove(player.getName());
			EndersGame.creating_lobby_ids.remove(player.getName());
			player.getInventory().remove(Material.WOOD_SPADE);
			player.sendMessage(ChatColor.GOLD + "You have canceled creating a lobby");
			return;
		}
		else if (EndersGame.creating_spawns_players.contains(player.getName())) {
			EndersGame.creating_spawns_players.remove(player.getName());
			EndersGame.creating_spawns_ids.remove(player.getName());
			player.sendMessage(ChatColor.GOLD + "You have canceled setting a games spawn points");
			return;
		} else {
			player.sendMessage(ChatColor.RED + "You aren't creating anything!");
			return;
		}
	}
}
