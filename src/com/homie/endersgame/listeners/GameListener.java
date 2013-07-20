package com.homie.endersgame.listeners;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.one4me.ImprovedOfflinePlayer;
import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.Game.GameTeam;
import com.homie.endersgame.api.Lobby;

public class GameListener implements Listener {

	private EndersGame plugin;
	
	public static HashMap<String, Location> creating_game_locations = new HashMap<String, Location>();
	public static HashMap<String, Location> creating_lobby_locations = new HashMap<String, Location>();
	public static HashMap<String, Integer> players_hit = new HashMap<String, Integer>();
	public static HashMap<String, Integer> times_players_hit = new HashMap<String, Integer>();
	
	public GameListener(EndersGame plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
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
				if (plugin.getRunningGames().get(i) != null && !Game.isRegisteredSign((Sign) event.getBlock().getState(), i, plugin.getSQL())) {
					event.setLine(0, ChatColor.DARK_RED + "Ender's Game");
					event.setLine(1, "Arena " + i);
					event.setLine(2, "0/" + plugin.getEnderConfig().getMaxPlayers());
					event.setLine(3, "Lobby");
					try {
						Game.registerSign(i, (Sign) event.getBlock().getState(), plugin.getSQL());
						plugin.getRunningGames().get(i).setSignLocation(event.getBlock().getLocation());
						return;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					player.sendMessage(ChatColor.RED + "That arena doesn't exist or a sign to that arena already exists");
					event.getBlock().breakNaturally();
					return;
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission (EndersGame.createsign)");
				event.getBlock().breakNaturally();
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		for (Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
			if (en.getValue().getArrayListofPlayers().contains(player.getName())) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if (b.getState() instanceof Sign) {
			Sign s = (Sign) b.getState();
			if (s.getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "Ender's Game")) {
				try {
					if (Game.getSign(Integer.parseInt(s.getLine(1).split("Arena ")[1]), plugin.getSQL()) != null) {
						Game.unregisterSign(Integer.parseInt(s.getLine(1).split("Arena ")[1]), plugin.getSQL());
						return;
					}
				} catch (NumberFormatException | IndexOutOfBoundsException | SQLException e) {
					if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("ResultSet closed")) return;
					e.printStackTrace();
				}
			}
		}
		Player player = event.getPlayer();
		for (Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
			if (en.getValue().getArrayListofPlayers().contains(player.getName())) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			for (Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
				if (en.getValue().getArrayListofPlayers().contains(player.getName())) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDrop(PlayerDropItemEvent event) {
		for (Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
			if (en.getValue().getArrayListofPlayers().contains(event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (players_hit.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventory(InventoryCreativeEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			for (Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
				if (en.getValue().getArrayListofPlayers().contains(player.getName())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSnowallHit(ProjectileHitEvent event) {
		if (event.getEntity().getType() == EntityType.SNOWBALL) {
			List<Entity> e = event.getEntity().getNearbyEntities(1, 1.5, 1);
			if (!e.isEmpty()) {
				int size = e.size();
				if (e.get(size-1) instanceof Player) {
					Player player = (Player) e.get(size-1);
					if (!players_hit.containsKey(player.getName())) {
						for (Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
							if (en.getValue().getArrayListofPlayers().contains(player.getName())) {
								if (event.getEntity().getShooter() instanceof Player) {
									Player shooter = (Player) event.getEntity().getShooter();
									HashMap<String, GameTeam> w = en.getValue().getPlayerList();
									if (w.get(shooter.getName()) == w.get(player.getName())) {
										shooter.sendMessage(ChatColor.GOLD + "[EndersGame] " + ChatColor.RED + "Don't shoot yourself or your team!");
										return;
									}
									if (times_players_hit.containsKey(player.getName()) && !players_hit.containsKey(player.getName())) {
										int u = times_players_hit.get(player.getName());
										times_players_hit.remove(player.getName());
										times_players_hit.put(player.getName(), u+1);
									}
									if (!times_players_hit.containsKey(player.getName())) times_players_hit.put(player.getName(), 1);
									players_hit.put(player.getName(), 0);
									player.sendMessage(ChatColor.GOLD + "[EndersGame] " + ChatColor.RED + "You've been hit, you cannot move 3 seconds");
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		for (Map.Entry<Integer, Game> i : plugin.getRunningGames().entrySet()) {
			if (i.getValue().getArrayListofPlayers().contains(player.getName())) {
				HashMap<String, GameTeam> pl = i.getValue().getPlayerList();
				GameTeam pteam = pl.get(player.getName());
				if (pteam == GameTeam.Team1) {
					event.setMessage(ChatColor.AQUA + "[Team1] " + ChatColor.RESET + event.getMessage());
				}
				else if (pteam == GameTeam.Team1Leader) {
					event.setMessage(ChatColor.BLUE + "[Leader] " + ChatColor.RESET + event.getMessage());
				}
				else if (pteam == GameTeam.Team2) {
					event.setMessage(ChatColor.RED + "[Team2] " + ChatColor.RESET + event.getMessage());
				}
				else if (pteam == GameTeam.Team2Leader) {
					event.setMessage(ChatColor.DARK_RED + "[Leader] " + ChatColor.RESET + event.getMessage());
				}
				for (Map.Entry<String, GameTeam> en : pl.entrySet()) {
					if (en.getValue() != pteam) {
						if (pteam == GameTeam.Team1 && en.getValue() == GameTeam.Team1Leader) continue;
						if (pteam == GameTeam.Team1Leader && en.getValue() == GameTeam.Team1) continue;
						if (pteam == GameTeam.Team2 && en.getValue() == GameTeam.Team2Leader) continue;
						if (pteam == GameTeam.Team2Leader && en.getValue() == GameTeam.Team2) continue;
						event.getRecipients().remove(plugin.getServer().getPlayer(en.getKey()));
					}
				}
				ArrayList<String> all = i.getValue().getArrayListofPlayers();
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (!all.contains(p.getName())) {
						event.getRecipients().remove(p);
					}
				}
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase().startsWith("/eg leave")) return;
		Player player = event.getPlayer();
		for (Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
			if (en.getValue().getArrayListofPlayers().contains(player.getName()) && !player.hasPermission("EndersGame.override")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You cannot use commands while in-game. To leave, use /eg leave");
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeave(final PlayerQuitEvent event) {
		if (event.getPlayer().hasPermission("EndersGame.create")) {
			Game.cancelCreatingCommandEventForPlayer(event.getPlayer(), false);
		}
		for (final Map.Entry<Integer, Game> en : plugin.getRunningGames().entrySet()) {
			if (en.getValue().getArrayListofPlayers().contains(event.getPlayer().getName())) {
				event.getPlayer().resetPlayerTime();
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						en.getValue().ejectOfflinePlayer(new ImprovedOfflinePlayer(Bukkit.getOfflinePlayer(event.getPlayer().getName())));
					}
				}, 5L);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		if (players_hit.containsKey(name)) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (player.getItemInHand().getType() == Material.SNOW_BALL && new Random().nextInt(2) == 0) {
					event.setCancelled(true);
					return;
				}
			}
		}
		if (EndersGame.creating_game_players.contains(name) || EndersGame.creating_lobby_players.contains(name)) {
			if (event.getItem() != null && event.getItem().getType() == Material.WOOD_SPADE) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block b = event.getClickedBlock();
					event.setCancelled(true);
					if (creating_game_locations.containsKey(name)) {
						Location l1 = creating_game_locations.get(name);
						Location l2 = b.getLocation();
						creating_game_locations.remove(name);
						Game game = new Game(plugin, EndersGame.creating_game_ids.get(name).get(0),
								plugin.getLobbyList().get(EndersGame.creating_game_ids.get(name).get(1)), null, l1, l2, new ArrayList<Location>());
						try {
							game.addToDatabase();
						} catch (SQLException e) {
							player.sendMessage(ChatColor.RED + "Database error while adding arena, canceling creation, error message: " + e.getMessage());
							Game.cancelCreatingCommandEventForPlayer(player, false);
							e.printStackTrace();
							return;
						}
						plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, game, 20L, 20L);
						EndersGame.creating_game_players.remove(name);
						EndersGame.creating_game_ids.remove(name);
						player.getInventory().remove(Material.WOOD_SPADE);
						player.sendMessage(ChatColor.GREEN + "Point 2 specified, arena created. You should set the arena's spawn locations with /eg setspawn");
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
						Lobby lobby = new Lobby(plugin, EndersGame.creating_lobby_ids.get(name), l1, l2, null);
						try {
							lobby.addToDatabase();
						} catch (SQLException e) {
							player.sendMessage(ChatColor.RED + "Database error while adding lobby, canceling creation, error message: " + e.getMessage());
							Game.cancelCreatingCommandEventForPlayer(player, false);
							e.printStackTrace();
							return;
						}
						plugin.addLobby(lobby);
						EndersGame.creating_lobby_players.remove(name);
						EndersGame.creating_lobby_ids.remove(name);
						player.getInventory().remove(Material.WOOD_SPADE);
						player.sendMessage(ChatColor.GREEN + "Point 2 specified, lobby created. You should set the lobby's spawn location with /eg setspawn");
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
			Sign sign = (Sign) b.getState();
			if (Game.isRegisteredSign(sign, plugin.getSQL()) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (player.hasPermission("EndersGame.join")) {
					try {
						plugin.getRunningGames().get(Integer.parseInt(sign.getLine(1).split("Arena ")[1])).addPlayer(player);
						event.setCancelled(true);
						return;
					} catch (IndexOutOfBoundsException | NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "The sign isn't formatted properly!");
						return;
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission (EndersGame.join)");
					return;
				}
			}
		}
	}
}
