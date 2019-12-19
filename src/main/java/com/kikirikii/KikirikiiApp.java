/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [KikirikiiApp.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 26.05.18 19:11
 */

package com.kikirikii;

import com.kikirikii.model.Address;
import com.kikirikii.model.Role;
import com.kikirikii.model.User;
import com.kikirikii.model.UserData;
import com.kikirikii.services.UserService;
import com.kikirikii.storage.StorageService;
import com.kikirikii.storage.SuperUserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class KikirikiiApp {
	private static Logger logger = Logger.getLogger("KikirikiiApp");

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;

	@Value("${client.config.theme}")
	private String theme;

	@Autowired
	private SuperUserProperties superUserProps;

	public static void main(String[] args) {
		SpringApplication.run(KikirikiiApp.class, args);
	}

	@Bean
	CommandLineRunner initialize(StorageService storageService) {

		return (args) -> {
			System.out.println("CREATE_SUPERUSER " + env.getProperty("CREATE_SUPERUSER_DEFAULT"));
			System.out.println("NAME " + env.getProperty("client.config.name"));
			System.out.println("CLIENT " + env.getProperty("client.config.superuser"));
			System.out.println("DATASOURCE " + env.getProperty("spring.datasource.url"));
			System.out.println("DATASOURCE_USERNAME " + env.getProperty("spring.datasource.username"));
			System.out.println("DATASOURCE_PASSWORD " + env.getProperty("spring.datasource.password"));
			System.out.println("THEME " + this.theme);
			System.out.println("EMAIL " + superUserProps.getEmail());

			Arrays.stream(args).forEach(System.out::println);

			if(env.getProperty("CREATE_SUPERUSER_DEFAULT") != null) {
				try {
					createSuperUser();
				} catch(Exception e) {
					logger.log(Level.SEVERE, "Cannot create default superuser", e);
				}
			}

			if(env.getProperty("DELETE_UPLOAD_STORAGE") != null) {
				storageService.deleteAll();
			}

			storageService.init();
		};
	}


	private void createSuperUser() {
		if (!userService.findByUsername(superUserProps.getUsername()).isPresent()) {
			User user = User.of(superUserProps.getEmail(),
					superUserProps.getUsername(),
					superUserProps.getFirstname(),
					superUserProps.getLastname(),
					superUserProps.getPassword(),
					superUserProps.getAvatar(),
					Role.asArray(Role.Type.USER))
					.setUserData(UserData.of(null, LocalDate.of(1970, 1, 1),
							UserData.Gender.NONE,
							UserData.Marital.NONE,
							UserData.Interest.NONE,
							superUserProps.getAboutYou(), null, null,
							superUserProps.getWork(), null, null,
							superUserProps.getWeb(),
							Address.of(null, null, null, null,
									superUserProps.getCity(),
									superUserProps.getCountry())
					));

			userService.addRole(user, Role.of(Role.Type.SUPERUSER));
			userService.createUser(user);
			userService.createPublicSpaces(user);
		}
	}
}
