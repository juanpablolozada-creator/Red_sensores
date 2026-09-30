/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   BancoDeOrdenamiento - VERSION SEMANA 4 COMPLETA
   Estructuras de Datos · Programa IDIA

   Contiene los cinco experimentos de la Semana 4:
   1. Algoritmos simples sobre 10.000 datos desordenados.
   2. Algoritmos simples sobre 10.000 datos ordenados (corte temprano).
   3. Inserción vs MergeSort vs HeapSort a escala creciente (1k, 10k, 100k)
      con cálculo de razones de crecimiento.
   4. QuickSort con pivote primero vs pivote aleatorio (50.000 datos).
   5. Efecto colateral: el ranking por PM2.5 y la consulta binaria por timestamp.
   ============================================================ */

import java.util.Random;

public class BancoDeOrdenamiento {

    public static void main(String[] args) {
        ejecutarTodos();
    }

    public static void ejecutarTodos() {
        experimentoUno();
        experimentoDos();
        experimentoTres();
        experimentoCuatro();
        experimentoCinco();
    }

    // ---------- UTILIDADES ----------

    /** Copia el arreglo para que cada algoritmo empiece en igualdad de condiciones. */
    public static LecturaSensor[] copiar(LecturaSensor[] original) {
        LecturaSensor[] copia = new LecturaSensor[original.length];
        System.arraycopy(original, 0, copia, 0, original.length);
        return copia;
    }

    /** Desordena un arreglo con una semilla fija, para que el experimento sea repetible. */
    public static LecturaSensor[] desordenar(LecturaSensor[] original) {
        LecturaSensor[] copia = copiar(original);
        Random azar = new Random(777L);
        for (int i = copia.length - 1; i > 0; i--) {
            int j = azar.nextInt(i + 1);
            LecturaSensor t = copia[i]; copia[i] = copia[j]; copia[j] = t;
        }
        return copia;
    }

    private static void reportar(String nombre, long milis) {
        System.out.printf("%-22s comparaciones: %,14d   intercambios: %,14d   %6d ms%n",
                nombre, Ordenador.getComparaciones(), Ordenador.getIntercambios(), milis);
    }

    // ---------- EXPERIMENTO 1 ----------

    /** Los tres algoritmos simples sobre 10.000 lecturas DESORDENADAS. */
    public static void experimentoUno() {
        System.out.println("=== EXP 1: ALGORITMOS SIMPLES, 10.000 LECTURAS DESORDENADAS ===");
        LecturaSensor[] base = desordenar(GeneradorDatos.generar(10_000));

        LecturaSensor[] a = copiar(base);
        long t = System.currentTimeMillis();
        Ordenador.burbuja(a);
        reportar("Burbuja", System.currentTimeMillis() - t);

        LecturaSensor[] b = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.seleccion(b);
        reportar("Seleccion", System.currentTimeMillis() - t);

        LecturaSensor[] c = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.insercion(c);
        reportar("Insercion", System.currentTimeMillis() - t);
        System.out.println();
    }

    // ---------- EXPERIMENTO 2 ----------

    /** Los mismos tres algoritmos sobre datos que YA VIENEN ORDENADOS. */
    public static void experimentoDos() {
        System.out.println("=== EXP 2: LOS MISMOS TRES, PERO CON DATOS YA ORDENADOS ===");
        System.out.println("(asi es como llegan de la red de sensores: en orden cronologico)");
        LecturaSensor[] base = GeneradorDatos.generar(10_000);

        LecturaSensor[] a = copiar(base);
        long t = System.currentTimeMillis();
        Ordenador.burbuja(a);
        reportar("Burbuja (Corte T.)", System.currentTimeMillis() - t);

        LecturaSensor[] b = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.seleccion(b);
        reportar("Seleccion", System.currentTimeMillis() - t);

        LecturaSensor[] c = copiar(base);
        t = System.currentTimeMillis();
        Ordenador.insercion(c);
        reportar("Insercion", System.currentTimeMillis() - t);
        System.out.println();
    }

    // ---------- EXPERIMENTO 3 ----------

