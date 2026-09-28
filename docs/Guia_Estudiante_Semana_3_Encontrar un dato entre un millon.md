# Guía paso a paso — Semana 3
## Encontrar un dato entre un millón

**Espacio académico:** Estructuras de Datos  
**Programa:** Ingeniería de Datos e Inteligencia Artificial — II semestre  
**Proyecto integrador:** Red de Sensores IoT  
**Tema:** Búsqueda lineal, búsqueda binaria y análisis de eficiencia

---

# 1. Propósito de esta semana

En las semanas anteriores construimos progresivamente nuestra plataforma de monitoreo ambiental.

- En la **Semana 1** aprendimos a recibir, validar y organizar datos.
- En la **Semana 2** construimos un repositorio utilizando arreglos y trabajamos con estructuras de datos.
- En esta **Semana 3** no vamos a crear una aplicación nueva.

> **Vamos a ampliar la misma aplicación.**

Nuestro problema ahora es diferente:

> **¿Cómo encontramos una lectura específica cuando el repositorio pasa de cientos a cientos de miles o millones de registros?**

La pregunta deja de ser solamente:

```text
¿Funciona?
```

y comienza a ser:

```text
¿Cuánto cuesta hacerlo?
```

La guía conserva el principio fundamental del proyecto integrador:

```text
Semana 1
   ↓
Ingesta y validación
   ↓
Semana 2
   ↓
Almacenamiento
   ↓
Semana 3
   ↓
Búsqueda y eficiencia
   ↓
Semana 4
   ↓
Ordenamiento
```

Al terminar esta guía habrás construido y probado **todos los archivos correspondientes a la Semana 3** y los habrás integrado al proyecto existente.

---

# 2. Regla arquitectónica del proyecto

Durante todo el semestre tendremos:

> **Un único punto de entrada para la aplicación: `IngestaSensores.java`.**

No crearemos un segundo `main` para cada semana.

La arquitectura de esta semana será:

```text
                    IngestaSensores.java
                           main()
                             │
             ┌───────────────┼────────────────┐
             │               │                │
             ▼               ▼                ▼
      RepositorioLecturas  AnalizadorMatriz  BancoDePruebas
             │                                │
             │                                ├── Experimento 1
             │                                ├── Experimento 2
             │                                ├── Experimento 3
             │                                └── Experimento 4
             │
             ▼
      BuscadorLecturas
             │
             ▼
       GeneradorDatos
```

### Regla importante

`BancoDePruebas.java` **no tendrá `main()`**.

Su responsabilidad será contener métodos que ejecutan experimentos. El único `main()` seguirá estando en:

```text
IngestaSensores.java
```

Esto permite que el estudiante entienda que el proyecto crece semana tras semana, en lugar de convertirse en una colección de programas independientes.

---

# 3. Los archivos de la Semana 3

Al finalizar la semana tendremos:

```text
Proyecto/
│
├── IngestaSensores.java
├── LecturaSensor.java
├── RepositorioLecturas.java
├── AnalizadorMatriz.java
│
├── BuscadorLecturas.java       ← nuevo
├── GeneradorDatos.java         ← nuevo
├── BancoDePruebas.java         ← nuevo, sin main
│
├── data/
│   └── lecturas_ampliadas.csv
│
└── docs/
    └── decisiones.md
```

Los archivos de las semanas anteriores **no se reemplazan**. Se reutilizan.

---

# 4. Primera idea: buscar es comparar

Supongamos que tenemos:

```text
[10, 25, 32, 41, 58]
```

y buscamos:

```text
41
```

Una búsqueda lineal realiza:

```text
10 ≠ 41
25 ≠ 41
32 ≠ 41
41 = 41
```

Necesitamos cuatro comparaciones.

La idea fundamental es:

> **Buscar significa comparar hasta encontrar el dato o demostrar que no está.**

---

# 5. Búsqueda lineal

La búsqueda lineal revisa los elementos desde el primero hasta encontrar el objetivo.

Su estructura conceptual es:

```text
inicio
  ↓
dato 0 → ¿es el objetivo?
  ↓ no
dato 1 → ¿es el objetivo?
  ↓ no
dato 2 → ¿es el objetivo?
  ↓
...
```

## Complejidad

Si existen `n` elementos, en el peor caso podemos necesitar:

```text
n comparaciones
```

Por eso hablamos de:

```text
O(n)
```

---

