package com.kikirikii;

import com.kikirikii.db.*;
import com.kikirikii.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

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

    @Test
    public void init() {
        logger.info("Init database with test data");
        createUsersAndSpaces();
        User amaru = userRepository.findByName("Amaru");
        createFriendsForUser(amaru);
        createFollowersForUser(amaru);
        createPostsAndMediaForUser(amaru);
        createCommentsAndLikesForPosts();
    }

    void createUsersAndSpaces() {
        userRepository.save(User.of("amaru.london@gmail.com", "Amaru", "password", "/static/users/amaru-pic.jpg"));
        userRepository.save(User.of("julia.jobs@gmail.com", "Julia", "password", "/static/users/user-01-200x200.jpg"));
        userRepository.save(User.of("marc.shell@gmail.com", "Marc", "password", "/static/users/user-02-200x200.jpg"));
        userRepository.save(User.of("peter.hummel@gmail.com", "Peter", "password", "/static/users/user-03-200x200.jpg"));
        userRepository.save(User.of("anita.huebsch@gmail.com", "Anita", "password", "/static/users/user-04-200x200.jpg"));
        userRepository.save(User.of("ana.kern@gmail.com", "Ana", "password", "/static/users/user-05-200x200.jpg"));
        userRepository.save(User.of("heidi.angeles@gmail.com", "Heidi", "password", "/static/users/user-06-200x200.jpg"));
        userRepository.save(User.of("jack.north@gmail.com", "Jack", "password", "/static/users/user-07-200x200.jpg"));
        userRepository.save(User.of("beate.schulz@gmail.com", "Beate", "password", "/static/users/user-08-200x200.jpg"));
        userRepository.save(User.of("thomas.earl@gmail.com", "Thomas", "password", "/static/users/user-09-200x200.jpg"));

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

    void createFriendsForUser(User user) {
        friendRepository.save(Friend.of(user, userRepository.findByName("Julia")));
        friendRepository.save(Friend.of(user, userRepository.findByName("Marc")));
        friendRepository.save(Friend.of(user, userRepository.findByName("Peter")));
        friendRepository.save(Friend.of(user, userRepository.findByName("Anita")));
    }

    void createFollowersForUser(User user) {
        followerRepository.save(Follower.of(user, userRepository.findByName("Ana")));
        followerRepository.save(Follower.of(user, userRepository.findByName("Heidi")));
        followerRepository.save(Follower.of(user, userRepository.findByName("Jack")));
        followerRepository.save(Follower.of(user, userRepository.findByName("Beate")));
        followerRepository.save(Follower.of(user, userRepository.findByName("Thomas")));
    }

    void createPostsAndMediaForUser(User user) {
        Optional<Space> home = spaceRepository.findHomeSpace(user.getId());
        
        MediaHelper media = new MediaHelper();

        new PostHelper(home.get(), user)
                .save("Hello from Helgoland", ":laughing: :laughing: Eine Frau hat immer Besuch von ihrem Liebhaber, während ihr Mann bei der Arbeit ist. Eines Tages versteckt sich der neunjährige Sohn im Schrank um zu beobachten, was die beiden denn so machen… " + "Auf einmal kommt der Ehemann überraschend nach Hause und die Frau versteckt auch ihren Liebhaber im Schrank: Der Sohn: 'Dunkel hier drinnen…' Der Mann (flüstert): 'Stimmt.' Der Sohn: 'Ich hab einen Fußball…' Der Mann: 'Schön für Dich.' Der Sohn: 'Willst Du den kaufen?' Der Mann: 'Nee, vielen Dank!' Der Sohn: 'Mein Vater ist draußen!' Der Mann: 'OK, wie viel?' Der Sohn: '250 Euro.'" +
                                "In den nächsten Wochen passiert es nochmal, dass der Sohn und der Liebhaber im gleichen Schrank enden. Der Sohn: 'Dunkel hier drinnen…' Der Mann (flüstert): 'Stimmt.' Der Sohn: 'Ich hab Turnschuhe.' Der Mann, in Erinnerung, gedanklich seufzend: 'Wieviel?' Der Sohn: '500 Euro.' Nach ein paar Tagen sagt der Vater zum Sohn: 'Nimm deinen Fußballsachen und lass uns eine Runde spielen.' Der Sohn: 'Geht nicht hab ich alles verkauft!'" +
                                "Der Vater: 'Für wie viel?' Der Sohn: 'Für 750 Euro.' Der Vater: 'Es ist unglaublich wie Du Deine Freunde betrügst, das ist viel mehr als die Sachen gekostet haben. Ich werde Dich zum Beichten in die Kirche bringen!' Der Vater bringt seinen Sohn in die Kirche, setzt ihn in den Beichtstuhl und schließt die Tür. " +
                                "Der Sohn: 'Dunkel hier drinnen…' Der Pfarrer: 'Hör auf mit der Scheisse!!!' :satisfied::satisfied:",
                        media.ramdom())
                .save("The fascinating world of Maria", "Wie gut, dass mich keiner denken hören kann., :grin::grin:", media.ramdom())
                .save("Empty Text", "", media.ramdom())
                .save("From Roberto - The Killer", "Es ist wichtig, :lollipop::lollipop:dass man den Leuten sagt was man denkt. Vor allem den Arschlöchern. :green_apple:", media.ramdom())
                .save("Wasserrohrbruch -Was nun ?", "Wenn Faulheit eine olympische Disziplin wäre, wäre ich vierter, damit ich nicht aufs Podest steigen muss.:flushed::flushed:", media.ramdom())
                .save("The meaning of life", "Warum freut sich eine Blondine so, wenn sie ein Puzzle nach 6 Monaten fertig hat? – Weil auf der Packung steht: 2-4 Jahre.:laughing::laughing::joy::joy::joy:", media.ramdom())
                .save("London by night..", "Heute abend liebe Freunde, just als ich mich zu meiner :stuck_out_tongue_winking_eye: nächtlichen Netflix Session begeben woll, wurde mir ganz Schwarz..", media.ramdom())
                .save("Warum Groko sch.. ist", "Hey was geht ? :blush: Netflix Session begeben woll, wurde mir ganz Schwarz..", media.ramdom())
                .save("Mar para Bolivia", ":stuck_out_tongue_winking_eye: Ich bin ein Mädchen :girl:, ich kann eifersüchtig sein ohne das wir überhaupt eine Beziehung begonnen haben...", media.ramdom())
                .save("Hello from Helgoland", ":moneybag: Heute hat mein nachbar um 3:00 Uhr nachts bei mir geklopft! Vor schreck wär mir fast die bohrmaschine aus der hand gefallen!! :moneybag: :moneybag: ", media.ramdom())
                .save("The fascinating world of Raymi", "This is a story about Maria, .. :heart: :heart: :heart:", media.ramdom())
                .save("", "No title", media.ramdom())
                .save("From Roberto - The Killer", "Not what you think :laughing: :heart_eyes:", media.ramdom())
                .save("", "Die Pflicht ruft, sag ihr ich ruf zurück :heart_eyes: meiner nächtlichen Netflix Session begeben woll, wurde mir ganz Schwarz..", media.ramdom())
                .save("The meaning of life", "Dinge kommen im Leben nicht mehr zurück. :exclamation::exclamation: Die Tage, die du erlebt hast. Die Erfahrungen, die du gemacht hast. Die Worte, die du benutzt hast. Die Chance, die du verpasst hast!..", media.ramdom())
                .save("London by night..", ":sunny: :sunny: Heute abend liebe Freunde, just als ich mich zu meiner nächtlichen Netflix Session begeben woll, wurde mir ganz Schwarz..", media.ramdom())
                .save("Warum Groko sch.. ist", "Sei lieber zu alt für den Scheiss als zu scheisse für dein Alter. :heart_eyes: ..", media.ramdom())
                .save("Wer denkt an Weihnachten?", "Noch zwei mal joggen, dann ist Weihnachten...:runner::runner::runner:", media.ramdom())
                .save("Eine unglaubliche Geschichte", ":heart: Lieber ein ehrlicher Teufel, als ein scheinheiliger Engel.. :smiling_imp::sunglasses::sunglasses:", media.ramdom());
    }

    void createCommentsAndLikesForPosts() {

        CommentHelper helper = new CommentHelper();
        postRepository.findAll().forEach(post -> {
            int count = (int) Math.floor((Math.random() * 5 + 1) - 1);
            for(int i = 0; i < count; i++) {
                commentRepository.save(Comment.of(post, helper.randomuser(), helper.randomcomment()));
            }
        });
    }

    class PostHelper {
        private Space space;
        private User user;

        public PostHelper(Space space, User user) {
            this.space = space;
            this.user = user;
        }

        public PostHelper save(String title, String text, String url) {
            postRepository.save(
                    Post.of(space, user, title, text).addMedia(Media.of(url, mediatype.apply(url)))
            );
            return this;
        }
    }

    class MediaHelper {
        private List<String> medialist;

        public MediaHelper() {
            medialist = Loader.load("medialist.txt");
        }

        public String ramdom() {
            int index = (int) Math.floor((Math.random() * medialist.size() + 1) - 1);
            return medialist.get(index);
        }
    }

    class CommentHelper {
        private List<String> comments;
        private List<User> users = new ArrayList<>();

        public CommentHelper() {
            comments = Loader.load("comments.txt");
            userRepository.findAll().forEach(user -> users.add(user));
        }

        public String randomcomment() {
            int index = (int) Math.floor((Math.random() * comments.size() + 1) - 1);
            return comments.get(index);
        }

        public User randomuser() {
            int index = (int) Math.floor((Math.random() * users.size() + 1) - 1);
            return users.get(index);
        }

    }

    static class Loader {
        private static ClassLoader loader = MediaHelper.class.getClassLoader();

        public static List<String> load(String filename) {
            List<String> data = new ArrayList<>();

            try {
                File file = new File(loader.getResource(filename).getFile());
                BufferedReader reader = new BufferedReader(new FileReader(file));
                for(String l = reader.readLine(); l != null; l = reader.readLine()) {
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
