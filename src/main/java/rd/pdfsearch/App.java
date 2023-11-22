package rd.pdfsearch;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;


public class App {


    public static void main(String[] args) {
        String fileName = "C:/Users/Administrator/OneDrive/FI11_AE/_IHK/Pruefungen_Muster_00-21/2023_LoesungAP1Fr/AP1Fr2023 text.pdf";
        new PDFEntity().searchPdf(fileName);
    }
}
