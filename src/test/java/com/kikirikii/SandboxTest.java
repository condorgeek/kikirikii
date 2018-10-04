/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SandboxTest.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 07.06.18 18:02
 */

package com.kikirikii;

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

//    @Test
    public void parsePosts() {
        List<String> postlist = PersistenceInit.Loader.load("postlist.txt");

        postlist.forEach(line -> {
            String[] values = line.split("\\| ");
            logger.info(values[0] + " ::: " + values[1]);

        });
    }

}
