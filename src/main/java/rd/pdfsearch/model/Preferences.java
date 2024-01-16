package rd.pdfsearch.model;

import java.util.ArrayList;

import static rd.pdfsearch.model.SearchCriteria.WordScopeType.DOCUMENT;

public class Preferences {
    private String searchLocation;
    private String keywordsSeparator;
    private SearchCriteria searchCriteria;

    private Preferences() {}

    public static class Builder {
        Preferences preferences;

        public Builder() {
            preferences = new Preferences();
            preferences.keywordsSeparator = " ";
            preferences.searchCriteria = new SearchCriteria(new ArrayList<>(), DOCUMENT, 200);
        }

        public Builder keywordsSeparator(String keywordsSeparator) {
            preferences.setKeywordsSeparator(keywordsSeparator);
            return this;
        }

        public Builder rangeSize(int rangeSize) {
            SearchCriteria searchCriteria = new SearchCriteria(preferences.searchCriteria.getKeywords(), preferences.searchCriteria.getWordScopeType(), rangeSize);
            preferences.setSearchCriteria(searchCriteria);
            return this;
        }

        public Preferences build() {
            return preferences;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public String getKeywordsSeparator() {
        return keywordsSeparator;
    }

    public void setKeywordsSeparator(String keywordsSeparator) {
        this.keywordsSeparator = keywordsSeparator;
    }

    public SearchCriteria getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
}