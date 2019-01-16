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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

/**
 * Collection of quick hacks for initializing the kikirikii database. Could be used as basis for some bot apis.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class InstitutMedInit {
    private Logger logger = Logger.getLogger("InstitutMedInit");
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
    public void createUsersAndSpaces() {

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

        /* step 0 - create superuser posts */
        userService.findHomeSpace(SUPERUSER).ifPresent(space -> {
            createSpacePostsMultiMedia("institutmed/superuser_posts.csv",
                    "superuser/cover", "Institut für Ganzheitsmedizin", space);
        });


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
                    "Weltkongress", space);
        });

        /* step 5 - create gesundheitsmesse 2019 posts */
        spaceService.findBySpacename("Gesundheitsmesse 2019").ifPresent(space -> {
            createSpacePostsMultiMedia("institutmed/gm19.csv", "gesundheitsmesse-2019/cover",
                    "Gesundheitsmesse", space);
        });

        /* step 6 - create praxis posts */
        spaceService.findBySpacename("Praxis Seminar").ifPresent(space -> {
            createSpacePostsMultiMedia("institutmed/praxis19.csv", "praxis-seminar-2019/cover",
                    "Praxis Heilworkshop", space);
        });

        /* step 7 - create spenden posts */
        spaceService.findBySpacename("Spenden-Projekte").ifPresent(space -> {
            createSpacePostsMultiMedia("institutmed/spenden.csv", "spenden-projekte/cover",
                    "Spenden Projekt", space);
        });

        /* step 8 - create sammelband 2019  posts */
        spaceService.findBySpacename("Sammelband 2019").ifPresent(space -> {
            createSpacePostsMultiMedia("institutmed/sammelband-19.csv", "sammelband-2019/cover",
                    "Sammelband 2019", space);
        });

        /* step 9 - create presseschau  posts */
        spaceService.findBySpacename("Presseschau").ifPresent(space -> {
            createSpacePostsMultiMedia("institutmed/presseschau.csv", "presseschau/cover",
                    "Presseschau", space);
        });
    }

