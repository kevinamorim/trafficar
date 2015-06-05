package sdis.trafficar.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class MyDatabaseTest {
	
	ConnectionSource connectionSource;
	
	Dao<User, String> userDao;
	Dao<AuthToken, String> authTokenDao;
	Dao<UsersFollow, String> usersFollowDao;
	Dao<TrafficInformation, String> trafficInformationDao;
	Dao<UserTrafficInfoFeedback, String> userTrafficInfoFeedbackDao;

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
			
			userTrafficInfoFeedbackDao = DaoManager.createDao(connectionSource, UserTrafficInfoFeedback.class);
			TableUtils.createTableIfNotExists(connectionSource, UserTrafficInfoFeedback.class);

		} catch (SQLException e) {
			System.err.println("Error creating database: ");
			e.printStackTrace();
		} 
	
	}
	
	public void close() {
		
		try {	
			connectionSource.close();
		} catch (SQLException e) {
			System.err.println("Error closing database.");
		}

	}
	
	public boolean registerUser(String username, String password, String email, String name, String location, boolean facebook) {
		
		QueryBuilder<User, String> queryBuilder = userDao.queryBuilder();
		try {
			// Check for duplicate username
			if(queryBuilder.where().eq(User.USERNAME_FIELD_NAME, username).countOf() == 0) {
				User user = new User();
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail(email);
				user.setName(name);
				user.setLocation(location);
				user.setFacebookLogin(facebook);
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
	
	public void addTrafficInformation(String authToken, String description, String location, String category, int intensity) {
		TrafficInformation tf = new TrafficInformation();
		tf.setDescription(description);
		tf.setLocation(location);
		tf.setCategory(category);
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

	public List<TrafficInformation> getTrafficInformationNotOf(int userId) {
		try {
			return trafficInformationDao.queryBuilder().where().not().eq(TrafficInformation.USER_ID_FIELD_NAME, userId).query();
		} catch(SQLException e) {
			System.err.println("Error querying for User.");
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
	
	public TrafficInformation getInfoById(int id) {
		try {
			return trafficInformationDao.queryBuilder().where().eq(TrafficInformation.ID_FIELD_NAME, id).queryForFirst();
		} catch(SQLException e) {
			System.err.println("Error querying for TrafficInformation.");
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
	
	public void unfollowUser(int sourceId, int targetId) {
		
		DeleteBuilder<UsersFollow, String> builder = usersFollowDao.deleteBuilder();
		try {
			builder.where().eq("sourceUser_id", sourceId).and().eq("targetUser_id", targetId);
			usersFollowDao.delete(builder.prepare());
		} catch (SQLException e) {
			System.err.println("Error deleting user follow.");
		}

	}

	public void giveFeedbackToTrafficInfo(TrafficInformation info, User user, boolean positive) {
		
		if(info != null && user != null) {
			UserTrafficInfoFeedback feedback = new UserTrafficInfoFeedback(user, info, positive);
			info.addFeedback(feedback);
			try {
				trafficInformationDao.update(info);
			} catch (SQLException e) {
				System.err.println("Error adding feedback to traffic information");
			}
		}
	}
	
	public ArrayList<TrafficInformation> getTrafficInfoFromFollowing(int id) {
		
		ArrayList<TrafficInformation> trafficInfo = new ArrayList<TrafficInformation>();
		System.out.println("Entrou...");
		try {
			List<UsersFollow> usersFollow = usersFollowDao.queryBuilder().where().eq("sourceUser_id", id).query();
			System.out.println("Size1: " + usersFollow.size());
			
			
			List<TrafficInformation> all = getTrafficInformation();
			System.out.println("Size2: " + all.size());
			for(int i = 0; i < all.size(); i++) {
				if(anyFollowingInArray(usersFollow, all.get(i).getUser().getId())) {
					trafficInfo.add(all.get(i));
				}
			}
			
		} catch (SQLException e) {
			System.err.println("Error getting traffic information...");
		}
		
		System.out.println("Size: " + trafficInfo.size());
		return trafficInfo;
		
	}
	
	private boolean anyFollowingInArray(List<UsersFollow> list, int id) {
		for(int i = 0; i < list.size(); i++)
			if(list.get(i).getTarget().getId() == id) return true;	
		return false;
	}
	
	public User checkIfUserExists(String username) {
		
		User user = null;
		try {
			user = userDao.queryBuilder().where().eq(User.USERNAME_FIELD_NAME, username).queryForFirst();
		} catch (SQLException e) {
			System.err.println("Error getting user...");
		}
		
		return user;
	}
	
	public void addAuthToken(String token, User user) {
		AuthToken authToken = new AuthToken();
		authToken.setToken(token);
		authToken.setUser(user);
		
		try {
			authTokenDao.create(authToken);
		} catch (SQLException e) {
			System.err.println("Error inserting AuthToken.");
		}
	}
}
