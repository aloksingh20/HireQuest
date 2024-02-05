/**
 * 
 */
package com.alok91340.gethired.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author alok91340
 *
 */
@Entity
@Table(name="user_profile")
@Setter
@Getter
@NoArgsConstructor
public class UserProfile extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String about;
	
	
	
	private List<String> skills= new ArrayList<>();
	
	
	private List<String> languages;
	
	
	private List<String> hobbies=new ArrayList<>();
	
	
	@OneToOne
	@JsonManagedReference
	private User user;
	
}
