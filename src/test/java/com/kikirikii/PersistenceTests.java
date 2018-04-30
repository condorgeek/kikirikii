package com.kikirikii;

import com.kikirikii.db.*;
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
import java.util.stream.StreamSupport;

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

//    @Test
    public void createUsers() {
        userRepository.save(User.create("amaru.kusku@gmail.com", "Amaru", "password"));
        userRepository.save(User.create("julia.jobs@gmail.com", "Julia", "password"));
        userRepository.save(User.create("marc.shell@gmail.com", "Marc", "password"));
        userRepository.save(User.create("peter.hummel@gmail.com", "Peter", "password"));
        userRepository.save(User.create("anita.huebsch@gmail.com", "Anita", "password"));
        userRepository.save(User.create("heidi.angeles@gmail.com", "Heidi", "password"));

        List<User> users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        Assert.assertEquals(6L, users.size());
    }

//    @Test
    public void createSpaces() {

        User amaru = userRepository.findByName("Amaru");
        User julia = userRepository.findByName("Julia");

        spaceRepository.save(Space.create(amaru,
                "Amaru's Space",
                "This is a place for Amaru and friends",
                Space.Type.HOME));
        spaceRepository.save(Space.create(amaru,
                "Amaru's Global Space",
                "This is a place for Amaru, friends and followers",
                Space.Type.GLOBAL));
        spaceRepository.save(Space.create(amaru,
                "Some Amaru's Space",
                "This is a place for Amaru, friends and followers",
                Space.Type.GENERIC));
        spaceRepository.save(Space.create(julia,
                "Julia's Space",
                "This is a place for Julia and friends",
                Space.Type.HOME));
        spaceRepository.save(Space.create(julia,
                "Julia's Global Space",
                "This is a place for Julia, friends and followers",
                Space.Type.GLOBAL));
        spaceRepository.save(Space.create(julia,
                "Some Julia's Space",
                "This is a place for Julia, friends and followers",
                Space.Type.GENERIC));

//        List<Space> spaces = StreamSupport.stream(spaceRepository.findAll().spliterator(), false)
//                .collect(Collectors.toList());
//        Assert.assertEquals(4L, spaces.size());

        Stream<Space> amaru_spaces = spaceRepository.findAllByUserId(amaru.getId());
        Assert.assertEquals(1, amaru_spaces.count());

        Stream<Space> julia_spaces = spaceRepository.findAllByUserId(amaru.getId());
        Assert.assertEquals(1, julia_spaces.count());

    }


    @Test
    @Transactional
    public void createPosts() {

        User user = userRepository.findByName("Amaru");
        Optional<Space> home = spaceRepository.findHomeSpace(user.getId());
        Assert.assertTrue(home.isPresent());

        postRepository.save(Post.create(home.get(), user, "urlpath-to-pic.jpg", Post.MediaType.IMAGE,
                "Picture Title", "This is a great sample with emoticons :wimp:"));
        postRepository.save(Post.create(home.get(), user, "urlpath-to-pic-2.jpg", Post.MediaType.VIDEO,
                "Video Title", "This is another great sample with emoticons :wimp:"));
        postRepository.save(Post.create(home.get(), user, "urlpath-to-pic-2.jpg", Post.MediaType.IMAGE,
                "More Picture Title", "This is some more great sample with emoticons :wimp:"));

        postRepository.findAllBySpaceId(home.get().getId()).forEach(post -> logger.info(post.getText()));
        Stream<Post> posts = postRepository.findAllBySpaceId(home.get().getId());
        Assert.assertEquals(3, posts.count());

    }

    @Test
    @Transactional
    public void createComments() {
        User user = userRepository.findByName("Amaru");
        Optional<Space> home = spaceRepository.findHomeSpace(user.getId());
        Assert.assertTrue(home.isPresent());

        Post post = postRepository.save(Post.create(home.get(), user, "urlpath-to-video.mp4", Post.MediaType.VIDEO,
                "Video Title Again", "This is a great sample with emoticons :wimp:"));

        commentRepository.save(Comment.create(post, user, "This is comment one"));
        commentRepository.save(Comment.create(post, user, "This is comment two"));
        commentRepository.save(Comment.create(post, user, "This is comment three"));
        commentRepository.save(Comment.create(post, user, "This is comment four"));

        commentRepository.findAllByPostId(post.getId()).forEach(comment -> logger.info(comment.getText()));
        Stream<Comment> comments = commentRepository.findAllByPostId(post.getId());

        Assert.assertEquals(4, comments.count());
    }

    @Test
    @Transactional
    public void createFriends() {
        User amaru = userRepository.findByName("Amaru");
        User julia = userRepository.findByName("Julia");
        User marc = userRepository.findByName("Marc");
        User peter = userRepository.findByName("Peter");

        friendRepository.save(Friend.create(amaru, julia));
        friendRepository.save(Friend.create(amaru, marc));
        friendRepository.save(Friend.create(amaru, peter));

        Stream<Friend> friends = friendRepository.findAllByUserId(amaru.getId());
        Assert.assertEquals(3, friends.count());
    }

    @Test
    @Transactional
    public void createFollowers() {
        User amaru = userRepository.findByName("Amaru");
        User julia = userRepository.findByName("Julia");
        User marc = userRepository.findByName("Marc");
        User peter = userRepository.findByName("Peter");

        followerRepository.save(Follower.create(amaru, julia));
        followerRepository.save(Follower.create(amaru, marc));
        followerRepository.save(Follower.create(amaru, peter));

        Stream<Follower> followers = followerRepository.findAllByUserId(amaru.getId());
        Assert.assertEquals(3, followers.count());
    }

    @Test
    @Transactional
    public void createLikes() {

        User jack = userRepository.save(User.create("jack.london@gmail.com", "Jack", "password"));
        User julia = userRepository.save(User.create("julia.black@gmail.com", "Julia", "password"));
        User richard = userRepository.save(User.create("richard.loewe@gmail.com", "Richard", "password"));

        Space home = spaceRepository.save(Space.create(jack,
                "Jack's Space",
                "This is a place for Jack and friends",
                Space.Type.HOME));

        Post post = postRepository.save(Post.create(home, jack, "urlpath-to-video.mp4", Post.MediaType.VIDEO,
                "Video Title Again", "This is a great sample with emoticons :wimp:"));

        likeRepository.save(Like.create(post, julia, Like.Type.HAPPY));
        likeRepository.save(Like.create(post, richard, Like.Type.SURPRISED));

        Stream<Like> likes = likeRepository.findAllByPostId(post.getId());
    }
}
