/**
 * 
 */
package com.alok91340.gethired.dto;


import lombok.Data;

/**
 * @author alok91340
 *
 */
@Data
public class LoginDto {
    private String username;
    private String password;
    private String fcmToken;
    private String googleIdToken;
}