    /** Simples contra avanzados, a escala creciente con cálculo de crecimiento. */
    public static void experimentoTres() {
        System.out.println("=== EXP 3: SIMPLES CONTRA AVANZADOS (ESCALABILIDAD) ===");
        int[] tamanos = {1_000, 10_000, 100_000};

        // Estructuras para almacenar métricas y calcular crecimiento
        long[][] comparaciones = new long[3][3]; // [algoritmo][tamano]
        long[][] tiempos = new long[3][3];
        String[] nombres = {"Insercion", "MergeSort", "HeapSort"};

        for (int idx = 0; idx < tamanos.length; idx++) {
            int n = tamanos[idx];
            System.out.println("-- " + String.format("%,d", n) + " lecturas desordenadas --");
            LecturaSensor[] base = desordenar(GeneradorDatos.generar(n));

            // Inserción
            LecturaSensor[] a = copiar(base);
            long t = System.currentTimeMillis();
            Ordenador.insercion(a);
            long milisIns = System.currentTimeMillis() - t;
            comparaciones[0][idx] = Ordenador.getComparaciones();
            tiempos[0][idx] = milisIns;
            reportar("Insercion", milisIns);

            // MergeSort
            LecturaSensor[] b = copiar(base);
            t = System.currentTimeMillis();
            Ordenador.mergeSort(b);
            long milisMerge = System.currentTimeMillis() - t;
            comparaciones[1][idx] = Ordenador.getComparaciones();
            tiempos[1][idx] = milisMerge;
            reportar("MergeSort", milisMerge);

            // HeapSort
            LecturaSensor[] c = copiar(base);
            t = System.currentTimeMillis();
            Ordenador.heapSort(c);
            long milisHeap = System.currentTimeMillis() - t;
            comparaciones[2][idx] = Ordenador.getComparaciones();
            tiempos[2][idx] = milisHeap;
            reportar("HeapSort", milisHeap);
            System.out.println();
        }

        // Análisis de factores de crecimiento
        System.out.println("--- RAZONES DE CRECIMIENTO EXPERIMENTAL ---");
        System.out.printf("%-12s | %-24s | %-24s%n",
                "Algoritmo", "Factor Cmp (10k / 1k)", "Factor Cmp (100k / 10k)");
        System.out.println("------------------------------------------------------------------");
        for (int i = 0; i < 3; i++) {
            double factor1 = (double) comparaciones[i][1] / comparaciones[i][0];
            double factor2 = (double) comparaciones[i][2] / comparaciones[i][1];
            System.out.printf("%-12s | %20.2fx | %20.2fx%n", nombres[i], factor1, factor2);
        }
        System.out.println();
    }

    // ---------- EXPERIMENTO 4 ----------

    /** QuickSort con pivote fijo vs pivote aleatorio. */
    public static void experimentoCuatro() {
        System.out.println("=== EXP 4: QUICKSORT: PIVOTE PRIMERO vs PIVOTE ALEATORIO ===");

        System.out.println("-- Caso A: 50.000 lecturas DESORDENADAS (Pivote primero) --");
        LecturaSensor[] revueltas = desordenar(GeneradorDatos.generar(50_000));
        long t = System.currentTimeMillis();
        Ordenador.quickSortPivotePrimero(revueltas);
        reportar("QuickSort (Pivote 1)", System.currentTimeMillis() - t);

        System.out.println();
        System.out.println("-- Caso B: 50.000 lecturas EN ORDEN CRONOLOGICO (Pivote primero) --");
        LecturaSensor[] enOrden = GeneradorDatos.generar(50_000);
        try {
            t = System.currentTimeMillis();
            Ordenador.quickSortPivotePrimero(enOrden);
            reportar("QuickSort (Pivote 1)", System.currentTimeMillis() - t);
        } catch (StackOverflowError e) {
            System.out.println("QuickSort (Pivote 1) -> StackOverflowError: el programa se quedo sin pila.");
            System.out.println("                        Comparaciones alcanzadas antes de morir: "
                    + String.format("%,d", Ordenador.getComparaciones()));
        }

        System.out.println();
        System.out.println("-- Caso B Mejorado: 50.000 lecturas EN ORDEN CRONOLOGICO (Pivote Aleatorio - TODO 2) --");
        LecturaSensor[] enOrdenMejorado = GeneradorDatos.generar(50_000);
        t = System.currentTimeMillis();
        Ordenador.quickSort(enOrdenMejorado);
        reportar("QuickSort (Aleatorio)", System.currentTimeMillis() - t);
        System.out.println();
    }

    // ---------- EXPERIMENTO 5 ----------

    /** Ordenar por PM2.5 para el ranking... y consultar por timestamp despues. */
    public static void experimentoCinco() {
        System.out.println("=== EXP 5: EL RANKING Y LA CONSULTA (EFECTO COLATERAL) ===");

        LecturaSensor[] datos = GeneradorDatos.generar(100_000);
        String objetivo = GeneradorDatos.timestampEnPosicion(73_412);

        System.out.println("Paso 1. Los datos llegan de la red en orden cronologico.");
        System.out.println("        Ordenado por timestamp: " + Ordenador.estaOrdenadoPorTimestamp(datos));
        int pos = BuscadorLecturas.busquedaBinariaPorTimestamp(datos, objetivo);
        System.out.println("        Consulta binaria por timestamp -> posicion: " + pos
                + "  (comparaciones: " + BuscadorLecturas.getComparaciones() + ")");

        System.out.println();
        System.out.println("Paso 2. El area de comunicaciones pide el ranking de estaciones");
        System.out.println("        mas contaminadas. Ordenamos por PM2.5.");
        Ordenador.ordenarPorPm25(datos);
        System.out.println("        Ranking listo. PM2.5 mas bajo: " + datos[0].getPm25()
                + " | mas alto: " + datos[datos.length - 1].getPm25());

        System.out.println();
        System.out.println("Paso 3. Otro usuario vuelve a consultar la misma lectura de siempre.");
        System.out.println("        Ordenado por timestamp: " + Ordenador.estaOrdenadoPorTimestamp(datos));
        pos = BuscadorLecturas.busquedaBinariaPorTimestamp(datos, objetivo);
        System.out.println("        Consulta binaria por timestamp -> posicion: " + pos
                + "  (comparaciones: " + BuscadorLecturas.getComparaciones() + ")");

        System.out.println();
        System.out.println("        Verificacion con busqueda lineal -> posicion: "
                + BuscadorLecturas.busquedaLinealPorTimestamp(datos, objetivo)
                + "  (comparaciones: " + BuscadorLecturas.getComparaciones() + ")");
        System.out.println();
    }
}
