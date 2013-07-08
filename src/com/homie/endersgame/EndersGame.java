package com.homie.endersgame;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.homie.endersgame.api.GameManager;
import com.homie.endersgame.listeners.DebugListener;
import com.homie.endersgame.listeners.GameListener;
import com.homie.endersgame.runnable.SignRun;
import com.homie.endersgame.sql.SQL;
import com.homie.endersgame.sql.options.DatabaseOptions;
import com.homie.endersgame.sql.options.MySQLOptions;
import com.homie.endersgame.sql.options.SQLiteOptions;

public class EndersGame extends JavaPlugin {

	private SQL sql;
	private DatabaseOptions dop;
	private GameManager gm;
	private Config config;
	private static boolean debug;
	
	public static ArrayList<String> creating_game_players = new ArrayList<String>();
	public static HashMap<String, ArrayList<Integer>> creating_game_ids = new HashMap<String, ArrayList<Integer>>();
	
	public static ArrayList<String> creating_lobby_players = new ArrayList<String>();
	public static HashMap<String, Integer> creating_lobby_ids = new HashMap<String, Integer>();
	
	public void onEnable() {
		config = new Config(this);
		debug = config.getDebug();
		
		// Setup MySQL/SQlite
		if (!setupSQL()) {
			sendErr("SQL could not be setup");
			getPluginLoader().disablePlugin(this);
			return;
		}
		// With SQL setup, we can initiate the game manager
		gm = new GameManager(this);
		
		// Register events and runnables
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		if (debug) getServer().getPluginManager().registerEvents(new DebugListener(), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new SignRun(this), 75L, 8000L);
	}
	
	public void onDisable() {
		try {
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql = null;
		dop = null;
		gm = null;
		config = null;
	}
	
	private boolean setupSQL() {
		if (config.getSQLValue().equalsIgnoreCase("MySQL")) {
			dop = new MySQLOptions(config.getHostname(), config.getPort(), config.getDatabase(), config.getUsername(), config.getPassword());
		}
		
		else if (config.getSQLValue().equalsIgnoreCase("SQLite")) {
			dop = new SQLiteOptions(new File(getDataFolder() + "/game_data.db"));
		}
		
		else {
			sendErr("Enders Game cannot enable because the SQL is set to " + config.getSQLValue());
			return false;
		}
		
		sql = new SQL(this, dop);
		try {
			if (!sql.open()) return false;
			sql.createTable("CREATE TABLE IF NOT EXISTS signs (gameid INT(10), coordX INT(15), coordY INT(15), coordZ INT(15), world VARCHAR(255))");
			sql.createTable("CREATE TABLE IF NOT EXISTS games (gameid INT(10), lobbyid INT(10), x1 INT(15), y1 INT(15), z1 INT(15), x2 INT(15), y2 INT(15), z2 INT(15), world VARCHAR(255))");
			sql.createTable("CREATE TABLE IF NOT EXISTS lobbies (lobbyid INT(10), x1 INT(15), y1 INT(15), z1 INT(15), x2 INT(15), y2 INT(15), z2 INT(15), world VARCHAR(255))");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public SQL getSQL() {
		return sql;
	}
	
	public GameManager getGameManager() {
		return gm;
	}
	
	public Config getEnderConfig() {
		return config;
	}
	
	public static void send(String message) {
		System.out.println("[EndersGame] " + message);
	}
	
	public static void sendErr(String error) {
		System.out.println("[EndersGame] [Error] " + error);
	}
	
	public static void debug(String message) {
		if (debug) System.out.println("[EndersGame] [Debug] " + message);
	}
	
	public void help(CommandSender s) {
		s.sendMessage(ChatColor.RED + "Ender's Game Commands You Can Use");
		if (s.hasPermission("EndersGame.join")) s.sendMessage(ChatColor.GOLD + "/eg leave");
		if (s.hasPermission("EndersGame.list")) s.sendMessage(ChatColor.GOLD + "/eg list");
		if (s.hasPermission("EndersGame.create")) s.sendMessage(ChatColor.GOLD + "/eg create arena [id] [lobbyID]");
		if (s.hasPermission("EndersGame.create")) s.sendMessage(ChatColor.GOLD + "/eg create lobby [id]");
		if (s.hasPermission("EndersGame.create")) s.sendMessage(ChatColor.GOLD + "/eg cancel");
		if (s.hasPermission("EndersGame.delete")) s.sendMessage(ChatColor.GOLD + "/eg delete [arena|lobby] [id]");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (args.length == 0) {
			help(sender);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("create")) {
			if (sender.hasPermission("EndersGame.create")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (creating_game_players.contains(player.getName()) || creating_lobby_players.contains(player.getName())) {
						player.sendMessage(ChatColor.RED + "You are already creating, to cancel, use /eg cancel");
						return true;
					}
					if (args.length >= 3) {
						int a = -1;
						int b = -1;
						try {
							a = Integer.parseInt(args[2]);
							if (args[1].equalsIgnoreCase("arena")) b = Integer.parseInt(args[3]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.GOLD + args[2] + ChatColor.RED + " and/or " + ChatColor.GOLD + args[3] + ChatColor.RED + " are not numbers");
							return true;
						}
						if (args[1].equalsIgnoreCase("arena")) {
							if (args.length != 4) {
								player.sendMessage(ChatColor.GOLD + "/eg create arena [id] [lobbyID]");
								return true;
							}
							ArrayList<Integer> j = new ArrayList<Integer>();
							j.add(a);
							j.add(b);
							creating_game_players.add(player.getName());
							creating_game_ids.put(player.getName(), j);
							Inventory i = player.getInventory();
							if (i.firstEmpty() == -1) {
								player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.WOOD_SPADE));
								player.sendMessage(ChatColor.GREEN + "A wooden spade has been dropped at your location because your inventory is full");
								player.sendMessage(ChatColor.GOLD + "Right-click at position 1 of the arena with the wooden spade");
								return true;
							} else {
								i.addItem(new ItemStack(Material.WOOD_SPADE));
								player.sendMessage(ChatColor.GREEN + "A wooden spade has been placed in your inventory");
								player.sendMessage(ChatColor.GOLD + "Right-click at position 1 of the arena with the wooden spade");
								return true;
							}
						}
						else if (args[1].equalsIgnoreCase("lobby")) {
							if (args.length != 3) {
								player.sendMessage(ChatColor.GOLD + "/eg create lobby [id]");
								return true;
							}
							creating_lobby_players.add(player.getName());
							creating_lobby_ids.put(player.getName(), a);
							Inventory i = player.getInventory();
							if (i.firstEmpty() == -1) {
								player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.WOOD_SPADE));
								player.sendMessage(ChatColor.GREEN + "A wooden spade has been dropped at your location because your inventory is full");
								player.sendMessage(ChatColor.GOLD + "Right-click at position 1 of the lobby with the wooden spade");
								return true;
							} else {
								i.addItem(new ItemStack(Material.WOOD_SPADE));
								player.sendMessage(ChatColor.GREEN + "A wooden spade has been placed in your inventory");
								player.sendMessage(ChatColor.GOLD + "Right-click at position 1 of the lobby with the wooden spade");
								return true;
							}
						}
					} else {
						player.sendMessage(ChatColor.GOLD + "/eg create arena [id] [lobbyID]");
						player.sendMessage(ChatColor.GOLD + "/eg create lobby [id]");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You must be a player to create a game");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission (EndersGame.create)");
				return true;
			}
		}
		