//    @Ignore
    @Test
    @Deprecated
    public void createDeltas() {
//        spaceService.findBySpacename("Partner & Austeller").ifPresent(space -> {
//            createPartners("institutmed/partner_delta.csv", "partner_profiles/thumbs",
//                    "partner_profiles/cover", space);
//        });
//
//        spaceService.findBySpacename("Referenten & Autoren").ifPresent(space -> {
//            createUsers("institutmed/referenten-delta.csv", "referenten_profiles/thumbs",
//                    space);
//        });

//        createUserIntroductoryPost("institutmed/referenten.csv");

//        userService.findHomeSpace(SUPERUSER).ifPresent(space -> {
//            createSpacePostsMultiMedia("institutmed/superuser_posts.csv",
//                    "superuser/cover", "Institut für Ganzheitsmedizin", space);
//        });


        userService.findByUsername(SUPERUSER).ifPresent(user -> {
            createGenericSpaces(user, "institutmed/spaces.csv", "spaces/cover");
        });

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
                                SpaceData.of(null, getLocalDate(attrs[type], attrs[start_date]),
                                        getLocalDate(attrs[type], attrs[start_date]),
                                        attrs[general_information], attrs[venue], attrs[travel_information],
                                        attrs[venue], attrs[city], null, null,
                                        attrs[key_dates], attrs[tickets], attrs[dates]));

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

    private LocalDate getLocalDate(String type, String date) {
        if(type.equals("E")) {
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        }
        return null;
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
                            String fullname = "<h4>" + attrs[firstname] + " " + attrs[lastname] + "</h4>";
                            String mediapath = copyMedia(thumbspath, attrs[username], attrs[username]);

                            // create post in generic space
                            userService.addPost(space, user, attrs[aboutYou], fullname + attrs[work],
                                    Media.of(mediapath, Media.Type.PICTURE));

                            if (!spaceService.isMember(space.getId(), user)) {
                                spaceService.addMember(space, user, user, "MEMBER");
                            }

                            // create post in home space
                            userService.findHomeSpace(attrs[username]).ifPresent(homespace -> {
                                userService.addPost(homespace, user, attrs[aboutYou], fullname + attrs[work],
                                        Media.of(mediapath, Media.Type.PICTURE));
                            });

                            System.out.println(++count[0] + " " + user.getUsername() + " " + mediapath);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
    }

    private void createSpacePostsMultiMedia(String filename, String thumbspath, String detail, Space space) {
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
                            String subtitle = "<h6>" + detail + "</h6> ";

                            List<String> pathlist = copyMultiMedia(thumbspath, user.getUsername(), attrs[cover]);

                            if (!spaceService.isMember(space.getId(), user)) {
                                spaceService.addMember(space, user, user, "MEMBER");
                            }

                            userService.addPost(space, user, attrs[title].trim(), subtitle + attrs[text],
                                    asMediaSet(pathlist));

                            System.out.println(attrs[index] + " " + attrs[username] + " " + attrs[title] + " ");
                        }
                    } catch (Exception e) {
                        logger.warning(e.getMessage());
                    }
                });
    }

    private Set<Media> asMediaSet(List<String> pathlist) {
        Set<Media> set = new HashSet<>();
        pathlist.forEach(m -> {
            set.add(Media.of(m, m.startsWith("https://www.youtube.com") ? Media.Type.YOUTUBE : Media.Type.PICTURE));
        });
        return set;
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

                            String mediapath = copyMedia(thumbspath, user.getUsername(), attrs[cover]);
                            if (!spaceService.isMember(space.getId(), user)) {
                                spaceService.addMember(space, user, user, "MEMBER");
                            }

                            userService.addPost(space, user, attrs[title].trim(), vortrag + attrs[text],
                                    Media.of(mediapath, Media.Type.PICTURE));

                            System.out.println(attrs[index] + " " + attrs[username] + " " + attrs[title] + " ");
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
//                        /* copy resources */
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

                        System.out.println(++idx[0] + "  " + attrs[username].trim() + " " + avatar + " " + " created");

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

                        String fullname = "<h4>" + attrs[firstname] + " " + attrs[lastname] + "</h4>";
                        String mediapath = copyMedia(thumbspath, attrs[username], attrs[username]);
                        String webaddress = asUrl(attrs[web], attrs[firstname] + " " + attrs[lastname]);

                        userService.addPost(space, user, attrs[aboutYou], fullname + attrs[text] + webaddress,
                                Media.of(mediapath, Media.Type.PICTURE));

                        if (!spaceService.isMember(space.getId(), user)) {
                            spaceService.addMember(space, user, user, "MEMBER");
                        }

                        // create post in home space
                        userService.findHomeSpace(attrs[username]).ifPresent(homespace -> {
                            userService.addPost(homespace, user, attrs[aboutYou], fullname + attrs[text] + webaddress,
                                    Media.of(mediapath, Media.Type.PICTURE));
                        });

                        System.out.println(++count[0] + " " + user.getUsername() + " " + mediapath);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private String asUrl(String url, String name) {
        return "<br><p class='mt-2'>Web page: <a href='" + url + "' target='_blank'>" + name + "</a></p>";
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

    private String copyMedia(String sourcepath, String targetpath, String filename) throws IOException {
        StorageProperties.Location location = storageProperties.getLocation();

        Path root = Paths.get(location.getRoot(), targetpath);
        if (Files.notExists(root)) {
            Files.createDirectories(root);
        }

        Path source = Paths.get(location.getThumbs(), sourcepath).resolve(filename + ".jpg");
        Path target = root.resolve(source.getFileName());

        if (Files.notExists(source)) return null;

        if (Files.notExists(target)) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return Paths.get(targetpath).resolve(filename + ".jpg").toString();
    }

    private String resolveMediaTarget(String targetpath, String filename) {
        return Paths.get(targetpath).resolve(filename + ".jpg").toString();
    }

    private List<String> copyMultiMedia(String sourcepath, String targetpath, String filename) throws IOException {
        StorageProperties.Location location = storageProperties.getLocation();
        List<String> media = new ArrayList<>();

        if(filename.startsWith("https://www.youtube.com")) {
            media.add(filename);
            return media;
        }

        Path root = Paths.get(location.getRoot(), targetpath);
        if (Files.notExists(root)) {
            Files.createDirectories(root);
        }


        Path source = Paths.get(location.getThumbs(), sourcepath).resolve(filename + ".jpg");
        Path target = root.resolve(source.getFileName());
        int cnt = 0;

        while(Files.exists(source)) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            media.add(Paths.get(targetpath).resolve(source.getFileName()).toString());

            source = Paths.get(location.getThumbs(), sourcepath).resolve(filename + "-0" + ++cnt + ".jpg");
            target = root.resolve(source.getFileName());
        }

        return media;

    }

    /*
     * @Deprecated
     */

    @Deprecated
    /** Achtung: this function assumes media has been already copied */
    private void createUserIntroductoryPost(String filename) {
        final int firstname = 0, lastname = 1, username = 2, email = 3, gender = 4, city = 5, country = 6, aboutYou = 7, work = 8;
        int[] count = {0};
        List<String> team = PersistenceInit.Loader.load(filename);

        team.stream().map(member -> member.split("§"))
                .sorted((attrs1, attrs2) -> attrs2[lastname].compareTo(attrs1[lastname]))
                .forEach(attrs -> {
                    userService.findByUsername(attrs[username]).ifPresent(user -> {
                        try {
                            userService.findHomeSpace(attrs[username]).ifPresent(homespace -> {
                                String fullname = "<h4>" + attrs[firstname] + " " + attrs[lastname] + "</h4>";
                                String mediapath = resolveMediaTarget(attrs[username], attrs[username]);

                                userService.addPost(homespace, user, attrs[aboutYou], fullname + attrs[work],
                                        Media.of(mediapath, Media.Type.PICTURE));

                                System.out.println(++count[0] + " " + user.getUsername() + " " + mediapath);
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
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

}