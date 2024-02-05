package com.alok91340.gethired.utils;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
public @interface isAuthenticatedAsAdminOrUser {
}
