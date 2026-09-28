# Bitacora individual - Semana 03

## 1. Datos de la actividad

- **Estudiante:** Juan Pablo Lozada López
- **Equipo:** Equipo Proyecto Integrador Red Sensores IoT
- **Semana:** 03
- **Fecha del laboratorio:** 2026-09-28
- **Fecha del taller:** 2026-09-28
- **Tema principal:** Búsqueda lineal, búsqueda binaria, complejidad algorítmica O(n) vs O(log n) y precondiciones de ordenamiento
- **Pregunta de la semana:** ¿Cómo encontramos una lectura específica cuando el repositorio pasa de cientos a cientos de miles o millones de registros y cuánto cuesta hacerlo?

## 2. Prediccion antes de ejecutar

1. **Que creo que va a ocurrir?**  
   Predigo que la búsqueda lineal tardará un tiempo proporcional al tamaño del arreglo (crecimiento lineal $O(n)$) y requerirá exactamente $n$ comparaciones cuando busquemos el último elemento o un dato inexistente. En contraste, predigo que la búsqueda binaria resolverá la búsqueda en un número ínfimo de comparaciones (del orden de $\approx 20$ para 1.000.000 de registros) gracias a la división sucesiva por dos del espacio de búsqueda.

2. **Que parte del programa o del algoritmo puede fallar?**  
   Puede fallar la actualización de los índices de la búsqueda binaria si se asigna `inicio = medio` en vez de `inicio = medio + 1`, provocando un ciclo infinito. También fallará la búsqueda binaria sobre arreglos no ordenados (como en el experimento con PM2.5).

3. **Como comprobare mi prediccion?**  
   Mediante la ejecución de `BancoDePruebas` con experimentos de 1.000, 100.000 y 1.000.000 de registros, midiendo el contador estático `getComparaciones()` y el tiempo en nanosegundos con `System.nanoTime()`.

## 3. Evidencia del laboratorio

### Resultado observado

Al ejecutar `IngestaSensores.java`, los experimentos arrojaron:
- **Experimento 1 (Lineal en peor caso):**
  - 1.000 lecturas: 1.000 comparaciones, 0,877 ms
  - 100.000 lecturas: 100.000 comparaciones, 3,373 ms
  - 1.000.000 lecturas: 1.000.000 comparaciones, 8,467 ms
- **Experimento 2 (Lineal vs Binaria):**
  - 1.000: Lineal 1.000 vs Binaria 10 (relación 100,0x)
  - 100.000: Lineal 100.000 vs Binaria 17 (relación 5.882,4x)
  - 1.000.000: Lineal 1.000.000 vs Binaria 20 (relación 50.000,0x)
- **Experimento 3 (Dato Inexistente, 100.000 elementos):**
  - Lineal: 100.000 comparaciones
  - Binaria: 17 comparaciones
- **Experimento 4 (Binaria sobre PM2.5 no ordenado, 20 datos existentes):**
  - Búsqueda lineal: 20 encontrados de 20 (100%)
  - Búsqueda binaria: 0 encontrados de 20 (0%)

### Diferencia entre la prediccion y el resultado

La predicción coincidió exactamente con la teoría matemática: $\lceil\log_2(1000)\rceil \approx 10$, $\lceil\log_2(100000)\rceil \approx 17$, $\lceil\log_2(1000000)\rceil \approx 20$. En el experimento 4 se evidenció de forma contundente que la búsqueda binaria no puede encontrar datos cuando se viola la precondición de ordenamiento.

### Error o comportamiento inesperado

- **Que ocurrio?** En el diseño inicial de búsqueda binaria, al evaluar `datos[medio] < objetivo`, si se asignaba `inicio = medio`, el bucle quedaba atrapado indefinidamente al buscar en subintervalos de tamaño 2.
- **Por que ocurrio?** Porque la división entera trunca hacia abajo, produciendo que `medio` sea igual a `inicio`, impidiendo que el límite inferior avance.
- **Como lo corregimos o que falta corregir?** Se ajustó el límite a `inicio = medio + 1` y `fin = medio - 1`, dado que la posición `medio` ya fue comparada y descartada.

