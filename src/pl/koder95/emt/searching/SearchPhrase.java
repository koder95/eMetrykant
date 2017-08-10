/*
 * Copyright (C) 2017 Kamil Jan Mularski [@koder95]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.koder95.emt.searching;

/*
Wpisywana fraza może mieć różną postać. Podstawą wydaje się podzielić ją na
słowa, które później trzeba zindentyfikować.

Użytkownik może wpisać np.:
Kamil Mularski 1995
albo w innej kolejności te same słowa.

Może wpisać też:
#137
albo:
117/1990

W pierwszym przypadku należy szukać, czy w ogóle taki indeks jest i dopiero go
zwrócić. W tym drugim natomiast jest oczywiste, że użytkownik wie, że taki
indeks istnieje (podaje konkretne, jednoznaczne dane dla indeksu, które nie mogą
się powtórzyć), ale chce otrzymać dane dotyczące tego indeksu.

Jak widać nie we wszystkich przypadkach dzielenie frazy na wyrazy jest potrzebne
do znalezienia właściwego indeksu. Lepiej na początku sprawdzić, czy dany ciąg
znaków zawiera spację. Jeśli nie, należy założyć, że fraza jest albo ID albo AN.
To założenie oczywiście powinno zostać zweryfikowane przez sprawdzenie
charakterystycznych znaków dla ID(#) lub AN(/).
Już po tym przypadku widać, że będą różne typy fraz. Tu widać dwie:
1> IDSearchPhrase
2> ANSearchPhrase

Jeśli weryfikacja przebiegnie negatywnie to należy uznać frazę za ten sam typ,
co w pierwszym przypadku, tzn.
3> DataSearchPhrase

Jednak, aby nie było to zamknięte ostateczne rozwiązanie lepiej dać dowolność
określania jakie są słowa i które z nich to te unikalne ID lub AN.
*/

/**
 *
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.147, 2017-08-08
 * @since 0.0.147
 */
public class SearchPhrase {
    private final String[] words;
    private final int[] word_index = new int[2];

    public SearchPhrase(String[] words, int idIndex, int anIndex) {
        this.words = words;
        this.word_index[0] = idIndex;
        this.word_index[1] = anIndex;
    }

    public String[] getWords() {
        return words;
    }

    public String getWord(int index) {
        if (index < 0 || index >= words.length) return null;
        return words[index];
    }

    public int getIDWordIndex() {
        return word_index[0];
    }

    public int getANWordIndex() {
        return word_index[1];
    }

    public String getIDWord() {
        return getWord(getIDWordIndex());
    }

    public String getANWord() {
        return getWord(getANWordIndex());
    }
    
    public static class IDSearchPhrase extends SearchPhrase {

        private IDSearchPhrase(String value) {
            super(new String[] {value}, 0, -1);
        }
        
    }
    
    public static class ANSearchPhrase extends SearchPhrase {

        private ANSearchPhrase(String value) {
            super(new String[] {value}, -1, 0);
        }
        
    }
    
    public static class DataSearchPhrase extends SearchPhrase {

        private DataSearchPhrase(String... data) {
            super(data, -1, -1);
        }
        
    }
    
    public static SearchPhrase createDefault(String s) {
        if (s.contains(" ")) return new DataSearchPhrase(s.split(" "));
        else {
            if (s.startsWith("#")) return new IDSearchPhrase(s);
            if (s.contains("/")) return new ANSearchPhrase(s);
            return new DataSearchPhrase(new String[] {s});
        }
    }
}

