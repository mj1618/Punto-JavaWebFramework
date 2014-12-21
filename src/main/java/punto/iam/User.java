package punto.iam;

import punto.util.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class User {

	String username;
    String passwordHash;
    List<String> roles=new ArrayList<>();

    public User(){}

    public boolean hasRole(String role){
        return roles.contains(role);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void addRole(String role){
        roles.add(role);
    }

    public User(String username) {
		super();
		this.username = username;
	}
    public User(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        super();
        this.username = username;
        this.passwordHash = PasswordHash.createHash(password);
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


    public boolean isCMSManager() {
        return true;
    }

    public String toString(){
        return Utils.toJson(this);
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
