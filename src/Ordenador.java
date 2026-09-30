/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   Clase Ordenador - SEMANA 4
   Estructuras de Datos · Programa IDIA

   Implementa seis algoritmos de ordenamiento:
   1. Burbuja (con bandera de corte temprano - TODO 1)
   2. Selección
   3. Inserción
   4. MergeSort (Divide y Vencerás)
   5. HeapSort (Montículo binario)
   6. QuickSort (Pivote inicial y pivote aleatorio - TODO 2)

   Permite ordenar por Timestamp (cronológico) y por PM2.5,
   registrando comparaciones, intercambios y tiempo.
   ============================================================ */

import java.util.Random;

public class Ordenador {

    // Contadores de operaciones
    private static long comparaciones = 0;
    private static long intercambios = 0;

    // Generador aleatorio para selección de pivote en QuickSort
    private static final Random azar = new Random();

    public Ordenador() {
        reiniciarContadores();
    }

    // ---------- GESTIÓN DE CONTADORES ----------

    public static void reiniciarContadores() {
        comparaciones = 0;
        intercambios = 0;
    }

    public static long getComparaciones() {
        return comparaciones;
    }

    public static long getIntercambios() {
        return intercambios;
    }

    private static void registrarComparacion() {
        comparaciones++;
    }

    private static void registrarIntercambio() {
        intercambios++;
    }

    // ---------- OPERACIONES AUXILIARES ----------

    /**
     * Intercambia dos elementos en el arreglo e incrementa el contador.
     */
    private static void intercambiar(LecturaSensor[] datos, int i, int j) {
        LecturaSensor temporal = datos[i];
        datos[i] = datos[j];
        datos[j] = temporal;
        registrarIntercambio();
    }

    /**
     * Compara dos lecturas según el criterio especificado.
     * @param a primera lectura
     * @param b segunda lectura
     * @param porPm25 true para comparar por PM2.5, false para comparar por Timestamp
     * @return valor negativo si a < b, 0 si a == b, positivo si a > b
     */
    private static int comparar(LecturaSensor a, LecturaSensor b, boolean porPm25) {
        registrarComparacion();
        if (porPm25) {
            return Double.compare(a.getPm25(), b.getPm25());
        }
        // Criterio cronológico por Timestamp
        int cmp = a.getTimestamp().compareTo(b.getTimestamp());
        if (cmp != 0) {
            return cmp;
        }
        // Desempate por PM2.5 si los timestamps fuesen idénticos
        return Double.compare(a.getPm25(), b.getPm25());
    }

    // ============================================================
    // 1. BURBUJA (BUBBLE SORT) - CON CORTE TEMPRANO (TODO 1)
    // ============================================================

    public static void burbuja(LecturaSensor[] datos) {
        burbuja(datos, false);
    }

    public static void burbujaPorPm25(LecturaSensor[] datos) {
        burbuja(datos, true);
    }

    public static void burbuja(LecturaSensor[] datos, boolean porPm25) {
        reiniciarContadores();
        int n = datos.length;

        for (int pasada = 0; pasada < n - 1; pasada++) {
            // TODO 1: Bandera de corte temprano
            boolean huboIntercambio = false;

            for (int j = 0; j < n - 1 - pasada; j++) {
                if (comparar(datos[j], datos[j + 1], porPm25) > 0) {
                    intercambiar(datos, j, j + 1);
                    huboIntercambio = true;
                }
            }

            // Si en una pasada completa no hubo intercambios, el arreglo ya está ordenado
            if (!huboIntercambio) {
                break;
            }
        }
    }

    // ============================================================
    // 2. SELECCIÓN (SELECTION SORT)
    // ============================================================

    public static void seleccion(LecturaSensor[] datos) {
        seleccion(datos, false);
    }

    public static void seleccionPorPm25(LecturaSensor[] datos) {
        seleccion(datos, true);
    }

    public static void seleccion(LecturaSensor[] datos, boolean porPm25) {
        reiniciarContadores();
        int n = datos.length;

        for (int i = 0; i < n - 1; i++) {
            int posicionMenor = i;

            for (int j = i + 1; j < n; j++) {
                if (comparar(datos[j], datos[posicionMenor], porPm25) < 0) {
                    posicionMenor = j;
                }
            }

            if (posicionMenor != i) {
                intercambiar(datos, i, posicionMenor);
            }
        }
    }

