package org.aksw.semmap.widget.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.sql.*;
import java.util.prefs.Preferences;

import org.ini4j.IniPreferences;

/**
 * JDBC Tools
 */
public class DBTool {

	/** serverName */
	private String serverName;
	/** portNumber */
	private String database;
	/** userName */
	private String userName;
	/** password */
	private String password;
	/** url Connection data */
	private String url;

	private String iniFilePath;

	/**
	 * Constructor
	 * 
	 * @param newServerName
	 *            name of the server
	 * @param newDatabase
	 *            name of the database
	 * @param newUserName
	 *            username
	 * @param newPassword
	 *            password
	 */
	public DBTool(String newServerName, String newDatabase, String newUserName,
			String newPassword) {
		this.serverName = newServerName;
		this.database = newDatabase;
		this.userName = newUserName;
		this.password = newPassword;
		this.createUrl();
	}

	/**
	 * Constructor for Veri-Links. Change according to your database.
	 */
	public DBTool(String iniFilePath) {
		this.iniFilePath = iniFilePath;
		try {

			Preferences prefs = new IniPreferences(new FileReader(iniFilePath));
			this.serverName = prefs.node("database").get("server", null);
			this.database = prefs.node("database").get("name", null);
			this.userName = prefs.node("database").get("user", null);
			this.password = prefs.node("database").get("pass", null);

			Class.forName("com.mysql.jdbc.Driver");
			this.url = "jdbc:mysql://" + serverName + "/" + database;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Appending serverName and database to url String
	 */
	public void createUrl() {
		this.url = "jdbc:mysql://" + this.serverName + "/" + this.database;
	}

	/**
	 * Connects to database
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {
		System.out.println("Connecting to database..");
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); // JDBC Driver
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(this.url, this.userName,
					this.password);
			// con = DriverManager.getConnection(this.url);
			System.out.println("Connected to database");
		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		return con;
	}

	/**
	 * Executes the given statement, which may be an INSERT, UPDATE, or DELETE
	 * statement or an SQL statement that returns nothing. Automatically connect
	 * to database.
	 * 
	 * @param updateStatement
	 *            String
	 */
	public void queryUpdateOnce(String updateStatement) throws Exception {
		Connection con = this.getConnection(); // establish connection
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(updateStatement);
			stmt.close();
		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
		con.close();
	}

	/**
	 * Executes the given statement, which may be an INSERT, UPDATE, or DELETE
	 * statement or an SQL statement that returns nothing. Connection to
	 * database has to be managed manually.
	 * 
	 * @param updateStatement
	 *            String
	 */
	public void queryUpdate(String updateStatement, Connection con) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(updateStatement);
			stmt.close();
		} catch (SQLException ex) {
			System.err.println("SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Executes the given statement, which returns a ResultSet. Don't forget to
	 * close connection
	 * 
	 * @param resultSetQuery
	 *            String
	 * @return rs ResultSet
	 */
	public ResultSet queryExecute(String resultSetQuery) throws Exception {
		Connection con = this.getConnection(); // establish connection
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(resultSetQuery);
		return rs;
	}

	/**
	 * reads File
	 * 
	 * @param fileName
	 */
	public void readFile(String fileName) {
		File file = new File(fileName);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Connection initDBConnection() {
		System.out.println("Initialize database connection...");
		Connection conn = null;
		try {

			conn = DriverManager.getConnection(this.url, this.userName,
					this.password);
			System.out.println(url);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println("Connection established.");
		return conn;
	}

	public void setDatabase(String db) {
		this.database = db;
	}

	public String getDatabase() {
		return this.database;
	}

	public static void main(String args[]) {

		DBTool db = new DBTool("d://db_settings.ini");
		// db.createDatabase();
		System.out.println(db.getClass());

		// Test current dir
		String dir = "user.dir"; // set to current directory
		try {
			dir = new File(System.getProperty(dir)).getCanonicalPath();
		} catch (IOException e1) {
		}
		System.out.println("Current dir : " + dir);

	}

}
