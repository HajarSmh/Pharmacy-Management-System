
package model;

import java.util.Objects;

public abstract class User {
    private int id;
    private String email;
    private String password;
    private String role;
    
    
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public User() {
    }

	public String getRole() {
		return role;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", role=" + role + "]";
	}

	public void setRole(String role) {
		this.role = role;
	}
	  public boolean login(String email, String password) {
	        return this.email.equals(email) && this.password.equals(password);
	    }
	  
	@Override
	public int hashCode() {
		return Objects.hash(email, id, password, role);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && id == other.id && Objects.equals(password, other.password)
				&& Objects.equals(role, other.role);
	}
	
	
	public User(int id, String email, String password, String role) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
		this.role = role;
	}
    
    
    
    
    
    
    
    
 
    
    
    
    
    
}
    