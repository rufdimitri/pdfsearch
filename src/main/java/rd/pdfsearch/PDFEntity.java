package rd.pdfsearch;

import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import java.io.File;

public class PDFEntity {

    void searchPdf(String fileName) {
        try {
            File src = new File(fileName);

            PdfReader pdfReader = new PdfReader(src.getAbsolutePath());
            int pageCount = pdfReader.getNumberOfPages();
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);

            for (int i = 0; i < pageCount; i++) {
                String text = pdfTextExtractor.getTextFromPage(i);
                System.out.println(text);
                System.out.println();
            }

        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
