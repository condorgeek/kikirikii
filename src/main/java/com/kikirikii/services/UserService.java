/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [UserService.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 17.09.18 19:23
 */

package com.kikirikii.services;

import com.kikirikii.exceptions.DuplicateResourceException;
import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.*;
import com.kikirikii.model.dto.UserProspect;
import com.kikirikii.repos.*;
import com.sun.corba.se.impl.resolver.FileResolverImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
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
        try {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                return user.get();
            }
        } catch (Exception e) { /*empty*/ }

        throw new InvalidResourceException("User " + username + " is invalid.");
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
        if(global.isPresent()) {
            return postRepository.findAllBySpaceId(global.get().getId()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Post> getUserHomePosts(User user) {
        Optional<Space> home = userRepository.findHomeSpace(user.getId());
        if(home.isPresent()) {
            return postRepository.findAllBySpaceId(home.get().getId()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Post> getSpacePosts(User user, Long spaceId) {
        Optional<Space> space = spaceRepository.findById(spaceId);
         if(space.isPresent()) {
             return postRepository.findAllBySpaceId(space.get().getId()).collect(Collectors.toList());
         }
         return Collections.emptyList();
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
        return friendRepository.countActiveBlockedByUsername(username);
    }

    public Long getFollowersCount(String username) {
        return followerRepository.countActiveBlockedFollowers(username);
    }

    /* 0 - request, 1- pending */
    public Friend[] addFriend(User user, User surrogate) {

        Friend request = Friend.of(user, surrogate, Friend.State.PENDING, Friend.Action.REQUESTING);
        Friend pending = Friend.of(surrogate, user, Friend.State.PENDING, Friend.Action.REQUESTED);

        Chat chat = Chat.of(request, pending);
        request.setChat(chat);
        pending.setChat(chat);

        return new Friend[]{friendRepository.save(request), friendRepository.save(pending)};
    }

    public boolean isFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateActiveState(user.getUsername(), surrogate.getUsername());
        return active.isPresent();
    }

    public boolean isFriend(String username, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateActiveState(username, surrogate.getUsername());
        return active.isPresent();
    }

    public Friend getFriend(String username, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateActiveState(username, surrogate.getUsername());
        return active.orElse(null);
    }

    public boolean isFollowee(User user, User surrogate) {
        Optional<Follower> active = followerRepository.findByUserSurrogateActiveState(user.getUsername(), surrogate.getUsername());
        return active.isPresent();
    }

    public boolean isFollowee(String username, User surrogate) {
        Optional<Follower> active = followerRepository.findByUserSurrogateActiveState(username, surrogate.getUsername());
        return active.isPresent();
    }

    public Follower getFollowee(String username, User surrogate) {
        Optional<Follower> active = followerRepository.findByUserSurrogateActiveState(username, surrogate.getUsername());
        return active.orElse(null);
    }

    public Friend[] acceptFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.PENDING);
        Optional<Friend> accepted = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.PENDING);

        if(active.isPresent() && accepted.isPresent()) {
            if(active.get().getAction() == Friend.Action.REQUESTED) {

                active.get().setState(Friend.State.ACTIVE);
                accepted.get().setState(Friend.State.ACTIVE);

                active.get().setAction(Friend.Action.ACCEPTING);
                accepted.get().setAction(Friend.Action.ACCEPTED);

                return new Friend[]{friendRepository.save(active.get()), friendRepository.save(accepted.get())};
            }
        }

        throw new InvalidResourceException("Cannot find either user " + user.getUsername() + " or friend " + surrogate.getUsername());
    }

    public Friend blockFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.ACTIVE);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.ACTIVE);

        if(active.isPresent() && passive.isPresent()) {
            active.get().setState(Friend.State.BLOCKED);
            passive.get().setState(Friend.State.BLOCKED);

            active.get().setAction(Friend.Action.BLOCKING);
            passive.get().setAction(Friend.Action.BLOCKED);

            friendRepository.save(active.get());
            return friendRepository.save(passive.get());
        }
        return null;
    }

    public Friend unblockFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.BLOCKED);
        Optional<Friend> passive = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.BLOCKED);

        if(active.isPresent() && passive.isPresent()) {
            if(active.get().getAction() == Friend.Action.BLOCKING) {
                active.get().setState(Friend.State.ACTIVE);
                passive.get().setState(Friend.State.ACTIVE);

                active.get().setAction(Friend.Action.UNBLOCKING);
                passive.get().setAction(Friend.Action.UNBLOCKED);

                friendRepository.save(active.get());
                return friendRepository.save(passive.get());
            }
        }
        return null;
    }

    public Friend[] cancelFriendRequest(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.PENDING);
        Optional<Friend> cancelled = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.PENDING);

        if(cancelled.isPresent() && active.isPresent()) {
            if(active.get().getAction() == Friend.Action.REQUESTING) {
                active.get().setState(Friend.State.CANCELLED);
                cancelled.get().setState(Friend.State.CANCELLED);

                active.get().setAction(Friend.Action.CANCELLING);
                cancelled.get().setAction(Friend.Action.CANCELLED);

                return new Friend[] {friendRepository.save(active.get()), friendRepository.save(cancelled.get())};
            }
        }
        return null;
    }

    public Friend[] ignoreFriendRequest(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.PENDING);
        Optional<Friend> ignored = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.PENDING);

        if(ignored.isPresent() && active.isPresent()) {
            if(active.get().getAction() == Friend.Action.REQUESTED) {
                active.get().setState(Friend.State.IGNORED);
                ignored.get().setState(Friend.State.IGNORED);

                active.get().setAction(Friend.Action.IGNORING);
                ignored.get().setAction(Friend.Action.IGNORED);

                return new Friend[] {friendRepository.save(active.get()), friendRepository.save(ignored.get())};
            }
        }

        throw new InvalidResourceException("Cannot find either user " + user.getUsername() + " or friend " + surrogate.getUsername());
    }

    public Friend[] deleteFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(),
                surrogate.getUsername(), Friend.State.ACTIVE);
        Optional<Friend> deleted = friendRepository.findBySurrogateAndState(surrogate.getUsername(),
                user.getUsername(), Friend.State.ACTIVE);

        if (deleted.isPresent() && active.isPresent()) {
            active.get().setState(Friend.State.DELETED);
            deleted.get().setState(Friend.State.DELETED);

            active.get().setAction(Friend.Action.DELETING);
            deleted.get().setAction(Friend.Action.DELETED);

            return new Friend[]{friendRepository.save(active.get()), friendRepository.save(deleted.get())};
        }

        throw new InvalidResourceException("Cannot find either user " + user.getUsername() + " or friend " + surrogate.getUsername());
    }

    public Follower addFollowee(User user, User surrogate) {
        Follower follower = Follower.of(user, surrogate, Follower.State.ACTIVE);
        return followerRepository.save(follower);
    }

    public Follower deleteFollowee(User user, User surrogate) {
        Optional<Follower> follower = followerRepository.findByUserSurrogateAndState(user.getUsername(),
                surrogate.getUsername(), Follower.State.ACTIVE);
       if(follower.isPresent()) {
            follower.get().setState(Follower.State.DELETED);
            return followerRepository.save(follower.get());
        }
        return null;
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

    public Follower blockFollower(User user, User surrogate) {
        Optional<Follower> follower = followerRepository.findByUserSurrogateAndState(surrogate.getUsername(),
                user.getUsername(), Follower.State.ACTIVE);
       if(follower.isPresent()) {
            follower.get().setState(Follower.State.BLOCKED);
            return followerRepository.save(follower.get());
        }
       return null;
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

    public Follower unblockFollower(User user, User surrogate) {
        Optional<Follower> follower = followerRepository.findByUserSurrogateAndState(surrogate.getUsername(),
                user.getUsername(), Follower.State.BLOCKED);
        if(follower.isPresent()) {
            follower.get().setState(Follower.State.ACTIVE);
            return followerRepository.save(follower.get());
        }
        return null;
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
