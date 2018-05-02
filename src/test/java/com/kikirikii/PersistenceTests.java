package com.kikirikii;

import com.kikirikii.repos.*;
import com.kikirikii.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
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

    @Test
    @Transactional
    public void createUsers() {
        userRepository.save(User.of("jack.london@testmail.com", "London", "password"));
        userRepository.save(User.of("marga.barcelona@testmail.com", "Barcelona", "password"));
        userRepository.save(User.of("ronny.helsinki@testmail.com", "Helsinki", "password"));
        userRepository.save(User.of("hans.munich@testmail.com", "Munich", "password"));
        userRepository.save(User.of("maria.hamburg@testmail.com", "Hamburg", "password"));
        userRepository.save(User.of("luisa.brighton@testmail.com", "Brighton", "password"));

        User user = userRepository.findByName("London");
        Assert.assertEquals("jack.london@testmail.com", user.getId());

        user = userRepository.findByName("Helsinki");
        Assert.assertEquals("ronny.helsinki@testmail.com", user.getId());

        user = userRepository.findByName("Brighton");
        Assert.assertEquals("luisa.brighton@testmail.com", user.getId());
    }

    @Test
    @Transactional
    public void createSpaces() {

        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "password"));
        User lapaz = userRepository.save(User.of("marga.lapaz@testmail.com", "Lapaz", "password"));

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

        User user = userRepository.save(User.of("jack.london@testmail.com", "London", "password"));
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
        User user = userRepository.save(User.of("jack.london@testmail.com", "London", "password"));
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
    public void createFriends() {
        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "password"));
        User paris = userRepository.save(User.of("marga.paris@testmail.com", "Paris", "password"));
        User madrid = userRepository.save(User.of("ronny.madrid@testmail.com", "Madrid", "password"));
        User moscu = userRepository.save(User.of("hans.moscu@testmail.com", "Mosku", "password"));

        friendRepository.save(Friend.of(london, paris));
        friendRepository.save(Friend.of(london, madrid));
        friendRepository.save(Friend.of(london, moscu));

        Stream<Friend> friends = friendRepository.findAllByUserId(london.getId());
        Assert.assertEquals(3, friends.count());
    }

    @Test
    @Transactional
    public void createFollowers() {
        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "password"));
        User paris = userRepository.save(User.of("marga.paris@testmail.com", "Paris", "password"));
        User madrid = userRepository.save(User.of("ronny.madrid@testmail.com", "Madrid", "password"));
        User moscu = userRepository.save(User.of("hans.moscu@testmail.com", "Mosku", "password"));

        followerRepository.save(Follower.of(london, paris));
        followerRepository.save(Follower.of(london, madrid));
        followerRepository.save(Follower.of(london, moscu));

        Stream<Follower> followers = followerRepository.findAllByUserId(london.getId());
        Assert.assertEquals(3, followers.count());
    }

    @Test
    @Transactional
    public void createLikes() {

        User london = userRepository.save(User.of("jack.london@testmail.com", "London", "password"));
        User barcelona = userRepository.save(User.of("julia.barcelona@testmail.com", "Barcelona", "password"));
        User munich = userRepository.save(User.of("john.munich@testmail.com", "Munich", "password"));

        Space home = spaceRepository.save(Space.of(london,
                "Jack's Space",
                "This is a place for Jack and friends",
                Space.Type.HOME));

        Post post = postRepository.save(Post.of(home, london,
                "Video Title Again", "This is a great sample with emoticons :wimp:"));

        likeRepository.save(Like.of(post, barcelona, Like.Type.HAPPY));
        likeRepository.save(Like.of(post, munich, Like.Type.SURPRISED));

        Stream<Like> likes = likeRepository.findAllByPostId(post.getId());
        Assert.assertEquals(2, likes.count());
    }
}
