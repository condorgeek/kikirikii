package com.kikirikii.services;

import com.kikirikii.model.*;
import com.kikirikii.repos.FollowerRepository;
import com.kikirikii.repos.FriendRepository;
import com.kikirikii.repos.PostRepository;
import com.kikirikii.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    public static Logger logger = Logger.getLogger("UserService");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FollowerRepository followerRepository;

    public UserService() {
        logger.info("Initialized");
    }

    public User getUser(String name) {

        Optional<User> user = userRepository.findByName(name);
        assert user.isPresent() : "Invalid User Id " + name;

        return user.get();
    }

    public Post addPost(Space space, User user, String title, String text) {
        return postRepository.save(Post.of(space, user, title, text));
    }

    public Space getHomeSpace(String name) {
        Optional<Space> home = userRepository.findHomeSpaceByName(name);
        assert home.isPresent() : "Invalid user name or user has no home space " + name;

        return home.get();
    }

    public Space getGlobalSpace(String name) {
        Optional<Space> global = userRepository.findGlobalSpaceByName(name);
        assert global.isPresent() : "Invalid user name or user has no global space " + name;

        return global.get();
    }

    public List<Post> getUserGlobalPosts(User user) {
        Optional<Space> global = userRepository.findGlobalSpace(user.getId());
        List<Post> posts = postRepository.findAllBySpaceId(global.get().getId()).collect(Collectors.toList());
        return posts;
    }

    public List<Post> getUserHomePosts(User user) {
        Optional<Space> home = userRepository.findHomeSpace(user.getId());
        List<Post> posts = postRepository.findAllBySpaceId(home.get().getId()).collect(Collectors.toList());
        return posts;
    }

    public Post getPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        assert post.isPresent() : "Invalid postId " + postId;

        return post.get();
    }

    public List<User> getUserFriends(User user) {
        return friendRepository.findAllByUserId(user.getId())
                .map(f -> f.getSurrogate())
                .collect(Collectors.toList());
    }

    public List<User> getUserFollowers(User user) {
        return followerRepository.findAllByUserId(user.getId())
                .map(f -> f.getSurrogate())
                .collect(Collectors.toList());
    }
}