# 6. Mejor, promedio y peor caso

Para una búsqueda lineal:

| Situación | Costo aproximado |
|---|---:|
| Primer elemento | 1 comparación |
| Posición intermedia | aproximadamente n/2 |
| Último elemento | n |
| Dato inexistente | n |

Una búsqueda que no encuentra el dato puede ser tan costosa como buscar el último elemento.

---

# 7. Crear `BuscadorLecturas.java`

Ahora vamos a crear nuestra primera clase nueva.

### Archivo: `BuscadorLecturas.java`

Crea el archivo en la raíz del proyecto:

```java
/**
 * PLATAFORMA DE MONITOREO AMBIENTAL URBANO
 *
 * Contiene algoritmos de búsqueda utilizados por el proyecto.
 */
public class BuscadorLecturas {

    /**
     * Cantidad de comparaciones realizadas por la última búsqueda.
     */
    private static int comparaciones = 0;

    public static int getComparaciones() {
        return comparaciones;
    }

    /**
     * Búsqueda lineal por timestamp.
     *
     * No necesita que los datos estén ordenados.
     *
     * @param datos arreglo de lecturas
     * @param timestamp timestamp que se desea encontrar
     * @return posición de la lectura o -1 si no existe
     */
    public static int busquedaLinealPorTimestamp(
            LecturaSensor[] datos,
            String timestamp) {

        comparaciones = 0;

        for (int i = 0; i < datos.length; i++) {
            comparaciones++;

            if (datos[i].getTimestamp().equals(timestamp)) {
                return i;
            }
        }

        return -1;
    }
}
```

### ¿Por qué utilizamos `.equals()`?

Porque `timestamp` es un `String`.

En Java:

```java
==
```

compara referencias de objetos.

Mientras que:

```java
.equals()
```

permite comparar el contenido.

---

# 8. El contador de comparaciones

Agregamos:

```java
private static int comparaciones = 0;
```

y antes de cada búsqueda:

```java
comparaciones = 0;
```

Cada comparación incrementa:

```java
comparaciones++;
```

Esto nos permite medir el costo algorítmico sin depender únicamente del tiempo de ejecución.

---

# 9. Crear `GeneradorDatos.java`

En las semanas anteriores trabajamos con cientos de lecturas.

Para estudiar eficiencia necesitamos experimentar con:

```text
1.000
100.000
1.000.000
```

No necesitamos almacenar millones de registros en un archivo.

Podemos generarlos en memoria.

### Archivo: `GeneradorDatos.java`

```java
import java.util.Random;

/**
 * Genera lecturas sintéticas para los experimentos de la Semana 3.
 */
public class GeneradorDatos {

    private static final int NUM_ESTACIONES = 9;
    private static final long SEMILLA = 20262L;

    /**
     * Genera n lecturas en orden cronológico ascendente.
     *
     * El timestamp aumenta con la posición del arreglo.
     */
    public static LecturaSensor[] generar(int n) {

        Random azar = new Random(SEMILLA);
        LecturaSensor[] datos = new LecturaSensor[n];

        for (int i = 0; i < n; i++) {

            String id = String.format(
                    "EST-%03d",
                    (i % NUM_ESTACIONES) + 1
            );

            String timestamp = String.format(
                    "%010d",
                    i
            );

            double temperatura =
                    11 + azar.nextDouble() * 18;

            double humedad =
                    55 + azar.nextDouble() * 35;

            double pm25 =
                    5 + azar.nextDouble() * 55;

            datos[i] = new LecturaSensor(
                    id,
                    timestamp,
                    redondear(temperatura),
                    redondear(humedad),
                    redondear(pm25)
            );
        }

        return datos;
    }

    private static double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    /**
     * Devuelve un timestamp que existe.
     */
    public static String timestampEnPosicion(int posicion) {
        return String.format("%010d", posicion);
    }

    /**
     * Devuelve un timestamp que no existe
     * en los arreglos generados.
     */
    public static String timestampInexistente() {
        return "9999999999";
    }
}
```

---

# 10. ¿Por qué los timestamps están ordenados?

Observa:

```java
String timestamp = String.format("%010d", i);
```

Para:

```text
i = 0
i = 1
i = 2
...
```

obtenemos:

```text
0000000000
0000000001
0000000002
...
```

Por lo tanto:

```text
posición del arreglo
        ↓
orden cronológico
```

Esta característica será fundamental para la búsqueda binaria.

