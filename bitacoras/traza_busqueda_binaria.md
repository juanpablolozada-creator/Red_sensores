# Traza de Búsqueda Binaria, Análisis de Ciclo y Mediciones Experimentales

**Espacio académico:** Estructuras de Datos  
**Proyecto:** Plataforma de Monitoreo Ambiental Urbano — Red de Sensores IoT  
**Semana 03:** Encontrar un dato entre un millón  
**Estudiante:** Juan Pablo Lozada López  

---

## 1. Traza Paso a Paso: Búsqueda Binaria

### Escenario de Prueba
- **Arreglo ordenado:** `datos = [0, 1, 2, 3]` (tamaño $n = 4$)
- **Objetivo buscado:** `3`

### Tabla de Ejecución Paso a Paso

| Paso | `inicio` | `fin` | `medio` (`(inicio+fin)/2`) | `datos[medio]` | Comparación | Acción tomada |
|:---:|:---:|:---:|:---:|:---:|:---:|:---|
| **1** | 0 | 3 | $(0 + 3) / 2 = 1$ | 1 | $1 < 3$ (`valor < objetivo`) | El objetivo está a la derecha. Ajustar `inicio = medio + 1 = 2`. Espacio restante: `[2, 3]`. |
| **2** | 2 | 3 | $(2 + 3) / 2 = 2$ | 2 | $2 < 3$ (`valor < objetivo`) | El objetivo está a la derecha. Ajustar `inicio = medio + 1 = 3`. Espacio restante: `[3]`. |
| **3** | 3 | 3 | $(3 + 3) / 2 = 3$ | 3 | $3 == 3$ (`valor == objetivo`) | **¡Coincidencia encontrada!** Retornar índice `3`. |

**Total de comparaciones realizadas:** 3 comparaciones (en búsqueda lineal habrían sido 4 comparaciones).

---

## 2. Análisis del Defecto: El Ciclo Infinito

### ¿Qué causa el ciclo infinito?
Si en lugar de `inicio = medio + 1` se utiliza la asignación ingenua `inicio = medio`:
- En el **Paso 2**, tenemos `inicio = 2` y `fin = 3`.
- Se calcula `medio = (2 + 3) / 2 = 2` (por división entera).
- Al comparar, `datos[2]` (2) es menor que el objetivo (3).
- Si asignamos `inicio = medio`, el nuevo valor de `inicio` vuelve a ser `2`.
- En la siguiente iteración, `inicio = 2` y `fin = 3` permanecen inalterados. El cálculo de `medio` vuelve a dar `2`, la comparación se repite de idéntica manera, y el ciclo `while (inicio <= fin)` nunca termina, consumiendo el 100% de la CPU en un bucle infinito.

### Regla de Corrección
1. Cuando `datos[medio] < objetivo`:
   - El elemento en `medio` ya fue evaluado y descartado.
   - El nuevo límite inferior debe ser obligatoriamente: `inicio = medio + 1`.
2. Cuando `datos[medio] > objetivo`:
   - El elemento en `medio` ya fue evaluado y descartado.
   - El nuevo límite superior debe ser obligatoriamente: `fin = medio - 1`.

Esta regla garantiza que el intervalo de búsqueda `[inicio, fin]` se reduzca estrictamente en al menos un elemento en cada iteración, garantizando convergencia y terminación en a lo sumo $\lfloor\log_2 n\rfloor + 1$ pasos.

---

## 3. Comparación de Cadenas en Java (`String ==` vs `.equals()`)

En Java, el operador `==` compara **identidad de referencias en memoria**, es decir, si ambas variables apuntan a la misma dirección física en el Heap.
- Dos cadenas creadas independientemente como `new String("EST-002")` y `new String("EST-002")` tienen el mismo contenido léxico, pero residen en posiciones de memoria distintas.
- Por ende, `a == b` evalúa a `false`, generando falsos negativos en las búsquedas.
- El método `.equals()` compara el contenido carácter por carácter, garantizando la igualdad semántica.
- Para comparaciones de orden lexicográfico en búsqueda binaria, se utiliza `.compareTo(objetivo)`, el cual devuelve `0` si son iguales, un valor negativo si la cadena actual antecede al objetivo, y un valor positivo si lo sucede.

