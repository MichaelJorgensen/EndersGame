package com.homie.endersgame;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

	private FileConfiguration config;
	
	public Config(EndersGame plugin) {
		this.config = plugin.getConfig();
		config.options().copyDefaults(true);
		config.options().copyHeader(true);
		plugin.saveConfig();
	}
	
	public boolean getDebug() {
		return config.getBoolean("debug", false);
	}
	
	public String getSQLValue() {
		return config.getString("sql", "sqlite");
	}
	
	public String getHostname() {
		return config.getString("mysql.hostname", "localhost");
	}
	
	public String getPort() {
		return config.getString("mysql.port", "3306");
	}
	
	public String getDatabase() {
		return config.getString("mysql.database", "EndersGame");
	}
	
	public String getUsername() {
		return config.getString("mysql.username", "minecraft");
	}
	
	public String getPassword() {
		return config.getString("mysql.password", "password");
	}
	
	public int getMaxPlayers() {
		return config.getInt("game.max-players", 24);
	}
	
	public int getMinPercentToStart() {
		return config.getInt("game.min-percent-to-start", 50);
	}
	
	public int getPercentInSpawnToWin() {
		return config.getInt("game.percent-in-spawn-to-win", 20);
	}
	
	public int getHitsToBeEjected() {
		return config.getInt("game.hits-to-be-ejected-from-game", 3);
	}
}
