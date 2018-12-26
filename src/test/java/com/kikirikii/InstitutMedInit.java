/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [InstitutMedInit.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 20.12.18 19:03
 */

package com.kikirikii;

import com.kikirikii.model.*;
import com.kikirikii.repos.SpaceRepository;
import com.kikirikii.services.SpaceService;
import com.kikirikii.services.UserService;
import com.kikirikii.storage.StorageProperties;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InstitutMedInit {
    private Logger logger = Logger.getLogger("InstitutMedInit");
    final private int firstname = 0, lastname = 1, username = 2, email = 3, gender = 4, city = 5, country = 6, aboutYou = 7, work = 8;
    final private long TEAM_SPACE_ID = 138L;
    final private long REFERENTEN_SPACE_ID = 135L;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private StorageProperties storageProperties;


    @Ignore
    @Test
    public void createTestUser() {
        createUsers("institutmed/test.csv", "team_profiles/thumbs",
                spaceService.getSpace(TEAM_SPACE_ID));
    }

    @Ignore
    @Test
    public void createTestSpaces() {
        createSpaces("institutmed/test.csv");
    }

    @Ignore
    @Test
    public void createTeamMembers() {
        createUsers("institutmed/team.csv", "team_profiles/thumbs",
                spaceService.getSpace(TEAM_SPACE_ID));
    }

    @Ignore
    @Test
    public void createTeamSpaces() {
        createSpaces("institutmed/team.csv");
    }

    @Ignore
    @Test
    public void createTeamPosts() {
        createPosts("institutmed/team.csv", "team_profiles/thumbs",
                spaceService.getSpace(TEAM_SPACE_ID));
    }

    @Ignore
    @Test
    public void deleteSpacePosts() {
        List<Post> posts = userService.getSpacePosts(TEAM_SPACE_ID);
        posts.forEach(post -> userService.deletePostById(post.getId()));
    }

    @Ignore
    @Test
    public void createReferenten() {
        createUsers("institutmed/referenten.csv", "referenten_profiles/thumbs",
                spaceService.getSpace(REFERENTEN_SPACE_ID));
    }

    @Test
    public void createReferentenPosts() {
        createPosts("institutmed/referenten.csv", "referenten_profiles/thumbs",
                spaceService.getSpace(REFERENTEN_SPACE_ID));
    }

    @Ignore
    @Test
    public void createReferentenSpaces() {
        createSpaces("institutmed/referenten.csv");
    }

    @Ignore
    @Test
    @Transactional
    public void createPartner() {
        Optional<Space> space = spaceRepository.findById(135L); // referenten space (create space instead)
        space.ifPresent(s -> createUsers("institutmed/partner.csv",
                "partner_profiles/thumbs",
                s));
    }

    private void createUsers(String filename, String thumbspath, Space space) {
        List<String> team = PersistenceInit.Loader.load(filename);
        team.forEach(member -> {
            try {
                String[] attrs = member.split("§");
                String avatar = copyAvatar(storageProperties.getLocation(), thumbspath, attrs[username].trim());

                User user = createUser(attrs[email].trim(), attrs[username].trim(), attrs[firstname].trim(),
                        attrs[lastname].trim(), avatar, attrs[gender].trim(), attrs[aboutYou].trim(),
                        attrs[work].trim(), attrs[city].trim(), attrs[country].trim());

                user = userService.createUser(user);
                spaceService.addMember(space, user, user, "MEMBER");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createSpaces(String filename) {
        List<String> team = PersistenceInit.Loader.load(filename);
        team.forEach(member -> {
            try {
                String[] attrs = member.split("§");

                User user = userService.getUser(attrs[username]);

                spaceRepository.save(Space.of(user,
                        user.getUsername() + "'s Space",
                        "This is a personal place for me and friends",
                        Space.Type.HOME));
                spaceRepository.save(Space.of(user,
                        user.getUsername() + "'s Global Space",
                        "This is a place for me, friends and followers",
                        Space.Type.GLOBAL));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void createPosts(String filename, String thumbspath, Space space) {
        List<String> team = PersistenceInit.Loader.load(filename);
        team.stream().map(member -> member.split("§"))
                .sorted((attrs1, attrs2) -> attrs2[lastname].compareTo(attrs1[lastname]))
                .forEach(attrs -> {
                    try {
                        Optional<User> user = userService.findByUsername(attrs[username]);
                        if (user.isPresent()) {
                            String fullname = "<h4>" + attrs[firstname] + " " + attrs[lastname] + "</h4>";
                            String mediapath = copyMedia(storageProperties.getLocation(), thumbspath, attrs[username]);
                            userService.addPost(space, user.get(), attrs[aboutYou], fullname + attrs[work],
                                    Media.of(mediapath, Media.Type.PICTURE));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private User createUser(String email, String username, String firstname, String lastname, String avatar,
                            String gender, String aboutYou, String work, String city, String country) {

        return User.of((email == null || email.equals("")) ? username + "@institutmed.de" : email,
                username, firstname, lastname, "password", avatar)
                .setUserData(UserData.of(null,
                        LocalDate.of(1970, 1, 1),
                        getGender(gender),
                        UserData.Marital.NONE,
                        UserData.Interest.NONE,
                        aboutYou, null, null, work, null, null,
                        Address.of(null, null, null, null, city, country)
                ));
    }

    private UserData.Gender getGender(String gender) {
        return gender != null ? gender.equals("M") ? UserData.Gender.MALE : gender.equals("F") ? UserData.Gender.FEMALE :
                UserData.Gender.NONE : UserData.Gender.NONE;
    }

    private String copyAvatar(StorageProperties.Location location, String thumbspath, String username) throws IOException {

        Path root = Paths.get(location.getRoot(), username, location.getProfile());
        if (Files.notExists(root)) {
            Files.createDirectories(root);
        }

        Path source = Paths.get(location.getThumbs(), thumbspath).resolve(username + ".jpg");
        Path target = root.resolve(source.getFileName());

        if (Files.notExists(target)) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return Paths.get(username, location.getProfile()).
                resolve(username + ".jpg").toString();
    }

    private String copyMedia(StorageProperties.Location location, String thumbspath, String username)
            throws IOException {
        Path root = Paths.get(location.getRoot(), username);
        if (Files.notExists(root)) {
            Files.createDirectories(root);
        }

        Path source = Paths.get(location.getThumbs(), thumbspath).resolve(username + ".jpg");
        Path target = root.resolve(source.getFileName());

        if (Files.notExists(target)) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return Paths.get(username).resolve(username + ".jpg").toString();
    }

}