---

# 11. Primer experimento: búsqueda lineal

Necesitamos ejecutar nuestra búsqueda sobre diferentes tamaños.

Pero existe una decisión arquitectónica:

> **No vamos a crear otro `main`.**

Crearemos una clase de experimentos.

---

# 12. Crear `BancoDePruebas.java`

### Archivo: `BancoDePruebas.java`

```java
/**
 * Contiene los experimentos de la Semana 3.
 *
 * Esta clase NO tiene main().
 * Los experimentos son llamados desde IngestaSensores.
 */
public class BancoDePruebas {

    private static final int[] TAMANOS = {
        1_000,
        100_000,
        1_000_000
    };

    /**
     * Experimento 1:
     * búsqueda lineal en el peor caso.
     */
    public static void experimentoUno() {

        System.out.println(
                "=== EXPERIMENTO 1: BUSQUEDA LINEAL ==="
        );

        System.out.printf(
                "%12s %16s %14s%n",
                "lecturas",
                "comparaciones",
                "tiempo (ms)"
        );

        for (int n : TAMANOS) {

            LecturaSensor[] datos =
                    GeneradorDatos.generar(n);

            String objetivo =
                    GeneradorDatos.timestampEnPosicion(n - 1);

            long inicio = System.nanoTime();

            int posicion =
                    BuscadorLecturas
                            .busquedaLinealPorTimestamp(
                                    datos,
                                    objetivo
                            );

            long fin = System.nanoTime();

            System.out.printf(
                    "%12d %16d %14.3f%n",
                    n,
                    BuscadorLecturas.getComparaciones(),
                    (fin - inicio) / 1_000_000.0
            );

            if (posicion < 0) {
                System.out.println(
                        "ADVERTENCIA: no encontro una lectura existente."
                );
            }
        }

        System.out.println();
    }
}
```

---

# 13. ¿Qué está haciendo este experimento?

Para cada tamaño:

```text
1.000
100.000
1.000.000
```

buscamos deliberadamente:

```text
la última lectura
```

Esto representa el peor caso.

Esperamos observar aproximadamente:

```text
1.000       → 1.000 comparaciones
100.000     → 100.000 comparaciones
1.000.000   → 1.000.000 comparaciones
```

---

# 14. Tiempo versus cantidad de operaciones

El programa mide:

```java
System.nanoTime()
```

pero también mide:

```java
getComparaciones()
```

El tiempo puede variar por:

- máquina virtual de Java;
- sistema operativo;
- caché;
- procesos ejecutándose simultáneamente;
- carga del computador.

Por eso utilizaremos las comparaciones como evidencia principal del comportamiento algorítmico.

> **El tiempo sirve como evidencia experimental; el número de operaciones ayuda a explicar el comportamiento del algoritmo.**

---

# 15. Integrar el experimento al único `main`

Ahora modificaremos el archivo que ya existía.

### Archivo: `IngestaSensores.java`

En el `main()` existente, después de la ejecución de la ingesta, agrega:

```java
System.out.println(
        "=== EXPERIMENTOS SEMANA 3 ==="
);

BancoDePruebas.experimentoUno();
```

No crees otro:

```java
public static void main(String[] args)
```

El proyecto continúa teniendo **un solo main**.

---

# 16. Compilar antes de continuar

Desde la terminal, ubicados en la carpeta del proyecto:

```bash
javac *.java
```

Si no aparece ningún mensaje de error, ejecuta:

```bash
java IngestaSensores
```

### Importante

La aplicación se ejecuta mediante:

```text
IngestaSensores
```

y no mediante:

```text
BancoDePruebas
```

---

# 17. El problema de `String ==`

Ahora vamos a introducir un defecto deliberadamente.

Agrega en `BuscadorLecturas.java`:

### Archivo: `BuscadorLecturas.java`

```java
/**
 * Busca la primera lectura de una estación.
 *
 * ESTA VERSION CONTIENE UN ERROR INTENCIONAL.
 */
public static int buscarPorEstacionDefectuoso(
        LecturaSensor[] datos,
        String idSensor) {

    comparaciones = 0;

    for (int i = 0; i < datos.length; i++) {

        comparaciones++;

        if (datos[i].getIdSensor() == idSensor) {
            return i;
        }
    }

    return -1;
}
```

La condición:

```java
==
```

no compara el contenido del `String`.

---

# 18. Comprender el problema

Observa:

