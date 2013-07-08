package com.homie.endersgame.listeners;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.homie.endersgame.EndersGame;

public class GameListener implements Listener {

	private EndersGame plugin;
	
	public static HashMap<String, Location> creating_game_locations = new HashMap<String, Location>();
	public static HashMap<String, Location> creating_lobby_locations = new HashMap<String, Location>();
	
	public GameListener(EndersGame plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] line = event.getLines();
		int i;
		if (line[0].toLowerCase().contains("[endersgame]")) {
			try {
				i = Integer.parseInt(line[1]);
			} catch (NumberFormatException e) {
				return;
			}
			if (player.hasPermission("EndersGame.createsign")) {
				try {
					if (plugin.getGameManager().getGame(i) != null) {
						event.setLine(0, ChatColor.DARK_RED + "Ender's Game");
						event.setLine(1, "Arena " + i);
						event.setLine(2, "0/" + plugin.getEnderConfig().getMaxPlayers());
						event.setLine(3, "Do Not Edit");
						try {
							plugin.getGameManager().registerSign(event.getBlock(), i);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						player.sendMessage(ChatColor.RED + "That arena doesn't exist");
						event.getBlock().breakNaturally();
						return;
					}
				} catch (SQLException e) {
					if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("ResultSet closed")) {
						player.sendMessage(ChatColor.RED + "That arena doesn't exist");
						event.getBlock().breakNaturally();
						return;
					}
					e.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if (b.getState() instanceof Sign) {
			Sign s = (Sign) b.getState();
			if (s.getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "Ender's Game")) {
				try {
					if (plugin.getGameManager().getSign(Integer.parseInt(s.getLine(1).split("Arena ")[1])) != null && !event.isCancelled()) {
						plugin.getGameManager().unregisterSign(Integer.parseInt(s.getLine(1).split("Arena ")[1]));
					}
				} catch (NumberFormatException | IndexOutOfBoundsException | SQLException e) {
					if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("ResultSet closed")) return;
					e.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		if (EndersGame.creating_game_players.contains(name) || EndersGame.creating_lobby_players.contains(name)) {
			if (event.getItem().getType() == Material.WOOD_SPADE) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block b = event.getClickedBlock();
					event.setCancelled(true);
					if (creating_game_locations.containsKey(name)) {
						Location l1 = creating_game_locations.get(name);
						Location l2 = b.getLocation();
						creating_game_locations.remove(name);
						try {
							plugin.getGameManager().registerGame(EndersGame.creating_game_ids.get(name).get(0), EndersGame.creating_game_ids.get(name).get(1), 
									(int)l1.getX(), (int)l1.getY(), (int)l1.getZ(), (int)l2.getX(), (int)l2.getY(), (int)l2.getZ(), l1.getWorld());
						} catch (SQLException e) {
							player.sendMessage(ChatColor.RED + "There has been an error with the database: " + e.getMessage());
							EndersGame.sendErr("SQLException while trying to register a new game, error: " + e.getErrorCode() + ", message: " + e.getMessage());
							e.printStackTrace();
							return;
						}
						EndersGame.creating_game_players.remove(name);
						EndersGame.creating_game_ids.remove(name);
						player.sendMessage(ChatColor.GREEN + "Point 2 specified, arena created. If you have not already, create a sign to correspond to this arena.");
						return;
					}
					else if (!creating_game_locations.containsKey(name) && !creating_lobby_locations.containsKey(name) && EndersGame.creating_game_players.contains(name)) {
						creating_game_locations.put(name, b.getLocation());
						player.sendMessage(ChatColor.GREEN + "Position 1 specified, select position two (opposite corner)");
						return;
					}
					else if (creating_lobby_locations.containsKey(name)) {
						Location l1 = creating_lobby_locations.get(name);
						Location l2 = b.getLocation();
						creating_lobby_locations.remove(name);
						try {
							plugin.getGameManager().registerLobby(EndersGame.creating_lobby_ids.get(name), 
									(int)l1.getX(), (int)l1.getY(), (int)l1.getZ(), (int)l2.getX(), (int)l2.getY(), (int)l2.getZ(), l1.getWorld());
						} catch (SQLException e) {
							player.sendMessage(ChatColor.RED + "There has been an error with the database: " + e.getMessage());
							EndersGame.sendErr("SQLException while trying to register a new lobby, error: " + e.getErrorCode() + ", message: " + e.getMessage());
							e.printStackTrace();
							return;
						}
						EndersGame.creating_lobby_players.remove(name);
						EndersGame.creating_lobby_ids.remove(name);
						player.sendMessage(ChatColor.GREEN + "Point 2 specified, lobby created. You can now point arenas to this lobby");
						return;
					}
					else if (!creating_game_locations.containsKey(name) && !creating_lobby_locations.containsKey(name) && EndersGame.creating_lobby_players.contains(name)) {
						creating_lobby_locations.put(name, b.getLocation());
						player.sendMessage(ChatColor.GREEN + "Position 1 specified, select position two (opposite corner)");
						return;
					}
				}
			}
		}
		
		if (!event.hasBlock()) return;
		Block b = event.getClickedBlock();
		
		if (b.getState() instanceof Sign) {
			// TODO: check sign, add player to game
		}
	}
}
