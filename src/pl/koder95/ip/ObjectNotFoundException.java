/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.koder95.ip;

import java.util.Arrays;

/**
 *
 * @author Kamil
 */
public class ObjectNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ObjectNotFoundException</code> without
     * detail message.
     */
    private ObjectNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ObjectNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    private ObjectNotFoundException(String msg) {
        super(msg);
    }
    
    public ObjectNotFoundException(Class<?> obj, Object... findArgs) {
        this(obj.getSimpleName() + " not found:\nfindArgs=" + Arrays.toString(findArgs));
    }
}
