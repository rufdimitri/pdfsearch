package rd.pdfsearch.model;

public class Preferences {
    private String searchLocation;
    private String keywords;
    private String keywordsSeparator;

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywordsSeparator() {
        return keywordsSeparator;
    }

    public void setKeywordsSeparator(String keywordsSeparator) {
        this.keywordsSeparator = keywordsSeparator;
    }
}