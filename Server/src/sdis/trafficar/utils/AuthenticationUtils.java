package sdis.trafficar.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.json.MyJSON;

public class AuthenticationUtils {

	private static final String HASHING = "SHA-256";
	private static final String CHARSET = "UTF-8";
	
	public static String unauthorizedAccess() {
		return (new MyJSON(false, "Unauthorized access!!!")).toString();
	}
	
	public static boolean authorize(MyDatabaseTest db, String authToken) {
		boolean authorized = false;
		
		if(authToken != null) {
			authorized = db.checkAuthToken(authToken);
		}
		
		return authorized;
	}
	
	// Credits to: http://stackoverflow.com/questions/5531455/how-to-encode-some-string-with-sha256-in-java
	public static String hash(String base) {
			try {
				MessageDigest digest = MessageDigest.getInstance(HASHING);
				byte[] hash = digest.digest(base.getBytes(CHARSET));
				StringBuffer hexString = new StringBuffer();

				for (int i = 0; i < hash.length; i++) {
					String hex = Integer.toHexString(0xff & hash[i]);
					if(hex.length() == 1) hexString.append('0');
					hexString.append(hex);
				}

				return hexString.toString();

			} catch (NoSuchAlgorithmException e) {
				System.err.println("Hashing algorithm not found: " + HASHING);
			} catch (UnsupportedEncodingException e) {
				System.err.println("Encoding unsopported: " + CHARSET);
			}

			return "";
	}
	
	public static String generateRandomPassword() {
		return UUID.randomUUID().toString();
	}
}
