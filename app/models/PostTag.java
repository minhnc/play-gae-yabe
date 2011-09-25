package models;

import java.util.ArrayList;
import java.util.List;

import siena.Column;
import siena.Id;
import siena.Index;
import siena.Model;
import siena.NotNull;
import siena.Query;

public class PostTag extends Model {

	@Id
	public Long id;
	
	@NotNull
	@Column("postId")
	@Index("post_id")
	public Post post;

	@NotNull
	@Column("tagId")
	@Index("tag_id")
	public Tag tag;
	
	public PostTag(Post post, Tag tag) {
		super();
		this.post = post;
		this.tag = tag;
	}
	
	public static List<Tag> findAllByPost(Post post) {
		List<PostTag> pTags = all().filter("post", post).fetch();
		List<Tag> tags = new ArrayList<Tag>();
    	
    	for(PostTag pt : pTags) {
    		tags.add(Tag.findById(pt.tag.id));
    	}
    	
    	return tags;
	}
	
	public static List<Post> findAllByTag(Tag tag) {
		List<PostTag> pTags = all().filter("tag", tag).fetch();
		List<Post> posts = new ArrayList<Post>();
    	
    	for(PostTag pt : pTags) {
    		posts.add(Post.findById(pt.post.id));
    	}
    	
    	return posts;
	}
	
	public static Query<PostTag> all() {
		return Model.all(PostTag.class);
	}

	public static void deleteAllByPost(Post post) {
		List<PostTag> pTags = all().filter("post", post).fetch();
		
		for(PostTag pt : pTags) {
    		pt.delete();
    	}
	}
	
	public String toString() {
		PostTag pt = Model.getByKey(PostTag.class, this.id);
		return Tag.findById(pt.tag.id).name;
	}
	
}
