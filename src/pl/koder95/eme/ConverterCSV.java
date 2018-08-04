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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import static pl.koder95.eme.Main.showErrorMessage;
import pl.koder95.eme.idf.BookLoader;
import pl.koder95.eme.idf.IndexTemplate;

/**
 * Umożliwia przekonwertowanie plików o rozszerzeniu CSV na plik XML.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.1.11, 2018-03-21
 * @since 0.1.1
 */
public final class ConverterCSV implements LaunchMethod {

    private final LaunchMethod next = new AbstractDefaultLaunch() {
        @Override
        public void launch(List<String> args) {
            pl.koder95.eme.fx.Main.main(args);
        }
    };
    private File out;
    private File csvDir;
    private Document doc;
    private Element root;

    private ConverterCSV(File csvDir, File out) {
        this.out = out;
        this.csvDir = csvDir;
    }

    private ConverterCSV(File csvDir, File xmlDir, String xmlFileName) {
        this(csvDir, new File(xmlDir, xmlFileName));
    }

    private ConverterCSV(File csvDir, String xmlFileName) {
        this(csvDir, Files.XML_DIR, xmlFileName);
    }

    private ConverterCSV(String xmlFileName) {
        this(Files.CSV_DIR, xmlFileName);
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
    
    private Element createIndex(Element book, String an, Attr[] attrs) {
        Element index = doc.createElement("index");
        index.setAttribute("an", an);
        for (Attr a: attrs) {
            if (!a.getValue().isEmpty()) index.setAttributeNode(a);
        }
        book.appendChild(index);
        return index;
    }
    
    private Element createIndex(Element book, String an, IndexTemplate tmpl,
            String[] data) {
        if (data.length == 0) return createIndex(book, an, new Attr[0]);
        
        Attr[] attrs = tmpl.createAttrXMLArray(doc);
        for (Attr attr : attrs) {
            attr.setValue(tmpl.getData(data, attr.getName()));
        }
        return createIndex(book, an, attrs);
    }
    
    private Element createIndex(Element book, IndexTemplate tmpl, String line) {
        while (line.endsWith(";")) line = line.substring(0, line.length()-1);
        String[] cells = line.split(";");
        if (cells.length < 2) return createIndex(book, "", tmpl, new String[0]);
        else {
            String an = cells[cells.length-2] + "/" + cells[cells.length-1];
            String data = line.substring(0, line.length()-an.length()-1);
            cells = data.split(";");
            if (cells.length != tmpl.getDataLength()) {
                String[] array = cells;
                cells = new String[tmpl.getDataLength()];
                System.arraycopy(array, 0, cells, 0, array.length);
            }
            return createIndex(book, an, tmpl, cells);
        }
    }
    
    private void saveXML() throws IOException, TransformerException {
        TransformerFactory ftrans = TransformerFactory.newInstance();
        Transformer trans = ftrans.newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        DOMSource src = new DOMSource(doc);
        trans.transform(src, new StreamResult(out));
    }
    
    private void convert(String csvFileName, String bookName) {
        File in = new File(csvDir, csvFileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(in), Main.CSV_DEFAULT_CHARSET))) {
            Element book = createBook(bookName);
            IndexTemplate tmpl = BookLoader
                    .loadIndexTemplate(Files.TEMPLATE_XML, bookName);
            while (reader.ready()) createIndex(book, tmpl, reader.readLine());
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            // do nothing
            System.err.println("ER158");
        }
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
    }
    
    /**
     * Tworzy nowy konwerter, który pracować będzie w określonych warunkach.
     * 
     * @param csvDir definiuje folder, z którego pobierane będą pliki CSV do
     * przekonwertowania
     * @param xmlDir definiuje folder, gdzie zapisany zostanie plik XML
     * @param xmlFileName definiuje nazwę nowego pliku XML
     * @return nowy konwerter
     */
    public static ConverterCSV create(File csvDir, File xmlDir,
            String xmlFileName) {
        return new ConverterCSV(csvDir, xmlDir, xmlFileName);
    }

    @Override
    public void launch(List<String> args) {
        System.out.println(Arrays.asList(args));
        if (args.get(0).equalsIgnoreCase("-c")) {
            try {
                String[] csvFileNames = pl.koder95.eme.Files.CSV_DIR.list(
                        (File dir, String name) -> name.endsWith(".csv")
                );
                createXML();
                createRootElement();
                for (String csvFileName: csvFileNames) convert(csvFileName);
                saveXML();
                clear();
            } catch (ParserConfigurationException | IOException
                    | TransformerException ex) {
                showErrorMessage(ex.getMessage(),
                        ex.getClass().getCanonicalName());
                nextMethod().launch(args);
            } finally {
                MemoryUtils.releaseMemory();
            }
        } else nextMethod().launch(args);
    }

    @Override
    public void setNextLaunchMethod(LaunchMethod next) {
        // do nothing
    }

    @Override
    public LaunchMethod nextMethod() {
        return next;
    }
}