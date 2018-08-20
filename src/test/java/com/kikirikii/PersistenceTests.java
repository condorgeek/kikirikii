package com.kikirikii;

import com.kikirikii.model.*;
import com.kikirikii.repos.*;
import com.kikirikii.services.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceTests {
    private Logger logger = Logger.getLogger("PersistenceTests");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void createUsers() {
        userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        userRepository.save(User.of("marga.barcelona@testmail.com", "Barcelona", "Marga", "Barcelona", "password"));
        userRepository.save(User.of("ronny.helsinki@testmail.com", "Helsinki", "Ronny", "Helsinki", "password"));
        userRepository.save(User.of("hans.munich@testmail.com", "Munich", "Hans", "Munich", "password"));
        userRepository.save(User.of("maria.hamburg@testmail.com", "Hamburg", "Maria", "Hamburg", "password"));
        userRepository.save(User.of("luisa.brighton@testmail.com", "Brighton", "Luisa", "Brighton", "password"));

        User london = findByName("london");
        Assert.assertEquals("jack.london@testmail.com", london.getEmail());
        Assert.assertTrue(london.verifyPassword("password"));

        london.addRole(Role.of(Role.Type.ADMIN));
        london.addRole(Role.of(Role.Type.SUPERUSER));
        london = userRepository.save(london);
        Assert.assertEquals(3, london.getRoles().size());

        User user = findByName("helsinki");
        Assert.assertEquals("ronny.helsinki@testmail.com", user.getEmail());
        Assert.assertTrue(user.verifyPassword("password"));
//        Assert.assertEquals("ROLE_USER", user.getRoles().get(0).getAuthority());

        user = findByName("brighton");
        Assert.assertEquals("luisa.brighton@testmail.com", user.getEmail());
        Assert.assertTrue(user.verifyPassword("password"));

        user.setPassword("newpassword");
        user = userRepository.save(user);
        Assert.assertTrue(user.verifyPassword("newpassword"));

        UserData userData = userDataRepository.save(UserData.of(user, LocalDate.of(1989, 06, 18),
                UserData.Gender.FEMALE, UserData.Marital.SINGLE, UserData.Interest.NONE, "About me", "religion", "politics", Address.of("Victoria Street", "1A",
                        "3th Floor", "C-12-999", "London", "UK")
        ));
        Assert.assertEquals("Victoria Street", userData.getAddress().getStreet());

        london.setUserData(UserData.of(LocalDate.of(1976, 03, 22),
                UserData.Gender.MALE, UserData.Marital.SINGLE, UserData.Interest.WOMEN, "About me",
                Address.of("Ginger Road", "08", "Royal Docks", "C-12-111", "London", "UK")
        ));
        london = userRepository.save(london);
        Assert.assertEquals("Ginger Road", london.getUserData().getAddress().getStreet());
    }

    private User findByName(String name) {
        Optional<User> user = userRepository.findByUsername(name);
        Assert.assertTrue(user.isPresent());
        return user.get();
    }

    @Test
    @Transactional
    public void createSpaces() {

        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        User lapaz = userRepository.save(User.of("marga.lapaz@testmail.com", "Lapaz", "Marga", "LaPaz", "password"));

        spaceRepository.save(Space.of(london,
                "London's Space",
                "This is a place for London and friends",
                Space.Type.HOME));
        spaceRepository.save(Space.of(london,
                "London's Global Space",
                "This is a place for London, friends and followers",
                Space.Type.GLOBAL));
        spaceRepository.save(Space.of(london,
                "Some London's Space",
                "This is a place for London, friends and followers",
                Space.Type.GENERIC));
        spaceRepository.save(Space.of(lapaz,
                "Marga's Space",
                "This is a place for Marga and friends",
                Space.Type.HOME));
        spaceRepository.save(Space.of(lapaz,
                "Marga's Global Space",
                "This is a place for Marga, friends and followers",
                Space.Type.GLOBAL));
        spaceRepository.save(Space.of(lapaz,
                "Some Marga's Space",
                "This is a place for Marga, friends and followers",
                Space.Type.GENERIC));

        Stream<Space> spaces = spaceRepository.findAllByUserId(london.getId());
        Assert.assertEquals(1, spaces.count());

        Optional<Space> home = spaceRepository.findHomeSpace(london.getId());
        Assert.assertTrue(home.isPresent());

        spaces = spaceRepository.findAllByUserId(lapaz.getId());
        Assert.assertEquals(1, spaces.count());

        Optional<Space> global = spaceRepository.findGlobalSpace(lapaz.getId());
        Assert.assertTrue(global.isPresent());
    }

    @Test
    @Transactional
    public void createPostsWithMedia() {

        User user = userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        Space home = spaceRepository.save(Space.of(user,
                "London's Space",
                "This is a place for London and friends",
                Space.Type.HOME));

        postRepository.save(Post.of(home, user, "Picture Title", "This is a great sample with emoticons :heart:")
                .addMedia(Media.of("http://somehost/somepic.jpg", Media.Type.PICTURE))
                .addMedia(Media.of("http://somehost/somevid.mp4", Media.Type.VIDEO))
                .addMedia(Media.of("http://somehost/otherpic.jpg", Media.Type.PICTURE)));

        postRepository.save(Post.of(home, user,
                "Video Title", "This is another great sample with emoticons :heart::heart:")
                .addMedia(Media.of("http://somehost/somepic.jpg", Media.Type.PICTURE)));

        Media media = Media.of("http://somehost/othervid.jpg", Media.Type.VIDEO);
        Post post = postRepository.save(Post.of(home, user,
                "More Picture Title", ":heart_eyes::heart_eyes: This is some more great sample with emoticons")
                .addMedia(media)
                .addMedia(Media.of("http://somehost/somepic.jpg", Media.Type.PICTURE))
                .addMedia(Media.of("http://somehost/somevid.mp4", Media.Type.VIDEO))
                .addMedia(Media.of("http://somehost/otherpic.jpg", Media.Type.PICTURE))
                .addMedia(Media.of("http://somehost/anotherpic.jpg", Media.Type.PICTURE)));

        Assert.assertEquals(5, post.getMedia().size());

        List<Post> posts = postRepository.findAllBySpaceId(home.getId()).collect(Collectors.toList());
        Assert.assertEquals(3, posts.size());

        post = postRepository.save(post.removeMedia(media));
        Assert.assertEquals(4, post.getMedia().size());
    }

    @Test
    @Transactional
    public void createComments() {
        User user = userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        Space home = spaceRepository.save(Space.of(user,
                "London's Space",
                "This is a place for London and friends",
                Space.Type.HOME));

        Post post = postRepository.save(Post.of(home, user,
                "Video Title Again", "This is a great sample with emoticons :wimp:"));

        commentRepository.save(Comment.of(post, user, ":heart:This is comment one"));
        commentRepository.save(Comment.of(post, user, "This is comment two:heart:"));
        commentRepository.save(Comment.of(post, user, "This is comment three"));
        commentRepository.save(Comment.of(post, user, "This is comment four"));

        Stream<Comment> comments = commentRepository.findAllByPostId(post.getId());
        Assert.assertEquals(4, comments.count());
    }

    @Test
    @Transactional
    public void createFriendsRaw() {
        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        User paris = userRepository.save(User.of("marga.paris@testmail.com", "Paris", "Marga", "Paris", "password"));
        User madrid = userRepository.save(User.of("ronny.madrid@testmail.com", "Madrid", "Ronny", "Madrid", "password"));
        User moscu = userRepository.save(User.of("hans.moscu@testmail.com", "Mosku", "Hans", "Moscu", "password"));

        friendRepository.save(Friend.of(london, paris));
        friendRepository.save(Friend.of(london, madrid));
        friendRepository.save(Friend.of(london, moscu));

        Stream<Friend> friends = friendRepository.findAllByUserId(london.getId());
        Assert.assertEquals(3, friends.count());
    }

    @Test
    @Transactional
    public void createFriendsStaged() {
        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        User paris = userRepository.save(User.of("marga.paris@testmail.com", "Paris", "Marga", "Paris", "password"));
        User madrid = userRepository.save(User.of("ronny.madrid@testmail.com", "Madrid", "Ronny", "Madrid", "password"));
        User moscu = userRepository.save(User.of("hans.moscu@testmail.com", "Mosku", "Hans", "Moscu", "password"));
        User munich = userRepository.save(User.of("tom.shell@testmail.com", "Tom", "Tom", "Shell", "password"));
        User julietta = userRepository.save(User.of("julietta.fago@testmail.com", "Julietta", "Julietta", "Fago", "password"));
        User maria = userRepository.save(User.of("maria.perez@testmail.com", "Maria", "Maria", "Perez", "password"));

        userService.addFriend(london, paris);
        userService.addFriend(london, madrid);
        userService.addFriend(london, moscu);
        userService.addFriend(london, munich);
        userService.addFriend(london, julietta);
        userService.addFriend(london, maria);

        List<Friend> friends = friendRepository.findByState(london.getUsername(), Friend.State.PENDING);
        Assert.assertEquals(6, friends.size());

        userService.acceptFriend(paris, london);
        userService.acceptFriend(madrid, london);
        userService.acceptFriend(julietta, london);

        friends = friendRepository.findByState(london.getUsername(), Friend.State.ACTIVE);
        Assert.assertEquals(3, friends.size());

        userService.ignoreFriendRequest(moscu, london);
        userService.cancelFriendRequest(london, munich);
        userService.deleteFriend(london, julietta);

        friends = friendRepository.findActivePendingBlocked(london.getUsername());
        Assert.assertEquals(3, friends.size());

        long pending = friends.stream().filter(f -> f.getState() == Friend.State.PENDING).count();
        Assert.assertEquals(1L, pending);

    }

    @Test
    @Transactional
    public void createFollowers() {
        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        User paris = userRepository.save(User.of("marga.paris@testmail.com", "Paris", "Marga", "Paris", "password"));
        User madrid = userRepository.save(User.of("ronny.madrid@testmail.com", "Madrid", "Ronny", "Madrid", "password"));
        User moscu = userRepository.save(User.of("hans.moscu@testmail.com", "Mosku", "Hans", "Mosku", "password"));

        followerRepository.save(Follower.of(london, paris));
        followerRepository.save(Follower.of(london, madrid));
        followerRepository.save(Follower.of(london, moscu));

        Stream<Follower> followers = followerRepository.findAllByUserId(london.getId());
        Assert.assertEquals(3, followers.count());
    }

    @Test
    @Transactional
    public void createLikes() {

        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "Jack", "London", "password"));
        User barcelona = userRepository.save(User.of("julia.barcelona@testmail.com", "Barcelona", "Julia", "Barcelona", "password"));
        User munich = userRepository.save(User.of("john.munich@testmail.com", "Munich", "John", "Munich", "password"));

        Space home = spaceRepository.save(Space.of(london,
                "Jack's Space",
                "This is a place for Jack and friends",
                Space.Type.HOME));

        Post post = postRepository.save(Post.of(home, london,
                "Video Title Again", "This is a great sample with emoticons :wimp:"));

        likeRepository.save(Like.of(post, barcelona, LikeReaction.LIKE));
        likeRepository.save(Like.of(post, munich, LikeReaction.WOW));

        Stream<Like> likes = likeRepository.findAllByPostId(post.getId());
        Assert.assertEquals(2, likes.count());
    }
}
