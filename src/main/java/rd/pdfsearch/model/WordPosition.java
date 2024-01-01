package rd.pdfsearch.model;

/**
 * @param position     position of search-result relative to begin of current page (represents number of characters)
 * @param pagePosition position of current page in document (represents number of characters from document begin to current page)
 * @param pageNumber   page in document
 */
public record WordPosition(int position, int pagePosition, int pageNumber) {

    public int getAbsolutePosition() {
        return position + pagePosition;
    }

}
