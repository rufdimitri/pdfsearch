package rd.pdfsearch.model;

import java.util.List;

public record SearchResult(String filename, List<SearchScope> searchResults) {
}
