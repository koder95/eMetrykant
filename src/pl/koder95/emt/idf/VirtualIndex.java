/*
 * Ten utwór jest dostępny na licencji
 * Creative Commons BY-NC-SA 4.0 Międzynarodowe.
 * Aby zapoznać się z tekstem licencji wejdź na stronę
 * http://creativecommons.org/licenses/by-nc-sa/4.0/.
 */
package pl.koder95.emt.idf;

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.146, 2017-08-02
 * @since 0.0.138
 */
class VirtualIndex extends Index {
    private final Indices indices;

    VirtualIndex(int id, Indices indices) {
        super(id);
        this.indices = indices;
    }

    @Override
    public ActNumber getActNumber() {
        return getRealIndex().AN;
    }

    @Override
    public String[] getData() {
        return getRealIndex().getData();
    }

    @Override
    public String getData(int index) {
        return getRealIndex().getData(index);
    }
    
    public RealIndex getRealIndex() {
        return indices.getReal(ID);
    }
}
