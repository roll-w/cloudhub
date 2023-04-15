package org.cloudhub.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author RollW
 */
public class KeywordsScorer {
    private final Keywords keywords;

    public KeywordsScorer(Keywords keywords) {
        this.keywords = keywords;
    }

    public static final class Rank implements Comparable<Rank> {
        private int score;
        private final String group;
        private final List<String> keywords = new ArrayList<>();

        public Rank(int score, String group) {
            this.score = score;
            this.group = group;
        }

        public int getScore() {
            return score;
        }

        public String getGroup() {
            return group;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        private void increaseScore(int score) {
            this.score += score;
        }

        private void addKeyword(String keyword) {
            keywords.add(keyword);
        }

        private void plusKeyword(Keywords.Keyword keyword) {
            increaseScore(keyword.weight());
            addKeyword(keyword.word());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Rank) obj;
            return this.score == that.score &&
                    Objects.equals(this.group, that.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(score, group);
        }

        @Override
        public String toString() {
            return "Rank[" +
                    "score=" + score + ", " +
                    "group=" + group + "," +
                    "keywords=" + keywords + ']';
        }

        @Override
        public int compareTo(Rank o) {
            return o.score - this.score;
        }
    }

    public List<Rank> score(String text) {
        List<Keywords.KeywordsGroup> keywordsGroups =
                keywords.listGroups();
        List<Rank> ranks = new ArrayList<>();
        for (Keywords.KeywordsGroup keywordsGroup : keywordsGroups) {
            List<Keywords.Keyword> keywords = keywordsGroup.keywords();
            Rank rank = new Rank(0, keywordsGroup.name());
            for (Keywords.Keyword keyword : keywords) {
                if (text.contains(keyword.word())) {
                    rank.plusKeyword(keyword);
                }
            }
            if (rank.getScore() > 0) {
                ranks.add(rank);
            }
        }
        ranks.sort(Rank::compareTo);
        return ranks;
    }
}
