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

package pl.koder95.eme;

/**
 * Klasa udostępnia metody do oszczędniejszego zarządzania pamięcią.
 * 
 * @author Kamil Jan Mularski [@koder95]
 * @version 0.0.203, 2017-08-26
 * @since 0.0.203
 */
public class MemoryUtils {
    
    private static final double KB = 1d/        1_024;
    private static final double MB = 1d/    1_048_576;
    private static final double GB = 1d/1_073_741_824;
    
    private static String toBLabel(long memory) {
        return memory + "B";
    }
    
    private static String toKBLabel(long memory) {
        return Math.round(memory*KB) + "kB";
    }
    
    private static String toMBLabel(long memory) {
        return Math.round(memory*MB) + "MB";
    }
    
    private static String toGBLabel(long memory) {
        return Math.round(memory*GB) + "GB";
    }
    
    private static String toLabel(long memory) {
        return toBLabel(memory) + " = ok." + toKBLabel(memory)
                + " = ok." + toMBLabel(memory) + " = ok." + toGBLabel(memory);
    }
    
    /**
     * Próbuje zwolnić pamięć, która nie jest używana.
     * 
     * @see System#gc()
     * @see System#runFinalization()
     * @see Runtime#gc()
     * @see Runtime#runFinalization()
     */
    public static void releaseMemory() {
        Runtime r = Runtime.getRuntime();
        
        long preRuntimeMemory = r.totalMemory() - r.freeMemory();
        System.out.println("Zapełniona pamięć przed odśmiecaniem: "
                + toLabel(preRuntimeMemory));
        
        r.gc();
        
        long postRuntimeMemory = r.totalMemory() - r.freeMemory();
        System.out.println("Zapełniona pamięć po odśmiecaniu: "
                + toLabel(postRuntimeMemory));
        
        long releaseMemory = preRuntimeMemory - postRuntimeMemory;
        System.out.println("Zwolniono " + toLabel(releaseMemory));
    }
    
    public static void memory() {
        Runtime r = Runtime.getRuntime();
        long preRuntimeMemory = r.totalMemory() - r.freeMemory();
        System.out.println("Stan pamięci: " + toLabel(preRuntimeMemory));
    }

}
