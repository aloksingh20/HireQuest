/**
 * 
 */
package com.alok91340.gethired.entities;

import java.time.Instant;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author alokSingh
 *
 */

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Otp {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String otpCode;
	
	private String email;
	
	private Instant createdAt;

}
