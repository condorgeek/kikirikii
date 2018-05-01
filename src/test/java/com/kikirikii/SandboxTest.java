package com.kikirikii;

import org.junit.Test;
import sun.jvm.hotspot.oops.Array;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public class SandboxTest {
    private static Logger logger = Logger.getLogger("sandboxTest");

//    @Test
    public void parsePicture() {
        List<String> medialist = Arrays.asList(
                "http://sosmehost.de/somedir/mythumb.jpg",
                "http://sosmehost.de/somedir/TOWN.JPG",
                "http://sosmehost.de/somedir/building.JPEG",
                "http://sosmehost.de/somedir/abc.png",
                "http://sosmehost.de/somedir/hotvid.vid",
                "http://youtube.de/somedir/129873192837",
                "http://sosmehost.de/somedir/strong3u3.PNG",
                "http://sosmehost.de/somedir/woman566.gif",
                "https://sosmehost.de/somedir/waterfall.mp4",
                "https://sosmehost.de/somedir/waterfall.mp4",
                "sosmehost.de/somedir/nice291.GIF"
                );

        medialist.stream().forEach(media ->{
                String isPicture = media.matches("(http|https)://(.)+([jpg|JPG|JPEG|jpeg|png|PNG|gif|GIF])") ? "PICTURE" : "VIDEO";
                logger.info( media + " is " + isPicture);
        });

    }
}
