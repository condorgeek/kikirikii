/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [RankingRequest.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 27.02.19 08:34
 */

package com.kikirikii.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RankingRequest {
    private Rank[] ranks;

    public RankingRequest() {}

    public Rank[] getRanks() {
        return ranks;
    }

    public void setRanks(Rank[] ranks) {
        this.ranks = ranks;
    }

    public static class Rank {
        private long spaceId;
        private int ranking;

        public Rank() {}

        public long getSpaceId() {
            return spaceId;
        }

        public void setSpaceId(long spaceId) {
            this.spaceId = spaceId;
        }

        public int getRanking() {
            return ranking;
        }

        public void setRanking(int ranking) {
            this.ranking = ranking;
        }
    }
}


