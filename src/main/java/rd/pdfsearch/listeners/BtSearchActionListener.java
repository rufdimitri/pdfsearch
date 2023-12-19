package rd.pdfsearch.listeners;

import rd.pdfsearch.PDFUtil;
import rd.pdfsearch.PanelNorth;
import rd.pdfsearch.SearchResult;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class BtSearchActionListener implements ActionListener {
    private final PanelNorth panelNorth;

    public BtSearchActionListener(PanelNorth panelNorth) {
        this.panelNorth = panelNorth;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String filename = panelNorth.tfSearchLocation.getText().replaceAll("\"", "");

        //add regex quotation (\Q \E) to escape regex special characters that could appear in tfKeywordSeparator
        String splitter = "\\Q" + panelNorth.tfKeywordSeparator.getText() + "\\E";

        String[] keywordsArray = panelNorth.tfKeywords.getText()
                .split(splitter);

        for (int i = 0; i < keywordsArray.length; ++i) {
            keywordsArray[i] = keywordsArray[i].trim().toLowerCase();
        }

        //TODO call recursive subfolders
        Map<String, List<SearchResult>> searchResults = PDFUtil.searchInPdf(filename, List.of(keywordsArray));
        System.out.println(searchResults);
    }
}
