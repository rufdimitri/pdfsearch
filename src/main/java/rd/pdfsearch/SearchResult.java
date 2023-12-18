package rd.pdfsearch;

public class SearchResult {
    public final int charPosition;
    public final int pagePosition;

    public SearchResult(int charPosition, int pagePosition) {
        this.charPosition = charPosition;
        this.pagePosition = pagePosition;
    }
}
