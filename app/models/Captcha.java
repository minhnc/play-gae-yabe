package models;
 
import play.data.validation.Required;
import siena.Id;
import siena.Model;
import siena.Query;

public class Captcha extends Model {
 
	@Id
	public Long id;
	
    @Required
    public String code;
    
    public Captcha(String code) {
    	this.code = code;
    }
    
    static Query<Captcha> all() {
    	return Model.all(Captcha.class);
    }
    
}