package rd.pdfsearch.listeners;

import rd.pdfsearch.PDFUtil;
import rd.pdfsearch.PanelNorth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BtSearchActionListener implements ActionListener {
    private final PanelNorth panelNorth;

    public BtSearchActionListener(PanelNorth panelNorth) {
        this.panelNorth = panelNorth;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String filename = panelNorth.tfSearchLocation.getText();
        //TODO
        PDFUtil.searchInPdf()
    }
}
