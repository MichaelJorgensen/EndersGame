package com.homie.endersgame.runnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.Game.GameStage;
import com.homie.endersgame.api.Game.GameTeam;
import com.homie.endersgame.api.GameManager;

public class GameManageRun implements Runnable {

	private EndersGame plugin;
	private GameManager gm;
	private int gameid;
	private int id;
	
	private ArrayList<String> ingame_players = new ArrayList<String>();
	private ItemStack[] definv;
	private int max;
	private int per;
	
	private int wait = 9;
	private int twomin = 0;
	private int timelimit = 0;
	private boolean begin = false;
	
	public GameManageRun(EndersGame plugin, int gameid) {
		this.plugin = plugin;
		this.gm = plugin.getGameManager();
		this.gameid = gameid;
		this.max = plugin.getConfiguration().getMaxPlayers();
		this.per = plugin.getConfiguration().getMinPercentToStart();
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		try {
			Game game = gm.getGame(gameid);
			if (game.getPlayerList().size() == 0) {
				gm.updateGameStage(gameid, GameStage.Lobby);
				Bukkit.getScheduler().cancelTask(id);
				return;
			}
			if (game.getGameStage() == GameStage.Ingame) {
				timelimit++;
				if (timelimit == 300) {
					gm.sendGameMessage(gameid, "Game over");
					plugin.ejectAll();
					return;
				}
				ingame_players = game.getArrayListofPlayers();
				for (String i : ingame_players) {
					Player player = plugin.getServer().getPlayer(i);
					if (player == null) continue;
					if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && !player.isFlying()) {
						player.teleport(new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY()+4, player.getLocation().getZ(),
								player.getLocation().getYaw(), player.getLocation().getPitch()));
						player.setFlying(true);
					}
					if (!player.getInventory().contains(Material.SNOW_BALL)) {
						player.sendMessage(ChatColor.RED + "Resetting inventory");
						player.getInventory().setContents(definv);
					}
				}
			}
			if (game.getGameStage() == GameStage.PreGame && !begin) {
				twomin++;
				if (twomin == 90) {
					gm.sendGameMessage(gameid, ChatColor.RED + "30 seconds left");
				}
				if (twomin == 120) {
					begin = true;
					twomin = 0;
				}
				return;
			}
			if (game.getGameStage() == GameStage.PreGame && begin) {
				game.setGameStage(GameStage.Ingame);
				gm.updateGameStage(gameid, game.getGameStage());
				ArrayList<String> team1 = gm.getPlayersOnTeam(gameid, GameTeam.Team1);
				team1.add(gm.getPlayersOnTeam(gameid, GameTeam.Team1Leader).get(0));
				ArrayList<String> team2 = gm.getPlayersOnTeam(gameid, GameTeam.Team2);
				team2.add(gm.getPlayersOnTeam(gameid, GameTeam.Team2Leader).get(0));
				ArrayList<Location> spawns = gm.getGameSpawns(gameid);
				for (String i : team1) {
					Player player = plugin.getServer().getPlayer(i);
					player.teleport(spawns.get(0));
					player.getInventory().clear();
					ItemStack[] def = player.getInventory().getContents();
					def[0] = new ItemStack(Material.SNOW_BALL);
					definv = def;
					ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
					ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
					ItemStack plate = new ItemStack(Material.LEATHER_CHESTPLATE);
					LeatherArmorMeta bl = (LeatherArmorMeta) boots.getItemMeta();
					LeatherArmorMeta ll = (LeatherArmorMeta) leg.getItemMeta();
					LeatherArmorMeta pl = (LeatherArmorMeta) plate.getItemMeta();
					bl.setColor(Color.BLUE);
					ll.setColor(Color.BLUE);
					pl.setColor(Color.BLUE);
					boots.setItemMeta(bl);
					leg.setItemMeta(ll);
					plate.setItemMeta(pl);
					player.getInventory().setBoots(boots);
					player.getInventory().setLeggings(leg);
					player.getInventory().setChestplate(plate);
					player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (byte) 11));
					player.getInventory().setContents(def);
				}
				for (String i : team2) {
					Player player = plugin.getServer().getPlayer(i);
					player.teleport(spawns.get(1));
					player.getInventory().clear();
					ItemStack[] def = player.getInventory().getContents();
					def[0] = new ItemStack(Material.SNOW_BALL);
					definv = def;
					ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
					ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
					ItemStack plate = new ItemStack(Material.LEATHER_CHESTPLATE);
					LeatherArmorMeta bl = (LeatherArmorMeta) boots.getItemMeta();
					LeatherArmorMeta ll = (LeatherArmorMeta) leg.getItemMeta();
					LeatherArmorMeta pl = (LeatherArmorMeta) plate.getItemMeta();
					bl.setColor(Color.RED);
					ll.setColor(Color.RED);
					pl.setColor(Color.RED);
					boots.setItemMeta(bl);
					leg.setItemMeta(ll);
					plate.setItemMeta(pl);
					player.getInventory().setBoots(boots);
					player.getInventory().setLeggings(leg);
					player.getInventory().setChestplate(plate);
					player.getInventory().setHelmet(new ItemStack(Material.WOOL, 1, (byte) 14));
					player.getInventory().setContents(def);
				}
				gm.sendGameMessage(gameid, ChatColor.DARK_GREEN + "Begin!");
			}
			if (game.getGameStage() == GameStage.Lobby) {
				wait++;
				int u = gm.getGamePlayers(gameid).size();
				int r = gm.getPlayersOnTeam(gameid, GameTeam.Team1).size();
				int w = gm.getPlayersOnTeam(gameid, GameTeam.Team2).size();
				if (u >= (max*per/100)) {
					game.setGameStage(GameStage.PreGame);
					gm.updateGameStage(gameid, game.getGameStage());
					gm.sendGameMessage(gameid, ChatColor.AQUA + "There are now enough players in the game, randomly selecting team leaders");
					int t11 = (int) (Math.random() * r);
					int t22 = (int) (Math.random() * w);
					String t1 = gm.getPlayersOnTeam(gameid, GameTeam.Team1).get(t11);
					String t2 = gm.getPlayersOnTeam(gameid, GameTeam.Team2).get(t22);
					HashMap<String, GameTeam> list = game.getPlayerList();
					list.remove(t1);
					list.remove(t2);
					list.put(t1, GameTeam.Team1Leader);
					list.put(t2, GameTeam.Team2Leader);
					game.setPlayerList(list);
					gm.updateGamePlayers(gameid, list);
					Player l1 = plugin.getServer().getPlayer(t1);
					Player l2 = plugin.getServer().getPlayer(t2);
					l1.sendMessage(ChatColor.GOLD + "[EndersGame] " + ChatColor.DARK_GREEN + "You are team 1's leader!");
					l2.sendMessage(ChatColor.GOLD + "[EndersGame] " + ChatColor.DARK_GREEN + "You are team 2's leader!");
					gm.sendGameMessage(gameid, ChatColor.DARK_GREEN + l1.getDisplayName() + ChatColor.DARK_GREEN + " and " + l2.getDisplayName() + ChatColor.DARK_GREEN + " are team 1 and team 2 leaders respectively");
					gm.sendGameMessage(gameid, ChatColor.RED + "Each team now has 2 minutes with their leader to discuss battle plans, the other team can't see your messages");
				}
				if (wait == 10) {
					gm.sendGameMessage(gameid, ChatColor.DARK_GREEN + "Waiting for more players...");
					wait = 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
