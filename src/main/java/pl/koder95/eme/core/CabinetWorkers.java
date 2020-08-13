package pl.koder95.eme.core;

import pl.koder95.eme.core.spi.CabinetWorker;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasa singleton dostarczająca mapę {@link CabinetWorker pracowników} szafy według ich rodzaju.
 * Jest dostawcą implementacji usługi jądra systemu eMetrykant.
 *
 * @author Kamil Jan Mularski [@Koder95]
 * @version 0.4.0, 2020-08-13
 * @since 0.4.0
 */
public final class CabinetWorkers {

    private static final Map<Class<? extends CabinetWorker>, CabinetWorker> WORKER_MAP = new HashMap<>();

    private CabinetWorkers() {}

    /**
     * Zwraca implementację pracownika szafy aktowej, która została zarejestrowana przez wywołanie motody
     * {@link #register(Class, CabinetWorker)}.
     *
     * @param type klasa implementacji {@link CabinetWorker pracownika}
     * @param <T> typ zwracany implementacji {@link CabinetWorker pracownika}
     * @return implementacja {@link CabinetWorker pracownika} jeżeli została zarejestrowana taka,
     * albo {@code null} jeżeli wręcz przeciwnie
     */
    public static <T extends CabinetWorker> T get(Class<T> type) {
        CabinetWorker worker = WORKER_MAP.get(type);
        System.out.println("CWs: " + worker);
        return type.cast(worker);
    }

    /**
     * Rejestruje implementację o określonym typie w systemie jądra eMetrykant. Aby później móc
     * skorzystać z niej wystarczy podać typ implementacji metodzie {@link #get(Class)};
     *
     * @param type klasa implementacji {@link CabinetWorker pracownika}
     * @param <T> typ zwracany implementacji {@link CabinetWorker pracownika}
     * @param worker implementacja {@link CabinetWorker pracownika} jeżeli została zarejestrowana taka,
     * albo {@code null} jeżeli wręcz przeciwnie
     */
    public static <T extends CabinetWorker> void register(Class<T> type, T worker) {
        WORKER_MAP.put(type, worker);
        System.out.println("Registered worker: " + type);
    }
}
