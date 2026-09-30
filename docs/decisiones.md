# Decisiones de Diseno - Bitacora Tecnica

Formato: cada entrada con fecha, decision, alternativas consideradas, justificacion.

## S1 - De codigo fragil a confiable - 2026-08-26

### Decision 1: Crear clase LecturaSensor
- **Alternativas:** mantener 5 variables sueltas vs crear clase.
- **Elegida:** clase con atributos privados, constantes de rangos.
- **Justificacion:** encapsulamiento (numeral 1.2), reutilizable 12 semanas, firma de metodos pasa de 5 params a 1. Evita error de orden de parametros.

### Decision 2: Estrategia ante fila invalida
- **Alternativas:** A) descartar solo campo malo B) descartar fila completa.
- **Elegida:** B) fila completa.
- **Justificacion:** principio conservador para reporte oficial. Si un canal falla (-999), no confiamos en sincronia de los otros dos. Preferimos perdida de datos a contaminacion silenciosa. Registrado en descartes.csv para auditoria.

### Decision 3: Manejo de excepciones
- **Alternativas:** catch generico Exception vs especificos.
- **Elegida:** catch especifico NumberFormatException y ArrayIndexOutOfBoundsException + validacion fisica.
- **Justificacion:** catch vacio o generico esconde causa. Necesitamos trazabilidad: cuantos, por que motivo.

### Decision 4: Constantes vs numeros magicos
- **Elegida:** TEMP_MIN=-40, TEMP_MAX=60, HUM_MIN=0, HUM_MAX=100, PM_MIN=0, CODIGO_DESCONECTADO=-999
- **Justificacion:** si cambian umbrales de la norma ambiental, se cambia en un solo lugar.

## S2 - Especificación de TAD, Estructuras de Datos y Matriz - 2026-09-14

### Decisión 5: Reglas de Validación de Lecturas en la Ingesta
- **Diseñado por:** Alejandro Tafur
- **Alternativas:** A) Permitir datos corruptos y tratarlos individualmente en la matriz. B) Descartar la fila completa ante cualquier anomalía numérica o de rango.
- **Elegida:** B) Filtro conservador con descarte de fila completa.
- **Justificación:** Garantiza la integridad del dataset procesado. Si una variable presenta inconsistencias de formato (`ERR`, texto), valores nulos/desconectados (`-999`) o está fuera de los rangos físicos (humedad > 100% o PM2.5 negativo), se descarta toda la lectura para evitar desfasar las mediciones de la estación.

### Decisión 6: Control de Integridad para Lecturas Duplicadas
- **Diseñado por:** Alejandro Tafur
- **Alternativas:** A) Sobrescribir la lectura previa con el nuevo registro entrante. B) Ignorar la lectura duplicada detectada (misma estación y misma hora).
- **Elegida:** B) Ignorar la lectura duplicada.
- **Justificación:** Mantiene la precisión de las lecturas en memoria. Evita que registros repetidos en el archivo de origen incrementen innecesariamente el tamaño del repositorio o distorsionen los cálculos de promedios ambientales.

## S3 - Búsqueda y Eficiencia - 2026-09-28

### Decisión 7: Búsqueda binaria por timestamp

- **Precondición:** La búsqueda binaria requiere que las lecturas estén ordenadas ascendentemente por timestamp.
- **Condición actual del proyecto:** `GeneradorDatos` produce timestamps en orden cronológico ascendente.
- **Decisión:** Utilizar búsqueda binaria para consultas por timestamp sobre el repositorio ordenado.
- **Justificación:** La búsqueda binaria reduce drásticamente el número de comparaciones de un crecimiento lineal O(n) a un crecimiento logarítmico O(log n), pasando de 1.000.000 de comparaciones en el peor caso a tan solo 20 comparaciones en arreglos de gran escala.

### Decisión 8: Búsqueda sobre campos no ordenados (PM2.5)

- **Precondición:** La búsqueda binaria exige ordenamiento en el campo clave.
- **Condición actual del proyecto:** Los valores de PM2.5 se generan con variabilidad continua y no garantizan ordenamiento.
- **Decisión:** No se utilizará búsqueda binaria directamente sobre PM2.5 mientras los datos no estén ordenados; se emplea búsqueda lineal secuencial O(n).
- **Justificación:** El experimento 4 demostró que aplicar búsqueda binaria sobre datos no ordenados provoca que el algoritmo falle silenciosamente (0 aciertos de 20 datos que sí existían). La corrección y fiabilidad del sistema prevalece sobre la velocidad.

### Pregunta resuelta en la Semana 4
¿Conviene ordenar los datos previamente para habilitar búsquedas binarias frecuentes, o el costo computacional de ordenar supera el beneficio de las búsquedas?
Se comprobó que ordenar 100.000 elementos toma entre 49 ms (MergeSort) y 31 segundos (Inserción). Una búsqueda binaria toma ~17 comparaciones (< 0.001 ms). Si se realizan miles de consultas, el costo del ordenamiento inicial se amortiza rápidamente, siempre que se preserve el orden o se utilicen copias/índices.

