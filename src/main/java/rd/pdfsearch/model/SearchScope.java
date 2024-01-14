package rd.pdfsearch.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scope, where words are found, it has a position, size and words + their positions that belong to this scope.
 * Scope is used to define an area where words should be found, if a word is found outside of the scope, then it
 * doesn't belong to this scope.
 * Normally position is defined by the 1st word (has the least position in document) and size is only a limitation,
 * represents number of characters that are allowed between first word and last word.
 */
public class SearchScope {

    private final int startPosition;
    private final int size;
    private final Map<String,List<WordPosition>> positionsPerWord;

    /**
     * Creates Scope with given parameters
     * @param startPosition
     * @param size
     */
    public SearchScope(int startPosition, int size) {
        this.startPosition = startPosition;
        this.size = size;
        positionsPerWord = new HashMap<>();
    }

    /**
     * Creates Scope with a position = 0 and size = Integer.MAX_VALUE
     * This parameter combination means that this Scope represents whole document.
     */
    public SearchScope() {
        this(0, Integer.MAX_VALUE);
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getSize() {
        return size;
    }

    public int getEndPosition() {
        return startPosition + size;
    }

    /**
     * Used to get access to the Map containing found words and their positions
     * @return the map
     */
    public Map<String, List<WordPosition>> getPositionsPerWord() {
        return positionsPerWord;
    }
}
