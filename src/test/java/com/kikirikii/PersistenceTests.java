package com.kikirikii;

import com.kikirikii.db.CommentRepository;
import com.kikirikii.db.PostRepository;
import com.kikirikii.db.SpaceRepository;
import com.kikirikii.db.UserRepository;
import com.kikirikii.model.Comment;
import com.kikirikii.model.Post;
import com.kikirikii.model.Space;
import com.kikirikii.model.User;
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

    //    @Test
    public void createUsers() {
        userRepository.save(User.create("amaru.kusku@gmail.com", "Amaru", "password"));
        userRepository.save(User.create("julia.jobs@gmail.com", "Julia", "password"));

        List<User> users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        Assert.assertEquals(2L, users.size());
    }

    //    @Test
    public void createSpaces() {
        spaceRepository.save(Space.create(userRepository.findByName("Amaru"),
                "Amaru's Space",
                "This is a place for Amaru and friends",
                Space.Type.HOME));
        spaceRepository.save(Space.create(userRepository.findByName("Amaru"),
                "Amaru's Global Space",
                "This is a place for Amaru, friends and followers",
                Space.Type.GLOBAL));
        spaceRepository.save(Space.create(userRepository.findByName("Julia"),
                "Julia's Space",
                "This is a place for Julia and friends",
                Space.Type.HOME));
        spaceRepository.save(Space.create(userRepository.findByName("Julia"),
                "Julia's Global Space",
                "This is a place for Julia, friends and followers",
                Space.Type.GLOBAL));

        List<Space> spaces = StreamSupport.stream(spaceRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        Assert.assertEquals(2L, spaces.size());
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

        spaceRepository.findAllPostsById(home.get().getId()).forEach(post -> logger.info(post.getText()));
        long count = spaceRepository.findAllPostsById(home.get().getId()).count();
        Assert.assertEquals(3, count);

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

        postRepository.findAllCommentsById(post.getId()).forEach(comment -> logger.info(comment.getText()));
        long count = postRepository.findAllCommentsById(post.getId()).count();

        Assert.assertEquals(4, count);
    }

}
