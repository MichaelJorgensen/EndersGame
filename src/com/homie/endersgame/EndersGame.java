package com.homie.endersgame;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import com.homie.endersgame.api.GameManager;
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
	
	public void onEnable() {
		this.config = new Config(this);
		debug = config.getDebug();
		
		// Setup MySQL/SQlite
		if (!setupSQL()) {
			sendErr("SQL could not be setup");
			getPluginLoader().disablePlugin(this);
			return;
		}
		gm = new GameManager(this);
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new SignRun(this), 75L, 1500L);
	}
	
	public void onDisable() {
		try {
			sql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
}
