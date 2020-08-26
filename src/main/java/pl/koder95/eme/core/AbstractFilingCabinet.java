package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.Briefcase;
import pl.koder95.eme.core.spi.FilingCabinet;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * <b>Ogólna szafa aktowa</b> jest implementacją dostarczającą ogólny kształt szafy, który bazuje na
 * interfejsach mapy i wytwórcy map. Dostarcza również implementację metody {@link #getPersonalData()},
 * która korzysta ze statycznej metody {@link #createPersonalData(AbstractFilingCabinet, Map, NameSetFactory)}
 * podczas tworzenia danych osobowych.
 *
 * <h3>Mapa szafy aktowej - opis mapowania</h3>
 * <p>Mapa szafy wiąże nazwisko z mapą, która wiąże imię z aktówką. Dzięki temu za pomocą nazwiska można uzyskać
 * dostęp do wszystkich imion i aktówek do nich przypisanych. Bez tych dwóch informacji nie jest możliwe
 * dostanie się do aktówki.</p>
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-26
 * @since 0.4.0
 */
public class AbstractFilingCabinet implements FilingCabinet {

    private final MapFactory mapFactory;
    private final Map<String, Map<String, Briefcase>> map;

    private final Map<String, Set<String>> personalData;
    private final NameSetFactory nameSetFactory;

    /**
     * Tworzy nowy obiekt ustawiając odpowiednią implementację mapy oraz interfejs wykorzystywany podczas
     * dodawania nowych wartości do mapy. Umożliwia również określenie przestrzeni nazwisk i imion, do których
     * jest umożliwiony dostęp. Jeżeli parametr {@code personalData} zostanie ustalony na wartość inną niż {@code null},
     * wtedy można zmienić domyślną implementację mapy nazwisk. Jeżeli natomiast ta mapa zawiera już jakieś
     * dane, tzn. nie jest pusta, wtedy dostęp do informacji jest uzależniony od tych danych. Jeżeli
     * klient będzie chciał odwołać się do nazwiska albo imienia, którego nie ma w mapie {@code personalData},
     * wtedy zostanie wypluty {@link SecurityException wyjątek bezpieczeństwa}. Jeżeli jest mapa jest pusta,
     * wtedy każde wywołanie metody {@link #getPersonalData()} będzie generowało nową przestrzeń nazwisk
     * i imion w zależności od już dodanych danych do szafy.
     *
     * @param map mapa wiążąca nazwisko z mapą, która z kolei wiąże imię z aktówką
     * @param mapFactory metoda wytwórcza dla map wiążących imię z aktówką
     * @param personalData przestrzeń nazwisk i imion, może być pusta lub {@code null}
     * @param nameSetFactory jeżeli {@code personalData == null || personalData.isEmpty()},
     *                      wtedy parametr ten ma znaczenie, ponieważ udostępnia możliwość tworzenia
     *                       zbiorów imion dla każdego nazwiska, korzystając z dowolnej implementacji
     */
    public AbstractFilingCabinet(Map<String, Map<String, Briefcase>> map, MapFactory mapFactory,
                                 Map<String, Set<String>> personalData, NameSetFactory nameSetFactory) {
        this.mapFactory = mapFactory;
        this.map = map;
        this.personalData = personalData == null? new TreeMap<>() : personalData;
        this.nameSetFactory = nameSetFactory == null? TreeSet::new : nameSetFactory;
    }

    /**
     * Tworzy nowy obiekt ustawiając odpowiednią implementację mapy oraz interfejs wykorzystywany podczas
     * dodawania nowych wartości do mapy.
     *
     * @param map mapa wiążąca nazwisko z mapą, która z kolei wiąże imię z aktówką
     * @param mapFactory metoda wytwórcza dla map wiążących imię z aktówką
     */
    public AbstractFilingCabinet(Map<String, Map<String, Briefcase>> map, MapFactory mapFactory) {
        this(map, mapFactory, null, null);
    }

    @Override
    public void add(String surname, String name, Briefcase b) {
        if (!map.containsKey(surname)) {
            Map<String, Briefcase> map = mapFactory.create();
            if (!map.isEmpty()) {
                map.clear();
            }
            this.map.put(surname, map);
        }
        Map<String, Briefcase> briefcaseMap = map.get(surname);
        briefcaseMap.put(name, b);
    }

    @Override
    public void remove(String surname, String name) {
        Map<String, Briefcase> briefcaseMap = map.get(surname);
        if (briefcaseMap == null) return;

        Briefcase briefcase = briefcaseMap.get(name);
        if (briefcase != null) {
            briefcaseMap.remove(briefcase);
        }

        if (briefcaseMap.isEmpty()) {
            map.remove(briefcaseMap);
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Map<String, Briefcase> get(String surname) {
        if (!personalData.isEmpty() && !personalData.containsKey(surname)) {
            throw new SecurityException("You cannot read this data.");
        }
        return map.get(surname);
    }

    @Override
    public Briefcase get(String surname, String name) {
        if (!personalData.isEmpty() && personalData.containsKey(surname)) {
            if (!personalData.get(surname).contains(name)) {
                throw new SecurityException("You cannot read this data.");
            }
        }
        Map<String, Briefcase> briefcaseMap = get(surname);
        if (briefcaseMap == null) {
            return null;
        }
        return briefcaseMap.get(name);
    }

    @Override
    public Map<String, Set<String>> getPersonalData() {
        if (personalData.isEmpty()) {
            return createPersonalData(this, personalData, nameSetFactory);
        }
        return personalData;
    }

    /**
     * Interfejs umożliwia stworzenie mapy wiążącej imię z konkretną aktówką.
     * Rezultatem fabryki powinna być pusta mapa.
     */
    public interface MapFactory {
        /**
         * Tworzy nową pustą mapę.
         * @return nowa instancja mapy
         */
        Map<String, Briefcase> create();
    }

    /**
     * Interfejs umożliwia stworzenie zbioru imion dla konkretnego nazwiska.
     * Rezultatem fabryki powinien być pusty zbiór.
     */
    public interface NameSetFactory {
        /**
         * Tworzy nowy pusty zbiór.
         * @return nowa instancja zbioru
         */
        Set<String> create();
    }

    private static Map<String, Set<String>> createPersonalData(AbstractFilingCabinet cabinet,
                                                              Map<String, Set<String>> personalDataIdentity,
                                                              NameSetFactory nameSetFactory) {
        return cabinet.map.entrySet().stream().reduce(personalDataIdentity, (tm, e) -> {
            String key = e.getKey();
            Map<String, Briefcase> value = e.getValue();
            if (value == null) return tm;
            if (!tm.containsKey(key)) {
                Set<String> set = nameSetFactory.create();
                if (!set.isEmpty()) {
                    set.clear();
                }
                tm.put(key, set);
            }
            tm.get(key).addAll(value.keySet());
            return tm;
        }, (r, c) -> {
            c.forEach((s, ns) -> {
                if (!r.containsKey(s)) {
                    Set<String> set = nameSetFactory.create();
                    if (!set.isEmpty()) {
                        set.clear();
                    }
                    r.put(s, set);
                }
                r.get(s).addAll(ns);
            });
            c.clear();
            return r;
        });
    }
}
