/**
 * 
 */
package com.alok91340.gethired.entities;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author alok91340
 *
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String username;
	
	private String name;
	
	private String email;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	private String headline;
	
//	
//	@OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
//	@JsonManagedReference
	private String image;
	
	private String birthdate;
	private String gender;
	
	private String currentOccupation;
	
	private boolean status;
	
	private String phone;
	
	private int isRecuriter;
	
	@OneToOne(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
	@JsonManagedReference
	private Address address;
	
	@JsonIgnore
	private String fcmToken;
	
	
	// relation with role
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return null;
	}

    @JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
				
		return false;
	}

    @JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		
		return false;
	}

    @JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {

		return false;
	}

    @JsonIgnore
	@Override
	public boolean isEnabled() {

		return false;
	}

}
