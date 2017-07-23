/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

/**
 *
 * @author Kamil
 * @param <T> typ
 */
public interface LineParser<T> {
    
    public T parseOf(String line);
}