```java
String a = new String("EST-002");
String b = new String("EST-002");
```

El contenido es igual:

```text
EST-002
EST-002
```

pero las referencias pueden ser diferentes.

Por eso:

```java
a == b
```

puede ser:

```text
false
```

Mientras:

```java
a.equals(b)
```

es:

```text
true
```

---

# 19. Corregir el problema

Ahora reemplaza el método anterior por:

### Archivo: `BuscadorLecturas.java`

```java
/**
 * Busca la primera lectura de una estación.
 *
 * @return posición de la primera coincidencia o -1
 */
public static int buscarPorEstacion(
        LecturaSensor[] datos,
        String idSensor) {

    comparaciones = 0;

    for (int i = 0; i < datos.length; i++) {

        comparaciones++;

        if (datos[i].getIdSensor().equals(idSensor)) {
            return i;
        }
    }

    return -1;
}
```

---

# 20. Ahora aparece la búsqueda binaria

La búsqueda binaria utiliza una condición adicional.

Los datos deben estar ordenados por el campo que queremos buscar.

Por ejemplo:

```text
[10, 20, 30, 40, 50, 60, 70, 80]
```

Buscamos:

```text
70
```

Comenzamos aproximadamente en:

```text
50
```

Como:

```text
70 > 50
```

podemos descartar los elementos menores.

Nos quedamos con:

```text
60 70 80
```

La idea central es:

> **Cada comparación permite descartar una parte del espacio de búsqueda.**

---

# 21. Complejidad de la búsqueda binaria

La búsqueda lineal:

```text
O(n)
```

La búsqueda binaria:

```text
O(log₂ n)
```

Ejemplo:

| Elementos | Lineal | Binaria |
|---:|---:|---:|
| 8 | 8 | 3 |
| 64 | 64 | 6 |
| 1.024 | 1.024 | 10 |
| 1.048.576 | 1.048.576 | 20 |

La diferencia se vuelve importante cuando `n` crece.

---

# 22. Implementar búsqueda binaria por timestamp

Ahora agregaremos el segundo algoritmo.

### Archivo: `BuscadorLecturas.java`

```java
/**
 * Busca un timestamp mediante búsqueda binaria.
 *
 * PRECONDICIÓN:
 * las lecturas deben estar ordenadas
 * ascendentemente por timestamp.
 *
 * @return posición de la lectura o -1 si no existe
 */
public static int busquedaBinariaPorTimestamp(
        LecturaSensor[] datos,
        String timestamp) {

    comparaciones = 0;

    int inicio = 0;
    int fin = datos.length - 1;

    while (inicio <= fin) {

        int medio = (inicio + fin) / 2;

        comparaciones++;

        int comparacion =
                datos[medio]
                        .getTimestamp()
                        .compareTo(timestamp);

        if (comparacion == 0) {
            return medio;
        }

        if (comparacion < 0) {
            inicio = medio + 1;
        } else {
            fin = medio - 1;
        }
    }

    return -1;
}
```

---

# 23. Analizar `inicio`, `fin` y `medio`

La búsqueda trabaja sobre un intervalo:

```text
inicio ---------------- fin
              ↑
            medio
```

Si el dato buscado es mayor:

```java
inicio = medio + 1;
```

Si el dato buscado es menor:

```java
fin = medio - 1;
```

¿Por qué `+1` y `-1`?

Porque `medio` ya fue comparado.

No necesitamos volver a buscarlo.

---

# 24. El ciclo infinito

Una implementación incorrecta podría utilizar:

```java
inicio = medio;
```

en lugar de:

```java
inicio = medio + 1;
```

En determinados casos:

```text
inicio
fin
medio
```

pueden quedar iguales entre iteraciones.

Entonces:

```text
inicio no avanza
fin no cambia
medio no cambia
```

y el `while` puede ejecutarse indefinidamente.

---

# 25. Antes de corregir: hacer una traza

Consideremos:

```text
[0, 1, 2, 3]
```

Buscamos:

```text
3
```

Construye esta tabla:

| Paso | inicio | fin | medio | valor medio | acción |
|---|---:|---:|---:|---:|---|
| 1 | 0 | 3 | ? | ? | ? |
| 2 | ? | ? | ? | ? | ? |
| 3 | ? | ? | ? | ? | ? |
| 4 | ? | ? | ? | ? | ? |

Debes identificar exactamente qué ocurre cuando:

