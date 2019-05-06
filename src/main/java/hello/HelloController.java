package hello;

import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	@Autowired
	RedisOperationsSessionRepository redisRepo;
	
	@RequestMapping(value = "/", produces = "application/json")
	public Map<String, String> helloUser(Principal principal) {
		HashMap<String, String> result = new HashMap<>();
		result.put("username", principal.getName());
		return result;
	}
//
//	@RequestMapping("/logout")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void logout(HttpSession session) {
//		session.invalidate();
//	}
    
	@RequestMapping("idletime")
	public Map<String, String> getIdleTime(HttpServletRequest request) {
		String sessionId = request.getHeader(IAMConst.IAM_API_AUTH_HEADER_NAME);
		
		if (sessionId == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					logger.debug(cookie.getName() + " : " + cookie.getValue());
					if (cookie.getName().equals(IAMConst.IAM_COOKIE_NAME)) {
						sessionId = new String(Base64.getDecoder().decode(cookie.getValue()));
					}
				}
			}			
		}

		Map<Object, Object> entries = redisRepo.getSessionRedisOperations()
				.boundHashOps(IAMConst.SPRING_SESSION_REDIS_PREFIX + sessionId).entries();

		long now = System.currentTimeMillis();
		long lastTime = now;
		Object obj = entries.get(IAMConst.LAST_ACCESSED_ATTR);
		if ( obj != null ) {
			lastTime = (long) obj;
		}

		logger.debug("Session id = " + sessionId);
		logger.debug("getLastAccessedTime = " + (new Date(lastTime)));
		logger.debug("currentTimeMillis = " + (new Date(now)));
		logger.debug("IdleTime = " + (now - lastTime) / 1000);

		HashMap<String, String> result = new HashMap<>();
		result.put("IdleTime", "" + (now - lastTime) / 1000);
		return result;
	}

}
