package com.kikirikii;

import com.kikirikii.db.*;
import com.kikirikii.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
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
        createCommentsAndLikesForPost(null);
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

        new PostHelper(home.get(), user)
                .save("Hello from Helgoland", ":laughing: :laughing: Eine Frau hat immer Besuch von ihrem Liebhaber, während ihr Mann bei der Arbeit ist. Eines Tages versteckt sich der neunjährige Sohn im Schrank um zu beobachten, was die beiden denn so machen… " + "Auf einmal kommt der Ehemann überraschend nach Hause und die Frau versteckt auch ihren Liebhaber im Schrank: Der Sohn: 'Dunkel hier drinnen…' Der Mann (flüstert): 'Stimmt.' Der Sohn: 'Ich hab einen Fußball…' Der Mann: 'Schön für Dich.' Der Sohn: 'Willst Du den kaufen?' Der Mann: 'Nee, vielen Dank!' Der Sohn: 'Mein Vater ist draußen!' Der Mann: 'OK, wie viel?' Der Sohn: '250 Euro.'" +
                                "In den nächsten Wochen passiert es nochmal, dass der Sohn und der Liebhaber im gleichen Schrank enden. Der Sohn: 'Dunkel hier drinnen…' Der Mann (flüstert): 'Stimmt.' Der Sohn: 'Ich hab Turnschuhe.' Der Mann, in Erinnerung, gedanklich seufzend: 'Wieviel?' Der Sohn: '500 Euro.' Nach ein paar Tagen sagt der Vater zum Sohn: 'Nimm deinen Fußballsachen und lass uns eine Runde spielen.' Der Sohn: 'Geht nicht hab ich alles verkauft!'" +
                                "Der Vater: 'Für wie viel?' Der Sohn: 'Für 750 Euro.' Der Vater: 'Es ist unglaublich wie Du Deine Freunde betrügst, das ist viel mehr als die Sachen gekostet haben. Ich werde Dich zum Beichten in die Kirche bringen!' Der Vater bringt seinen Sohn in die Kirche, setzt ihn in den Beichtstuhl und schließt die Tür. " +
                                "Der Sohn: 'Dunkel hier drinnen…' Der Pfarrer: 'Hör auf mit der Scheisse!!!' :satisfied::satisfied:",
                        media.get())
                .save("The fascinating world of Maria", "Wie gut, dass mich keiner denken hören kann., :grin::grin:", media.get())
                .save("Empty Text", "", media.get())
                .save("From Roberto - The Killer", "Es ist wichtig, :lollipop::lollipop:dass man den Leuten sagt was man denkt. Vor allem den Arschlöchern. :green_apple:", media.get())
                .save("Wasserrohrbruch -Was nun ?", "Wenn Faulheit eine olympische Disziplin wäre, wäre ich vierter, damit ich nicht aufs Podest steigen muss.:flushed::flushed:", media.get())
                .save("The meaning of life", "Warum freut sich eine Blondine so, wenn sie ein Puzzle nach 6 Monaten fertig hat? – Weil auf der Packung steht: 2-4 Jahre.:laughing::laughing::joy::joy::joy:", media.get())
                .save("London by night..", "Heute abend liebe Freunde, just als ich mich zu meiner :stuck_out_tongue_winking_eye: nächtlichen Netflix Session begeben woll, wurde mir ganz Schwarz..", media.get())
                .save("Warum Groko sch.. ist", "Hey was geht ? :blush: Netflix Session begeben woll, wurde mir ganz Schwarz..", media.get())
                .save("Mar para Bolivia", ":stuck_out_tongue_winking_eye: Ich bin ein Mädchen :girl:, ich kann eifersüchtig sein ohne das wir überhaupt eine Beziehung begonnen haben...", media.get())
                .save("Hello from Helgoland", ":moneybag: Heute hat mein nachbar um 3:00 Uhr nachts bei mir geklopft! Vor schreck wär mir fast die bohrmaschine aus der hand gefallen!! :moneybag: :moneybag: ", media.get())
                .save("The fascinating world of Raymi", "This is a story about Maria, .. :heart: :heart: :heart:", media.get())
                .save("", "No title", media.get())
                .save("From Roberto - The Killer", "Not what you think :laughing: :heart_eyes:", media.get())
                .save("", "Die Pflicht ruft, sag ihr ich ruf zurück :heart_eyes: meiner nächtlichen Netflix Session begeben woll, wurde mir ganz Schwarz..", media.get())
                .save("The meaning of life", "Dinge kommen im Leben nicht mehr zurück. :exclamation::exclamation: Die Tage, die du erlebt hast. Die Erfahrungen, die du gemacht hast. Die Worte, die du benutzt hast. Die Chance, die du verpasst hast!..", media.get())
                .save("London by night..", ":sunny: :sunny: Heute abend liebe Freunde, just als ich mich zu meiner nächtlichen Netflix Session begeben woll, wurde mir ganz Schwarz..", media.get())
                .save("Warum Groko sch.. ist", "Sei lieber zu alt für den Scheiss als zu scheisse für dein Alter. :heart_eyes: ..", media.get())
                .save("Wer denkt an Weihnachten?", "Noch zwei mal joggen, dann ist Weihnachten...:runner::runner::runner:", media.get())
                .save("Eine unglaubliche Geschichte", ":heart: Lieber ein ehrlicher Teufel, als ein scheinheiliger Engel.. :smiling_imp::sunglasses::sunglasses:", media.get());

    }

    void savePost() {

    }

    void createCommentsAndLikesForPost(Post post) {

    }

    class PostHelper {
        private Space space;
        private User user;

        public PostHelper(Space space, User user) {
            this.space = space;
            this.user = user;
        }

        public PostHelper save(String title, String text, String media) {
            Post post = postRepository.save(Post.of(space, user, title, text));
            post.addMedia(Media.of(post, media, isPicture(media)));
            return this;
        }

        private Media.Type isPicture(String media) {
            return (media.matches("(http|https)://(.)+([jpg|JPG|JPEG|jpeg|png|PNG|gif|GIF])")) ? Media.Type.PICTURE : Media.Type.VIDEO;
        }
    }

    static List<String> urls = Arrays.asList(
            "https://www.youtube.com/embed/zpOULjyy-n8?rel=0",
            "https://www.youtube.com/watch?v=2HH4CiAjjvM",
            "https://soundcloud.com/thomasjackmusic/gabriel-rios-gold-thomas-jack-remix",
            "https://vimeo.com/65428790",
            "https://www.youtube.com/watch?v=35ChA32HUiI",
            "https://vimeo.com/148246925",
            "https://www.youtube.com/watch?v=U2lZIUZ_ZwU",
            "https://vimeo.com/12032183",
            "https://www.youtube.com/watch?v=EjVyAHFYrUQ",
            "https://vimeo.com/26847728",
            "https://vimeo.com/99557169",
            "https://vimeo.com/198032459",
            "https://soundcloud.com/salsard/chiquito-team-band-tengo-que-colgar-salsardcom2017?in=salsard/sets/chiquito-team-band",
            "https://www.youtube.com/watch?v=GqCXG9a2qi8",
            "https://vimeo.com/7519184",
            "https://soundcloud.com/salsard/davis-daniel-la-historiadora-salsardcom2018",
            "https://vimeo.com/209457922",
            "https://www.youtube.com/watch?v=GqCXG9a2qi8&list=RDGqCXG9a2qi8&t=4",
            "https://vimeo.com/9376183",
            "https://soundcloud.com/dananggg/overwerk-daybreak-gopro-hero3-edit",
            "https://vimeo.com/45211334",
            "https://soundcloud.com/jovas-drumms-sc/sets/reggae",
            "https://www.youtube.com/watch?v=SC7Tli683GE&list=RDGqCXG9a2qi8&index=23",
            "https://www.youtube.com/watch?v=9KgAyC6gxKI",
            "https://vimeo.com/114924438",
            "https://www.youtube.com/watch?v=TIwv3eh4Mq4",
            "https://vimeo.com/223872691",
            "https://vimeo.com/111356679",
            "https://soundcloud.com/thomasjackmusic/little-talks-of-monsters-and",
            "https://www.youtube.com/watch?time_continue=1&v=QL6C9LwWC30",
            "https://vimeo.com/196236491",
            "https://soundcloud.com/evelyndiederich/sets/gopro-vid-music",
            "https://vimeo.com/56918907",
            "https://soundcloud.com/javiercabanillas/conga-jam-salsa-dura-dj-good",
            "https://www.youtube.com/watch?time_continue=11&v=TR9nEjlVBLw",
            "https://vimeo.com/14381111",
            "https://soundcloud.com/salsard/chiquito-team-band-los-creadores-del-sonido-salsardcom?in=salsard/sets/chiquito-team-band",
            "https://soundcloud.com/risedownmusic/daybreak-gopro-hero3-edit-with"
    );

    Supplier<String> media = () -> {
        int index = (int) Math.floor((Math.random() * urls.size() + 1) - 1);
        return urls.get(index);
    };

}
