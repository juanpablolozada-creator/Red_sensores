# Bitácora individual - Semana 04

## 1. Datos de la actividad

- **Estudiante:** Juan Pablo Lozada López
- **Equipo:** Equipo Proyecto Integrador Red Sensores IoT
- **Semana:** 04
- **Fecha del laboratorio:** 2026-09-30
- **Fecha del taller:** 2026-09-30
- **Tema principal:** Algoritmos de ordenamiento (Burbuja, Selección, Inserción, MergeSort, HeapSort, QuickSort), contadores de eficiencia, corte temprano, optimización de pivote y efectos colaterales sobre búsqueda binaria.
- **Pregunta de la semana:** ¿Cuánto cuesta ordenar lecturas en la plataforma ambiental, cómo escala el trabajo de cada algoritmo cuando aumentan los datos y qué consecuencias tiene ordenar por un criterio sobre las búsquedas existentes?

---

## 2. Predicción antes de ejecutar

1. **¿Qué creo que va a ocurrir?**  
   - Con datos desordenados, predigo que los algoritmos simples ($O(n^2)$) realizarán un número cuadrático de comparaciones ($\approx 50$ millones para $n = 10.000$).
   - Con datos ya ordenados cronológicamente, Burbuja con corte temprano e Inserción detectarán inmediatamente el orden en una sola pasada, bajando a solo $n - 1$ comparaciones ($9.999$) y $0$ intercambios.
   - En escalabilidad ($n = 1.000, 10.000, 100.000$), MergeSort y HeapSort ($O(n \log n)$) mantendrán tiempos de pocos milisegundos, mientras Inserción superará los 30 segundos para $100.000$ elementos desordenados.
   - QuickSort con pivote en el primer elemento causará `StackOverflowError` al recibir 50.000 lecturas ordenadas cronológicamente por degenerar la recursión a profundidad 50.000. La versión con pivote aleatorio resolverá el problema inmediatamente.
   - Al ordenar los datos por PM2.5, la búsqueda binaria por timestamp fallará porque se pierde la precondición de ordenamiento cronológico.

2. **¿Qué parte del programa o del algoritmo puede fallar?**  
   - QuickSort con pivote fijo en datos ordenados provocará desbordamiento de la pila de memoria (`StackOverflowError`).
   - El corte temprano en Burbuja podría no detenerse si la condición booleana se reinicia en el lugar incorrecto.
   - La búsqueda binaria sobre arreglos reordenados por PM2.5 fallará al intentar buscar por timestamp.

3. **¿Cómo comprobaré mi predicción?**  
   - Ejecutando `BancoDeOrdenamiento.java` y `IngestaSensores.java`, midiendo contadores estáticos de comparaciones e intercambios (`getComparaciones()`, `getIntercambios()`) y midiendo tiempos de reloj con `System.currentTimeMillis()`.

---

## 3. Evidencia del laboratorio (Mediciones Reales)

### Experimento 1: 10.000 lecturas DESORDENADAS (Algoritmos Simples)
| Algoritmo | Comparaciones | Intercambios | Tiempo (ms) |
| :--- | :---: | :---: | :---: |
| **Burbuja** | 49.990.814 | 24.928.244 | 656 ms |
| **Selección** | 49.995.000 | 9.994 | 459 ms |
| **Inserción** | 24.938.233 | 24.928.244 | 190 ms |

*Observación clave:* Selección realiza prácticamente las mismas comparaciones que Burbuja ($\approx 50$ millones), pero reduce los intercambios de 24.9 millones a solo 9.994 (un intercambio por posición). Demuestra que comparar y mover datos tienen costos muy diferentes.

### Experimento 2: 10.000 lecturas YA ORDENADAS (Corte temprano)
| Algoritmo | Comparaciones | Intercambios | Tiempo (ms) |
| :--- | :---: | :---: | :---: |
| **Burbuja (Corte T.)** | 9.999 | 0 | 0 ms |
| **Selección** | 49.995.000 | 0 | 305 ms |
| **Inserción** | 9.999 | 0 | 0 ms |

*Observación clave:* Gracias al TODO 1 (bandera `huboIntercambio`), Burbuja se detiene tras la primera pasada al no encontrar elementos fuera de lugar. Inserción también aprovecha la preordenación realizando solo $n - 1$ comparaciones. Selección, en cambio, sigue buscando el mínimo ciegamente en todo el arreglo.

### Experimento 3: Simples vs Avanzados a escala creciente
| $n$ | Algoritmo | Comparaciones | Intercambios | Tiempo (ms) |
| :---: | :--- | :---: | :---: | :---: |
| **1.000** | Inserción | 242.787 | 241.797 | 5 ms |
| 1.000 | MergeSort | 8.696 | 0 | 1 ms |
| 1.000 | HeapSort | 16.786 | 9.065 | 1 ms |
| **10.000** | Inserción | 24.938.233 | 24.928.244 | 220 ms |
| 10.000 | MergeSort | 120.476 | 0 | 4 ms |
| 10.000 | HeapSort | 235.434 | 124.208 | 4 ms |
| **100.000** | Inserción | 2.497.222.762 | 2.497.122.770 | 31.025 ms |
| 100.000 | MergeSort | 1.536.461 | 0 | 49 ms |
| 100.000 | HeapSort | 3.019.556 | 1.574.970 | 88 ms |