## 4. Explicacion en lenguaje llano

Imagina que buscas una palabra en un diccionario de un millón de páginas. Si usas búsqueda lineal, lees página por página desde la primera hasta encontrarla; si la palabra está al final, tendrás que pasar un millón de hojas. En cambio, con la búsqueda binaria abres el diccionario justo por la mitad. Si la palabra que buscas va después, descartas de golpe medio millón de páginas de la izquierda y repites lo mismo con la otra mitad. Con solo 20 aperturas de libro encuentras cualquier página entre un millón.

### Ejemplo o analogia

Es como el juego de adivinar un número del 1 al 100 donde el oponente solo dice "más alto" o "más bajo". La mejor estrategia no es preguntar 1, 2, 3... sino 50, luego 75 u 25, partiendo siempre el rango por la mitad. La analogía deja de ser exacta cuando las páginas o números no están ordenados: si las hojas estuvieran barajadas al azar, abrir por la mitad no te diría absolutamente nada sobre hacia qué lado buscar.

## 5. El vacio que encontre

- **Mi duda concreta es:** Si la búsqueda binaria es tan rápida, ¿por qué no ordenamos siempre todos los arreglos antes de buscar?
- **Lo que ya puedo explicar es:** Que la búsqueda binaria exige precondición de ordenamiento, pero ordenar un arreglo cuesta $O(n \log n)$, lo cual es mucho más costoso que unas pocas búsquedas lineales $O(n)$ si el arreglo cambia constantemente.
- **Para resolver la duda consulte:** El experimento 4 y las preguntas de pensamiento crítico de la guía de la Semana 3.
- **Ahora lo entiendo asi:** Ordenar solo se justifica si la cantidad de búsquedas futuras amortiza la inversión inicial de ordenar.

## 6. Trazado de la solucion

Búsqueda binaria sobre arreglo `[0, 1, 2, 3]` buscando el valor `3`:

| Paso | Estado de los datos o estructura | Decision o resultado |
|---|---|---|
| 1 | `inicio=0`, `fin=3`, `medio=1`, `valor=1` | $1 < 3 \rightarrow$ Mover `inicio = medio + 1 = 2` |
| 2 | `inicio=2`, `fin=3`, `medio=2`, `valor=2` | $2 < 3 \rightarrow$ Mover `inicio = medio + 1 = 3` |
| 3 | `inicio=3`, `fin=3`, `medio=3`, `valor=3` | $3 == 3 \rightarrow$ Coincidencia encontrada en índice 3 |
| 4 | Finalización exitosa | Retorna índice `3` en solo 3 comparaciones |

## 7. Decision de diseño

- **Problema que debiamos resolver:** Permitir consultas eficientes por timestamp sobre volúmenes masivos de sensores sin degradar el rendimiento.
- **Estructura, algoritmo o estrategia elegida:** Búsqueda binaria sobre arreglos generados en orden cronológico ascendente.
- **Alternativa descartada:** Búsqueda lineal para consultas frecuentes por timestamp, y búsqueda binaria sin validación previa sobre variables desordenadas como PM2.5.
- **Por que elegimos la primera:** La búsqueda binaria escala en $O(\log n)$, realizando apenas 20 comparaciones sobre 1.000.000 de registros frente a 1.000.000 de comparaciones en la lineal.
- **Que evidencia respalda la decision:** Mediciones del experimento 2 (aceleración de 50.000×) y del experimento 4 (fallo total al aplicarla sin orden).

## 8. Aporte al proyecto

