/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.text.Collator;
import java.util.Objects;

/**
 *
 * @author Kamil
 */
public class Person implements Comparable<Person> {
    
    private String lastName, name;

    public Person(String lastName, String name) {
        this.lastName = lastName;
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set(String lastName, String name) {
        this.lastName = lastName;
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.lastName);
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        final Person other = (Person) obj;
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    public boolean equals(String lastName, String name) {
        if (!Objects.equals(this.lastName, lastName)) {
            return false;
        }
        return Objects.equals(this.name, name);
    }

    @Override
    public int compareTo(Person o) {
        Collator col = Main.DEFAULT_COLLATOR;
        int ln = col.compare(lastName, o.lastName);
        System.out.println("C " + lastName + " " + o.lastName + " " + ln);
        if (ln == 0) {
            return col.compare(name, o.name);
        }
        return ln;
    }

    @Override
    public String toString() {
        return lastName + " " + name; //NOI18N
    }
}