#### Razones de crecimiento experimental:
- **Crecimiento de 1.000 a 10.000 ($n \times 10$):**
  - Inserción: **102,72x** en comparaciones (crecimiento cuadrático $10^2 = 100$).
  - MergeSort: **13,85x** en comparaciones (crecimiento $O(n \log n)$).
  - HeapSort: **14,03x** en comparaciones (crecimiento $O(n \log n)$).
- **Crecimiento de 10.000 a 100.000 ($n \times 10$):**
  - Inserción: **100,14x** en comparaciones y pasó de 220 ms a más de 31 segundos.
  - MergeSort: **12,75x** en comparaciones y solo 49 ms.
  - HeapSort: **12,83x** en comparaciones y solo 88 ms.

### Experimento 4: QuickSort con 50.000 lecturas
- **Caso A (50.000 desordenadas, pivote primero):** 900.318 comparaciones, 450.373 intercambios, 44 ms.
- **Caso B (50.000 en orden cronológico, pivote primero):**
  `StackOverflowError`: El programa agotó la pila de ejecución. Antes de colapsar acumuló más de 596 millones de comparaciones.
- **Caso B Mejorado (50.000 en orden cronológico, pivote aleatorio - TODO 2):**
  904.611 comparaciones, 511.721 intercambios, **9 ms**.

### Experimento 5: El Ranking por PM2.5 y la Consulta por Timestamp
1. Datos en orden cronológico original:
   - `estaOrdenadoPorTimestamp == true`.
   - Búsqueda binaria por timestamp `0000073412` -> Encontrado en **posición 73.412** con **16 comparaciones**.
2. Ordenamiento por PM2.5 (`Ordenador.ordenarPorPm25(datos)`):
   - Ranking generado: PM2.5 mínimo = 5.0, máximo = 60.0.
3. Consulta posterior del mismo timestamp:
   - `estaOrdenadoPorTimestamp == false`.
   - Búsqueda binaria por timestamp `0000073412` -> **Posición: -1** (No encontrado, 17 comparaciones).
   - Verificación con búsqueda lineal -> **Posición: 87.594** (Encontrado con 87.595 comparaciones).

---

## 4. Explicación en lenguaje llano

### ¿Cómo funcionan los ordenamientos simples?
- **Burbuja:** Es como tener botellas en fila y comparar siempre dos vecinas; si la de la izquierda es más pesada, las cambias de puesto. La más pesada va flotando hasta el final. Si recorres toda la fila sin cambiar ninguna botella, sabes que ya están ordenadas y te detienes (corte temprano).
- **Selección:** Buscas en toda la fila la botella más liviana de todas y la pones de primera. Luego buscas la más liviana del resto y la pones de segunda. Haces muchas miradas (comparaciones), pero mueves los brazos muy pocas veces (intercambios).
- **Inserción:** Como ordenar naipes en la mano: tomas la siguiente carta y la vas metiendo en el lugar exacto entre las cartas que ya tienes ordenadas. Si la baraja ya viene en orden, solo revisas la carta anterior y no tienes que desplazar nada.

### ¿Por qué QuickSort falló con el pivote primero y cómo lo arregló el azar?
QuickSort divide el grupo eligiendo un líder (pivote) y poniendo a los menores a la izquierda y a los mayores a la derecha. Si el grupo ya está ordenado del 1 al 50.000 y siempre eliges al primero como pivote:
- El líder es el 1. A la izquierda no hay nadie (0 cartas), y a la derecha quedan 49.999 cartas.
- En la siguiente llamada el líder es el 2, y a la derecha quedan 49.998 cartas.
En lugar de partir el problema por la mitad ($50.000 \to 25.000 \to 12.500$), estás quitando una sola carta cada vez. Esto genera una torre de 50.000 llamadas pendientes que rebosa la memoria asignada a Java (`StackOverflowError`).
Al elegir un **pivote al azar**, es extremadamente improbable que siempre escojas el más pequeño; en promedio partes la baraja casi por la mitad, resolviendo el ordenamiento en solo 9 milisegundos con una profundidad de recursión de apenas unas 16 a 20 llamadas.

### ¿Por qué ordenar por PM2.5 rompió la búsqueda binaria?
La búsqueda binaria depende de que los datos estén ordenados por el mismo criterio que buscas. Si un libro telefónico está ordenado por apellidos, puedes buscar a "Lozada" abriendo por la mitad. Pero si alguien reorganiza el libro por orden de edad de los clientes para hacer un concurso, las páginas siguen teniendo a todas las personas, pero buscar por apellido abriendo por la mitad ya no funciona porque la regla de "los apellidos con L están más adelante" dejó de ser cierta.

---

## 5. Decisiones de ingeniería y conclusiones

1. **Eficiencia en contexto:** Para datos de sensores que llegan cronológicamente, Inserción y Burbuja con bandera son extremadamente veloces ($O(n)$) para verificar o mantener el orden.
2. **Escalabilidad:** Para grandes volúmenes de datos desordenados ($n \ge 100.000$), algoritmos $O(n^2)$ como Inserción tardan más de 30 segundos, mientras MergeSort y HeapSort resuelven la tarea en menos de una décima de segundo.
3. **Protección estructural:** Para generar rankings analíticos por PM2.5, nunca debemos mutar el arreglo principal del repositorio: debemos trabajar sobre copias (`copiar(datos)`), preservando intacta la precondición que permite búsquedas binarias instantáneas por timestamp.