```text
valor medio < objetivo
```

---

# 26. Regla de corrección

Cuando:

```java
datos[medio] < objetivo
```

debemos buscar hacia la derecha:

```java
inicio = medio + 1;
```

Cuando:

```java
datos[medio] > objetivo
```

debemos buscar hacia la izquierda:

```java
fin = medio - 1;
```

Esta regla garantiza que el intervalo de búsqueda avance.

---

# 27. Segundo experimento: comparar lineal y binaria

Ahora agregaremos:

### Archivo: `BancoDePruebas.java`

```java
/**
 * Experimento 2:
 * compara búsqueda lineal y búsqueda binaria.
 */
public static void experimentoDos() {

    System.out.println(
            "=== EXPERIMENTO 2: LINEAL vs BINARIA ==="
    );

    System.out.printf(
            "%12s %14s %14s %12s%n",
            "lecturas",
            "lineal",
            "binaria",
            "relacion"
    );

    for (int n : TAMANOS) {

        LecturaSensor[] datos =
                GeneradorDatos.generar(n);

        String objetivo =
                GeneradorDatos.timestampEnPosicion(n - 1);

        BuscadorLecturas.busquedaLinealPorTimestamp(
                datos,
                objetivo
        );

        int lineal =
                BuscadorLecturas.getComparaciones();

        BuscadorLecturas.busquedaBinariaPorTimestamp(
                datos,
                objetivo
        );

        int binaria =
                BuscadorLecturas.getComparaciones();

        System.out.printf(
                "%12d %14d %14d %12.1f%n",
                n,
                lineal,
                binaria,
                (double) lineal / binaria
        );
    }

    System.out.println();
}
```

---

# 28. Integrar el segundo experimento

### Archivo: `IngestaSensores.java`

En el único `main()`:

```java
BancoDePruebas.experimentoUno();
BancoDePruebas.experimentoDos();
```

La estructura sigue siendo:

```text
IngestaSensores.main()
        │
        ├── experimentoUno()
        └── experimentoDos()
```

No existe otro `main`.

---

# 29. Buscar un dato que no existe

Esta situación es fundamental.

Creamos:

### Archivo: `BancoDePruebas.java`

```java
/**
 * Compara búsqueda de un timestamp inexistente.
 */
public static void experimentoTres() {

    System.out.println(
            "=== EXPERIMENTO 3: DATO INEXISTENTE ==="
    );

    LecturaSensor[] datos =
            GeneradorDatos.generar(100_000);

    String objetivo =
            GeneradorDatos.timestampInexistente();

    BuscadorLecturas.busquedaLinealPorTimestamp(
            datos,
            objetivo
    );

    int lineal =
            BuscadorLecturas.getComparaciones();

    BuscadorLecturas.busquedaBinariaPorTimestamp(
            datos,
            objetivo
    );

    int binaria =
            BuscadorLecturas.getComparaciones();

    System.out.println(
            "Lineal  -> comparaciones: " + lineal
    );

    System.out.println(
            "Binaria -> comparaciones: " + binaria
    );

    System.out.println();
}
```

Esperamos aproximadamente:

```text
Lineal  → 100.000
Binaria → 17
```

si los datos están correctamente ordenados.

---

# 30. La búsqueda binaria y sus precondiciones

Una **precondición** es una condición que debe cumplirse antes de ejecutar correctamente una operación.

Para nuestra búsqueda binaria:

```text
Precondición:
los datos están ordenados por el campo buscado.
```

Por ejemplo:

```text
timestamp
    ↓
ordenado
    ↓
búsqueda binaria
```

Pero:

```text
PM2.5
    ↓
no necesariamente ordenado
    ↓
búsqueda binaria
```

no es una operación válida bajo la misma precondición.

---

# 31. Experimento con PM2.5

Ahora vamos a demostrarlo experimentalmente.

### Archivo: `BuscadorLecturas.java`

Agrega:

```java
/**
 * Búsqueda binaria por PM2.5.
 *
 * PRECONDICIÓN:
 * el arreglo debe estar ordenado ascendentemente
 * por PM2.5.
 *
 * El generador de la Semana 3 no garantiza esta condición.
 */
public static int busquedaBinariaPorPm25(
        LecturaSensor[] datos,
        double pm25) {

    comparaciones = 0;

    int inicio = 0;
    int fin = datos.length - 1;

    while (inicio <= fin) {

        int medio = (inicio + fin) / 2;

        comparaciones++;

        if (datos[medio].getPm25() == pm25) {
            return medio;
        }

        if (datos[medio].getPm25() < pm25) {
            inicio = medio + 1;
        } else {
            fin = medio - 1;
        }
    }

    return -1;
}
```

