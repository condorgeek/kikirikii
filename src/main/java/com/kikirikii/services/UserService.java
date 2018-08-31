package com.kikirikii.services;

import com.kikirikii.exceptions.DuplicateResourceException;
import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.*;
import com.kikirikii.model.dto.UserProspect;
import com.kikirikii.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Autowired
    private SpaceRepository spaceRepository;

    public UserService() {
        logger.info("Initialized");
    }

    public User getUser(String username) {

        Optional<User> user = userRepository.findByUsername(username);
        assert user.isPresent() : "Invalid username " + username;

        return user.get();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(String username, UserProspect prospect) {
        if(findByUsername(prospect.username).isPresent()) {
            throw new DuplicateResourceException("Username already exists.");
        }

        if(findByEmail(prospect.email).isPresent()) {
            throw new DuplicateResourceException("Email already associated to user.");
        }

        try {
            return userRepository.save(prospect.createUser());

        } catch(Exception e) {
            throw new InvalidResourceException("User cannot be created. " + e.getMessage());
        }

    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void createSpaces(User user) {
        try {
            spaceRepository.save(Space.of(user,
                    user.getUsername() + "'s Home Space",
                    "This is a personal place for me and friends",
                    Space.Type.HOME));
            spaceRepository.save(Space.of(user,
                    user.getUsername() + "'s Global Space",
                    "This is a place for me, friends and followers",
                    Space.Type.GLOBAL));

        } catch (Exception e) {
            throw new InvalidResourceException("User spaces cannot be created. " + e.getMessage());
        }
    }

    public Post addPost(Space space, User user, String title, String text, Set<Media> media) {
        return postRepository.save(Post.of(space, user, title, text, media));
    }

    public Space getHomeSpace(String name) {
        Optional<Space> home = userRepository.findHomeSpaceByName(name);
        assert home.isPresent() : "Invalid user name or user has no home space " + name;

        return home.get();
    }

    public Space updateSpace(Space space) {
        return spaceRepository.save(space);
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
        return friendRepository.findActiveBlocked(user.getUsername()).stream()
                .map(Friend::getSurrogate)
                .collect(Collectors.toList());
    }

    public List<Friend> getFriends(User user) {
        return friendRepository.findActiveBlocked(user.getUsername());
    }

    public List<User> getUserFriendsPending(User user) {
        return friendRepository.findByState(user.getUsername(), Friend.State.PENDING).stream()
                .map(Friend::getSurrogate)
                .collect(Collectors.toList());
    }

    public List<Friend> getFriendsPending(User user) {
        return friendRepository.findByState(user.getUsername(), Friend.State.PENDING);
    }

    public List<User> getUserFollowers(User user) {
        return followerRepository.findActiveBlockedFollowers(user.getUsername()).stream()
                .map(Follower::getUser)
                .collect(Collectors.toList());
    }

    public List<Follower> getFollowers(User user) {
        return followerRepository.findActiveBlockedFollowers(user.getUsername());
    }

    public List<User> getUserFollowees(User user) {
        return followerRepository.findActiveBlockedFollowees(user.getUsername()).stream()
                .map(Follower::getSurrogate)
                .collect(Collectors.toList());
    }

    public List<Follower> getFollowees(User user) {
        return followerRepository.findActiveBlockedFollowees(user.getUsername());
    }

    public Long getFriendsCount(String username) {
        return friendRepository.countByUsername(username);
    }

    public Long getFollowersCount(String username) {
        return followerRepository.countByUsername(username);
    }

    public void addFriend(User user, User surrogate) {
        Friend request = Friend.of(user, surrogate, Friend.State.PENDING, Friend.Action.REQUESTING);
        Friend pending = Friend.of(surrogate, user, Friend.State.PENDING, Friend.Action.REQUESTED);

        friendRepository.save(request);
        friendRepository.save(pending);
    }

    public boolean isFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateActiveState(user.getUsername(), surrogate.getUsername());
        return active.isPresent();
    }

    public boolean isFollowee(User user, User surrogate) {
        Optional<Follower> active = followerRepository.findByUserSurrogateActiveState(user.getUsername(), surrogate.getUsername());
        return active.isPresent();
    }

    public void acceptFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.PENDING);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.PENDING);

        if(active.isPresent() && passive.isPresent()) {
            if(active.get().getAction() == Friend.Action.REQUESTED) {
                active.get().setState(Friend.State.ACTIVE);
                passive.get().setState(Friend.State.ACTIVE);

                active.get().setAction(Friend.Action.ACCEPTING);
                passive.get().setAction(Friend.Action.ACCEPTED);

                friendRepository.save(active.get());
                friendRepository.save(passive.get());
            }
        }
    }

    public void blockFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.ACTIVE);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.ACTIVE);

        if(active.isPresent() && passive.isPresent()) {
            active.get().setState(Friend.State.BLOCKED);
            passive.get().setState(Friend.State.BLOCKED);

            active.get().setAction(Friend.Action.BLOCKING);
            passive.get().setAction(Friend.Action.BLOCKED);

            friendRepository.save(active.get());
            friendRepository.save(passive.get());
        }
    }

    public void unblockFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.BLOCKED);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.BLOCKED);

        if(active.isPresent() && passive.isPresent()) {
            if(active.get().getAction() == Friend.Action.BLOCKING) {
                active.get().setState(Friend.State.ACTIVE);
                passive.get().setState(Friend.State.ACTIVE);

                active.get().setAction(Friend.Action.UNBLOCKING);
                passive.get().setAction(Friend.Action.UNBLOCKED);

                friendRepository.save(active.get());
                friendRepository.save(passive.get());
            }
        }
    }

    public void cancelFriendRequest(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.PENDING);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.PENDING);

        if(passive.isPresent() && active.isPresent()) {
            if(active.get().getAction() == Friend.Action.REQUESTING) {
                active.get().setState(Friend.State.CANCELLED);
                passive.get().setState(Friend.State.CANCELLED);

                active.get().setAction(Friend.Action.CANCELLING);
                passive.get().setAction(Friend.Action.CANCELLED);

                friendRepository.save(active.get());
                friendRepository.save(passive.get());
            }
        }
    }

    public void ignoreFriendRequest(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.PENDING);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.PENDING);

        if(passive.isPresent() && active.isPresent()) {
            if(active.get().getAction() == Friend.Action.REQUESTED) {
                active.get().setState(Friend.State.IGNORED);
                passive.get().setState(Friend.State.IGNORED);

                active.get().setAction(Friend.Action.IGNORING);
                passive.get().setAction(Friend.Action.IGNORED);

                friendRepository.save(active.get());
                friendRepository.save(passive.get());
            }
        }
    }

    public void deleteFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(),
                surrogate.getUsername(), Friend.State.ACTIVE);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(),
                user.getUsername(), Friend.State.ACTIVE);

        if(passive.isPresent() && active.isPresent()) {
                active.get().setState(Friend.State.DELETED);
                passive.get().setState(Friend.State.DELETED);

                active.get().setAction(Friend.Action.DELETING);
                passive.get().setAction(Friend.Action.DELETED);

                friendRepository.save(active.get());
                friendRepository.save(passive.get());
        }
    }

    public void addFollowee(User user, User surrogate) {
        Follower follower = Follower.of(user, surrogate, Follower.State.ACTIVE);
        followerRepository.save(follower);
    }

    public void deleteFollowee(User user, User surrogate) {
        Optional<Follower> opt = followerRepository.findByUserSurrogateAndState(user.getUsername(),
                surrogate.getUsername(), Follower.State.ACTIVE);
        opt.ifPresent(follower -> {
            follower.setState(Follower.State.DELETED);
            followerRepository.save(follower);
        });
    }

    public void deleteFollowee(String username, String surrogate, Long id) {
        Optional<Follower> opt = followerRepository.findById(id);
        opt.ifPresent(follower -> {
            if(follower.getUser().getUsername().equals(username) && follower.getSurrogate().getUsername().equals(surrogate)) {
                follower.setState(Follower.State.DELETED);
                followerRepository.save(follower);
            }
        });
    }

    public void blockFollower(User user, User surrogate) {
        Optional<Follower> opt = followerRepository.findByUserSurrogateAndState(surrogate.getUsername(),
                user.getUsername(), Follower.State.ACTIVE);
        opt.ifPresent(follower -> {
            follower.setState(Follower.State.BLOCKED);
            followerRepository.save(follower);
        });
    }

    public void blockFollower(String username, String surrogate, Long id) {
        Optional<Follower> opt = followerRepository.findById(id);
        opt.ifPresent(follower -> {
            if(follower.getUser().getUsername().equals(surrogate) &&
                    follower.getSurrogate().getUsername().equals(username) &&
                    follower.getState() == Follower.State.ACTIVE) {
                follower.setState(Follower.State.BLOCKED);
                followerRepository.save(follower);
            }
        });
    }

    public void unblockFollower(User user, User surrogate) {
        Optional<Follower> opt = followerRepository.findByUserSurrogateAndState(surrogate.getUsername(),
                user.getUsername(), Follower.State.BLOCKED);
        opt.ifPresent(follower -> {
            follower.setState(Follower.State.ACTIVE);
            followerRepository.save(follower);
        });
    }

    public void unblockFollower(String username, String surrogate, Long id) {
        Optional<Follower> opt = followerRepository.findById(id);
        opt.ifPresent(follower -> {
            if(follower.getUser().getUsername().equals(surrogate)
                    && follower.getSurrogate().getUsername().equals(username) &&
                    follower.getState() == Follower.State.BLOCKED) {
                follower.setState(Follower.State.ACTIVE);
                followerRepository.save(follower);
            }
        });
    }

}
