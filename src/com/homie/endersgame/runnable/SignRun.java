package com.homie.endersgame.runnable;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.GameManager;
import com.homie.endersgame.sql.SQL;

public class SignRun implements Runnable {

	SQL sql;
	GameManager gm;
	
	public SignRun(EndersGame plugin) {
		this.sql = plugin.getSQL();
		this.gm = plugin.getGameManager();
	}
	
	@Override
	public void run() {
		try {
			EndersGame.debug("Checking registered signs");
			ArrayList<Integer> l = gm.getAllSignsFromDatabase();
			for (int i = 0; i < l.size(); i++) {
				Block b = gm.getSign(l.get(i));
				if (!(b.getState() instanceof Sign)) {
					gm.unregisterSign(i+1);
					EndersGame.debug("Unregistered sign with ID " + l.get(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