Observa algo importante:

> **El algoritmo está correctamente implementado.**

El problema será la condición de los datos.

---

# 32. ¿Por qué PM2.5 no está ordenado?

En `GeneradorDatos.java` usamos:

```java
double pm25 =
        5 + azar.nextDouble() * 55;
```

El valor se genera aleatoriamente.

Por tanto, podemos tener:

```text
12.4
48.2
17.8
33.1
8.9
...
```

No podemos asumir que esté ordenado.

---

# 33. Cuarto experimento: precondición

### Archivo: `BancoDePruebas.java`

```java
/**
 * Demuestra qué ocurre cuando la búsqueda binaria
 * se aplica sobre un campo que no está ordenado.
 */
public static void experimentoCuatro() {

    System.out.println(
            "=== EXPERIMENTO 4: BINARIA POR PM2.5 ==="
    );

    LecturaSensor[] datos =
            GeneradorDatos.generar(10_000);

    int aciertosLineal = 0;
    int aciertosBinaria = 0;

    for (int i = 0; i < 20; i++) {

        double valor =
                datos[i * 137].getPm25();

        int posLineal = -1;

        for (int j = 0; j < datos.length; j++) {

            if (datos[j].getPm25() == valor) {
                posLineal = j;
                break;
            }
        }

        int posBinaria =
                BuscadorLecturas
                        .busquedaBinariaPorPm25(
                                datos,
                                valor
                        );

        if (posLineal >= 0) {
            aciertosLineal++;
        }

        if (posBinaria >= 0) {
            aciertosBinaria++;
        }
    }

    System.out.println(
            "Valores buscados que SI existen: 20"
    );

    System.out.println(
            "Encontrados por búsqueda lineal:  "
                    + aciertosLineal
    );

    System.out.println(
            "Encontrados por búsqueda binaria: "
                    + aciertosBinaria
    );

    System.out.println();
}
```

---

# 34. ¿Qué esperamos descubrir?

El experimento busca mostrar una situación conceptualmente importante:

```text
Valores existentes
        ↓
Búsqueda lineal
        ↓
los encuentra
```

pero:

```text
Valores existentes
        ↓
Búsqueda binaria
        ↓
datos no ordenados
        ↓
puede no encontrarlos
```

La conclusión es:

```text
Algoritmo correcto
        +
Precondición falsa
        =
Resultado incorrecto
```

---

# 35. Integrar todos los experimentos

### Archivo: `IngestaSensores.java`

El único `main()` debe terminar esta semana incorporando los experimentos:

```java
public static void main(String[] args) throws IOException {

    RepositorioLecturas repositorio =
            new RepositorioLecturas();

    AnalizadorMatriz analizador =
            new AnalizadorMatriz();

    cargarArchivo(repositorio, analizador);

    System.out.println();
    System.out.println("=== INGESTA ===");
    System.out.println(
            "Lecturas almacenadas: "
                    + repositorio.tamano()
    );

    System.out.println(
            "PM2.5 promedio: "
                    + repositorio.promedioPm25()
    );

    System.out.println();
    System.out.println("=== SEMANA 3 ===");

    BancoDePruebas.experimentoUno();
    BancoDePruebas.experimentoDos();
    BancoDePruebas.experimentoTres();
    BancoDePruebas.experimentoCuatro();
}
```

**Importante:** conserva en este archivo el código de ingesta que ya construiste en las semanas anteriores. No reemplaces toda la clase solamente para agregar los experimentos.

---

# 36. Documentar la decisión de diseño

Ahora crea:

### Archivo: `docs/decisiones.md`

```markdown
# Decisiones de diseño — Semana 3

## Búsqueda binaria por timestamp

### Precondición

La búsqueda binaria requiere que las lecturas estén ordenadas
ascendentemente por timestamp.

### Condición actual del proyecto

`GeneradorDatos` produce timestamps en orden cronológico.

### Decisión

Utilizar búsqueda binaria para consultas por timestamp.

### Justificación

La búsqueda binaria reduce el número de comparaciones
de un crecimiento O(n) a un crecimiento O(log n),
siempre que se mantenga la precondición de ordenamiento.

## PM2.5

No se utilizará directamente búsqueda binaria sobre PM2.5
mientras los datos no estén ordenados por ese campo.

El experimento de la Semana 3 demuestra la importancia
de respetar las precondiciones de un algoritmo.

## Pregunta pendiente

¿Conviene ordenar los datos antes de realizar las búsquedas?

Esta pregunta será retomada en la Semana 4.
```

