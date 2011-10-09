package models;
 
import java.util.*;

import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.appengine.repackaged.org.joda.time.DateTime;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.Required;

import siena.Filter;
import siena.Id;
import siena.Index;
import siena.Max;
import siena.Model;
import siena.NotNull;
import siena.Query;
import siena.embed.Embedded;
import siena.embed.EmbeddedMap;

public class Post extends Model {
 
	@Id
	public Long id;
	
	@Required
    public String title;
    
    @Required @siena.DateTime
    public Date postedAt;
    
    @Lob
    @Required
    @Max(50000)
    public String content;
    
    @Required @Index("user_index")
    public User author;

    @Index("post_index")
    public List<Comment> comments;
    
    @Filter("post")
    public Query<PostTag> postTags;
    
    @Embedded
    public Map<String, Phrase> phrases;
    
    public static Post findById(Long id) {
    	return Model.getByKey(Post.class, id);
    }
    
    public Post(User author, String title, String content) { 
        this.comments = new ArrayList<Comment>();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
        this.phrases = new HashMap<String, Phrase>();
    }

    public Post addComment(String author, String content) {
        Comment newComment = new Comment(this, author, content);
        newComment.save();
        
        comments.add(newComment);
        return this;
    }
    
    public Post previous() {
        return all().filter("id <", id)
        			.order("id")
        			.get();
    }

    public Post next() {
    	return all().filter("id >", id)
					.order("id")
					.get();
    }
    
    public Post tagItWith(String name) {
    	PostTag pt = new PostTag(this, Tag.findOrCreateByName(name));
    	pt.save();
    	
        return this;
    }
    
    public static List<Post> findTaggedWith(String tag) {
        return PostTag.findAllByTag(Tag.findOrCreateByName(tag));
    }
    
    public List<Comment> getComments() {
    	if ( comments == null )
    		comments = Model.all(Comment.class).filter("post", this).fetch();
    	
    	return comments;
    }
    
    public List<Tag> getTags() {
    	return PostTag.findAllByPost(this);
    }
    
    public void deleteTags() {
    	PostTag.deleteAllByPost(this);
    }
    
    public User getAuthor() {
    	return User.findById(this.author.id);
    }
    
    public String toString() {
        return title;
    }
    
    public static Query<Post> all() {
    	return Model.all(Post.class);
    }
 
}