---

## 4. Tabla de Mediciones Experimentales Reales

Resultados obtenidos en la máquina local al ejecutar `BancoDePruebas` integrado en `IngestaSensores.java`:

| Lecturas ($n$) | Comparaciones Lineal | Comparaciones Binaria | Relación de Eficiencia (Lineal / Binaria) | Tiempo Lineal (ms) |
|---:|---:|---:|---:|---:|
| **1.000** | 1.000 | 10 | **100,0×** | ~0,877 ms |
| **100.000** | 100.000 | 17 | **5.882,4×** | ~3,373 ms |
| **1.000.000** | 1.000.000 | 20 | **50.000,0×** | ~8,467 ms |

### Experimento 3: Búsqueda de Dato Inexistente ($n = 100.000$)
- **Búsqueda Lineal:** Requiere recorrer la totalidad del arreglo: **100.000 comparaciones**.
- **Búsqueda Binaria:** Descarta la mitad del espacio en cada paso: **17 comparaciones**.

### Experimento 4: Precondición de Ordenamiento sobre PM2.5 ($n = 10.000$)
- **Valores buscados (existentes en el arreglo):** 20
- **Encontrados por búsqueda lineal:** **20 / 20** (100% de efectividad)
- **Encontrados por búsqueda binaria (sin ordenar):** **0 / 20** (0% de efectividad)

> **Conclusión empírica:** Un algoritmo de búsqueda binaria implementado de manera impecable produce un 100% de fallo si los datos no satisfacen la precondición de ordenamiento ascendente.

---

## 5. Respuestas a Preguntas de Pensamiento Crítico

### Pregunta 1
> *Una empresa tiene un millón de registros y realiza únicamente cinco búsquedas durante todo el día. ¿Tiene sentido diseñar toda la estrategia de almacenamiento alrededor de una búsqueda binaria? ¿Qué otros costos o factores considerarías?*

**Respuesta:**  
No tiene sentido estructurar todo el sistema alrededor de la búsqueda binaria para ese volumen de consultas.
1. **Costo de ordenamiento frente a costo de búsqueda:** Ordenar un millón de registros mediante algoritmos eficientes ($O(n \log n)$ como MergeSort o QuickSort) toma del orden de $1.000.000 \times 20 \approx 20.000.000$ operaciones. Realizar cinco búsquedas lineales directas sobre el arreglo sin ordenar cuesta en el peor caso $5 \times 1.000.000 = 5.000.000$ comparaciones (y en promedio $2.500.000$). Por ende, ordenar previamente cuesta entre 4 y 8 veces más que ejecutar las búsquedas lineales directamente.
2. **Costo de inserción continua:** En un sistema de sensores IoT que ingiere lecturas permanentemente, mantener un arreglo ordenado implica que cada inserción cuesta $O(n)$ por los desplazamientos de memoria requeridos.
3. **Criterio de ingeniería:** La búsqueda binaria solo amortiza su costo de ordenamiento cuando la tasa de consultas por unidad de tiempo supera con creces la frecuencia de reordenamiento o mutación.

---

### Pregunta 2
> *Un algoritmo puede ser mucho más rápido que otro y, sin embargo, producir una respuesta incorrecta. ¿Por qué consideras que la corrección debe analizarse antes que la eficiencia?*

**Respuesta:**  
Porque un resultado incorrecto tiene utilidad nula o destructiva, sin importar la rapidez con la que se compute.
- En un sistema de alerta temprana de calidad del aire, una búsqueda que entrega un falso negativo ("no hay niveles críticos de PM2.5") en 0.001 milisegundos puede impedir que se declare una contingencia ambiental, arriesgando la salud de la población.
- La eficiencia es una propiedad de optimización de una solución que ya es correcta. Si la solución no es correcta, optimizarla significa simplemente "equivocarse más rápido".
- Por ello, en el ciclo de desarrollo de software y análisis algorítmico, el primer paso irrenunciable es demostrar la validez semántica y el cumplimiento de precondiciones; únicamente cuando la corrección está verificada se procede a la optimización asintótica.

---

### Pregunta 3
> *Imagina que una plataforma consulta constantemente por `timestamp`, pero ocasionalmente necesita consultar por `PM2.5`. ¿Qué consecuencias tendría organizar los datos pensando principalmente en uno de estos campos?*

