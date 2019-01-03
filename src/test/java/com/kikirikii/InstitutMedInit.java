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

/**
 * Collection of quick hacks for initializing the kikirikii database
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class InstitutMedInit {
    private Logger logger = Logger.getLogger("InstitutMedInit");
    final private long TEAM_SPACE_ID = 138L;
    final private long REFERENTEN_SPACE_ID = 135L;
    final private long WK19_SPACE_ID = 127L;
    final private long PARTNER_AUSTELLER_SPACE_ID = 139L;
    final private String SUPERUSER = "christine.herrera";

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private StorageProperties storageProperties;


    @Test
    public void createMain() {

        /* step 1 - create superuser */
        createSuperUser("institutmed/superuser.csv", "team_profiles/thumbs")
                .ifPresent(user -> {
                    createGenericSpaces(user, "institutmed/spaces.csv", "spaces/cover");
                });

        /* step 2 - create team users and join team space */
        spaceService.findBySpacename("Team").ifPresent(space -> {
            createUsers("institutmed/team.csv", "team_profiles/thumbs", space);
        });

        /* step 3 - create referenten and join to referenten space */
        spaceService.findBySpacename("Referenten & Autoren").ifPresent(space -> {
            createUsers("institutmed/referenten.csv", "referenten_profiles/thumbs",
                    space);
        });

        /* step 4 - create partner and join to partner space */
        spaceService.findBySpacename("Partner & Austeller").ifPresent(space -> {
            createPartners("institutmed/partner.csv", "partner_profiles/thumbs",
                    "partner_profiles/cover", space);
        });
    }

    @Test
    public void createPosts() {
        /* step 1 - create team posts */
        spaceService.findBySpacename("Team").ifPresent(space -> {
            createUserPosts("institutmed/team.csv", "team_profiles/cover",
                    space);
        });

        /* step 2 - create referenten posts */
        spaceService.findBySpacename("Referenten & Autoren").ifPresent(space -> {
            createUserPosts("institutmed/referenten.csv", "referenten_profiles/cover",
                    space);
        });

        /* step 3 - create partner posts */
        spaceService.findBySpacename("Partner & Austeller").ifPresent(space -> {
            createPartnerPosts("institutmed/partner.csv", "partner_profiles/thumbs", space);
        });

        /* step 4 - create weltkongress 2019 posts */
        spaceService.findBySpacename("Weltkongress 2019").ifPresent(space -> {
            createSpacePosts("institutmed/wk19.csv", "weltkongress-2019/cover",
                    "Vortrag", space);
        });

        /* step 5 - create praxis posts */
        spaceService.findBySpacename("Praxis Seminar").ifPresent(space -> {
            createSpacePosts("institutmed/praxis19.csv", "praxis-seminar-2019/cover",
                    "Praxis Heilworkshop", space);
        });
    }

    @Ignore
    @Test
    public void createTeamMembers() {
        createUsers("institutmed/team.csv", "team_profiles/thumbs",
                spaceService.getSpace(TEAM_SPACE_ID));
    }

    @Test
    public void createUserSpaces() {
        createPublicSpaces("institutmed/team.csv");
    }

    @Ignore
    @Test
    public void createTestUser() {
        createUsers("institutmed/test.csv", "team_profiles/thumbs",
                spaceService.getSpace(TEAM_SPACE_ID));
    }

    @Ignore
    @Test
    public void createTeamPosts() {
        createUserPosts("institutmed/team.csv", "team_profiles/thumbs",
                spaceService.getSpace(121L));
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

    @Ignore
    @Test
    public void createReferentenPosts() {
        createUserPosts("institutmed/referenten.csv", "referenten_profiles/thumbs",
                spaceService.getSpace(REFERENTEN_SPACE_ID));
    }

    @Ignore
    @Test
    public void createWK19Posts() {
        createSpacePosts("institutmed/wk19.csv", "weltkongress-2019/cover",
                "Vortrag", spaceService.getSpace(WK19_SPACE_ID));
    }

    @Ignore
    @Test
    public void joinWK19Users() {
        joinWK19Space("institutmed/wk19.csv", spaceService.getSpace(WK19_SPACE_ID));
    }

    @Ignore
    @Test
    public void createPraxis19Posts() {
        User user = userService.getUser("julia.jobs");
        Space space = spaceService.createGenericSpace(user,
                "Praxis Seminar",
                "PRAXIS-SEMINAR ETHNOMEDIZIN. Von den Ethnotherapien zur Ganzheitsmedizin.",
                "PUBLIC");
        createSpacePosts("institutmed/praxis19.csv", "praxis-seminar-2019/cover",
                "Praxis Heilworkshop", space);
    }

    @Test
    public void dummyTester() {
//        createPartners("institutmed/partner.csv", "partner_profiles/thumbs",
//                "partner_profiles/cover", spaceService.getSpace(PARTNER_AUSTELLER_SPACE_ID));

//        deletePartners("institutmed/partner.csv", spaceService.getSpace(138L));
//        verifyPartnerCover("institutmed/partner.csv", "partner_profiles/cover");
        userService.findByUsername(SUPERUSER).ifPresent(user -> {
            createGenericSpaces(user, "institutmed/spaces.csv", "spaces/cover");
        });

    }

    @Ignore
    @Test
    public void createReferentenSpaces() {
        createPublicSpaces("institutmed/referenten.csv");
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

    private void createGenericSpaces(User user, String filename, String sourcepath) {
        int pos = 0, type = 1, name = 2, icon = 3, description = 4, start_date = 5, web = 6, general_information = 7,
                key_dates = 8, city = 9, venue = 10, travel_information = 11, tickets = 12, dates = 13;

        List<String> spaces = PersistenceInit.Loader.load(filename);

        spaces.stream().map(line -> line.split("§"))
                .filter(attrs -> attrs[type].equals("G") || attrs[type].equals("E"))
                .forEach(attrs -> {
                    try {
                        Space space = spaceService.createSpaceAndJoin(user, getType(attrs[type]), attrs[name],
                                getIcon(attrs[icon]), null, attrs[description], "PUBLIC",
                                SpaceData.of(null, null, null, attrs[general_information],
                                        attrs[venue], attrs[travel_information], attrs[venue], attrs[city],
                                        null, null, attrs[key_dates], attrs[tickets],
                                        attrs[dates]));

                        String targetpath = user.getUsername() + "/generic/" + space.getId();
                        String cover = copyCover(sourcepath, targetpath, asCover(attrs[name]));
                        spaceService.updateCoverPath(space, cover);

                        System.out.println(attrs[pos] + " " + attrs[name] + " " + cover);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private String getType(String type) {
        return type.equals("E") ? "EVENT" : "GENERIC";
    }

    private String getIcon(String icon) {
        return icon == null || icon.equals("") ? null : icon.trim();
    }

    String asCover(String name) {  // some convention for cover file names
        return name.chars().mapToObj(ch -> ch == ' ' ? "-" : Character.toLowerCase((char)ch) + "")
                .reduce("", (l, r) -> l + r);
    }

    private Optional<User> createSuperUser(String filename, String thumbspath) {
        final int firstname = 0, lastname = 1, username = 2, email = 3, gender = 4, city = 5, country = 6, aboutYou = 7, work = 8;

        List<String> team = PersistenceInit.Loader.load(filename);
        try {
            String[] attrs = team.get(0).split("§");
            String avatar = copyAvatar(storageProperties.getLocation(), thumbspath, attrs[username].trim());

            User user = populateUser(attrs[email].trim(), attrs[username].trim(), attrs[firstname].trim(),
                    attrs[lastname].trim(), avatar, attrs[gender].trim(), attrs[aboutYou].trim(),
                    attrs[work].trim(), null, attrs[city].trim(), attrs[country].trim());

            user = userService.createUser(user);
            userService.createPublicSpaces(user);

            return Optional.of(user);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void createUsers(String filename, String thumbspath, Space space) {
        final int firstname = 0, lastname = 1, username = 2, email = 3, gender = 4, city = 5, country = 6, aboutYou = 7, work = 8;

        List<String> team = PersistenceInit.Loader.load(filename);
        int[] count = {0};
        team.stream().map(line -> line.split("§")).forEach(attrs -> {
            try {
                String avatar = copyAvatar(storageProperties.getLocation(), thumbspath, attrs[username].trim());

                User user = populateUser(attrs[email].trim(), attrs[username].trim(), attrs[firstname].trim(),
                        attrs[lastname].trim(), avatar, attrs[gender].trim(), attrs[aboutYou].trim(),
                        attrs[work].trim(), null, attrs[city].trim(), attrs[country].trim());

                user = userService.createUser(user);
                userService.createPublicSpaces(user);

                if (space != null) {
                    spaceService.addMember(space, user, user, "MEMBER");
                }

                System.out.println(++count[0] + " " + attrs[username]);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Deprecated
    private void createPublicSpaces(String filename) {
        final int firstname = 0, lastname = 1, username = 2, email = 3, gender = 4, city = 5, country = 6, aboutYou = 7, work = 8;

        List<String> team = PersistenceInit.Loader.load(filename);
        team.forEach(member -> {
            try {
                String[] attrs = member.split("§");

                userService.findByUsername(attrs[username]).ifPresent(user -> {
                    userService.createPublicSpaces(user);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void createUserPosts(String filename, String thumbspath, Space space) {
        final int firstname = 0, lastname = 1, username = 2, email = 3, gender = 4, city = 5, country = 6, aboutYou = 7, work = 8;
        int[] count = {0};
        List<String> team = PersistenceInit.Loader.load(filename);

        team.stream().map(member -> member.split("§"))
                .sorted((attrs1, attrs2) -> attrs2[lastname].compareTo(attrs1[lastname]))
                .forEach(attrs -> {
                    userService.findByUsername(attrs[username]).ifPresent(user -> {
                        try {
                            String medianame = attrs[username];
                            String fullname = "<h4>" + attrs[firstname] + " " + attrs[lastname] + "</h4>";
                            String mediapath = copyMedia(storageProperties.getLocation(), thumbspath, medianame);

                            userService.addPost(space, user, attrs[aboutYou], fullname + attrs[work],
                                    Media.of(mediapath, Media.Type.PICTURE));

                            if (!spaceService.isMember(space.getId(), user)) {
                                spaceService.addMember(space, user, user, "MEMBER");
                            }

                            System.out.println(++count[0] + " " + user.getUsername() + " " + mediapath);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
    }

    private void createSpacePosts(String filename, String thumbspath, String detail, Space space) {
        int index = 0, username = 1, cover = 2, title = 3, text = 4;

        List<String> posts = PersistenceInit.Loader.load(filename);
        User defaultuser = userService.getUser(SUPERUSER);

        System.out.println("****** Creating Posts from " + thumbspath);
        posts.stream().map(post -> post.split("§"))
                .sorted((attrs1, attrs2) -> Integer.valueOf(attrs2[index]) > Integer.valueOf(attrs1[index]) ? 1 : -1)
                .forEach(attrs -> {
                    try {
                        User user = !attrs[username].equals("") ? userService.getUser(attrs[username]) : defaultuser;
                        if (user != null) {
                            String vortrag = "<h6>" + detail + "</h6> ";
                            String mediapath = copyMedia(storageProperties.getLocation(), thumbspath, attrs[cover]);

                            if (!spaceService.isMember(space.getId(), user)) {
                                spaceService.addMember(space, user, user, "MEMBER");
                            }

                            userService.addPost(space, user, attrs[title].trim(), vortrag + attrs[text],
                                    Media.of(mediapath, Media.Type.PICTURE));

                            System.out.println(attrs[index] + " " + attrs[username]);
                        }
                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                });
    }


    private void createPartners(String filename, String thumbspath, String coverpath, Space space) {
        int firstname = 0, lastname = 1, username = 2, web = 3, gender = 4, city = 5, country = 6, aboutYou = 7, text = 8;
        List<String> posts = PersistenceInit.Loader.load(filename);

        int[] idx = {0};
        posts.stream().filter(line -> line != null && !line.equals("")).map(post -> post.split("§"))
                .forEach(attrs -> {
                    try {
                        /* copy resources */
                        String avatar = copyAvatar(storageProperties.getLocation(), thumbspath, attrs[username].trim());

                        /* create user */
                        User user = populateUser(null, attrs[username].trim(), attrs[firstname].trim(),
                                attrs[lastname].trim(), avatar, attrs[gender].trim(), attrs[aboutYou].trim(),
                                attrs[text].trim(), attrs[web].trim(), attrs[city].trim(), attrs[country].trim());

                        String targetpath = user.getUsername();
                        String cover = copyCover(coverpath, targetpath, attrs[username].trim() + ".cover");

                        user = userService.createUser(user);
                        userService.createPublicSpaces(user, cover, attrs[aboutYou]);

                        /* add user to default space */
                        if (space != null && !spaceService.isMember(space.getId(), user)) {
                            spaceService.addMember(space, user, user, "MEMBER");
                        }

                        System.out.println(++idx[0] + "  " + attrs[username].trim() + " created");

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
    }

    private void createPartnerPosts(String filename, String thumbspath, Space space) {
        int firstname = 0, lastname = 1, username = 2, web = 3, gender = 4, city = 5, country = 6, aboutYou = 7, text = 8;
        List<String> posts = PersistenceInit.Loader.load(filename);
        int[] count = {0};
        User defaultuser = userService.getUser(SUPERUSER);


        System.out.println("****** Creating Partner Posts from " + thumbspath);
        posts.stream().map(post -> post.split("§"))
                .sorted((attrs1, attrs2) -> attrs2[username].compareTo(attrs1[username]))
                .forEach(attrs -> {
                    try {
                        User user = !attrs[username].equals("") ? userService.getUser(attrs[username]) : defaultuser;

                        String medianame = attrs[username];
                        String fullname = "<h4>" + attrs[firstname] + " " + attrs[lastname] + "</h4>";
                        String mediapath = copyMedia(storageProperties.getLocation(), thumbspath, medianame);
                        String webaddress = asUrl(attrs[web], attrs[firstname] + " " + attrs[lastname]);

                        userService.addPost(space, user, attrs[aboutYou], fullname + attrs[text] + webaddress,
                                Media.of(mediapath, Media.Type.PICTURE));

                        if (!spaceService.isMember(space.getId(), user)) {
                            spaceService.addMember(space, user, user, "MEMBER");
                        }

                        System.out.println(++count[0] + " " + user.getUsername() + " " + mediapath);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private String asUrl(String url, String name) {
        return "<p class='mt-2'>Web page: <a href='" + url + "' target='_blank'>" + name + "</a></p>";
    }

    @Deprecated
    private void deletePartners(String filename, Space space) {
        int firstname = 0, lastname = 1, username = 2, web = 3, gender = 4, city = 5, country = 6, aboutYou = 7, text = 8;
        List<String> posts = PersistenceInit.Loader.load(filename);

        posts.stream().filter(line -> line != null && !line.equals("")).map(post -> post.split("§"))
                .forEach(attrs -> {
                    Optional<User> opt = userService.findByUsername(attrs[username]);
                    opt.ifPresent(user -> {
                        try {
                            if (spaceService.isMember(space.getId(), user)) {
                                Member member = spaceService.getMember(space.getId(), user.getUsername());
                                spaceService.leaveSpace(space, member);
                            }

                            userService.deleteUser(user);
                            System.out.println("Deleted " + user.getUsername());
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    });
                });
    }

//    @Deprecated
//    private void verifyPartnerCover(String filename, String coverpath) {
//        int firstname = 0, lastname = 1, username = 2, web = 3, gender = 4, city = 5, country = 6, aboutYou = 7, text = 8;
//        List<String> posts = PersistenceInit.Loader.load(filename);
//
//        posts.stream().filter(line -> line != null && !line.equals("")).map(post -> post.split("§"))
//                .forEach(attrs -> {
//                    try {
//                        String cover = copyCover(storageProperties.getLocation(), coverpath, attrs[username].trim());
//                        if (cover == null) {
//                            Space home = userService.getHomeSpace(attrs[username]);
//                            home.setCover(null);
//                            spaceRepository.save(home);
//                        }
//                    } catch (Exception e) {
//                        System.out.println(e.getMessage());
//                    }
//                });
//    }

    @Deprecated
    private void joinWK19Space(String filename, Space space) {
        int index = 0, username = 1, cover = 2, title = 3, text = 4;
        List<String> posts = PersistenceInit.Loader.load(filename);
        posts.stream().map(post -> post.split("§"))
                .filter(attrs -> !attrs[username].equals(""))
                .forEach(attrs -> {
                    try {
                        User user = userService.getUser(attrs[username]);
                        spaceService.addMember(space, user, user, "MEMBER");

                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                });
    }

    @Deprecated
    private void joinTeamSpace(String filename, Space space) {
        final int firstname = 0, lastname = 1, username = 2, email = 3, gender = 4, city = 5, country = 6, aboutYou = 7, work = 8;

        List<String> posts = PersistenceInit.Loader.load(filename);
        posts.stream().map(post -> post.split("§"))
                .filter(attrs -> !attrs[username].equals(""))
                .forEach(attrs -> {
                    try {
                        User user = userService.getUser(attrs[username]);
                        spaceService.addMember(space, user, user, "MEMBER");

                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                });
    }

    private User populateUser(String email, String username, String firstname, String lastname, String avatar,
                              String gender, String aboutYou, String work, String web, String city, String country) {

        return User.of((email == null || email.equals("")) ? username + "@institutmed.de" : email,
                username, firstname, lastname, "password", avatar, Role.asArray(Role.Type.USER))
                .setUserData(UserData.of(null,
                        LocalDate.of(1970, 1, 1),
                        getGender(gender),
                        UserData.Marital.NONE,
                        UserData.Interest.NONE,
                        aboutYou, null, null, work, null, null, web,
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

        if (Files.notExists(source)) return null;

        if (Files.notExists(target)) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return Paths.get(username, location.getProfile()).
                resolve(username + ".jpg").toString();
    }

    // targetpath = username/generic/space-id/
    private String copyCover(String sourcepath, String targetpath, String filename) throws IOException {

        StorageProperties.Location location = storageProperties.getLocation();

        Path root = Paths.get(location.getRoot(), targetpath, location.getCover());
        if (Files.notExists(root)) {
            Files.createDirectories(root);
        }

        Path source = Paths.get(location.getThumbs(), sourcepath).resolve(filename + ".jpg");
        Path target = root.resolve(source.getFileName());

        if (Files.notExists(source)) return null;

        if (Files.notExists(target)) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return Paths.get(targetpath, location.getCover()).
                resolve(source.getFileName()).toString();
    }

    private String copyMedia(StorageProperties.Location location, String thumbspath, String filename)
            throws IOException {
        Path root = Paths.get(location.getRoot(), filename);
        if (Files.notExists(root)) {
            Files.createDirectories(root);
        }

        Path source = Paths.get(location.getThumbs(), thumbspath).resolve(filename + ".jpg");
        Path target = root.resolve(source.getFileName());

        if (Files.notExists(source)) return null;

        if (Files.notExists(target)) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return Paths.get(filename).resolve(filename + ".jpg").toString();
    }

}
