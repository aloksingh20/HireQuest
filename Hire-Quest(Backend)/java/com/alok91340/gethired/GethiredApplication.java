package com.alok91340.gethired;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.alok91340.gethired.utils.Constant;


import com.alok91340.gethired.entities.Role;
import com.alok91340.gethired.repository.RoleRepository;


@SpringBootApplication
@ComponentScan(basePackages = "com.alok91340.gethired")
public class GethiredApplication implements CommandLineRunner{

	@Autowired 
	RoleRepository roleRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(GethiredApplication.class, args);
		
		
		
	}
	
	@Override
	public void run(String... args) throws Exception {

		try {

			Role admin_role = new Role();
			admin_role.setId(Constant.ADMIN_USER );
			admin_role.setName("ROLE_ADMIN");

			Role user_role = new Role();
			user_role.setId(Constant.NORMAL_USER);
			user_role.setName("ROLE_USER");

			List<Role> roles = new ArrayList<>();

			roles.add(user_role);
			roles.add(admin_role);
			this.roleRepository.saveAll(roles);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
