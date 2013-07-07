package com.homie.endersgame.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

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
		sql.query("DELETE FROM signs WHERE gameid="+gameid);
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
}
