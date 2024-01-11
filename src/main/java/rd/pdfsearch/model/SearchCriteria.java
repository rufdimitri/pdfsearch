package rd.pdfsearch.model;

import java.util.List;

/**
 * Defines search criteria / query to search for
 */
public class SearchCriteria {
    /**
     * Defines in which scope should the keywords be together
     */
    public static enum WordScopeType { DOCUMENT, RANGE }
    /**
     *  How far should words be placed between each other (number of characters)
     */

    private final List<String> keywords;
    private final WordScopeType wordScopeType;
    private final Integer rangeSize;

    public SearchCriteria(List<String> keywords, WordScopeType wordScopeType) {
        this.keywords = keywords;
        this.wordScopeType = wordScopeType;
        rangeSize = null;
    }

    public SearchCriteria(List<String> keywords, WordScopeType wordScopeType, Integer rangeSize) {
        this.keywords = keywords;
        this.wordScopeType = wordScopeType;
        this.rangeSize = rangeSize;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public WordScopeType getWordScopeType() {
        return wordScopeType;
    }

    public Integer getRangeSize() {
        return rangeSize;
    }
}
