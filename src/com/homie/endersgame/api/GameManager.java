package com.homie.endersgame.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.sql.SQL;
import com.homie.endersgame.sql.options.MySQLOptions;

public class GameManager {

	private EndersGame plugin;
	private SQL sql;
	
	public GameManager(EndersGame plugin) {
		this.plugin = plugin;
		this.sql = plugin.getSQL();
	}
	
	public void registerSign(Block block, int gameid) throws SQLException {
		Connection con = sql.getConnection();
		PreparedStatement p = con.prepareStatement("INSERT INTO signs (gameid, coordX, coordY, coordZ, world) VALUES (?, ?, ?, ?, ?)");
		p.setInt(1, gameid);
		p.setInt(2, block.getX());
		p.setInt(3, block.getY());
		p.setInt(4, block.getZ());
		p.setString(5, block.getWorld().getName());
		p.addBatch();
		con.setAutoCommit(false);
		p.executeBatch();
		con.setAutoCommit(true);
		EndersGame.debug("Registered sign: " + gameid);
	}
	
	public void unregisterSign(int gameid) throws SQLException {
		sql.query("DELETE FROM signs WHERE gameid=" + gameid);
		EndersGame.debug("Deleted sign from database: " + gameid);
	}
	
	public Block getSign(int gameid) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM signs WHERE gameid=" + gameid);
		if (rs == null) return null;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		int x = rs.getInt("coordX");
		int y = rs.getInt("coordY");
		int z = rs.getInt("coordZ");
		World world = plugin.getServer().getWorld(rs.getString("world"));
		return world.getBlockAt(new Location(world, x, y, z));
	}
	
	/**
	 * Gets all of the signs in the database and returns them in an ArrayList of their gameid's
	 * @return ArrayList<Integer> of sign's gameid's
	 * @throws SQLException
	 */
	public ArrayList<Integer> getAllSignsFromDatabase() throws SQLException {
		ArrayList<Integer> s = new ArrayList<Integer>();
		ResultSet rs = sql.query("SELECT gameid FROM signs");
		if (rs == null) return s;
		while (rs.next()) {
			s.add(rs.getInt(1));
		}
		return s;
	}
	
	public void registerGame(int gameid, int lobbyid, int x1, int y1, int z1, int x2, int y2, int z2, World world) throws SQLException {
		Connection con = sql.getConnection();
		PreparedStatement p = con.prepareStatement("INSERT INTO games (gameid, lobbyid, x1, y1, z1, x2, y2, z2, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		p.setInt(1, gameid);
		p.setInt(2, lobbyid);
		p.setInt(3, x1);
		p.setInt(4, y1);
		p.setInt(5, z1);
		p.setInt(6, x2);
		p.setInt(7, y2);
		p.setInt(8, z2);
		p.setString(9, world.getName());
		p.addBatch();
		con.setAutoCommit(false);
		p.executeBatch();
		con.setAutoCommit(true);
		EndersGame.debug("Registered new game, ID: " + gameid);
	}
	
	public void unregisterGame(int gameid) throws SQLException {
		sql.query("DELETE FROM games WHERE gameid=" + gameid);
		EndersGame.debug("Deleted game from database: " + gameid);
	}
	
	public Game getGame(int gameid) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM games WHERE gameid=" + gameid);
		if (rs == null) return null;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		int lobbyid = rs.getInt("lobbyid");
		int x1 = rs.getInt("x1");
		int y1 = rs.getInt("y1");
		int z1 = rs.getInt("z1");
		int x2 = rs.getInt("x2");
		int y2 = rs.getInt("y2");
		int z2 = rs.getInt("z2");
		World world = plugin.getServer().getWorld(rs.getString("world"));
		return new Game(gameid, lobbyid, new Location(world, x1, y1, z1), new Location(world, x2, y2, z2), new HashMap<Player, Boolean>());
	}
	
	public void registerLobby(int lobbyid, int x1, int y1, int z1, int x2, int y2, int z2, World world) throws SQLException {
		Connection con = sql.getConnection();
		PreparedStatement p = con.prepareStatement("INSERT INTO lobbies (lobbyid, x1, y1, z1, x2, y2, z2, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		p.setInt(1, lobbyid);
		p.setInt(2, x1);
		p.setInt(3, y1);
		p.setInt(4, z1);
		p.setInt(5, x2);
		p.setInt(6, y2);
		p.setInt(7, z2);
		p.setString(8, world.getName());
		p.addBatch();
		con.setAutoCommit(false);
		p.executeBatch();
		con.setAutoCommit(true);
		EndersGame.debug("Registered new lobby, ID: " + lobbyid);
	}
	
	public void unregisterLobby(int lobbyid) throws SQLException {
		sql.query("DELETE FROM lobbies WHERE lobbyid=" + lobbyid);
		EndersGame.debug("Deleted lobby from database: " + lobbyid);
	}
	
	public Lobby getLobby(int lobbyid) throws SQLException {
		ResultSet rs = sql.query("SELECT FROM lobbies WHERE lobbyid=" + lobbyid);
		if (rs == null) return null;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		int x1 = rs.getInt("x1");
		int y1 = rs.getInt("y1");
		int z1 = rs.getInt("z1");
		int x2 = rs.getInt("x2");
		int y2 = rs.getInt("y2");
		int z2 = rs.getInt("z2");
		World world = plugin.getServer().getWorld(rs.getString("world"));
		return new Lobby(lobbyid, new Location(world, x1, y1, z1), new Location(world, x2, y2, z2));
	}
}
