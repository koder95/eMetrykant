/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;
import pl.koder95.ip.Main;
import static pl.koder95.ip.Main.DATA_DIR;

/**
 *
 * @author Kamil
 */
public class XLSXLoader {
    
    private static class Row {
        final String[] values;

        public Row(String[] values) {
            this.values = values;
        }
    }
    
    public void analize(XSSFSheet sheet) {
        sheet.rowIterator().forEachRemaining((r)->{
            XSSFRow row = (XSSFRow) r;
            row.cellIterator().forEachRemaining((c)->{
                System.out.print(c.getCellTypeEnum());
                System.out.print(": ");
                System.out.println(getValue(c));
            });
            System.out.println(";");
        });
    }
    
    public String getValue(Cell c) {
        switch (c.getCellTypeEnum()) {
            case BLANK: return "_blank";
            case BOOLEAN: return Boolean.toString(c.getBooleanCellValue());
            case ERROR: return Byte.toString(c.getErrorCellValue());
            case FORMULA: return c.getCellFormula();
            case NUMERIC: return Double.toString(c.getNumericCellValue());
            case STRING: return c.getStringCellValue();
            case _NONE: return "_none";
        }
        return null;
    }
    
    public XLSXHead loadHead(XSSFSheet sheet) {
        ArrayList<XSSFRow> headRows = new ArrayList<>();
        sheet.rowIterator().forEachRemaining((r)->{
            Iterator<Cell> cell = r.cellIterator();
            boolean blink = false;
            while (cell.hasNext()) {
                Cell c = cell.next();
                if (c.getCellTypeEnum() == CellType.BLANK) {
                    blink = true;
                    break;
                }
            }
            if (blink) headRows.add((XSSFRow) r);
            else return;
        });
        XLSXHead head = new XLSXHead();
        return head;
    }
    
    public static void main(String[] args) throws IOException, InvalidFormatException {
        XLSXLoader l = new XLSXLoader();
        l.analize(new XSSFWorkbook(new File(DATA_DIR, "indices.xlsx")).getSheetAt(2));
    }
}
