package controllers;

import play.modules.gae.GAE;
import models.*;

public class Security extends GAESecure.GAESecurity {

	static void login() {
		GAE.login("Security.authenticated");
	}
	
	static void logout() {
		GAE.logout("Application.index");
	}
	
	static void onAuthenticated() {
		if ( !GAE.isLoggedIn() )
			forbidden();
		
		com.google.appengine.api.users.User user = GAE.getUser();
		String email = user.getEmail();
		
		User u = User.findByEmail(email);
		if ( u == null ) {
			u = new User(email, user.getNickname(), GAE.isAdmin());
			u.insert();
		}
		
		// Mark user as connected
        session.put("username", email);
        
        // Redirect to the original URL (or /)
        String url = session.get("url");
        if(url == null) {
            url = "/";
        }
        redirect(url);
    }
	
    static boolean check(String profile) {
        if("admin".equals(profile)) {
        	return GAE.isAdmin();
        }
        return false;
    }
    
}

