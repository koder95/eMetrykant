/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip.io;

import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author Kamil
 */
public class XLSXHead {
    
    public static class Name {
        private final Name parent;
        private String value;
        private Name[] children = new Name[0];

        public Name(Name parent) {
            this.parent = parent;
        }

        public Name(Name parent, String value) {
            this.parent = parent;
            this.value = value;
        }

        public Name[] getChildren() {
            return children;
        }

        public Name getChild(int i) {
            return children[i];
        }

        public Name getParent() {
            return parent;
        }

        public String getValue() {
            return value;
        }

        public void setChildren(Name[] children) {
            this.children = children;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    
    private final Name root = new Name(null);
    
    public void setChildren(Name[] children) {
        root.setChildren(children);
    }
    
    public void setValue(String value) {
        root.setValue(value);
    }

    public Name getRoot() {
        return root;
    }
    
}