---

# 37. Casos de prueba mínimos

Antes de considerar terminada la búsqueda binaria, debes probar:

```text
✓ primer elemento
✓ elemento intermedio
✓ último elemento
✓ elemento existente
✓ elemento inexistente
```

También:

```text
✓ arreglo pequeño
✓ arreglo grande
```

Registra los resultados.

---

# 38. Tabla de mediciones

Construye una tabla como esta con tus propios resultados:

| Tamaño | Lineal | Binaria | Tiempo lineal | Tiempo binaria |
|---:|---:|---:|---:|---:|
| 1.000 | | | | |
| 100.000 | | | | |
| 1.000.000 | | | | |

No copies los valores de la guía.

> **Los resultados experimentales deben ser obtenidos por tu equipo.**

---

# 39. Traza de búsqueda binaria

Guarda en:

```text
bitacoras/
```

una evidencia de la traza que realizaste antes de corregir el ciclo.

Debe mostrar:

```text
inicio
fin
medio
comparación
acción
```

La finalidad no es demostrar que sabes llenar una tabla.

La finalidad es demostrar que puedes **razonar sobre el comportamiento de un algoritmo antes de modificarlo**.

---

# 40. Git como evidencia del proceso

No realices todo el trabajo y hagas un único commit.

Una secuencia razonable podría ser:

```text
commit
  Implementación búsqueda lineal
       ↓
commit
  Generador de datos
       ↓
commit
  Experimento lineal
       ↓
commit
  Búsqueda binaria
       ↓
commit
  Corrección ciclo infinito
       ↓
commit
  Experimento PM2.5
       ↓
commit
  Documentación de precondición
```

Los mensajes deben explicar qué cambió.

Ejemplos:

```text
feat: agregar búsqueda lineal por timestamp
feat: generar datos sintéticos ordenados
feat: implementar búsqueda binaria
fix: corregir actualización de límites
docs: documentar precondición de búsqueda binaria
test: agregar experimentos de búsqueda
```

---

# 41. Verificación final del proyecto

Antes de entregar ejecuta:

```bash
javac *.java
```

Después:

```bash
java IngestaSensores
```

Debes verificar que:

1. El proyecto compila.
2. Existe un único `main`.
3. La ingesta de las semanas anteriores continúa funcionando.
4. El repositorio continúa funcionando.
5. Los experimentos de Semana 3 se ejecutan desde `IngestaSensores`.
6. La búsqueda lineal encuentra datos.
7. La búsqueda binaria encuentra timestamps.
8. La búsqueda binaria termina cuando el dato no existe.
9. El experimento de PM2.5 permite identificar la precondición.
10. Las comparaciones se registran correctamente.

---

# 42. ¿Qué construiste realmente?

Al comenzar teníamos:

```text
Sensores
   ↓
Lecturas
   ↓
Repositorio
```

Ahora tenemos:

```text
Sensores
   ↓
Lecturas
   ↓
Repositorio
   ↓
Búsqueda
   ├── Lineal O(n)
   │
   └── Binaria O(log n)
          │
          └── requiere datos ordenados
```

La aplicación sigue siendo **una sola aplicación**.

El proyecto no se reinició.

Se extendió.

---

# 43. Conexión con la Semana 4

La búsqueda binaria nos deja una pregunta:

> **¿Cómo conseguimos que los datos estén ordenados?**

Aparecen nuevas preguntas:

```text
¿Cómo ordenar?
¿Cuánto cuesta ordenar?
¿Debemos ordenar una vez?
¿Debemos ordenar cada vez?
¿Con qué algoritmo?
```

Estas preguntas serán el punto de partida de la Semana 4.

La secuencia conceptual es:

```text
Semana 3
Necesitamos datos ordenados
        ↓
Semana 4
Aprendemos a ordenar
        ↓
Analizamos el costo
        ↓
Tomamos una decisión
```

---

# 44. Preguntas de pensamiento crítico

Responde con argumentos. **No se busca solamente una respuesta técnica o de código.**

