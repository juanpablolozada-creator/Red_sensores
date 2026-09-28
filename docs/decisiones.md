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

### Pregunta pendiente para la Semana 4
¿Conviene ordenar los datos previamente para habilitar búsquedas binarias frecuentes, o el costo computacional de ordenar supera el beneficio de las búsquedas? Se analizará en la Semana 4 con algoritmos de ordenamiento.



