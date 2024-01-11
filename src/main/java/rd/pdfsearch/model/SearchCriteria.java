package rd.pdfsearch.model;

import java.util.Collections;
import java.util.List;

/**
 * Defines search criteria / query to search for
 */
public class SearchCriteria {
    /**
     * Defines in which scope should the keywords be together
     */
    public static enum WordScope {
        DOCUMENT, RANGE;
        /**
         *  How far should words be placed between each other (number of characters)
         */
        private Integer rangeSize;

        public void rangeSize(Integer rangeSize) {
            if (WordScope.this != RANGE) throw new IllegalArgumentException("rangeSize is only applicable for RANGE type.");
            if (this.rangeSize != null) throw new RuntimeException("variable rangeSize is already set");
            this.rangeSize = rangeSize;
        }

        public Integer getRangeSize() {
            if (WordScope.this != RANGE) throw new IllegalArgumentException("rangeSize is only applicable for RANGE type.");
            return this.rangeSize;
        }
    }
    private final List<String> keywords;
    private final WordScope wordScope;

    public SearchCriteria(List<String> keywords, WordScope wordScope) {
        this.keywords = Collections.unmodifiableList(keywords);
        this.wordScope = wordScope;
        if (wordScope == WordScope.RANGE && wordScope.rangeSize == null) throw new RuntimeException("rangeSize in WordScope.RANGE is null");
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public WordScope getWordScope() {
        return wordScope;
    }
}
