package com.alok91340.gethired.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class HrProfile extends BaseEntity{
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String summary;
	
	private String linkedinProfile;
	
	private String companyName;
	
	private String companyUrl;
	
	private String jobTitle;
	
	private int yearOfExp;
	
	private boolean isVerified;
	
	private boolean isAvailable;
	
	private String jobType;
	
//	@OneToMany(mappedBy="hrProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
//	@JsonManagedReference
//	private Set<Skill> skills= new HashSet<>();
//	
//	@OneToMany(mappedBy="hrProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
//	@JsonManagedReference
//	private Set<Education> educations;
//	
//	@OneToMany(mappedBy="hrProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
//	@JsonManagedReference
//	private Set<Certificate> certificates;
//	
//	@OneToMany(mappedBy="hrProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
//	@JsonManagedReference
//	private Set<Language> languages;
//	
//	@OneToMany(mappedBy="hrProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
//	@JsonManagedReference
//	private Set<HrExperience> hrExperience;
//	
//	@OneToOne
//	@JsonManagedReference
//	private User user;

}