- **Archivo(s) o modulo(s) trabajado(s):** `src/BuscadorLecturas.java`, `src/GeneradorDatos.java`, `src/BancoDePruebas.java`, `src/IngestaSensores.java`, `src/RepositorioLecturas.java`, `docs/decisiones.md`, `bitacoras/traza_busqueda_binaria.md`.
- **Cambio realizado:** Implementación de algoritmos de búsqueda lineal y binaria con conteo de comparaciones, generador sintético, batería de 4 experimentos automatizados, redimensionamiento dinámico del TAD y documentación exhaustiva.
- **Como se conecta con la capa anterior:** Mantiene a `IngestaSensores.java` como único punto de entrada (`main`), reutilizando `LecturaSensor`, `RepositorioLecturas` y `AnalizadorMatriz` de las Semanas 1 y 2.
- **Que queda pendiente para la siguiente semana:** Implementación y análisis de algoritmos de ordenamiento en la Semana 4 para ordenar por PM2.5 u otros atributos antes de buscar.

## 9. Commits realizados

| Commit | Mensaje | Que demuestra |
|---|---|---|
| `b1e1a27` | `docs: agrega guias de estudio semana 3 y ramas git` | Integración de las especificaciones y guías de la semana |
| `26ea919` | `feat: implementa BuscadorLecturas con busqueda lineal y binaria` | Algoritmos de búsqueda con conteo de operaciones |
| `be1eb3c` | `feat: crea GeneradorDatos para generar lecturas sinteticas ordenadas` | Generación de grandes volúmenes para pruebas de escala |
| `bf50ddc` | `test: implementa BancoDePruebas con experimentos 1 a 4 de busqueda y eficiencia` | Batería experimental empírica |
| `cf7bf27` | `feat: integra experimentos de semana 3 en IngestaSensores` | Respeto a la regla de punto único de entrada arquitectónico |
| `8e1f57a` | `fix: implementa redimensionamiento dinamico en RepositorioLecturas segun contrato TAD` | Crecimiento dinámico de almacenamiento |
| `[actual]` | `docs: documenta decisiones S3, traza de busqueda y bitacora semanal` | Documentación técnica y evidencias de aprendizaje |

## 10. Reexplicacion final

Encontrar un dato entre un millón pasa de ser una cuestión de "¿funciona?" a "¿cuánto cuesta?". La búsqueda lineal revisa cada registro con un costo $O(n)$, volviéndose prohibitiva a gran escala. La búsqueda binaria reduce este costo a $O(\log n)$, requiriendo solo 20 comparaciones para un millón de datos. No obstante, su uso está estrictamente condicionado a que los datos estén previamente ordenados; si no lo están, la búsqueda binaria produce fallos catastróficos silenciosos. Por ende, la arquitectura debe justificar cuándo compensa pagar el costo de ordenar para habilitar búsquedas binarias.

## 11. Reflexion individual

1. **Lo que ahora puedo hacer y antes no podia:** Evaluar empírica y matemáticamente la eficiencia de un algoritmo contando operaciones elementales (`comparaciones`), no solo confiando en tiempos de reloj.
2. **El error o supuesto que mas me enseno:** Asumir que la búsqueda binaria funcionaría sobre cualquier arreglo; el experimento de PM2.5 me demostró que un código matemáticamente perfecto falla por completo si no valida sus precondiciones.
3. **La pregunta que llevaria a la proxima clase:** ¿Bajo qué ratio de búsquedas vs inserciones es matemáticamente rentable ordenar un arreglo en memoria frente a mantenerlo desordenado?
4. **Que parte del trabajo fue realmente mia:** El análisis y trazado del ciclo infinito, la implementación de los límites `inicio = medio + 1`, la integración de las pruebas en el `main` único y la consolidación de la bitácora técnica.

## Lista de verificacion antes de entregar

- [x] Escribi la prediccion antes de consultar el resultado.
- [x] Inclui evidencia concreta del laboratorio.
- [x] Explique un concepto sin depender de jerga.
- [x] Registre un vacio, una duda o un error real.
- [x] Trace al menos un caso paso a paso.
- [x] Justifique una decision del proyecto y una alternativa descartada.
- [x] Registre mis commits y mi aporte individual.
- [x] Deje claro que queda pendiente.
- [x] Renombre el archivo con el formato `sXX-nombre.md`.
