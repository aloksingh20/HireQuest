/**
 * 
 */
package com.alok91340.gethired.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author aloksingh
 *
 */

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Pdf {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String fileName;
	private String fileSize;
	
	@Column(length = 65555)
	private byte[] fileData;
	private String timeStamp;
	
	@ManyToOne
	@JsonBackReference
	private UserProfile userProfile;
}
