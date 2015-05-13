package sdis.trafficar.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDatabase {
	
	private String name;
	private Connection c;
	private Statement stmt;

	public MyDatabase(String name) {
		this.name = name;
		this.c = null;
		this.stmt = null;
	}
	
	public void connect() {
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find class: libraries not being loaded, maybe?");
			e.printStackTrace();
			System.exit(-1);
		} catch (SQLException e) {
			System.err.println("Could not open database.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Opened database successfully.");
		
	}
	
	public void close() {
		
		try {
			this.c.close();
		} catch (SQLException e) {
			System.out.println("Error closing database.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Database successfully closed.");
		
	}
	
	public void init() {
		String createUsersTable = "CREATE TABLE IF NOT EXISTS User " 
				+ "(Id INTEGER PRIMARY KEY, Username TEXT UNIQUE NOT NULL, Password TEXT NOT NULL);";
		
		executeUpdateStmt(createUsersTable);
	}
	
	public void registerUser(String username, String password) {
		String query = "INSERT INTO User (Username, Password) VALUES ('" + username + "', + '" + password + "')";
		executeUpdateStmt(query);
	}
	
	public boolean checkLogin(String username, String password) {
		String query = "SELECT * FROM User WHERE Username = '" + username + "';";
		
		try {
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				System.out.println("ENTROU");
				String passwordCheck = rs.getString("Password");
				return password.equals(passwordCheck);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("FODEU");
		return false;
		
		
	}
	

	public void executeUpdateStmt(String query) {
		
		try {
			stmt = c.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		} catch(SQLException e) {
			System.err.println("Could not create statement.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Update statement successfully executed.");

	}

}
