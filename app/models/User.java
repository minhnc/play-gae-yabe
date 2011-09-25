package models;

import java.util.Date;

import siena.*;

public class User extends Model {
	
	@Id
	public Long id;
	
	public String email;
	public String fullname;
	public boolean isAdmin;
	public Date created;
	public Date modified;
	
	public User() {
		super();
	}
	
    public User(String email, String fullname, boolean isAdmin) {
        this.email = email;
        this.fullname = fullname;
        this.isAdmin = isAdmin;
        this.created = this.modified = new Date();
    }
    
    public static User findById(Long id) {
    	return Model.getByKey(User.class, id);
    }
    
    public static User findByEmail(String email) {
    	return all().filter("email", email).get();
    }
    
    public static int count() {
    	return all().count();
    }
 
    public String toString() {
        return email;
    }
    
    static Query<User> all() {
    	return Model.all(User.class);
    }

}