    // ============================================================
    // 3. INSERCIÓN (INSERTION SORT)
    // ============================================================

    public static void insercion(LecturaSensor[] datos) {
        insercion(datos, false);
    }

    public static void insercionPorPm25(LecturaSensor[] datos) {
        insercion(datos, true);
    }

    public static void insercion(LecturaSensor[] datos, boolean porPm25) {
        reiniciarContadores();
        int n = datos.length;

        for (int i = 1; i < n; i++) {
            LecturaSensor actual = datos[i];
            int j = i - 1;

            while (j >= 0) {
                if (comparar(datos[j], actual, porPm25) <= 0) {
                    break;
                }
                datos[j + 1] = datos[j];
                registrarIntercambio(); // Desplazamiento/movimiento de elemento
                j--;
            }
            datos[j + 1] = actual;
        }
    }

    // ============================================================
    // 4. MERGESORT (DIVIDE Y VENCERÁS)
    // ============================================================

    public static void mergeSort(LecturaSensor[] datos) {
        mergeSort(datos, false);
    }

    public static void mergeSortPorPm25(LecturaSensor[] datos) {
        mergeSort(datos, true);
    }

    public static void mergeSort(LecturaSensor[] datos, boolean porPm25) {
        reiniciarContadores();
        mergeSortRecursivo(datos, porPm25);
    }

    private static void mergeSortRecursivo(LecturaSensor[] datos, boolean porPm25) {
        if (datos.length <= 1) {
            return;
        }

        int medio = datos.length / 2;
        LecturaSensor[] izquierda = new LecturaSensor[medio];
        LecturaSensor[] derecha = new LecturaSensor[datos.length - medio];

        System.arraycopy(datos, 0, izquierda, 0, izquierda.length);
        System.arraycopy(datos, medio, derecha, 0, derecha.length);

        mergeSortRecursivo(izquierda, porPm25);
        mergeSortRecursivo(derecha, porPm25);

        fusionar(datos, izquierda, derecha, porPm25);
    }

    private static void fusionar(LecturaSensor[] datos, LecturaSensor[] izquierda,
                                 LecturaSensor[] derecha, boolean porPm25) {
        int i = 0;
        int j = 0;
        int k = 0;

        while (i < izquierda.length && j < derecha.length) {
            if (comparar(izquierda[i], derecha[j], porPm25) <= 0) {
                datos[k++] = izquierda[i++];
            } else {
                datos[k++] = derecha[j++];
            }
        }

        while (i < izquierda.length) {
            datos[k++] = izquierda[i++];
        }

        while (j < derecha.length) {
            datos[k++] = derecha[j++];
        }
    }

    // ============================================================
    // 5. HEAPSORT (MONTÍCULO BINARIO)
    // ============================================================

    public static void heapSort(LecturaSensor[] datos) {
        heapSort(datos, false);
    }

    public static void heapSortPorPm25(LecturaSensor[] datos) {
        heapSort(datos, true);
    }

    public static void heapSort(LecturaSensor[] datos, boolean porPm25) {
        reiniciarContadores();
        int n = datos.length;

        // Fase 1: Construcción del max-heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(datos, n, i, porPm25);
        }

