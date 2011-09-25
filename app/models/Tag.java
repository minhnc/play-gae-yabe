package models;
 
import java.util.*;
 
import play.db.jpa.*;
import play.data.validation.*;
import siena.Id;
import siena.Model;
import siena.Query;
 
public class Tag extends Model implements Comparable<Tag> {
 
	@Id
	public Long id;
	
    @Required
    public String name;
    
    private Tag(String name) {
        this.name = name;
    }
    
    public static Tag findById(Long id) {
    	return Model.getByKey(Tag.class, id);
    }
    
    public static Tag findOrCreateByName(String name) {
    	Tag tag = all().filter("name", name).get();
    	
    	if ( tag == null ) {
    		tag = new Tag(name);
    		tag.save();
    	}
    	
        return tag;
    }
    
    public String toString() {
        return name;
    }
    
    public int compareTo(Tag otherTag) {
        return name.compareTo(otherTag.name);
    }
    
    public static Query<Tag> all() {
    	return Model.all(Tag.class);
    }
 
}