**Respuesta:**  
Organizar físicamente los datos por un único atributo (en este caso `timestamp`) optimiza de forma asimétrica el sistema:
1. **Consecuencia positiva:** Las consultas por `timestamp` se benefician de la búsqueda binaria $O(\log n)$, respondiendo en microsegundos ante flujos de consulta masivos.
2. **Consecuencia negativa:** El ordenamiento por `timestamp` destruye cualquier orden relativo en `PM2.5`. Por ende, cualquier consulta sobre `PM2.5` queda condenada a una búsqueda lineal exhaustiva $O(n)$ o requerirá ordenar una copia de los datos.
3. **Perspectiva arquitectónica:** Si las consultas por `PM2.5` son ocasionales, el diseño es aceptable porque el caso frecuente está optimizado. Sin embargo, si las consultas secundarias crecen, la solución industrial consiste en no forzar un ordenamiento destructivo en el arreglo principal, sino emplear **índices secundarios** (por ejemplo, arreglos de apuntadores indexados o estructuras en árbol / tablas hash) que permitan múltiples accesos eficientes sin alterar el orden cronológico primario.

---

### Pregunta 4
> *Supón que tienes un conjunto de datos perfectamente ordenado y alguien modifica algunos registros sin conservar el orden. ¿Qué riesgos aparecen si el sistema continúa utilizando búsqueda binaria sin verificar las condiciones de los datos?*

**Respuesta:**  
El mayor riesgo es la aparición de **fallos silenciosos (silent data corruption / falsos negativos)**:
1. La búsqueda binaria asume ciegamente que si `objetivo > datos[medio]`, el dato jamás podrá encontrarse en la mitad izquierda.
2. Si un registro modificado quedó ubicado en la mitad izquierda a pesar de ser mayor, el algoritmo descartará esa mitad y retornará `-1` (no encontrado), aun cuando el registro esté presente en el arreglo.
3. Lo peligroso de este fallo es que el sistema no lanza ninguna excepción (`Exception`), no se cae (`crash`) ni genera advertencias visibles: opera con total normalidad pero entregando información errónea a las capas superiores.
4. Para mitigar esto, o bien se encapsula la estructura para impedir modificaciones directas sin validación de orden, o bien se marcan banderas de "sucio" (`dirty flag`) para reordenar antes de buscar.

---

### Pregunta 5
> *En ingeniería de software suele decirse: "Que funcione no significa que sea una buena solución." Relaciona esta afirmación con lo aprendido en las semanas 1, 2 y 3 del proyecto. ¿Qué ha cambiado en la manera en que analizas una solución desde que comenzó el proyecto?*

**Respuesta:**  
- **Semana 1 (De código frágil a robusto):** Un script con 5 variables sueltas y sin validación "funcionaba" con archivos ideales, pero colapsaba ante una coma faltante, un `ERR` o un valor fuera de rango físico. Aprendimos que una buena solución exige encapsulamiento en objetos (`LecturaSensor`) y resiliencia ante datos corruptos.
- **Semana 2 (TAD y Abstracción):** Almacenar lecturas en un arreglo estático "funcionaba" hasta que se superaba la capacidad de 10 elementos. Implementar el redimensionamiento dinámico y ocultar los detalles internos tras una interfaz (`RepositorioLecturas`) nos enseñó la diferencia entre almacenar datos y diseñar un Tipo Abstracto de Datos profesional.
- **Semana 3 (Eficiencia y Escala):** La búsqueda lineal "funciona" y siempre encuentra el dato si existe, pero su costo escala linealmente $O(n)$, colapsando con millones de datos. Por su parte, la búsqueda binaria es ultrarrápida $O(\log n)$, pero es extremadamente vulnerable si se violan sus precondiciones.
- **Cambio de perspectiva:** Al inicio, la pregunta era binaria: *¿El programa compila y corre?* Hoy, el análisis se fundamenta en criterios de ingeniería: *¿Es seguro ante entradas no controladas? ¿Cuál es su costo asintótico en tiempo y memoria? ¿Bajo qué precondiciones garantiza corrección? ¿Cómo escala cuando el volumen de datos pasa de miles a millones?*