### Pregunta 1

Una empresa tiene un millón de registros y realiza únicamente cinco búsquedas durante todo el día.

¿Tiene sentido diseñar toda la estrategia de almacenamiento alrededor de una búsqueda binaria? ¿Qué otros costos o factores considerarías?

---

### Pregunta 2

Un algoritmo puede ser mucho más rápido que otro y, sin embargo, producir una respuesta incorrecta.

¿Por qué consideras que la corrección debe analizarse antes que la eficiencia?

---

### Pregunta 3

Imagina que una plataforma consulta constantemente por `timestamp`, pero ocasionalmente necesita consultar por `PM2.5`.

¿Qué consecuencias tendría organizar los datos pensando principalmente en uno de estos campos?

No respondas solamente desde el código: considera el funcionamiento de la plataforma.

---

### Pregunta 4

Supón que tienes un conjunto de datos perfectamente ordenado y alguien modifica algunos registros sin conservar el orden.

¿Qué riesgos aparecen si el sistema continúa utilizando búsqueda binaria sin verificar las condiciones de los datos?

---

### Pregunta 5

En ingeniería de software suele decirse:

> "Que funcione no significa que sea una buena solución."

Relaciona esta afirmación con lo aprendido en las semanas 1, 2 y 3 del proyecto.

¿Qué ha cambiado en la manera en que analizas una solución desde que comenzó el proyecto?

---

# 45. Entregables de la Semana 3

Al finalizar esta guía, tu repositorio debe contener:

```text
Proyecto/
│
├── IngestaSensores.java
├── LecturaSensor.java
├── RepositorioLecturas.java
├── AnalizadorMatriz.java
├── BuscadorLecturas.java
├── GeneradorDatos.java
├── BancoDePruebas.java
│
├── data/
│   └── lecturas_ampliadas.csv
│
├── bitacoras/
│   └── traza_busqueda_binaria.*
│
└── docs/
    └── decisiones.md
```

### Debes poder demostrar

- búsqueda lineal;
- búsqueda binaria;
- análisis de `O(n)`;
- análisis de `O(log n)`;
- mejor, promedio y peor caso;
- conteo de comparaciones;
- comparación correcta de `String`;
- corrección del ciclo de búsqueda binaria;
- comprensión de precondiciones;
- análisis del caso PM2.5;
- integración con el único `main`;
- uso de Git;
- pensamiento crítico sobre decisiones de diseño.

---

# 46. Criterio de finalización

La Semana 3 está terminada cuando puedas responder afirmativamente:

```text
[ ] ¿Mi proyecto tiene un único main?
[ ] ¿IngestaSensores sigue siendo el punto de entrada?
[ ] ¿Puedo buscar linealmente por timestamp?
[ ] ¿Puedo buscar binariamente por timestamp?
[ ] ¿Sé explicar por qué la binaria requiere orden?
[ ] ¿Sé explicar O(n) y O(log n)?
[ ] ¿Sé explicar el ciclo infinito que corregí?
[ ] ¿Sé explicar por qué String se compara con equals()?
[ ] ¿Puedo demostrar el problema de PM2.5?
[ ] ¿Tengo mediciones propias?
[ ] ¿Tengo la traza de la búsqueda binaria?
[ ] ¿Documenté la decisión de diseño?
[ ] ¿Mi proyecto compila?
[ ] ¿Mis cambios están registrados en Git?
[ ] ¿Puedo explicar qué agregué a la aplicación sin
    decir que construí "otro programa"?
```

---

# Idea clave para recordar

```text
             PROYECTO INTEGRADOR
                     │
             ┌───────┴────────┐
             │                │
        DATOS GRANDES       BÚSQUEDA
             │                │
             │        ┌───────┴───────┐
             │        │               │
             │      LINEAL          BINARIA
             │        │               │
             │      O(n)           O(log n)
             │                        │
             │                  necesita orden
             │                        │
             └──────────┬─────────────┘
                        │
                   DECISIÓN DE
                     DISEÑO
                        │
                  medir + justificar
```

> **No basta con que un algoritmo funcione. Hay que saber cuánto cuesta, bajo qué condiciones funciona y cómo integrarlo correctamente en el sistema que estamos construyendo.**

La Semana 3 no crea un nuevo proyecto.

**La Semana 3 hace que el proyecto de la Red de Sensores IoT pueda buscar y analizar datos a mayor escala.**
