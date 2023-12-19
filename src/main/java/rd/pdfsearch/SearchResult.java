package rd.pdfsearch;

public class SearchResult {
    private final int position;
    private final int pagePosition;
    private final int pageNumber;

    /**
     * @param position position of search-result relative to begin of current page (represents number of characters)
     * @param pagePosition position of current page in document (represents number of characters from document begin to current page)
     * @param pageNumber page in document
     */
    public SearchResult(int position, int pagePosition, int pageNumber) {
        this.position = position;
        this.pagePosition = pagePosition;
        this.pageNumber = pageNumber;
    }

    public int getAbsolutePosition() {
        return position + pagePosition;
    }

    public int getPosition() {
        return position;
    }

    public int getPagePosition() {
        return pagePosition;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "position=" + position +
                ", pagePosition=" + pagePosition +
                ", pageNumber=" + pageNumber +
                '}';
    }
}
