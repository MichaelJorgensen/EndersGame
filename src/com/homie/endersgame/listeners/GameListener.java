package com.homie.endersgame.listeners;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.homie.endersgame.EndersGame;

public class GameListener implements Listener {

	private EndersGame plugin;
	
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
				event.setLine(0, ChatColor.DARK_RED + "Ender's Game");
				event.setLine(1, "Arena " + i);
				event.setLine(2, "0/" + plugin.getEnderConfig().getMaxPlayers());
				event.setLine(3, "Do Not Edit");
				try {
					plugin.getGameManager().registerSign(event.getBlock(), i);
				} catch (SQLException e) {
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
		if (!event.hasBlock()) return;
		Block b = event.getClickedBlock();
		
		if (b.getState() instanceof Sign) {
			// TODO: check sign, add player to game
		}
	}
}