		else if (args[0].equalsIgnoreCase("query")) {
			if (sender.hasPermission("EndersGame.override")) {
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "You must enter something to query");
					return true;
				}
				StringBuilder b = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					b.append(args[i]);
					b.append(" ");
				}
				try {
					sender.sendMessage(sql.query(b.toString()).toString());
					return true;
				} catch (SQLException e) {
					sender.sendMessage(ChatColor.RED + "Failed to send query, error: " + e.getMessage());
					e.printStackTrace();
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission (EndersGame.override)");
				return true;
			}
		}
		
		else if (args[0].equalsIgnoreCase("delete")) {
			if (sender.hasPermission("EndersGame.delete")) {
				if (args.length == 3) {
					if (args[1].equalsIgnoreCase("arena")) {
						try {
							gm.unregisterGame(Integer.parseInt(args[2]));
							sender.sendMessage(ChatColor.GREEN + "Unregistered arena with the database, ID: " + args[2]);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.RED + " is not a number");
							return true;
						} catch (SQLException e) {
							sender.sendMessage(ChatColor.RED + "There has been an error with the database: " + e.getMessage());
							sendErr("SQLException while trying to delete a game, error: " + e.getErrorCode() + ", message: " + e.getMessage());
							e.printStackTrace();
							return true;
						}
					}
					
					else if (args[1].equalsIgnoreCase("lobby")) {
						try {
							gm.unregisterLobby(Integer.parseInt(args[2]));
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.GOLD + args[2] + ChatColor.RED + " is not a number");
							return true;
						} catch (SQLException e) {
							sender.sendMessage(ChatColor.RED + "There has been an error with the database: " + e.getMessage());
							sendErr("SQLException while trying to delete a lobby, error: " + e.getErrorCode() + ", message: " + e.getMessage());
							e.printStackTrace();
							return true;
						}
					}
					
					else {
						sender.sendMessage(ChatColor.GOLD + "/eg delete [arena|lobby] [id]");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.GOLD + "/eg delete [game|lobby] [id]");
					return true;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission (EndersGame.delete)");
				return true;
			}
		}
		return false;
	}
}
