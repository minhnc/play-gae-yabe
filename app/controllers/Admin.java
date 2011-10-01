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

            String phrases = new Gson().toJson(post.phrases);
            render(post, phrases);
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
        
        // Phrase
        post.phrases.put( "governing body", new Phrase("governing body", "Cơ quan chủ quản") );
        post.phrases.put( "concession", new Phrase("concession", "Nhường quyền") );
        
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
    
}
