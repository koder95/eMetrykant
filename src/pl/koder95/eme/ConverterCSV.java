/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.koder95.eme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static pl.koder95.eme.Main.DATA_DIR;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 */
public final class ConverterCSV {

    private File out;
    private File csvDir;
    private Document doc;
    private Element root;

    private ConverterCSV(File csvDir, File out) {
        this.out = out;
        this.csvDir = csvDir;
    }

    private ConverterCSV(String xmlFileName, File dataDir) {
        this(dataDir, new File(dataDir, xmlFileName));
    }

    private ConverterCSV(String xmlFileName) {
        this(xmlFileName, DATA_DIR);
    }

    private ConverterCSV() {
        this("indices.xml");
    }
    
    private void createXML() throws ParserConfigurationException {
        DocumentBuilderFactory fdoc = DocumentBuilderFactory.newInstance();
        DocumentBuilder bdoc = fdoc.newDocumentBuilder();
        doc = bdoc.newDocument();
    }
    
    private void createRootElement() {
        root = doc.createElement("indices");
        doc.appendChild(root);
    }
    
    private Element createBook(String name) {
        Element book = doc.createElement("book");
        book.setAttribute("name", name);
        root.appendChild(book);
        return book;
    }
    
    private Element createIndex(Element book, String an, String data) {
        Element index = doc.createElement("index");
        index.setAttribute("an", an);
        index.setAttribute("data", data);
        book.appendChild(index);
        return index;
    }
    
    private Element createIndex(Element book, String line) {
        while (line.endsWith(";")) line = line.substring(0, line.length()-1);
        System.out.println("line=" + line);
        String[] cells = line.split(";");
        if (cells.length < 2) return createIndex(book, "", "");
        else {
            String an = cells[cells.length-2] + "/" + cells[cells.length-1];
            String data = line.substring(0, line.length()-an.length()-1);
            return createIndex(book, an, data);
        }
    }
    
    private void saveXML() throws IOException,
            TransformerConfigurationException, TransformerException {
        TransformerFactory ftrans = TransformerFactory.newInstance();
        Transformer trans = ftrans.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource src = new DOMSource(doc);
        trans.transform(src, new StreamResult(out));
        trans.transform(src, new StreamResult(System.out));
    }
    
    private void convert(String csvFileName, String bookName) {
        File in = new File(csvDir, csvFileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(in))) {
            Element book = createBook(bookName);
            while (reader.ready()) createIndex(book, reader.readLine());
        } catch (IOException ex) {}
    }
    
    private void convert(String csvFileName) {
        if (csvFileName.contains(".")) {
            int dot = csvFileName.lastIndexOf(".");
            String before = csvFileName.substring(0, dot);
            convert(csvFileName, before);
        } else convert(csvFileName + ".csv", csvFileName);
    }
    
    private void clear() {
        csvDir = null;
        doc = null;
        out = null;
        root = null;
        Main.releaseMemory();
    }
    
    public static void convert(ConverterCSV csv, String[] csvFileNames,
            String[] bookNames)
            throws ParserConfigurationException, IOException,
            TransformerException {
        csv.createXML();
        csv.createRootElement();
        for (int i = 0; i < bookNames.length; i++) {
            csv.convert(csvFileNames[i], bookNames[i]);
        }
        csv.saveXML();
        csv.clear();
    }
    
    public static void convert(ConverterCSV csv, String[] csvFileNames)
            throws ParserConfigurationException, IOException,
            TransformerException {
        csv.createXML();
        csv.createRootElement();
        for (String csvFileName: csvFileNames) csv.convert(csvFileName);
        csv.saveXML();
        csv.clear();
    }
    
    public static ConverterCSV create(File csvDir, File xmlDir,
            String xmlFileName)
            throws ParserConfigurationException, IOException,
            TransformerException {
        return new ConverterCSV(csvDir, new File(xmlDir, xmlFileName));
    }
    
    public static ConverterCSV create(File dataDir, String xmlFileName)
            throws ParserConfigurationException, IOException,
            TransformerException {
        return new ConverterCSV(dataDir, new File(dataDir, xmlFileName));
    }
}
