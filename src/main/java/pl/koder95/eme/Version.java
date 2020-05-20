/*
 * Copyright (C) 2018 Kamil Jan Mularski [@koder95]
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.koder95.eme;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Klasa umożliwia zarządzanie numerami wersji oprogramowania.
 * Klasa projektowana na podstawie
 * <a href="https://semver.org/lang/pl/">Wersjonowania semantycznego 2.0.0</a>.
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.3.0, 2018-11-03
 * @since 0.2.0
 */
public class Version implements Comparable<Version> {

    private final int major, minor, patch;
    private final boolean preRelease;
    private final Collection<String> identifiers, buildMetadata;

    /**
     * Tworzy nowy nr wersji z podanymi wartościami.
     *
     * @param major główna liczba, określa kompatybilność
     * @param minor liczba podporządkowana, określa rozszerzenie
     * @param patch liczba łatkowa, określa numer poprawki
     * @param preRelease określa, czy wersja jest przedpremierowa
     * @param identifiers identyfikatory wersji przedpremierowej
     * @param buildMetadata metadane build'u
     */
    public Version(int major, int minor, int patch, boolean preRelease,
            Collection<String> identifiers,
            Collection<String> buildMetadata) {
        if (major < 0 || minor < 0 || patch < 0)
            throw new IllegalArgumentException();
        else {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.preRelease = preRelease;
            this.identifiers = identifiers;
            this.buildMetadata = buildMetadata;
        }
    }

    /**
     * Tworzy nowy nr wersji stabilnej z podanymi wartościami.
     *
     * @param major główna liczba, określa kompatybilność
     * @param minor liczba podporządkowana, określa rozszerzenie
     * @param patch liczba łatkowa, określa numer poprawki
     */
    public Version(int major, int minor, int patch) {
        this(major, minor, patch, false, Collections.emptyList(),
                Collections.emptyList());
    }

    @Override
    public final int compareTo(Version o) {
        /*
        Pierwszeństwo odnosi się do sposobu porównywania wersji między sobą
        podczas ich porządkowania.
        
        Pierwszeństwo MUSI być ustalane w rozdzieleniu wersji na identyfikatory
        major, minor, patch oraz identyfikator przedpremierowy w podanej
        kolejności (meta-dane buildu nie decydują o pierwszeństwie).
        */
        
        /*
        Pierwszeństwo jest ustalane przez pierwszą różnicę wykrytą podczas
        porównania każdego z identyfikatorów od lewej do prawej:
        wersje major, minor, patch są zawsze porównywane numerycznie.
        Przykład: 1.0.0 < 2.0.0 < 2.1.0 < 2.1.1.
        */
        if (major > o.major) return 1;
        if (major < o.major) return -1;
        // major'y są równe, więc:
        if (minor > o.minor) return 1;
        if (minor < o.minor) return -1;
        // major'y i minor'y są równe, więc:
        if (patch > o.patch) return 1;
        if (patch < o.patch) return -1;
        
        if (!preRelease && !o.preRelease) return 0;
        /*
        Gdy numery wersji major, minor i patch są równe, wydanie przedpremierowe
        poprzedza wersję standardową. Przykładowo: 1.0.0-alpha < 1.0.0.
        */
        if (preRelease && !o.preRelease) return -1;
        if (!preRelease && o.preRelease) return 1;
        /*
        Pierwszeństwo dwóch wydań przedpremierowych z takimi samymi numerami
        wersji major, minor i patch MUSI być ustalane przez porównywanie każdego
        z identyfikatorów rozdzielonych kropkami w kierunku od lewej do prawej,
        póki nie zostanie wykryta różnica w taki sposób:
        identyfikatory złożone z samych cyfr porównywane są numerycznie,
        a identyfikatory z literami lub dywizami porównywane są leksykalnie
        w kolejności ASCII.
        */
        if (identifiers == o.identifiers) return 0;
        
        Iterator<String> i0 = identifiers.iterator(),
                i1 = o.identifiers.iterator();
        while (i0.hasNext() || i1.hasNext()) {
            if (i0.hasNext() && i1.hasNext()) {
                String firstS = i0.next(), secondS = i1.next();
                int firstI = -1, secondI = -1;
                try {
                    firstI = Integer.parseInt(firstS);
                    secondI = Integer.parseInt(secondS);
                } catch (NumberFormatException ex) {
                    // do nothing
                }
                /*
                Identyfikatory numeryczne zawsze poprzedzają
                identyfikatory nienumeryczne.
                */
                if (firstI >= 0) {
                    if (secondI >= 0) {
                        // porównywanie numeryczne:
                        if (firstI < secondI) return -1;
                        if (firstI > secondI) return 1;
                    } else return -1;
                } else {
                    if (secondI >= 0) return 1;
                    else {
                        // porównywanie alfabetyczne:
                        int compareTo = firstS.compareTo(secondS);
                        if (compareTo != 0)
                            return compareTo > 0? 1 : -1;
                    }
                }
            }
            /*
            Większy zbiór przedpremierowych pól poprzedza mniejszy zbiór,
            o ile wszystkie poprzedzające identyfikatory są sobie równe.
            Przykład:
            1.0.0-alpha < 1.0.0-alpha.1 < 1.0.0-alpha.beta < 1.0.0-beta
            < 1.0.0-beta.2 < 1.0.0-beta.11 < 1.0.0-rc.1 < 1.0.0.
            */
            else if (!i0.hasNext() && i1.hasNext()) return -1;
            else if (i0.hasNext() && !i1.hasNext()) return 1;
        }
        return 0;
    }

