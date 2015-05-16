package sdis.trafficar.database;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MyDatabaseTest {
	
	ConnectionSource connectionSource;
	Dao<User, String> userDao;

	public MyDatabaseTest(String name) {
		
		try {
			
			connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + name + ".db");
			
			userDao = DaoManager.createDao(connectionSource, User.class);
			TableUtils.createTableIfNotExists(connectionSource, User.class);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
	}
	
	public void close() {
		
		try {	
			connectionSource.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean registerUser(String username, String password) {
		
		QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();
		try {
			// Check for duplicate username
			if(queryBuilder.where().eq("Username", username).countOf() == 0) {
				User user = new User();
				user.setUsername(username);
				user.setPassword(password);
				userDao.create(user);
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean loginUser(String username, String password) {
		QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();
		
		try {
			return queryBuilder.where().eq(User.USERNAME_FIELD_NAME, username).and().eq(User.PASSWORD_FIELD_NAME, password).countOf() == 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
		
	}
	

}