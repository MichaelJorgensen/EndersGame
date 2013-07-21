package com.homie.endersgame.api;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Location;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.events.EventHandle;
import com.homie.endersgame.sql.SQL;

public class Lobby {

	private EndersGame plugin;
	private SQL sql;
	
	private int lobbyid;
	private Location l1;
	private Location l2;
	private Location spawn;
	
	public Lobby(EndersGame plugin, int lobbyid, Location l1, Location l2, Location spawn) {
		this.plugin = plugin;
		this.sql = plugin.getSQL();
		this.lobbyid = lobbyid;
		this.l1 = l1;
		this.l2 = l2;
		this.spawn = spawn;
	}
	
	public static ArrayList<Integer> getAllLobbiesFromDatabase(SQL sql) throws SQLException {
		ArrayList<Integer> list = new ArrayList<Integer>();
		ResultSet rs = sql.query("SELECT lobbyid FROM lobbies");
		if (rs == null) return list;
		while (rs.next()) {
			list.add(rs.getInt(1));
		}
		return list;
	}
	
	public int getLobbyId() {
		return lobbyid;
	}
	
	public Location getLocationOne() {
		return l1;
	}
	
	public void setLocationOne(Location l1) {
		this.l1 = l1;
	}
	
	public Location getLocationTwo() {
		return l2;
	}
	
	public void setLocationTwo(Location l2) {
		this.l2 = l2;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public void setSpawn(Location spawn) throws SQLException {
		this.spawn = spawn;
		sql.query("INSERT INTO lobbyspawns (lobbyid, coordX, coordY, coordZ, world) " +
				"VALUES (" + lobbyid + ", " + spawn.getX() + ", " + spawn.getY() + ", " + spawn.getZ() + ", '" + spawn.getWorld().getName() + "')" );
	}
	
	public void addToDatabase() throws SQLException {
		sql.query("INSERT INTO lobbies (lobbyid, x1, y1, z1, x2, y2, z2, world) " +
				"VALUES (" + lobbyid + ", " + l1.getX() + ", " + l1.getY() + ", " + l1.getZ() + ", " + l2.getX() + ", " + l2.getY() + ", " + l2.getZ() + ", '" + l2.getWorld().getName() + "')");
	}
	
	public void removeFromDatabase() throws SQLException {
		sql.query("DELETE FROM lobbies WHERE lobbyid=" + lobbyid);
		sql.query("DELETE FROM lobbyspawns WHERE lobbyid=" + lobbyid);
		plugin.removeLobby(lobbyid);
		EventHandle.callLobbyDeleteEvent(this);
	}
}
