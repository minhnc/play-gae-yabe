package controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.gson.Gson;

import models.Captcha;
import models.Post;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Files;
import play.libs.IO;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Controller;
import play.vfs.VirtualFile;

public class Application extends Controller {

	@Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
        
        // This function will be called one time on DEV mode
        generateCaptcha(10, 180, 50, "#E4EAFD", 6);
    }
 
	public static void index() {
        Post frontPost = Post.all().order("-postedAt").get();
        List<Post> olderPosts = Post.all().order("-postedAt").fetch(10, 1);
        
        render(frontPost, olderPosts);
    }
    
    public static void show(Long id) {
        Post post = Post.findById(id);
        notFoundIfNull(post, "Hmm... Not Found.");
        
        String randomID = Codec.UUID();
        String phrases = new Gson().toJson(post.phrases.values());
        render(post, randomID, phrases);
    }
    
    public static void postComment(
            Long postId, 
            @Required(message="Author is required") String author, 
            @Required(message="A message is required.") String content, 
            @Required(message="Please type the code") String code, 
            String randomID) 
    {
        Post post = Post.findById(postId);
        if(!Play.id.equals("test")) {
            validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
        }
        if(validation.hasErrors()) {
            render("Application/show.html", post, randomID);
        }
        post.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        Cache.delete(randomID);
        show(postId);
    }
    
    /**
     * Captcha from Datastore - For workaround on GAE
     * @param id
     */
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha(180, 50);
        String code = captcha.getText("#E4EAFD", 6);
                
//        VirtualFile appRoot = VirtualFile.open(Play.applicationPath);
//        if( !appRoot.child("public/images/captcha").exists() ) {
//        	// TODO makedir
//        }
//		System.out.println(Play.applicationPath.getAbsolutePath() + appRoot.child("public/images/captcha").relativePath());
//		IO.write(captcha, new File(Play.applicationPath.getAbsolutePath() + appRoot.child("public/images/captcha").relativePath() + "/" + code + ".png"));
		
        Cache.set(id, code, "30mn");
        renderBinary(captcha);
        
    }
    
    private static void generateCaptcha(int total, int width, int height, String color, int length) {
    	
    	Images.Captcha captcha;
    	String code;
    	Captcha c;
    	String path;
    	
    	for(int i = 0; i < total; i++) {
    		captcha = Images.captcha(width, height);
            code = captcha.getText(color, length);

            path = "";
            
            c = new Captcha(code);
            c.save();
    	}
	}
    
    public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }
    
}