        // Fase 2: Extracción sucesiva de la raíz máxima hacia el final
        for (int fin = n - 1; fin > 0; fin--) {
            intercambiar(datos, 0, fin);
            heapify(datos, fin, 0, porPm25);
        }
    }

    private static void heapify(LecturaSensor[] datos, int n, int raiz, boolean porPm25) {
        int mayor = raiz;
        int izquierda = 2 * raiz + 1;
        int derecha = 2 * raiz + 2;

        if (izquierda < n && comparar(datos[izquierda], datos[mayor], porPm25) > 0) {
            mayor = izquierda;
        }

        if (derecha < n && comparar(datos[derecha], datos[mayor], porPm25) > 0) {
            mayor = derecha;
        }

        if (mayor != raiz) {
            intercambiar(datos, raiz, mayor);
            heapify(datos, n, mayor, porPm25);
        }
    }

    // ============================================================
    // 6. QUICKSORT (PIVOTE INICIAL Y PIVOTE MEJORADO - TODO 2)
    // ============================================================

    /**
     * Versión clásica con pivote = primer elemento.
     * Utilizada para demostrar la vulnerabilidad ante datos ordenados (StackOverflowError).
     */
    public static void quickSortPivotePrimero(LecturaSensor[] datos) {
        quickSortPivotePrimero(datos, false);
    }

    public static void quickSortPivotePrimeroPorPm25(LecturaSensor[] datos) {
        quickSortPivotePrimero(datos, true);
    }

    public static void quickSortPivotePrimero(LecturaSensor[] datos, boolean porPm25) {
        reiniciarContadores();
        quickSortPivotePrimeroRecursivo(datos, 0, datos.length - 1, porPm25);
    }

    private static void quickSortPivotePrimeroRecursivo(LecturaSensor[] datos, int inicio, int fin, boolean porPm25) {
        if (inicio >= fin) {
            return;
        }
        int posPivote = particionar(datos, inicio, fin, porPm25);
        quickSortPivotePrimeroRecursivo(datos, inicio, posPivote - 1, porPm25);
        quickSortPivotePrimeroRecursivo(datos, posPivote + 1, fin, porPm25);
    }

    /**
     * Versión estándar y recomendada de QuickSort con pivote aleatorio (TODO 2).
     */
    public static void quickSort(LecturaSensor[] datos) {
        quickSort(datos, false);
    }

    public static void quickSortPorPm25(LecturaSensor[] datos) {
        quickSort(datos, true);
    }

    public static void quickSortPivoteAleatorio(LecturaSensor[] datos) {
        quickSort(datos, false);
    }

    public static void quickSort(LecturaSensor[] datos, boolean porPm25) {
        reiniciarContadores();
        quickSortAleatorioRecursivo(datos, 0, datos.length - 1, porPm25);
    }

    private static void quickSortAleatorioRecursivo(LecturaSensor[] datos, int inicio, int fin, boolean porPm25) {
        if (inicio >= fin) {
            return;
        }

        // TODO 2: Selección de pivote aleatorio para evitar degradación a O(n^2)
        int indiceAleatorio = inicio + azar.nextInt(fin - inicio + 1);
        intercambiar(datos, inicio, indiceAleatorio);

        int posPivote = particionar(datos, inicio, fin, porPm25);
        quickSortAleatorioRecursivo(datos, inicio, posPivote - 1, porPm25);
        quickSortAleatorioRecursivo(datos, posPivote + 1, fin, porPm25);
    }

    /**
     * Partición estilo Lomuto donde el pivote se encuentra en datos[inicio].
     */
    private static int particionar(LecturaSensor[] datos, int inicio, int fin, boolean porPm25) {
        LecturaSensor pivote = datos[inicio];
        int i = inicio;

        for (int j = inicio + 1; j <= fin; j++) {
            if (comparar(datos[j], pivote, porPm25) < 0) {
                i++;
                intercambiar(datos, i, j);
            }
        }
        intercambiar(datos, inicio, i);
        return i;
    }

    // ============================================================
    // MÉTODOS DE INTEGRACIÓN Y VALIDACIÓN DEL SISTEMA
    // ============================================================

    /**
     * Ordena las lecturas por concentración de PM2.5 de menor a mayor.
     * Utiliza QuickSort aleatorio para máxima eficiencia O(n log n).
     */
    public static void ordenarPorPm25(LecturaSensor[] datos) {
        quickSortPorPm25(datos);
    }

    /**
     * Verifica si el arreglo está ordenado cronológicamente por Timestamp.
     */
    public static boolean estaOrdenadoPorTimestamp(LecturaSensor[] datos) {
        if (datos == null || datos.length <= 1) {
            return true;
        }
        for (int i = 0; i < datos.length - 1; i++) {
            if (datos[i].getTimestamp().compareTo(datos[i + 1].getTimestamp()) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica si el arreglo está ordenado ascendentemente por concentración de PM2.5.
     */
    public static boolean estaOrdenadoPorPm25(LecturaSensor[] datos) {
        if (datos == null || datos.length <= 1) {
            return true;
        }
        for (int i = 0; i < datos.length - 1; i++) {
            if (datos[i].getPm25() > datos[i + 1].getPm25()) {
                return false;
            }
        }
        return true;
    }
}
