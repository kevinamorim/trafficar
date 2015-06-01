package sdis.trafficar.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MyDatabaseTest {
	
	ConnectionSource connectionSource;
	
	Dao<User, String> userDao;
	Dao<AuthToken, String> authTokenDao;
	Dao<UsersFollow, String> usersFollowDao;
	Dao<TrafficInformation, String> trafficInformationDao;
	

	public MyDatabaseTest(String name) {
		
		try {
			
			connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + name + ".db");
			
			userDao = DaoManager.createDao(connectionSource, User.class);
			TableUtils.createTableIfNotExists(connectionSource, User.class);
			
			authTokenDao = DaoManager.createDao(connectionSource, AuthToken.class);
			TableUtils.createTableIfNotExists(connectionSource, AuthToken.class);
			
			usersFollowDao = DaoManager.createDao(connectionSource, UsersFollow.class);
			TableUtils.createTableIfNotExists(connectionSource, UsersFollow.class);
			
			trafficInformationDao = DaoManager.createDao(connectionSource, TrafficInformation.class);
			TableUtils.createTableIfNotExists(connectionSource, TrafficInformation.class);

		} catch (SQLException e) {
			System.err.println("Error creating database.");
		} 
	
	}
	
	public void close() {
		
		try {	
			connectionSource.close();
		} catch (SQLException e) {
			System.err.println("Error closing database.");
		}

	}
	
	public boolean registerUser(String username, String password) {
		
		QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();
		try {
			// Check for duplicate username
			if(queryBuilder.where().eq(User.USERNAME_FIELD_NAME, username).countOf() == 0) {
				User user = new User();
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail("teste@teste.com");
				user.setFacebookLogin(false);
				userDao.create(user);
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String loginUser(String username, String password) throws LoginException {
		
		QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();
		boolean loginValid = false;
		User user = null;
		
		System.out.println("PW: " + password);
		
		try {
			loginValid = queryBuilder.where().eq(User.USERNAME_FIELD_NAME, username).and().eq(User.PASSWORD_FIELD_NAME, password).countOf() == 1;
			if(loginValid) {
				user = userDao.queryForFirst(userDao.queryBuilder().where().eq(User.USERNAME_FIELD_NAME, username).prepare());
			}
		} catch (SQLException e) {
			System.err.println("Error executing SQL query.");
			e.printStackTrace();
		}
		
		if(loginValid && user != null) {
			String token = UUID.randomUUID().toString();
			AuthToken authToken = new AuthToken();
			authToken.setToken(token);
			authToken.setUser(user);
			
			try {
				authTokenDao.create(authToken);
				return token;
			} catch (SQLException e) {
				System.err.println("Error inserting AuthToken.");
				e.printStackTrace();
			}
		}
		
		throw new LoginException("Username or Password invalid.");
		
	}
	
	public void logoutUser(String authToken) {
		
		AuthToken obj = null;
		try {
			obj = authTokenDao.queryForFirst(authTokenDao.queryBuilder().where().eq(AuthToken.TOKEN_FIELD_NAME, authToken).prepare());
			authTokenDao.delete(obj);
		} catch (SQLException e) {
			System.err.println("Error deleting authorization token.");
			e.printStackTrace();
		}
		
	}
	
	public void addTrafficInformation(String authToken, String description, String location, int intensity) {
		TrafficInformation tf = new TrafficInformation();
		tf.setDescription(description);
		tf.setLocation(location);
		tf.setIntensity(intensity);
		
		User user = null;
		
		AuthToken authTokenObj = getAuthTokenByToken(authToken);
		
		if(authTokenObj != null) {
			user = authTokenObj.getUser();
			
			tf.setUser(user);
			
			try {
				trafficInformationDao.create(tf);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public List<TrafficInformation> getTrafficInformation() {
		
		try {
			List<TrafficInformation> result = trafficInformationDao.queryForAll();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

	public List<User> getAllUsers() {
		
		try {
			List<User> result = userDao.queryForAll();
			return result;
		} catch (SQLException e) {
			System.err.println("Error querying for users.");
		}
		
		return null;

	}
	
	public boolean checkAuthToken(String authToken) {
		try {
			return (authTokenDao.queryBuilder().where().eq(AuthToken.TOKEN_FIELD_NAME, authToken).countOf() == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public User getUserById(int id) {
		try {
			return userDao.queryBuilder().where().eq(User.ID_FIELD_NAME, id).queryForFirst();
		} catch(SQLException e) {
			System.err.println("Error querying for User.");
		}
		return null;
	}
	
	public void editUser(User user) {
		try {
			userDao.update(user);
		} catch (SQLException e) {
			System.err.println("Error updating user");
		}
	}
	
	public AuthToken getAuthTokenByToken(String authToken) {
		try {
			return authTokenDao.queryBuilder().where().eq(AuthToken.TOKEN_FIELD_NAME, authToken).queryForFirst();
		} catch(SQLException e) {
			System.err.println("Error querying for AuthToken.");
		}
		return null;
	}
	
	public void addUserFollow(UsersFollow userFollow) {
		try {
			usersFollowDao.create(userFollow);
		} catch (SQLException e) {
			System.err.println("Error inserting UsersFollow");
		}
	}
	
	public List<User> getUsersFollowing(int sourceId) {
		
		User user = getUserById(sourceId);
		List<User> following = null;
		try {
			List<UsersFollow> usersFollow = usersFollowDao.queryBuilder().where().eq("sourceUser_id", user.getId()).query();
			following = new ArrayList<User>();
			
			for(int i = 0; i < usersFollow.size(); i++) {
				following.add(usersFollow.get(i).getTarget());
			}
			
		} catch (SQLException e) {
			System.err.println("Could not query for following users.");
		}
		
		return following;
	}
	
	
}
