package com.homie.endersgame.runnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.inventory.PlayerInventory;

import com.github.one4me.ImprovedOfflinePlayer;
import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game.GameTeam;
import com.homie.endersgame.api.GameManager;

public class RemovePlayer implements Runnable {

	private GameManager gm;
	private String p;
	
	public RemovePlayer(EndersGame plugin, String p) {
		this.gm = plugin.getGameManager();
		this.p = p;
	}
	
	@Override
	public void run() {
		ImprovedOfflinePlayer player = new ImprovedOfflinePlayer(Bukkit.getServer().getOfflinePlayer(p));
		String name = player.getName();
		try {
			for (Integer i : gm.getAllGamesFromDatabase()) {
				HashMap<String, GameTeam> list = gm.getGamePlayers(i);
				for (Map.Entry<String, GameTeam> en : list.entrySet()) {
					if (en.getKey().equalsIgnoreCase(name)) {
						player.setLocation(player.getLocation().getWorld().getSpawnLocation());
						PlayerInventory in = player.getInventory();
						in.clear();
						try {
							in.setContents(EndersGame.playing_players_inventory.get(name));
							in.setArmorContents(EndersGame.player_players_armor.get(name));
							player.setGameMode(EndersGame.playing_players_gamemode.get(name));
						} catch (Exception e) {
							player.setGameMode(GameMode.SURVIVAL);
						}
						player.setInventory(in);
						EndersGame.playing_players_gamemode.remove(name);
						EndersGame.playing_players_inventory.remove(name);
						list.remove(name);
						gm.updateGamePlayers(i, list);
						player.savePlayerData();
						EndersGame.debug("Offline player " + player.getName() + " has been ejected from game " + i);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
