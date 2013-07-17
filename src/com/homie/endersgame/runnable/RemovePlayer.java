package com.homie.endersgame.runnable;

import java.sql.SQLException;
import java.util.ConcurrentModificationException;
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

	private EndersGame plugin;
	private GameManager gm;
	private String p;
	
	public RemovePlayer(EndersGame plugin, String p) {
		this.plugin = plugin;
		this.gm = plugin.getGameManager();
		this.p = p;
	}
	
	@Override
	public void run() {
		ImprovedOfflinePlayer player = new ImprovedOfflinePlayer(Bukkit.getServer().getOfflinePlayer(p));
		String name = player.getName();
		HashMap<String, Integer> toBeRemoved = new HashMap<String, Integer>();
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
						toBeRemoved.put(name, i);
						player.savePlayerData();
						EndersGame.debug("Offline player " + player.getName() + " has been ejected from game " + i);
					}
				}
			}
			for (Map.Entry<String, Integer> en : toBeRemoved.entrySet()) {
				EndersGame.playing_players_inventory.remove(en.getKey());
				EndersGame.playing_players_gamemode.remove(en.getKey());
				EndersGame.player_players_armor.remove(en.getKey());
				HashMap<String, GameTeam> t = gm.getGamePlayers(en.getValue());
				t.remove(en.getKey());
				gm.updateGamePlayers(en.getValue(), t);
			}
			toBeRemoved.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ConcurrentModificationException e) {
			if (plugin.debug()) {
				e.printStackTrace();
			}
		}
	}
}