## S4 - Ordenamientos y Comparación de Eficiencia - 2026-09-30

### DEC-04: Estrategia de Selección de Pivote en QuickSort

- **Semana:** 4
- **Diseñado por:** Juan Pablo Lozada López
- **Problema:**
  En la implementación básica de QuickSort con pivote en el primer elemento (`datos[inicio]`), al ejecutarse sobre las 50.000 lecturas que llegan en orden cronológico (natural de la red de sensores IoT), el pivote seleccionado resulta ser sistemáticamente el menor elemento de cada subintervalo. La partición degenera en un subarreglo vacío y otro de tamaño $n-1$, produciendo una profundidad de recursión de 50.000 llamadas en la pila y complejidad cuadrática $O(n^2)$.
- **Evidencia experimental real:**
  - Caso A (50.000 lecturas desordenadas, pivote primero): Se completó con éxito en 44 ms (900.318 comparaciones, 450.373 intercambios).
  - Caso B (50.000 lecturas en orden cronológico, pivote primero): Provocó un `StackOverflowError` fatal, desbordando la pila de la JVM tras alcanzar más de 596 millones de comparaciones acumuladas antes del colapso.
- **Alternativas consideradas:**
  1. Mantener pivote fijo en el primer elemento (inviable para datos ordenados).
  2. Pivote aleatorio (selección aleatoria de un índice en $[inicio, fin]$ e intercambio con $inicio$).
  3. Mediana de tres (primer, medio y último elemento).
- **Decisión elegida:**
  Implementar **Pivote Aleatorio** (TODO 2).
- **Justificación:**
  La selección de pivote aleatorio rompe la dependencia de cualquier orden preexistente en los datos (ascendente, descendente o con patrones periódicos), garantizando con altísima probabilidad divisiones equilibradas cercanas a $n/2$, manteniendo una profundidad de recursión de $O(\log n)$ y tiempo promedio $O(n \log n)$.
- **Consecuencia y validación:**
  Al repetir el Caso B con pivote aleatorio sobre las mismas 50.000 lecturas cronológicas, el algoritmo ordenó el arreglo en tan solo **9 - 11 ms**, realizando **904.611 comparaciones y 511.721 intercambios**, sin ninguna sobrecarga en la pila de ejecución.

### DEC-05: Ordenamiento Multicriterio y Efecto Colateral sobre la Búsqueda Binaria

- **Semana:** 4
- **Diseñado por:** Juan Pablo Lozada López
- **Problema:**
  La plataforma requiere generar reportes de alerta y rankings ambientales ordenados por concentración de PM2.5. Sin embargo, al aplicar `ordenarPorPm25(datos)` directamente sobre el arreglo del repositorio, los registros se redistribuyen de acuerdo con su nivel de contaminación, destruyendo el ordenamiento cronológico por timestamp.
- **Evidencia experimental real (Experimento 5):**
  1. En los datos originales en orden cronológico (`estaOrdenadoPorTimestamp == true`), la búsqueda binaria del timestamp `0000073412` localizó la lectura en la posición 73.412 con tan solo **16 comparaciones**.
  2. Tras ejecutar `ordenarPorPm25(datos)`, el ranking se generó correctamente (PM2.5 de 5.0 a 60.0), pero `estaOrdenadoPorTimestamp` pasó a ser `false`.
  3. Al consultar nuevamente el mismo timestamp mediante búsqueda binaria, el algoritmo falló retornando **posición -1** (17 comparaciones).
  4. La verificación mediante búsqueda lineal demostró que la lectura **aún existía** (encontrada en la posición 87.594 con 87.595 comparaciones).
  - **Conclusión:** El algoritmo de búsqueda binaria no tenía ningún error de programación; falló porque **se violó su precondición estructural**.
- **Alternativas consideradas:**
  1. Reordenar por timestamp después de cada consulta de ranking por PM2.5 (costo $O(n \log n)$ por cada reporte).
  2. Trabajar sobre copias del arreglo original (`LecturaSensor[] copia = copiar(original)`).
  3. Mantener estructuras de índices secundarios separados.
- **Decisión elegida:**
  Para reportes y rankings analíticos, operar siempre sobre **copias temporales de los datos**, preservando el arreglo principal del repositorio permanentemente ordenado por su clave primaria cronológica (timestamp).
- **Justificación:**
  Clonar el arreglo de referencias tiene un costo lineal $O(n)$ en tiempo y memoria despreciable, protegiendo la integridad del sistema y garantizando que las búsquedas binarias frecuentes continúen ejecutándose en tiempo logarítmico $O(\log n)$ sin inconsistencias entre módulos.



