package controllers;
 
import play.*;
import play.mvc.*;
import play.cache.Cache;
import play.data.validation.*;
 
import java.util.*;

import com.google.gson.Gson;
 
import models.*;
 
@With(GAESecure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.findByEmail(Security.connected());
            renderArgs.put("user", user.fullname);
            
            if ( Cache.get("userId") == null ) {
            	Cache.set("userId", user.id);
            }
        }
    }
 
    public static void index() {
    	User user = User.findById( (Long)Cache.get("userId") );
        List<Post> posts = Post.all().filter("author", user).fetch();
        render(posts);
    }

    public static void form(Long id) {
        if(id != null) {
            Post post = Post.findById(id);
            notFoundIfNull(post, "Hmm... Not Found.");

            render(post);
        }
        render();
    }
    
    public static void save(Long id, String title, String content, String tags) {
        Post post;
        if(id == null) {
            // Create post
            User author = User.findByEmail(Security.connected());
            post = new Post(author, title, content);
        } else {
            // Retrieve post
            post = Post.findById(id);
            post.title = title;
            post.content = content;
        }
        
        // Validate
        validation.valid(post);
        if(validation.hasErrors()) {
            render("@form", post);
        }
        // Save
        post.save();
        
        post.deleteTags();

        // Set tags list
        for(String t : tags.split(",")) {
            if(t.trim().length() > 0) {
                post.tagItWith(t.trim());
            }
        }
        
        index();
    }
    
    public static void addPhrase(Long id, String phrase) {
    	if (id != null) {
    		Post post = Post.findById(id);
    		notFoundIfNull(post, "Hmm... Not Found.");
    		
    		post.phrases.put(phrase.replaceAll(" ", "_"), new Phrase(phrase, phrase));
    		post.save();
    	}
    	
    	redirect("/admin/posts/" + id);
    }
    
}
