package com.homie.endersgame.runnable;

import java.sql.SQLException;

import org.bukkit.block.Sign;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game;
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
				if (g != k || q != e) {
					EndersGame.debug("Updating sign for game: " + game.getGameId());
					sign.setLine(2, k + "/" + e);
					sign.update();
				}
			}
		} catch (SQLException e) {
			EndersGame.sendErr("Possibly missing sign for an arena");
			if (plugin.debug()) e.printStackTrace();
		}
	}
}
