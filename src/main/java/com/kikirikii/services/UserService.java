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
import com.kikirikii.exceptions.InvalidAuthorizationException;
import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.exceptions.OpNotAllowedException;
import com.kikirikii.model.*;
import com.kikirikii.model.dto.UserRequest;
import com.kikirikii.model.enums.State;
import com.kikirikii.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserService() {
        logger.info("Initialized");
    }

    public User getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }

        throw new InvalidResourceException("User " + username + " is invalid.");
    }

    public User getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new InvalidResourceException("User with id " + userId + " is invalid.");
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(String username, UserRequest request) {
        return createUser(request.createUser());
    }

    public User createUser(User user) {
        if(findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException(user.getUsername() + " Username already exists.");
        }

        if(findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException(user.getEmail() + " Email already associated to user.");
        }

        try {
            return userRepository.save(user);

        } catch(Exception e) {
            throw new InvalidResourceException("User cannot be created. " + e.getMessage());
        }
    }

    /* Achtung - must delete associated entities ie. posts, comments, likes etc before this call */
    public User deleteUser(User user) {

        if(postRepository.countActiveByUserId(user.getId()) > 0) {
            throw new OpNotAllowedException(user.getUsername() + " has post entries active. Delete those first.");
        }

        userRepository.findHomeSpaceById(user.getId()).ifPresent(home -> {
            home.setState(State.DELETED);
            spaceRepository.save(home);
        });

        userRepository.findGlobalSpaceById(user.getId()).ifPresent(global -> {
            global.setState(State.DELETED);
            spaceRepository.save(global);
        });

        user.getRoles().forEach(role -> {
            role.setState(State.DELETED);
        });

        user.getUserData().setState(State.DELETED);

        spaceRepository.findActiveByUserId(user.getId()).forEach(space -> {
            space.setState(State.DELETED);
            spaceRepository.save(space);
        });

        user.setState(State.DELETED);
        return userRepository.save(user);
    }

    public User updateUser(User user, UserRequest request) {
        User updated = request.updateUser(user);
        return userRepository.save(updated);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public Stream<User> searchByTermAsStream(String term, Integer size) {
        return userRepository.searchActiveByTerm(term);
    }

    public void createPublicSpaces(User user) {

        if(spaceRepository.findHomeSpace(user.getId()).isPresent()) {
            throw new OpNotAllowedException(user.getUsername() + " has already a home space");
        }

        try {
            spaceRepository.save(Space.of(user,
                    user.getUsername() + " Home", "", Space.Type.HOME));
            spaceRepository.save(Space.of(user,
                    user.getUsername() + " Global", "", Space.Type.GLOBAL));

        } catch (Exception e) {
            throw new InvalidResourceException("User spaces cannot be created. " + e.getMessage());
        }
    }


    public void createPublicSpaces(User user, String cover, String description) {

        if(spaceRepository.findHomeSpace(user.getId()).isPresent()) {
            throw new OpNotAllowedException(user.getUsername() + " has already a home space");
        }

        // TODO support for space media array
        try {
            spaceRepository.save(Space.of(user,
                    user.getUsername() + " Home", cover, null, null, description, Space.Type.HOME, Space.Access.PUBLIC));
            spaceRepository.save(Space.of(user,
                    user.getUsername() + " Global", cover, null, null, description, Space.Type.GLOBAL, Space.Access.PUBLIC));

        } catch (Exception e) {
            throw new InvalidResourceException("User spaces cannot be created. " + e.getMessage());
        }
    }

    public Post addPost(Space space, User user, String title, String text, List<Media> media) {
        return postRepository.save(Post.of(space, user, title, text, media));
    }

    public Post addPost(Space space, User user, String title, String text, List<Media> media, int ranking) {
        return postRepository.save(Post.of(space, user, title, text, media, ranking));
    }

    public Post addPost(Space space, User user, String title, String text, Media media) {
        return postRepository.save(Post.of(space, user, title, text, Collections.singletonList(media)));
    }

    public Post addPost(Space space, User user, String title, String text, Media media, int ranking) {
        return postRepository.save(Post.of(space, user, title, text, Collections.singletonList(media), ranking));
    }

    public Post incrementPostRanking(Post post) {
        return postRepository.save(post.incrementRanking());
    }

    public Post updatePost(Post post, User user, String title, String text, List<Media> media) {
        if (post.getUser().getUsername().equals(user.getUsername())) {

            post.setTitle(title);
            post.setText(text);
            if(media != null) media.forEach(post::addMedia);

            return postRepository.save(post);
        }
        throw new InvalidAuthorizationException(user.getUsername() + " is not post author.");
    }

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post deleteMedia(Post post, Media media) {
        Post updated = post.removeMedia(media);
        return postRepository.save(updated);
    }

    public Post sharePost(Space space, User user, Post post, String comment) {
        String text = "** Post shared from " + post.getUser().getFullname() + " ** " + post.getText();

        Post shared = Post.of(space, user, post.getTitle(), text);
        shared.setState(State.SHARED);
        shared.setFrom(post.getUser());
        shared.setComment(comment);

        List<Media> medialist = post.getMedia().stream().map(m -> {
            Media media = Media.of(shared, m.getUrl(), m.getType(), m.getPosition());
            media.setThumbnail(m.getThumbnail());
            media.setUsername(m.getUsername());
            media.setState(State.SHARED);
            return media;
        }).collect(Collectors.toList());

        shared.setMedia(medialist);

        return postRepository.save(shared);
    }

    public Space getHomeSpace(String name) {
        Optional<Space> home = userRepository.findHomeSpaceByName(name);
        if (home.isPresent()) {
            return home.get();
        }
        throw new InvalidResourceException("Invalid user name or user has no home space " + name);
    }

    public Optional<Space> findHomeSpace(String name) {
        return userRepository.findHomeSpaceByName(name);
    }

    public Space getHomeSpace(Long id) {
        Optional<Space> home = userRepository.findHomeSpaceById(id);
        if (home.isPresent()) {
            return home.get();
        }
        throw new InvalidResourceException("Invalid user name or user has no home space id=" + id);
    }

    public Space updateSpace(Space space) {
        return spaceRepository.save(space);
    }

    public Space getGlobalSpace(String name) {
        Optional<Space> global = userRepository.findGlobalSpaceByName(name);
        if(global.isPresent()) {
            return global.get();
        }
        throw new InvalidResourceException("Invalid user name or user has no global space " + name);
    }

    public Space getGlobalSpace(Long id) {
        Optional<Space> global = userRepository.findGlobalSpaceById(id);
        if(global.isPresent()) {
            return global.get();
        }
        throw new InvalidResourceException("Invalid user name or user has no global space id=" + id);
    }

    public List<Post> getUserGlobalPosts(User user) {
        Optional<Space> global = userRepository.findGlobalSpace(user.getId());
        if(global.isPresent()) {
            return postRepository.findActiveBySpaceId(global.get().getId()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Post> getUserHomePosts(User user) {
        Optional<Space> home = userRepository.findHomeSpace(user.getId());
        if(home.isPresent()) {
            return postRepository.findActiveBySpaceId(home.get().getId()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Page<Post> getPageableHomePosts(User user, int page, int size) {
        Optional<Space> home = userRepository.findHomeSpace(user.getId());
        if(home.isPresent()) {
            return postRepository.findActivePageBySpaceId(home.get().getId(), PageRequest.of(page, size));
        }
        return Page.empty();
    }

    public List<Media> getUserSpaceMedia(User user, Space space) {
        return mediaRepository.findMediaByUserIdAndSpaceId(user.getId(), space.getId());
    }

    public List<Post> getSpacePosts(Long spaceId) {
        Optional<Space> space = spaceRepository.findById(spaceId);
         if(space.isPresent()) {
             return postRepository.findActiveBySpaceId(space.get().getId()).collect(Collectors.toList());
         }
         return Collections.emptyList();
    }

    public Page<Post> getPageableSpacePosts(Long spaceId, int page, int size) {
        Optional<Space> space = spaceRepository.findById(spaceId);
        if(space.isPresent()) {
            return postRepository.findActivePageBySpaceId(space.get().getId(), PageRequest.of(page, size));
        }
        return Page.empty();
    }

    public Post getPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);

        if(post.isPresent()) {
            return post.get();
        }

        throw new InvalidResourceException("Cannot find post with id " + postId);
    }

    public Post deletePostById(Long postId) {
        Post post = getPostById(postId);
        post.setState(State.DELETED);
        post.getMedia().forEach(media -> media.setState(State.DELETED));

        return postRepository.save(post);
    }

    public Post hidePostById(Long postId) {
        Post post = getPostById(postId);
        post.setState(State.HIDDEN);

        return postRepository.save(post);
    }

    public Post blockPostById(Long postId) {
        Post post = getPostById(postId);
        post.setState(State.BLOCKED);

        return postRepository.save(post);
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

    public Friend[] blockFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.ACTIVE);
        Optional<Friend> blocked = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.ACTIVE);

        if(active.isPresent() && blocked.isPresent()) {
            active.get().setState(Friend.State.BLOCKED);
            blocked.get().setState(Friend.State.BLOCKED);

            active.get().setAction(Friend.Action.BLOCKING);
            blocked.get().setAction(Friend.Action.BLOCKED);

            return new Friend[] {friendRepository.save(active.get()), friendRepository.save(blocked.get())};
        }

        throw new InvalidResourceException("Cannot find either user " + user.getUsername() + " or friend " + surrogate.getUsername());
    }

    public Friend[] unblockFriend(User user, User surrogate) {
        Optional<Friend> active = friendRepository.findBySurrogateAndState(user.getUsername(), surrogate.getUsername(), Friend.State.BLOCKED);
        Optional<Friend> unblocked = friendRepository.findBySurrogateAndState(surrogate.getUsername(), user.getUsername(), Friend.State.BLOCKED);

        if(active.isPresent() && unblocked.isPresent()) {
            if(active.get().getAction() == Friend.Action.BLOCKING) {
                active.get().setState(Friend.State.ACTIVE);
                unblocked.get().setState(Friend.State.ACTIVE);

                active.get().setAction(Friend.Action.UNBLOCKING);
                unblocked.get().setAction(Friend.Action.UNBLOCKED);

                return new Friend[] {friendRepository.save(active.get()), friendRepository.save(unblocked.get())};
            }
        }

        throw new InvalidResourceException("Cannot find either user " + user.getUsername() + " or friend " + surrogate.getUsername());
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
        Follower follower = Follower.of(user, surrogate, State.ACTIVE);
        return followerRepository.save(follower);
    }

    public Follower deleteFollowee(User user, User surrogate) {
        Optional<Follower> follower = followerRepository.findByUserSurrogateAndState(user.getUsername(),
                surrogate.getUsername(), State.ACTIVE);
       if(follower.isPresent()) {
            follower.get().setState(State.DELETED);
            return followerRepository.save(follower.get());
        }
        return null;
    }

    public void deleteFollowee(String username, String surrogate, Long id) {
        Optional<Follower> opt = followerRepository.findById(id);
        opt.ifPresent(follower -> {
            if(follower.getUser().getUsername().equals(username) && follower.getSurrogate().getUsername().equals(surrogate)) {
                follower.setState(State.DELETED);
                followerRepository.save(follower);
            }
        });
    }

    public Follower blockFollower(User user, User surrogate) {
        Optional<Follower> follower = followerRepository.findByUserSurrogateAndState(surrogate.getUsername(),
                user.getUsername(), State.ACTIVE);
       if(follower.isPresent()) {
            follower.get().setState(State.BLOCKED);
            return followerRepository.save(follower.get());
        }
       return null;
    }

    public void blockFollower(String username, String surrogate, Long id) {
        Optional<Follower> opt = followerRepository.findById(id);
        opt.ifPresent(follower -> {
            if(follower.getUser().getUsername().equals(surrogate) &&
                    follower.getSurrogate().getUsername().equals(username) &&
                    follower.getState() == State.ACTIVE) {
                follower.setState(State.BLOCKED);
                followerRepository.save(follower);
            }
        });
    }

    public Follower unblockFollower(User user, User surrogate) {
        Optional<Follower> follower = followerRepository.findByUserSurrogateAndState(surrogate.getUsername(),
                user.getUsername(), State.BLOCKED);
        if(follower.isPresent()) {
            follower.get().setState(State.ACTIVE);
            return followerRepository.save(follower.get());
        }
        return null;
    }

    public void unblockFollower(String username, String surrogate, Long id) {
        Optional<Follower> opt = followerRepository.findById(id);
        opt.ifPresent(follower -> {
            if(follower.getUser().getUsername().equals(surrogate)
                    && follower.getSurrogate().getUsername().equals(username) &&
                    follower.getState() == State.BLOCKED) {
                follower.setState(State.ACTIVE);
                followerRepository.save(follower);
            }
        });
    }

}
