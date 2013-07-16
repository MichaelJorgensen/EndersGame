package com.homie.endersgame.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.api.Game.GameStage;
import com.homie.endersgame.api.Game.GameTeam;
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
	
	public boolean isRegisteredSign(Sign sign) {
		try {
			int signID = Integer.parseInt(sign.getLine(1).split("Arena ")[1]);
			Block b = getSign(signID);
			return (sign.getX() == b.getX() && sign.getY() == b.getY() && sign.getZ() == b.getZ());
		} catch (Exception e) {
			return false;
		}
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
		PreparedStatement p = con.prepareStatement("INSERT INTO games (gameid, lobbyid, x1, y1, z1, x2, y2, z2, world, players, gamestage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		p.setInt(1, gameid);
		p.setInt(2, lobbyid);
		p.setInt(3, x1);
		p.setInt(4, y1);
		p.setInt(5, z1);
		p.setInt(6, x2);
		p.setInt(7, y2);
		p.setInt(8, z2);
		p.setString(9, world.getName());
		p.setString(10, "");
		p.setString(11, "Lobby");
		p.addBatch();
		con.setAutoCommit(false);
		p.executeBatch();
		con.setAutoCommit(true);
		EndersGame.debug("Registered new game, ID: " + gameid);
	}
	
	@Deprecated
	public void updateGame(Game game) throws SQLException {
		int gameid = game.getGameId();
		sql.query("UPDATE games SET gameid=" + game.getGameId() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET lobbyid=" + game.getLobbyId() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET x1=" + game.getLocationOne().getX() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET y1=" + game.getLocationOne().getY() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET z1=" + game.getLocationOne().getZ() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET x2=" + game.getLocationTwo().getX() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET y2=" + game.getLocationTwo().getY() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET z2=" + game.getLocationTwo().getZ() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET world=" + game.getLocationOne().getWorld().getName() + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET players=" + plugin.convertPlayerListToString(game.getPlayerList()) + " WHERE gameid=" + gameid);
		sql.query("UPDATE games SET gamestage=" + game.getGameStage() + " WHERE gameid=" + gameid);
		EndersGame.debug("Full update done on game " + gameid);
	}
	
	public HashMap<String, GameTeam> getGamePlayers(int gameid) throws SQLException {
		ResultSet rs = sql.query("SELECT players FROM games WHERE gameid=" + gameid);
		if (rs == null) return null;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		String players = rs.getString("players");
		return plugin.convertPlayerListToHash(players);
	}
	
	public ArrayList<String> getGamePlayerList(int gameid) throws SQLException {
		ResultSet rs = sql.query("SELECT players FROM games WHERE gameid=" + gameid);
		ArrayList<String> list = new ArrayList<String>();
		if (rs == null) return list;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		for (Map.Entry<String, GameTeam> en : plugin.convertPlayerListToHash(rs.getString("players")).entrySet()) {
			list.add(en.getKey());
		}
		return list;
	}
	
	public void updateGamePlayers(int gameid, HashMap<String, GameTeam> players) throws SQLException {
		sql.query("UPDATE games SET players='" + plugin.convertPlayerListToString(players) + "' WHERE gameid=" + gameid);
		EndersGame.debug("Updated player list for game " + gameid);
	}
	
	public void updateGameStage(int gameid, GameStage stage) throws SQLException {
		sql.query("UPDATE games SET gamestage='" + stage.toString() + "' WHERE gameid=" + gameid);
		EndersGame.debug("Updated gamestage for game " + gameid);
	}
	
	public ArrayList<String> getPlayersOnTeam(int gameid, GameTeam team) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = sql.query("SELECT players FROM games WHERE gameid=" +  gameid);
		if (rs == null) return list;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		HashMap<String, GameTeam> l = plugin.convertPlayerListToHash(rs.getString(1));
		for (Map.Entry<String, GameTeam> en : l.entrySet()) {
			if (en.getValue() == team) {
				list.add(en.getKey());
			}
		}
		return list;
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
		return new Game(gameid, lobbyid, new Location(world, x1, y1, z1), new Location(world, x2, y2, z2), plugin.convertPlayerListToHash(rs.getString("players")), GameStage.getFrom(rs.getString("gamestage")));
	}
	
	public void setGameSpawns(int gameid, ArrayList<Location> spawns) throws SQLException {
		Connection con = sql.getConnection();
		PreparedStatement p = con.prepareStatement("INSERT INTO gamespawns (gameid, x1, y1, z1, x2, y2, z2, world) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		Location one = spawns.get(0);
		Location two = spawns.get(1);
		p.setInt(1, gameid);
		p.setInt(2, (int) one.getX());
		p.setInt(3, (int) one.getY());
		p.setInt(4, (int) one.getZ());
		p.setInt(5, (int) two.getX());
		p.setInt(6, (int) two.getY());
		p.setInt(7, (int) two.getZ());
		p.setString(8, one.getWorld().getName());
		p.addBatch();
		con.setAutoCommit(false);
		p.executeBatch();
		con.setAutoCommit(true);
		EndersGame.debug("Set game spawn of: " + gameid);
	}
	
	/**
	 * Gets the spawn locations for each team for the given game
	 * Team 1's spawn point will always be the first location in the list
	 * @param gameid of desired game
	 * @return Array list of locations
	 * @throws SQLException
	 */
	public ArrayList<Location> getGameSpawns(int gameid) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM gamespawns WHERE gameid=" + gameid);
		ArrayList<Location> locations = new ArrayList<Location>();
		if (rs == null) return locations;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		locations.add(new Location(plugin.getServer().getWorld(rs.getString("world")),
				rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1")));
		locations.add(new Location(plugin.getServer().getWorld(rs.getString("world")),
				rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2")));
		return locations;
	}
	
	public ArrayList<Integer> getAllGamesFromDatabase() throws SQLException {
		ArrayList<Integer> list = new ArrayList<Integer>();
		ResultSet rs = sql.query("SELECT gameid FROM games");
		if (rs == null) return list;
		while (rs.next()) {
			list.add(rs.getInt(1));
		}
		return list;
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
		ResultSet rs = sql.query("SELECT * FROM lobbies WHERE lobbyid=" + lobbyid);
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
	
	public void setLobbySpawn(int lobbyid, Location location) throws SQLException {
		Connection con = sql.getConnection();
		PreparedStatement p = con.prepareStatement("INSERT INTO lobbyspawns (lobbyid, coordX, coordY, coordZ, world) VALUES (?, ?, ?, ?, ?)");
		p.setInt(1, lobbyid);
		p.setInt(2, (int) location.getX());
		p.setInt(3, (int) location.getY());
		p.setInt(4, (int) location.getZ());
		p.setString(5, location.getWorld().getName());
		p.addBatch();
		con.setAutoCommit(false);
		p.executeBatch();
		con.setAutoCommit(true);
		EndersGame.debug("Set spawn location for: " + lobbyid);
	}
	
	public Location getLobbySpawn(int lobbyid) throws SQLException {
		ResultSet rs = sql.query("SELECT * FROM lobbyspawns WHERE lobbyid=" + lobbyid);
		if (rs == null) return null;
		if (sql.getDatabaseOptions() instanceof MySQLOptions) rs.first();
		return new Location(plugin.getServer().getWorld(rs.getString("world")),
				rs.getInt("coordX"), rs.getInt("coordY"), rs.getInt("coordZ"));
	}
	
	public ArrayList<Integer> getAllLobbiesFromDatabase() throws SQLException {
		ResultSet rs = sql.query("SELECT lobbyid FROM lobbies");
		ArrayList<Integer> list = new ArrayList<Integer>();
		if (rs == null) return list;
		while (rs.next()) {
			list.add(rs.getInt(1));
		}
		return list;
	}
	
	public boolean isInsideCuboid(Location loc, Location l1, Location l2) {
		int x = (int) loc.getX();
		int y = (int) loc.getY();
		int z = (int) loc.getZ();
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
 
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
	}
	
	/**
	 * @author Adamki11s
	 */
	public ArrayList<String> getPlayersInTeamSpawn(Location center, int radius) {
		ArrayList<String> playerSet = new ArrayList<String>();
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			Location point1 = new Location(center.getWorld(), center.getX() - radius, center.getY() - radius, center.getZ() - radius);
			Location point2 = new Location(center.getWorld(), center.getX() + radius, center.getY() + radius, center.getZ() + radius);
			double x1 = point1.getX(), x2 = point2.getX(),
			y1 = point1.getY(), y2 = point2.getY(),
			z1 = point1.getZ(), z2 = point2.getZ(),
			px = p.getLocation().getX(),
			py = p.getLocation().getY(),
			pz = p.getLocation().getZ();
			if((((py <= y1) && 
				(py >= y2)) || 
				((py >= y1) && 
				(py <= y2))) && 
				(((pz <= z1) && 
				(pz >= z2)) || 
				((pz >= z1) && 
				(pz <= z2)))  &&  
				(((px <= x1) && 
				(px >= x2)) || 
				((px >= x1) && 
				(px <= x2))) && 
				(((px <= x1) && 
				(px >= x2)) || 
				((px >= x1) && 
				(px <= x2)))){
				playerSet.add(p.getName());
				}	
		}
		
		return playerSet;
	}
	
	/**
	 * @author Adamki11s
	 */
	public ArrayList<Block> getBlocksInRegion(Location point1, Location point2) {
		ArrayList<Block> al = new ArrayList<Block>();
		double x1 = point1.getX(), x2 = point2.getX(),
		    y1 = point1.getY(), y2 = point2.getY(),
		    z1 = point1.getZ(), z2 = point2.getZ(), tmp = 0;
		World w = point1.getWorld();

		if(x2 > x1){ tmp = x2; x2 = x1; x1 = tmp; }
		if(y2 > y1){ tmp = y2; y2 = y1; y1 = tmp; }
		if(z2 > z1){ tmp = z2; z2 = z1; z1 = tmp; }

		for(double x = x1 - x2; x <= x2; x++){
			for(double y = y1 - y2; y <= y2; y++){
				for(double z = z1 - z2; z <= z2; z++){
					Location construct = new Location(w, x2 + x, y2 + y, z2 + z);
					al.add(w.getBlockAt(construct));//This should add every block in the 3x3 cube, putting 9 blocks in the ArrayList.
				}
			}
		}
		return al;
	}
	
	public boolean sendGameMessage(int gameid, String message) {
		try {
			for (String i : getGamePlayerList(gameid)) {
				plugin.getServer().getPlayer(i).sendMessage(ChatColor.GOLD + "[EndersGame] " + message);
			}
			return true;
		} catch (SQLException | NullPointerException e) {
			return false;
		}
	}
	
	public boolean sendTeamMessage(int gameid, GameTeam team, String message) {
		try {
			for (Map.Entry<String, GameTeam> en : getGamePlayers(gameid).entrySet()) {
				if (en.getValue() == team) {
					plugin.getServer().getPlayer(en.getKey()).sendMessage(ChatColor.GOLD + "[EndersGame] " + message);
				}
				return true;
			}
		} catch (SQLException | NullPointerException e) {
			return false;
		}
		return false;
	}
}
