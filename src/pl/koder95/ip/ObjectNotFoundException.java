/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.ip;

import java.util.Arrays;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.136
 */
public class ObjectNotFoundException extends Exception {

    private static final long serialVersionUID = 6482530271503764049L;

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
