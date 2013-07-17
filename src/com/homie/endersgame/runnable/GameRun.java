package com.homie.endersgame.runnable;

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game;
import com.homie.endersgame.api.Game.GameStage;
import com.homie.endersgame.api.GameManager;

public class GameRun implements Runnable {

	private EndersGame plugin;
	private GameManager gm;
	
	public GameRun(EndersGame plugin) {
		this.plugin = plugin;
		this.gm = plugin.getGameManager();
	}
	
	@Override
	public void run() {
		try {
			for (Integer i : gm.getAllGamesFromDatabase()) {
				Game game = gm.getGame(i);
				Sign sign = (Sign) gm.getSign(game.getGameId()).getState();
				String u = sign.getLine(2);
				int g = Integer.parseInt(u.split("/")[0]);
				int k = game.getPlayerList().size();
				int e = plugin.getConfiguration().getMaxPlayers();
				int q = Integer.parseInt(u.split("/")[1]);
				if (k == 0 && game.getGameStage() != GameStage.Lobby) {
					game.setGameStage(GameStage.Lobby);
					gm.updateGameStage(i, game.getGameStage());
				}
				if (GameStage.getFrom(sign.getLine(3)) != game.getGameStage()) {
					sign.setLine(3, game.getGameStage().toString());
				}
				if (game.getGameStage() == GameStage.Ingame || k == q) {
					sign.setLine(0, ChatColor.DARK_RED + "Ender's Game");
				}
				if (game.getGameStage() == GameStage.Lobby || game.getGameStage() == GameStage.PreGame) {
					sign.setLine(0, ChatColor.DARK_GREEN + "Ender's Game");
				}
				sign.update();
				if (g != k || q != e) {
					EndersGame.debug("Updating sign for game: " + game.getGameId());
					sign.setLine(2, k + "/" + e);
					sign.update();
					if (g == 0) {
						GameManageRun r = new GameManageRun(plugin, game.getGameId());
						r.setId(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, r, 10L, 20L));
						gm.addRunner(r);
					}
				}
			}
		} catch (SQLException e) {
			EndersGame.sendErr("Possibly missing sign for an arena");
			if (plugin.debug()) e.printStackTrace();
		}
	}
}
