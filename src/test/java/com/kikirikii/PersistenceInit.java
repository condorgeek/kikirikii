package com.kikirikii;

import com.kikirikii.repos.*;
import com.kikirikii.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersistenceInit {
    private Logger logger = Logger.getLogger("PersistenceInit");

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

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Test
    public void init() {
        logger.info("Init database with test data");

        createUsersAndSpaces();
        createFriendsForUser("amaru", new String[]{"julia", "marc", "peter", "anita"});
        createFollowersForUser("amaru", new String[]{"ana", "heidi", "jack", "beate", "thomas"});
        createPostsAndMediaForUser("amaru");
        createCommentsAndLikesForPosts();
    }

    void createUsersAndSpaces() {
        UserHelper helper = new UserHelper();

        helper.getUserList().forEach(u -> {
            try {
                userRepository.save(helper.parseUser(u).setUserData(helper.randomUserData()));
            } catch (Exception e) {
            }
        });

        userRepository.findAll().forEach(user -> {
            spaceRepository.save(Space.of(user,
                    user.getName() + "'s Space",
                    "This is a personal place for me and friends",
                    Space.Type.HOME));
            spaceRepository.save(Space.of(user,
                    user.getName() + "'s Global Space",
                    "This is a place for me, friends and followers",
                    Space.Type.GLOBAL));
        });
    }

    void createFriendsForUser(String username, String[] friends) {
        User user = userRepository.findByName(username);
        Stream.of(friends).forEach(friend -> {
            friendRepository.save(Friend.of(user, userRepository.findByName(friend)));
        });
    }

    void createFollowersForUser(String username, String[] followers) {
        User user = userRepository.findByName(username);
        Stream.of(followers).forEach(follower -> {
            followerRepository.save(Follower.of(user, userRepository.findByName(follower)));
        });
    }

    void createPostsAndMediaForUser(String username) {
        User user = userRepository.findByName(username);
        Optional<Space> global = spaceRepository.findGlobalSpace(user.getId());

        MediaHelper media = new MediaHelper();
        PostHelper postHelper = new PostHelper(global.get(), user);

        postHelper.postlist.forEach(postdata -> {
            Post post = postHelper.parsePost(postdata);
            postRepository.save(post.addMedia(media.randomMedia()));
        });
    }

    void createCommentsAndLikesForPosts() {

        CommentHelper helper = new CommentHelper();
        postRepository.findAll().forEach(post -> {
            int count = (int) Math.floor((Math.random() * 5 + 1) - 1);
            for (int i = 0; i < count; i++) {
                commentRepository.save(Comment.of(post, helper.randomUser(), helper.randomComment()));
            }

            count = (int) Math.floor(Math.random() * 20 + 1);
            for (int i = 0; i < count; i++) {
                likeRepository.save(Like.of(post, helper.randomUser(), helper.randomLike()));
            }
        });

        commentRepository.findAll().forEach(comment -> {
            int count = (int) Math.floor(Math.random() * 5 + 1);
            for (int i = 0; i < count; i++) {
                commentLikeRepository.save(CommentLike.of(comment, helper.randomUser(), helper.randomCommentLike()));
            }
        });
    }

    class UserHelper {
        private List<String> userlist;
        private List<String> userdata;

        public UserHelper() {
            userlist = Loader.load("userlist.txt");
            userdata = Loader.load("userdata.txt");
        }

        public List<String> getUserList() {
            return userlist;
        }

        public User parseUser(String data) throws NoSuchFieldError {
            try {
                String[] values = data.split(", ");
                return User.of(values[0], values[1], values[2], values[3]);

            } catch (Exception e) {
                e.printStackTrace();
                throw new NoSuchFieldError("Invalid user format");
            }
        }

        public UserData randomUserData() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            int index = (int) Math.floor((Math.random() * userdata.size() + 1) - 1);
            try {
                String[] values = userdata.get(index).split(", ");
               return  UserData.of(LocalDate.parse(values[0], formatter), values[1],
                        Address.of(values[2], values[3], values[4], values[5], values[6], values[7]));
            } catch (Exception e) {
                e.printStackTrace();
                throw new NoSuchFieldError("Invalid user format");
            }
        }
    }

    class PostHelper {
        private Space space;
        private User user;
        List<String> postlist;

        public PostHelper(Space space, User user) {
            postlist = Loader.load("postlist.txt");
            this.space = space;
            this.user = user;
        }

        public Post parsePost(String data) throws NoSuchFieldError {
            try {
                String[] values = data.split("\\| ");
                return Post.of(space, user, values[0], values[1]);

            } catch (Exception e) {
                e.printStackTrace();
                throw new NoSuchFieldError("Invalid post format");
            }
        }
    }

    class MediaHelper {
        private List<String> medialist;

        public MediaHelper() {
            medialist = Loader.load("medialist.txt");
        }

        public Media randomMedia() {
            int index = (int) Math.floor((Math.random() * medialist.size() + 1) - 1);
            return Media.of(medialist.get(index), mediatype.apply(medialist.get(index)));
        }
    }

    class CommentHelper {
        private List<String> comments;
        private List<User> users = new ArrayList<>();

        public CommentHelper() {
            comments = Loader.load("comments.txt");
            userRepository.findAll().forEach(user -> users.add(user));
        }

        public String randomComment() {
            int index = (int) Math.floor((Math.random() * comments.size() + 1) - 1);
            return comments.get(index);
        }

        public User randomUser() {
            int index = (int) Math.floor((Math.random() * users.size() + 1) - 1);
            return users.get(index);
        }

        public Like.Type randomLike() {
            int index = (int) Math.floor((Math.random() * Like.Type.values().length + 1) - 1);
            return Like.Type.values()[index];
        }

        public CommentLike.Type randomCommentLike() {
            int index = (int) Math.floor((Math.random() * CommentLike.Type.values().length + 1) - 1);
            return CommentLike.Type.values()[index];
        }
    }

    static class Loader {
        private static ClassLoader loader = MediaHelper.class.getClassLoader();

        public static List<String> load(String filename) {
            List<String> data = new ArrayList<>();

            try {
                File file = new File(loader.getResource(filename).getFile());
                BufferedReader reader = new BufferedReader(new FileReader(file));
                for (String l = reader.readLine(); l != null; l = reader.readLine()) {
                    data.add(l);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    Function<String, Media.Type> mediatype = (url) -> {
//        return url.matches("(http|https)://(.)+([jpg|JPG|JPEG|jpeg|png|PNG|gif|GIF])") ? Media.Type.PICTURE : Media.Type.VIDEO;
        return url.matches(".+([jpg|JPG|JPEG|jpeg|png|PNG|gif|GIF])") ? Media.Type.PICTURE : Media.Type.VIDEO;
    };

}