    /**
     * @return metadane build'u
     */
    public Collection<String> getBuildMetadata() {
        return buildMetadata;
    }

    /**
     * @return identyfikatory przedpremierowe - gdy jest to wersja
     * przedpremierowa, {@code null} - gdy wersja stabilna
     */
    public Collection<String> getIdentifiers() {
        return isPreRelease()? identifiers : null;
    }

    /**
     * @return główna liczba, wskazuje na ciągłość idei i podejścia, które
     * gwarantuje kompatybilność rozszerzeń
     */
    public int getMajor() {
        return major;
    }

    /**
     * @return liczba podporządkowana, która definiuje ilość kompatybilnych
     * rozszerzeń
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @return liczba łatkowa, ile było poprawek rozszerzenia
     */
    public int getPatch() {
        return patch;
    }

    /**
     * @return czy jest to wersja niestabilna, czyli przedpremierowa
     */
    public boolean isPreRelease() {
        return preRelease;
    }
    
    @Override
    public String toString() {
        String stable = major + "." + minor + "." + patch;
        if (preRelease) {
            StringBuilder builder = new StringBuilder(stable + "-");
            identifiers.forEach((i) -> builder.append(i).append('.'));
            builder.deleteCharAt(builder.length()-1);
            if (!buildMetadata.isEmpty()) {
                builder.append('+');
                buildMetadata.forEach((bMd) -> builder.append(bMd).append('.'));
                builder.deleteCharAt(builder.length()-1);
            }
            return builder.toString();
        } else return stable;
    }

    private static Version parse(String str, boolean preRelease) {
        boolean isPreRelease = str.contains("-"),
                hasBuildMetadata = str.contains("+");
        if (hasBuildMetadata) {
            String[] mainParts = str.split(Pattern.quote("+"));
            Version r = parse(mainParts[0], preRelease);
            r.getBuildMetadata().addAll(
                    Arrays.asList(mainParts[1].split(Pattern.quote(".")))
            );
            return r;
        } else {
            if (isPreRelease) {
                // 0 - stable, 1 - pre-release:
                String[] nonBuildParts = str.split(Pattern.quote("-"));
                String[] preReleaseParts = nonBuildParts[1]
                        .split(Pattern.quote("."));
                Version r = parse(nonBuildParts[0], true);
                r.getIdentifiers().addAll(Arrays.asList(preReleaseParts));
                return r;
            } else {
                String[] stableParts = str.split(Pattern.quote("."));
                return new Version(
                        Integer.parseInt(stableParts[0]),
                        Integer.parseInt(stableParts[1]),
                        Integer.parseInt(stableParts[2]),
                        preRelease, new LinkedList<>(), new LinkedList<>());
            }
        }
    }

    /**
     * Odczytuje wersję z ciągu znaków.
     *
     * @param str wersja zapisana w ciągu znaków
     * @return wersja
     */
    public static Version parse(String str) {
        if (str.startsWith("v"))
            return parse(str.substring(1), str.contains("-"));
        return parse(str, str.contains("-"));
    }

    /**
     * Odczytuje wersję z klasy, która zawiera statyczną metodę {@code get()}
     * zwracającą instancję klasy. Wartości odczytywane są za pomocą wywołania
     * metody {@link Object#toString()}.
     *
     * @param klasa klasa, która ma zostać odczytana
     * @return wersja
     */
    public static Version parse(Class klasa) {
        try {
            Method get = Arrays.stream(klasa.getMethods())
                    .reduce(null, (r, c) -> c.getName().equals("get")? c : r);
            
            if (get != null) return parse(get.invoke(null).toString());
            return null;
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            return null;
        }
    }

    /**
     * @return wersja tego programu
     */
    public static Version get() {
        return parse("0.3.0");
    }
}
