package rd.pdfsearch;

import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import java.io.File;

public class PDFUtil {

    public static void searchPdf(String fileName) {
        File src = new File(fileName);
        try (PdfReader pdfReader = new PdfReader(src.getAbsolutePath())) {
            int pageCount = pdfReader.getNumberOfPages();
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(pdfReader);

            for (int i = 0; i < pageCount; i++) {
                String text = pdfTextExtractor.getTextFromPage(i);
                System.out.println(text);
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
