package com.homie.endersgame.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.homie.endersgame.EndersGame;
import com.homie.endersgame.sql.options.DatabaseOptions;
import com.homie.endersgame.sql.options.MySQLOptions;
import com.homie.endersgame.sql.options.SQLiteOptions;

public class SQL {
	
	private DatabaseOptions dop;
	private Connection con;
	
	public SQL(EndersGame plugin, DatabaseOptions dop) {
		this.dop = dop;
		plugin.getDataFolder().mkdirs();
		
		if (dop instanceof SQLiteOptions) {
			try {
				((SQLiteOptions) dop).getSQLFile().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public DatabaseOptions getDatabaseOptions() {
		return dop;
	}
	
	public Connection getConnection() {
		return con;
	}
	
	public boolean open() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			con = null;
			return false;
		}
		if (dop instanceof MySQLOptions){
			this.con = DriverManager.getConnection("jdbc:mysql://"+((MySQLOptions) dop).getHostname()+":"+
					((MySQLOptions) dop).getPort()+"/"+
					((MySQLOptions) dop).getDatabase(), 
					((MySQLOptions) dop).getUsername(), 
					((MySQLOptions) dop).getPassword());
			return true;
		}
		else if (dop instanceof SQLiteOptions) {
			this.con = DriverManager.getConnection("jdbc:sqlite:"+((SQLiteOptions) dop).getSQLFile().getAbsolutePath());
			return true;
		} else {
			return false;
		}
	}
	
	public void close() throws SQLException {
		con.close();
	}
	
	public boolean reload() {
		try {
			close();
			return open();
		} catch (SQLException e) {
			return false;
		}
	}
	
	public ResultSet query(String query) throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		st = con.createStatement();
		EndersGame.debug("Query: " + query);
		if (query.toLowerCase().contains("delete") || query.toLowerCase().contains("update") || query.toLowerCase().contains("insert")) {
			st.executeUpdate(query);
			return rs;
		} else {
			rs = st.executeQuery(query);
			return rs;
		}
	}
	
	public boolean createTable(String table) throws SQLException {
		Statement st = con.createStatement();
		return st.execute(table);
	}
}
