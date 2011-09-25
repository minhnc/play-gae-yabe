package models;
 
import java.util.*;

import javax.persistence.Lob;

import play.data.validation.MaxSize;
import play.data.validation.Required;
 
import siena.Column;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;

public class Comment extends Model {
 
	@Id
	public Long id;
	
    @Required
    public String author;
    
    @Required
    public Date postedAt;
     
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @Required @Index("post_index") @Column("postId")
    public Post post;
    
    public Comment(Post post, String author, String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }
    
    public Post getPost() {
    	return Post.findById(this.post.id);
    }
    
    public String toString() {
    	if ( content == null )
    		return "";
    	
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
 
}