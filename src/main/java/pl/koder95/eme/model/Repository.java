package pl.koder95.eme.model;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

public interface Repository {
    void createIndex(String id);
    Index getIndex(String id);
    void removeIndex(String id);
    List<Index> getIndices();

    default void addElement(String indexId, String element) {
        getIndex(indexId).elementList().add(element);
    }

    default String getElement(String indexId, int elementIndex) {
        return getIndex(indexId).elementList().get(elementIndex);
    }

    default int findElementIndex(BiPredicate<String, String> predicate) {
        record Ref(Index index, Integer elementIndex) {
            String element() {
                return index.elementList().get(elementIndex);
            }
        }
        return getIndices().stream()
                .flatMap(index -> IntStream.range(0, index.elementList().size())
                        .mapToObj(i -> new Ref(index, i)))
                .filter(ref -> predicate.test(ref.index().id(), ref.element()))
                .findFirst().map(Ref::elementIndex).orElse(-1);
    }

    default String removeElement(String indexId, int elementIndex) {
        return getIndex(indexId).elementList().remove(elementIndex);
    }
}
