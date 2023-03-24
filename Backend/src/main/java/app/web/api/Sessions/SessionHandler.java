package app.web.api.Sessions;

import app.web.api.Users.User;
import app.web.api.Users.UserHandler;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SessionHandler {
	private static final Duration sessionExpire = Duration.ofMinutes(5);
	private static Cache<String, String> activeSessions = CacheBuilder.newBuilder().expireAfterWrite(sessionExpire).build();

	public static String getSession(String userId){
		return activeSessions.getIfPresent(userId);
	}

	public static String setSession(String userId){
		UUID uuid = UUID.randomUUID();
		activeSessions.put(userId, uuid.toString());
		return uuid.toString();
	}

	public static boolean verifySession(String userId, String token){
		User user = UserHandler.getUser(Long.parseLong(userId));

		if(user != null){
			return Objects.equals(activeSessions.getIfPresent(user.getUserId().toString()), token);
		}

		return false;
	}

	@PostMapping("/login")
	public ResponseEntity userToken(@RequestBody Map<String, String> payload){
		String username = payload.getOrDefault("username", null);
		String pass = payload.getOrDefault("password", null);
		User user = UserHandler.getAllUsers().stream().filter(s -> s.getUsername().equalsIgnoreCase(username)).findAny().orElse(null);

		if(user != null && Objects.equals(pass, user.getPassword())){
			setSession(user.getUserId().toString());
			return ResponseEntity.ok(Map.of("token", getSession(user.getUserId().toString()),
					      "userId", user.getUserId().toString(),
					      "expire", (System.currentTimeMillis() + sessionExpire.toMillis())));
		}

		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/login/verify")
	public ResponseEntity verifyToken(@RequestBody Map<String, String> payload){
		String token = payload.getOrDefault("token", null);
		String userId = payload.getOrDefault("userId", null);
		boolean status = verifySession(userId, token);

		if(status){
			activeSessions.put(userId, token);
			return ResponseEntity.ok(status);
		}

		return ResponseEntity.badRequest().build();
	}
}
