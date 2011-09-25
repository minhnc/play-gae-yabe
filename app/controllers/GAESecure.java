package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import play.Play;
import play.modules.gae.GAE;
import play.mvc.*;
import play.data.validation.*;
import play.libs.*;
import play.utils.*;

public class GAESecure extends Controller {

	public static void login() throws Throwable {
		GAESecurity.invoke("login");
    }
    
    public static void logout() throws Throwable {
    	GAESecurity.invoke("logout");
    }
    
    public static void authenticated() throws Throwable {
		GAESecurity.invoke("onAuthenticated");
    }
    
    @Before(unless={"login", "authenticated", "logout"})
    static void checkAccess() throws Throwable {
        // Authent
        if(!session.contains("username")) {
            session.put("url", "GET".equals(request.method) ? request.url : "/"); // seems a good default
            login();
        }
        // Checks
        Check check = getActionAnnotation(Check.class);
        if(check != null) {
            check(check);
        }
        check = getControllerInheritedAnnotation(Check.class);
        if(check != null) {
            check(check);
        }
    }

    private static void check(Check check) throws Throwable {
        for(String profile : check.value()) {
            boolean hasProfile = (Boolean)GAESecurity.invoke("check", profile);
            if(!hasProfile) {
                GAESecurity.invoke("onCheckFailed", profile);
            }
        }
    }

    public static class GAESecurity extends Controller {
    	
    	static void login() {
    	}
    	
    	static void logout() {
    	}
    	
    	/**
         * This method is called after a successful authentication.
         * You need to override this method if you with to perform specific actions (eg. Record the time the user signed in)
         */
        static void onAuthenticated() {
        }

        /**
         * This method checks that a profile is allowed to view this page/method. This method is called prior
         * to the method's controller annotated with the @Check method. 
         *
         * @param profile
         * @return true if you are allowed to execute this controller method.
         */
        static boolean check(String profile) {
            return true;
        }

        /**
         * This method returns the current connected username
         * @return
         */
        static String connected() {
            return session.get("username");
        }

        /**
         * Indicate if a user is currently connected
         * @return  true if the user is connected
         */
        static boolean isConnected() {
            return session.contains("username");
        }

         /**
         * This method is called before a user tries to sign off.
         * You need to override this method if you wish to perform specific actions (eg. Record the name of the user who signed off)
         */
        static void onDisconnect() {
        }

        /**
         * This method is called if a check does not succeed. By default it shows the not allowed page (the controller forbidden method).
         * @param profile
         */
        static void onCheckFailed(String profile) {
            forbidden();
        }

        private static Object invoke(String m, Object... args) throws Throwable {
            Class security = null;
            List<Class> classes = Play.classloader.getAssignableClasses(GAESecurity.class);
            if(classes.size() == 0) {
                security = GAESecurity.class;
            } else {
                security = classes.get(0);
            }
            try {
                return Java.invokeStaticOrParent(security, m, args);
            } catch(InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

    }

}
