package com.homie.endersgame.listeners;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
		
		if (line[0].contains("[EnderGame]")) {
			if (player.hasPermission("EndersGame.createsign")) {
				event.setLine(0, ChatColor.DARK_RED + "Ender's Game");
				event.setLine(1, "Arena 1");
				event.setLine(2, "0/24");
				event.setLine(3, "");
				try {
					plugin.getGameManager().registerSign(event.getBlock(), 1);
				} catch (SQLException e) {
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
			// add the player to the game now that they clicked the sign
		}
	}
}
