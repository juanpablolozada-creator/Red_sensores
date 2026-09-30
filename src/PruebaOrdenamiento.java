/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   PruebaOrdenamiento - VALIDACIÓN PEQUEÑA
   Estructuras de Datos · Programa IDIA

   Valida los seis algoritmos de ordenamiento con un conjunto pequeño
   de prueba (PM2.5: 5.0, 2.0, 8.0, 1.0, 4.0 -> 1.0, 2.0, 4.0, 5.0, 8.0).
   ============================================================ */

import java.util.Arrays;

public class PruebaOrdenamiento {

    public static void main(String[] args) {
        probarTodos();
    }

    public static void probarTodos() {
        System.out.println("=== VALIDACIÓN DE ALGORITMOS CON CONJUNTO PEQUEÑO ===");
        double[] valoresIniciales = {5.0, 2.0, 8.0, 1.0, 4.0};
        double[] esperado = {1.0, 2.0, 4.0, 5.0, 8.0};

        System.out.println("Valores iniciales PM2.5: " + Arrays.toString(valoresIniciales));
        System.out.println("Resultado esperado:      " + Arrays.toString(esperado));
        System.out.println();

        probar("Burbuja", a -> Ordenador.burbujaPorPm25(a), valoresIniciales, esperado);
        probar("Selección", a -> Ordenador.seleccionPorPm25(a), valoresIniciales, esperado);
        probar("Inserción", a -> Ordenador.insercionPorPm25(a), valoresIniciales, esperado);
        probar("MergeSort", a -> Ordenador.mergeSortPorPm25(a), valoresIniciales, esperado);
        probar("HeapSort", a -> Ordenador.heapSortPorPm25(a), valoresIniciales, esperado);
        probar("QuickSort (Pivote 1)", a -> Ordenador.quickSortPivotePrimeroPorPm25(a), valoresIniciales, esperado);
        probar("QuickSort (Aleatorio)", a -> Ordenador.quickSortPorPm25(a), valoresIniciales, esperado);
        probar("ordenarPorPm25", a -> Ordenador.ordenarPorPm25(a), valoresIniciales, esperado);
        System.out.println();
    }

    interface Algoritmo {
        void ordenar(LecturaSensor[] datos);
    }

    private static void probar(String nombre, Algoritmo alg, double[] valores, double[] esperado) {
        LecturaSensor[] datos = new LecturaSensor[valores.length];
        for (int i = 0; i < valores.length; i++) {
            datos[i] = new LecturaSensor(
                    "EST-001",
                    "2026-09-30 00:0" + i,
                    20.0,
                    50.0,
                    valores[i]
            );
        }

        alg.ordenar(datos);

        double[] resultado = new double[datos.length];
        boolean correcto = true;
        for (int i = 0; i < datos.length; i++) {
            resultado[i] = datos[i].getPm25();
            if (Double.compare(resultado[i], esperado[i]) != 0) {
                correcto = false;
            }
        }

        System.out.printf("%-24s -> %s %s (cmp: %d, swap: %d)%n",
                nombre,
                Arrays.toString(resultado),
                (correcto ? "[CORRECTO]" : "[ERROR]"),
                Ordenador.getComparaciones(),
                Ordenador.getIntercambios());
    }
}
