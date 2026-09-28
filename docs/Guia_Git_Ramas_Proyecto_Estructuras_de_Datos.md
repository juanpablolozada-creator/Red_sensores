# Guía de Git — Del proyecto base a un proyecto con ramas por semana

## Estructuras de Datos — Proyecto Red de Sensores IoT

---

## 1. Propósito de esta guía

Hasta la **Semana 2** hemos construido una primera versión funcional del proyecto **Red de Sensores IoT**.

A partir de la **Semana 3** cambiaremos la forma de trabajar.

Ya no vamos a trabajar directamente sobre una única línea de desarrollo. Vamos a utilizar **ramas (branches)** de Git, como se hace habitualmente en proyectos reales de desarrollo de software.

La idea central es:

> **La rama `main` representa siempre una línea base estable y funcional del proyecto.**

Cada nueva funcionalidad, experimento o corrección se desarrollará en una rama independiente. Cuando el trabajo esté terminado, probado y revisado, la rama se integrará nuevamente a `main` mediante una **fusión (merge)**.

Al finalizar esta guía debes ser capaz de:

- comprender qué es una rama;
- diferenciar `main` de una rama de trabajo;
- crear una rama correctamente;
- utilizar nombres coherentes para las ramas;
- desarrollar una funcionalidad sin modificar directamente `main`;
- registrar los cambios mediante commits;
- comprobar que el proyecto sigue funcionando;
- fusionar una rama con `main`;
- comprender qué ocurre cuando aparecen conflictos;
- mantener `main` limpia, estable y funcional.

---

# 2. La situación actual: la línea base de Semana 2

Al terminar la Semana 2 debes tener una versión funcional del proyecto.

Esa versión será nuestra **línea base**.

Representaremos la situación inicial así:

```text
main
  |
  ● Semana 1
  |
  ● Semana 2
```

La rama `main` contiene el estado que consideramos estable hasta la Semana 2.

A partir de ahora:

```text
                    Semana 3
                       |
main ──●───────────────●
        \
         \
          ● cambios Semana 3
```

Sin embargo, la idea real es que **los cambios de Semana 3 no se hagan directamente sobre `main`**.

La estrategia será:

```text
main
  |
  ● Semana 1
  |
  ● Semana 2
  |
  └───────────────► base para nuevas ramas

                      \
                       ● Semana 3
                       ● Semana 3
                       ● Semana 3
                         |
                         └── merge ──► main
```

---

# 3. ¿Qué es una rama?

Una **rama (branch)** es una línea independiente de desarrollo dentro del repositorio.

Permite trabajar sobre una modificación sin alterar inmediatamente la versión estable del proyecto.

Una forma sencilla de imaginarlo:

```text
                    ┌── feature/semana-3-busqueda
                    │
main ───────────────┼────────────────────
                    │
                    └── otra línea de trabajo
```

La rama permite experimentar, modificar código, corregir errores y desarrollar funcionalidades manteniendo separada la línea base.

## 3.1 Una analogía

Imagina que `main` es el documento oficial de un proyecto.

Antes de modificarlo haces una copia de trabajo.

En esa copia puedes:

- agregar contenido;
- corregir errores;
- experimentar;
- probar alternativas.

Cuando el resultado está listo, incorporas los cambios al documento oficial.

Git permite hacer esto de forma controlada y trazable.

---

# 4. ¿Por qué no debemos trabajar directamente sobre `main`?

Porque `main` debe representar una versión **estable**.

Supongamos que Semana 3 consiste en implementar búsqueda binaria.

Si trabajamos directamente sobre `main` y cometemos un error:

```text
main
  |
  ● Semana 2
  |
  ● búsqueda binaria incompleta
  |
  ● error
  |
  ● proyecto no funciona
```

Ahora nuestra línea base quedó dañada.

En cambio:

```text
main
  |
  ● Semana 2
  |
  └─────────────── feature/semana-3-busquedas
                       |
                       ● implementación
                       |
                       ● pruebas
                       |
                       ● correcciones
```

Si algo sale mal, `main` continúa funcionando.

---

# 5. Regla fundamental del proyecto

A partir de esta semana utilizaremos la siguiente regla:

> **NO desarrollar directamente sobre `main`.**

La rama `main` debe permanecer:

- funcional;
- compilable;
- probada;
- limpia de errores conocidos;
- libre de código experimental;
- libre de archivos innecesarios;
- lista para ser utilizada como base de una nueva rama.

Esta regla será parte de la evaluación del proyecto.

---

# 6. ¿Qué significa "línea base"?

Una **línea base (baseline)** es una versión del proyecto que ha sido identificada como un punto estable desde el cual se puede continuar el desarrollo.

Para nuestro proyecto:

```text
Semana 2
    ↓
Línea base
    ↓
main
```

Esto significa que la `main` de la Semana 2 será el punto de partida para el trabajo de Semana 3.

No queremos solamente guardar código.

Queremos conservar una **versión estable del proyecto**.

---

# 7. Verificar la línea base antes de crear ramas

Antes de comenzar Semana 3, debes comprobar que tu proyecto funciona.

Ubícate en la carpeta del proyecto:

```bash
cd proyecto-red-sensores
```

Verifica el estado:

```bash
git status
```

Idealmente deberías obtener algo equivalente a:

```text
On branch main
nothing to commit, working tree clean
```

Ahora verifica las ramas:

```bash
git branch
```

Debes observar:

```text
* main
```

El `*` indica la rama en la que estás trabajando actualmente.

---

# 8. Verificar que `main` funciona

Antes de crear la rama de Semana 3:

1. compila el proyecto;
2. ejecuta el proyecto;
3. verifica que no existen errores conocidos;
4. comprueba que los archivos necesarios están presentes.

Por ejemplo:

```bash
javac *.java
```

Y posteriormente:

```bash
java IngestaSensores
```

Si el proyecto utiliza otra forma de ejecución, utiliza la definida por el proyecto.

## Regla importante

> **No crees una nueva rama a partir de una `main` que ya sabes que está rota.**

Primero corrige la línea base.

---

# 9. Crear una rama

La forma tradicional de crear una rama es:

```bash
git branch nombre-de-la-rama
```

Pero normalmente queremos crearla y cambiar inmediatamente a ella.

Para ello utilizaremos:

```bash
git switch -c nombre-de-la-rama
```

Por ejemplo:

```bash
git switch -c feature/semana-3-busqueda
```

Ahora puedes comprobar dónde estás:

```bash
git branch
```

Obtendrás algo similar a:

```text
  main
* feature/semana-3-busqueda
```

El `*` indica que ahora estás trabajando en:

```text
feature/semana-3-busqueda
```

---

# 10. ¿Por qué `feature/semana-3-busqueda`?

El nombre de una rama debe comunicar **qué se está haciendo**.

Evita nombres como:

```text
rama1
prueba
nueva
cambio
jose
final
final2
prueba-final
rama-definitiva
```

Estos nombres no permiten comprender el propósito de la rama.

En cambio:

```text
feature/semana-3-busqueda
```

permite identificar:

- `feature` → tipo de trabajo;
- `semana-3` → momento/contexto académico;
- `busqueda` → propósito de la modificación.

---

# 11. Tipos de ramas que utilizaremos

A partir de esta semana utilizaremos principalmente dos tipos.

## 11.1 `feature`

Se utiliza para agregar una nueva funcionalidad.

Ejemplos:

```text
feature/semana-3-busqueda
feature/semana-4-pilas
feature/semana-5-colas
feature/semana-6-arboles
feature/ordenamiento-lecturas
feature/exportar-resultados
```

La palabra `feature` significa que estamos agregando o desarrollando una capacidad nueva.

---

## 11.2 `bugfix`

Se utiliza para corregir un error existente.

Ejemplos:

```text
bugfix/correccion-busqueda-binaria
bugfix/error-promedio-pm25
bugfix/validacion-timestamp
bugfix/error-lecturas-nulas
```

Una rama `bugfix` no representa una nueva funcionalidad.

Representa una **corrección de un comportamiento incorrecto**.

---

# 12. ¿Qué tipo de rama debo crear?

Utiliza esta regla:

| Situación | Tipo de rama |
|---|---|
| Agregar una nueva funcionalidad | `feature/` |
| Implementar el tema de una nueva semana | `feature/` |
| Agregar una estructura de datos | `feature/` |
| Agregar una nueva búsqueda | `feature/` |
| Corregir un error | `bugfix/` |
| Corregir un cálculo incorrecto | `bugfix/` |
| Corregir una validación | `bugfix/` |
| Corregir un algoritmo existente | `bugfix/` |

### Regla de nomenclatura

> **El nombre de la rama debe explicar el propósito del trabajo.**

Se recomienda utilizar:

```text
tipo/descripcion-corta
```

Por ejemplo:

```text
feature/semana-3-busqueda
```

o:

```text
bugfix/error-busqueda-estacion
```

---

# 13. Una rama por trabajo, no una rama para todo

No debemos crear una única rama llamada:

```text
feature/cambios
```

y colocar allí todo lo que hagamos durante el semestre.

Es preferible separar trabajos con objetivos diferentes.

Por ejemplo:

```text
feature/semana-3-busqueda
feature/semana-4-pilas
bugfix/error-validacion
feature/exportar-csv
```

Esto permite comprender mejor la historia del proyecto.

---

# 14. Crear la rama de Semana 3

Como estamos iniciando Semana 3 y el objetivo es implementar búsquedas, ejecuta:

```bash
git switch main
```

Comprueba:

```bash
git status
```

Luego crea la rama:

```bash
git switch -c feature/semana-3-busqueda
```

Comprueba nuevamente:

```bash
git branch
```

Debe aparecer:

```text
  main
* feature/semana-3-busqueda
```

Ahora sí puedes comenzar a modificar el código.

---

# 15. ¿Qué sucede con los archivos?

Al crear una rama no se crea necesariamente una segunda copia física completa del proyecto.

Git administra las versiones y el historial de forma que diferentes líneas de desarrollo puedan coexistir.

Desde el punto de vista del estudiante, lo importante es comprender:

```text
main
  |
  | estado estable
  |
  └────► feature/semana-3-busqueda
             |
             | modificaciones
             |
             └── commits
```

La rama comienza desde un punto concreto de la historia.

Por eso es tan importante crearla desde la `main` correcta.

---

# 16. Comenzar el trabajo de Semana 3

En la rama:

```text
feature/semana-3-busqueda
```

construirás los archivos correspondientes a la Semana 3.

Entre ellos:

```text
BuscadorLecturas.java
GeneradorDatos.java
BancoDePruebas.java
```

y realizarás las modificaciones necesarias en:

```text
IngestaSensores.java
```

Los archivos existentes de las semanas anteriores forman parte de la base del proyecto.

La estructura continúa evolucionando:

```text
Semana 1
    ↓
Semana 2
    ↓
Semana 3
    ↓
Semana 4
    ↓
...
```

No son proyectos independientes.

Es **un único proyecto que evoluciona**.

---

# 17. Trabajar y realizar commits

A medida que desarrollas, debes guardar avances mediante commits.

Primero consulta:

```bash
git status
```

Después agrega los archivos:

```bash
git add .
```

Y realiza un commit:

```bash
git commit -m "feat: implementa busqueda lineal"
```

Otro ejemplo:

```bash
git commit -m "feat: agrega busqueda binaria por timestamp"
```

Y para una corrección:

```bash
git commit -m "fix: corrige comparacion de idSensor"
```

---

# 18. Convención recomendada para los commits

Aunque el objetivo principal de esta guía son las ramas, comenzaremos también a utilizar mensajes de commit descriptivos.

Recomendación:

```text
feat: nueva funcionalidad
fix: correccion de error
test: pruebas
docs: documentacion
refactor: reorganizacion sin cambiar comportamiento
```

Ejemplos:

```text
feat: agrega buscador de lecturas
feat: implementa busqueda binaria
test: agrega casos de busqueda
fix: corrige comparacion de cadenas
docs: actualiza decisiones de semana 3
```

El mensaje debe explicar **qué cambió**, no simplemente:

```text
cambios
avance
terminado
prueba
asdf
```

---

# 19. ¿Cuándo debo hacer un commit?

Un commit debe representar un cambio coherente.

Por ejemplo:

```text
Commit 1
feat: crea BuscadorLecturas

Commit 2
feat: implementa busqueda lineal

Commit 3
test: valida busqueda lineal

Commit 4
feat: implementa busqueda binaria

Commit 5
test: compara busqueda lineal y binaria
```

No es necesario hacer un commit por cada línea de código.

La pregunta útil es:

> ¿Este conjunto de cambios representa una unidad lógica de trabajo?

Si la respuesta es sí, puede ser un buen candidato para un commit.

---

# 20. Verificar que la rama funciona

Antes de pensar en fusionar la rama, debes comprobar que el proyecto funciona.

Ejecuta:

```bash
javac *.java
```

Después:

```bash
java IngestaSensores
```

Comprueba:

- compilación;
- ejecución;
- resultados;
- búsquedas;
- pruebas;
- ausencia de errores conocidos.

También puedes revisar el historial:

```bash
git log --oneline
```

---

# 21. La rama no está terminada solamente porque el código compile

Compilar no significa automáticamente que el trabajo esté terminado.

Antes de fusionar debes comprobar:

```text
Código
   ↓
Compilación
   ↓
Pruebas
   ↓
Corrección de errores
   ↓
Verificación
   ↓
Merge
```

La calidad del proyecto depende de este proceso.

---

# 22. ¿Qué pasa si encuentro un error?

Supongamos que durante Semana 3 encuentras un error en la búsqueda por estación.

No necesariamente debes modificar `main`.

Si estás trabajando en:

```text
feature/semana-3-busqueda
```

puedes corregirlo allí si forma parte del trabajo de esa funcionalidad.

Si el error corresponde a una funcionalidad ya existente y debe corregirse como trabajo independiente, puedes crear:

```text
bugfix/error-busqueda-estacion
```

La idea es separar claramente:

```text
Nueva funcionalidad
        ↓
feature/

Corrección de error
        ↓
bugfix/
```

---

# 23. Ejemplo de un bugfix

Supongamos que existe este código:

```java
if (datos[i].getIdSensor() == idSensor) {
```

En Java, esta comparación puede ser incorrecta para comparar contenido de `String`.

La corrección sería:

```java
if (datos[i].getIdSensor().equals(idSensor)) {
```

Podríamos crear:

```bash
git switch main
git switch -c bugfix/comparacion-id-sensor
```

Realizar la corrección:

```java
if (datos[i].getIdSensor().equals(idSensor)) {
```

Probar:

```bash
javac *.java
java IngestaSensores
```

Guardar:

```bash
git add .
git commit -m "fix: corrige comparacion de idSensor"
```

Y posteriormente fusionar la corrección.

---

# 24. ¿Qué significa fusionar (merge)?

Una vez que una rama está terminada, queremos incorporar sus cambios a `main`.

Eso se llama:

> **Merge o fusión.**

Supongamos que tenemos:

```text
main
  |
  ● Semana 2
  |
  └────── feature/semana-3-busqueda
                 |
                 ● búsqueda lineal
                 |
                 ● búsqueda binaria
```

Cuando hacemos merge:

```text
main
  |
  ● Semana 2
  | \
  |  ● búsqueda lineal
  |  ● búsqueda binaria
  | /
  ● merge
```

Ahora `main` contiene los cambios desarrollados en la rama.

---

# 25. Antes del merge: una regla fundamental

**Nunca debes fusionar una rama sin comprobar primero que funciona.**

Antes del merge:

```bash
git status
```

Luego:

```bash
javac *.java
```

Y:

```bash
java IngestaSensores
```

Realiza las pruebas correspondientes.

La pregunta es:

> ¿La rama está lista para convertirse nuevamente en parte de la línea estable?

Si la respuesta es no, continúa trabajando en la rama.

---

# 26. Preparar el merge

Supongamos que terminaste:

```text
feature/semana-3-busqueda
```

Primero verifica:

```bash
git status
```

Luego guarda todos los cambios:

```bash
git add .
git commit -m "feat: completa implementacion semana 3"
```

Ahora cambia a `main`:

```bash
git switch main
```

Comprueba:

```bash
git status
```

Debes estar en:

```text
* main
```

---

# 27. Fusionar la rama

Ejecuta:

```bash
git merge feature/semana-3-busqueda
```

Git intentará incorporar los cambios.

Si todo está bien, la rama queda integrada a `main`.

La historia ahora puede verse aproximadamente así:

```text
                    ● feature
                   /
● Semana 2 ───────● merge
```

---

# 28. Verificar `main` después del merge

Este paso es obligatorio.

Después del merge:

```bash
javac *.java
```

Luego:

```bash
java IngestaSensores
```

Y revisa:

```bash
git status
```

La idea es que `main` vuelva a estar:

```text
FUNCIONAL
+
COMPILABLE
+
PROBADA
+
LIMPIA
```

---

# 29. ¿Qué hacemos con la rama después del merge?

Una vez fusionada, la rama ya cumplió su objetivo.

Podemos eliminarla localmente:

```bash
git branch -d feature/semana-3-busqueda
```

Esto no elimina los cambios que ya fueron incorporados a `main`.

Los cambios forman parte del historial del repositorio.

Después:

```bash
git branch
```

Podríamos tener:

```text
* main
```

La rama de trabajo ya no es necesaria.

---

# 30. ¿Por qué eliminar ramas terminadas?

Porque un repositorio con demasiadas ramas antiguas puede volverse difícil de entender.

Si ya tenemos:

```text
feature/semana-3
feature/semana-4
feature/semana-5
feature/prueba
feature/prueba-final
feature/nueva
rama-jose
rama-definitiva
```

se vuelve difícil saber cuáles están activas y cuáles ya terminaron.

Una buena práctica es:

```text
Crear
  ↓
Trabajar
  ↓
Probar
  ↓
Merge
  ↓
Eliminar rama terminada
```

---

# 31. ¿Qué es un conflicto?

Un conflicto aparece cuando Git no puede determinar automáticamente cómo combinar dos cambios.

Por ejemplo, si dos ramas modifican las mismas líneas de un archivo de manera incompatible:

```text
main
   \
    \ cambio A
     \
      ?
     /
    / cambio B
   /
feature
```

Git puede detener el merge y solicitar que el desarrollador resuelva la situación.

---

# 32. Ejemplo conceptual de conflicto

Supongamos que `main` tiene:

```java
double promedio = calcularPromedio();
```

Y una rama modifica esas mismas líneas a:

```java
double promedio = calcularPromedioPm25();
```

Mientras otra modificación propone:

```java
double promedio = calcularPromedioHumedad();
```

Git no puede saber cuál de las dos debe mantenerse.

Entonces informa un conflicto.

---

# 33. ¿Qué hacer si aparece un conflicto?

No debes entrar en pánico.

Primero:

```bash
git status
```

Git indicará qué archivos presentan conflictos.

Abre el archivo y encontrarás marcas similares a:

```text
<<<<<<< HEAD
double promedio = calcularPromedioPm25();
=======
double promedio = calcularPromedioHumedad();
>>>>>>> feature/otra-rama
```

Debes analizar el problema y decidir qué código corresponde realmente al diseño del proyecto.

Después debes eliminar las marcas de conflicto y dejar el código correcto.

Luego:

```bash
git add archivo.java
```

Y finalmente completar el merge:

```bash
git commit
```

---

# 34. Regla para resolver conflictos

Nunca resuelvas un conflicto simplemente escogiendo una opción sin comprenderla.

Pregunta:

1. ¿Qué hacía `main`?
2. ¿Qué cambio introducía la rama?
3. ¿Cuál es el comportamiento correcto?
4. ¿Se pueden conservar ambos cambios?
5. ¿El proyecto sigue funcionando después de resolverlo?

Después de resolver un conflicto:

```bash
javac *.java
```

y:

```bash
java IngestaSensores
```

Las pruebas son obligatorias.

---

# 35. Flujo completo que utilizaremos desde Semana 3

El flujo general será:

```text
                 ┌─────────────────────┐
                 │        main         │
                 │ línea base estable  │
                 └──────────┬──────────┘
                            │
                            ▼
                  crear rama de trabajo
                            │
             ┌──────────────┴──────────────┐
             ▼                             ▼
       feature/...                     bugfix/...
             │                             │
             ▼                             ▼
          cambios                       corrección
             │                             │
             └──────────────┬──────────────┘
                            ▼
                          commits
                            │
                            ▼
                         pruebas
                            │
                            ▼
                           merge
                            │
                            ▼
                 ┌─────────────────────┐
                 │        main         │
                 │ estable y funcional │
                 └─────────────────────┘
```

---

# 36. Flujo para cada nueva semana

A partir de Semana 3, cada semana seguirá este patrón.

## Paso 1 — Partir de `main`

```bash
git switch main
```

## Paso 2 — Verificar estado

```bash
git status
```

Debe estar limpio.

## Paso 3 — Verificar funcionamiento

```bash
javac *.java
java IngestaSensores
```

## Paso 4 — Crear la rama

Ejemplo:

```bash
git switch -c feature/semana-4-pilas
```

## Paso 5 — Desarrollar

Modificar y crear los archivos necesarios.

## Paso 6 — Hacer commits

```bash
git add .
git commit -m "feat: implementa pila de lecturas"
```

## Paso 7 — Probar

```bash
javac *.java
java IngestaSensores
```

## Paso 8 — Volver a `main`

```bash
git switch main
```

## Paso 9 — Fusionar

```bash
git merge feature/semana-4-pilas
```

## Paso 10 — Probar nuevamente `main`

```bash
javac *.java
java IngestaSensores
```

## Paso 11 — Eliminar rama terminada

```bash
git branch -d feature/semana-4-pilas
```

---

# 37. La regla de oro del proyecto

Durante todo el semestre:

> **`main` debe estar siempre en condiciones de ser ejecutada.**

Esto significa que no debe utilizarse como laboratorio de experimentación.

El laboratorio de desarrollo es la rama de trabajo.

```text
main
│
├── estable
├── funcional
├── probado
└── limpio
       │
       └──► ramas de trabajo
                  │
                  ├── feature
                  └── bugfix
```

---

# 38. ¿Qué significa "limpio"?

Un proyecto limpio no significa que nunca tenga cambios.

Significa que no contiene:

- código comentado innecesariamente;
- archivos temporales;
- archivos generados que no deberían estar versionados;
- contraseñas;
- credenciales;
- datos personales;
- código experimental abandonado;
- errores conocidos;
- archivos duplicados;
- nombres ambiguos;
- commits sin propósito claro.

También debes utilizar un `.gitignore` adecuado.

---

# 39. Nunca subir credenciales

Nunca hagas:

```text
password.txt
.env
api-key.txt
credenciales.txt
```

ni incluyas claves directamente en el código.

Un repositorio Git conserva el historial.

Eliminar posteriormente un archivo no significa necesariamente que la información deje de existir en el historial.

---

# 40. ¿Qué ocurre si una rama queda mal?

Una de las ventajas de trabajar con ramas es que una rama defectuosa no necesariamente daña `main`.

Por ejemplo:

```text
main
  |
  ● Semana 2
  |
  ├──── feature/semana-3-busqueda
  │          |
  │          ● error
  │          ● otro error
  │
  └──── main continúa estable
```

Puedes corregir la rama:

```text
feature/semana-3-busqueda
        |
        ● corrección
        |
        ● pruebas
        |
        └── merge
```

El objetivo no es evitar todos los errores.

El objetivo es **controlar dónde ocurren los cambios y cómo llegan a la línea estable**.

---

# 41. ¿Qué ocurre si `main` tiene un error?

Si detectas un error en `main`, no debes ignorarlo para continuar agregando funcionalidades.

Debes determinar si corresponde crear un:

```text
bugfix/descripcion-del-error
```

Por ejemplo:

```bash
git switch main
git switch -c bugfix/error-calculo-promedio
```

Realizas la corrección, pruebas y haces merge.

Así la corrección también queda registrada en la historia del proyecto.

---

# 42. Las ramas también son una herramienta de organización

Una rama no es solamente un mecanismo técnico.

También permite comunicar:

> "Estoy trabajando en esta parte del proyecto."

Por ejemplo:

```text
feature/semana-3-busqueda
```

comunica claramente:

> "Estoy desarrollando la funcionalidad de búsqueda correspondiente a la Semana 3."

Mientras:

```text
bugfix/error-busqueda-estacion
```

comunica:

> "Estoy corrigiendo un error relacionado con la búsqueda por estación."

---

# 43. Actividad práctica

Ahora debes realizar el siguiente procedimiento en tu repositorio.

## Actividad 1 — Verificar la línea base

Ejecuta:

```bash
git switch main
git status
git log --oneline
```

Verifica que:

- estás en `main`;
- el árbol de trabajo está limpio;
- existe el historial de las semanas anteriores.

---

## Actividad 2 — Crear la rama de Semana 3

Ejecuta:

```bash
git switch -c feature/semana-3-busqueda
```

Comprueba:

```bash
git branch
```

---

## Actividad 3 — Desarrollar

Construye el trabajo correspondiente a Semana 3:

- `BuscadorLecturas.java`;
- `GeneradorDatos.java`;
- `BancoDePruebas.java`;
- modificaciones necesarias en `IngestaSensores.java`.

Utiliza commits coherentes.

---

## Actividad 4 — Probar

Ejecuta:

```bash
javac *.java
java IngestaSensores
```

Documenta los resultados.

---

## Actividad 5 — Revisar

Ejecuta:

```bash
git status
git log --oneline
```

Comprueba que los cambios están registrados.

---

## Actividad 6 — Fusionar

Regresa a `main`:

```bash
git switch main
```

Fusiona:

```bash
git merge feature/semana-3-busqueda
```

---

## Actividad 7 — Verificar `main`

Ejecuta nuevamente:

```bash
javac *.java
java IngestaSensores
```

El proyecto debe continuar funcionando.

---

## Actividad 8 — Limpiar

Si el merge fue exitoso:

```bash
git branch -d feature/semana-3-busqueda
```

---

# 44. Evidencias que debes entregar

Para esta actividad debes conservar evidencias de:

### 1. Rama inicial

```bash
git branch
```

Debe demostrar que existe:

```text
main
```

### 2. Rama de trabajo

Debe demostrar:

```text
feature/semana-3-busqueda
```

### 3. Historial

```bash
git log --oneline --graph --all
```

La salida debe permitir observar la evolución del proyecto.

### 4. Commits

Los mensajes deben ser descriptivos.

### 5. Merge

Debe existir evidencia de la integración de la rama.

### 6. Funcionamiento

Debe demostrarse que `main` funciona después del merge.

---

# 45. Evidencia especialmente importante: el gráfico de Git

Ejecuta:

```bash
git log --oneline --graph --all
```

Un resultado conceptual podría verse así:

```text
*   a82f31d Merge branch 'feature/semana-3-busqueda'
|\
| * 7bc421a feat: implementa busqueda binaria
| * 5da120f feat: implementa busqueda lineal
| * 31af890 feat: crea buscador de lecturas
|/
* 98fa210 Semana 2
* 72ca001 Semana 1
```

Este gráfico permite observar que el proyecto tuvo una línea base y una línea de desarrollo que posteriormente fue integrada.

---

# 46. Reto de comprensión

Sin ejecutar comandos, explica con tus propias palabras:

### Pregunta 1

¿Por qué `main` debe mantenerse estable mientras una nueva funcionalidad está en desarrollo?

### Pregunta 2

¿Cuál es la diferencia conceptual entre:

```text
feature/
```

y:

```text
bugfix/
```

### Pregunta 3

¿Por qué:

```text
feature/semana-3-busqueda
```

es un nombre más útil que:

```text
rama3
```

### Pregunta 4

¿Qué ventaja tiene desarrollar una funcionalidad en una rama antes de fusionarla con `main`?

### Pregunta 5

¿Por qué es importante probar `main` después de realizar un merge?

---

# 47. Reto práctico adicional

Crea una rama de prueba para simular una corrección:

```bash
git switch main
git switch -c bugfix/prueba-rama
```

Realiza un cambio pequeño y controlado.

Haz un commit:

```bash
git add .
git commit -m "fix: realiza correccion de prueba"
```

Luego:

```bash
git switch main
git merge bugfix/prueba-rama
```

Verifica:

```bash
git log --oneline --graph --all
```

Finalmente elimina la rama:

```bash
git branch -d bugfix/prueba-rama
```

Este ejercicio busca que comprendas el ciclo completo:

```text
crear
  ↓
modificar
  ↓
commit
  ↓
probar
  ↓
cambiar a main
  ↓
merge
  ↓
probar main
  ↓
eliminar rama
```

---

# 48. Buenas prácticas que aplicaremos durante todo el semestre

## Regla 1

**No trabajar directamente sobre `main`.**

## Regla 2

**Crear ramas desde una `main` funcional.**

## Regla 3

**Utilizar nombres descriptivos.**

Preferir:

```text
feature/semana-3-busqueda
```

sobre:

```text
rama1
```

## Regla 4

**Separar funcionalidades de correcciones.**

```text
feature/...
bugfix/...
```

## Regla 5

**Hacer commits pequeños y coherentes.**

## Regla 6

**Probar antes del merge.**

## Regla 7

**Probar después del merge.**

## Regla 8

**Mantener `main` funcional.**

## Regla 9

**No subir credenciales ni archivos sensibles.**

## Regla 10

**Eliminar ramas terminadas cuando ya fueron fusionadas.**

---

# 49. Modelo de trabajo que utilizaremos

Durante el semestre la evolución del proyecto será:

```text
                     feature/semana-3
                    /
Semana 2 ──────────●───────────────┐
       main                         │
                                   │ merge
                                   ▼
                              Semana 3
                                  main
                                   │
                                   ├──── feature/semana-4
                                   │
                                   └──── bugfix/...
```

La idea importante es que:

> **Las semanas no crean proyectos nuevos. Las semanas agregan capacidades a un único proyecto.**

Git nos ayuda a controlar esa evolución.

---

# 50. Conceptos que debes dominar

Al terminar esta guía debes poder explicar:

| Concepto | Qué debes comprender |
|---|---|
| Repositorio | Lugar donde Git administra el proyecto y su historial |
| Commit | Registro de un conjunto coherente de cambios |
| Branch / rama | Línea independiente de desarrollo |
| `main` | Línea base estable del proyecto |
| `feature` | Rama para desarrollar una nueva funcionalidad |
| `bugfix` | Rama para corregir un error |
| Merge | Integración de una rama en otra |
| Conflicto | Situación en la que Git necesita ayuda para combinar cambios |
| Línea base | Versión estable desde la cual continúa el desarrollo |
| Working tree | Estado actual de los archivos de trabajo |

---

# 51. Checklist final del estudiante

Antes de considerar terminada la actividad, verifica:

- [ ] Tengo una rama `main`.
- [ ] `main` corresponde a la línea base hasta Semana 2.
- [ ] `main` funciona correctamente.
- [ ] Comprendo qué es una rama.
- [ ] Comprendo por qué no debo trabajar directamente sobre `main`.
- [ ] Creé `feature/semana-3-busqueda`.
- [ ] El nombre de la rama describe su propósito.
- [ ] Construí el trabajo de Semana 3 dentro de la rama.
- [ ] Realicé commits descriptivos.
- [ ] Probé la rama antes del merge.
- [ ] Cambié nuevamente a `main`.
- [ ] Realicé el merge.
- [ ] Probé `main` después del merge.
- [ ] Comprendí qué es un conflicto.
- [ ] Comprendo cuándo utilizar `feature`.
- [ ] Comprendo cuándo utilizar `bugfix`.
- [ ] Comprendo que `main` debe mantenerse funcional y limpia.
- [ ] Eliminé la rama de trabajo después del merge.
- [ ] Puedo explicar el flujo completo sin copiar comandos.

---

# 52. Idea final

Git no se utiliza solamente para "guardar código".

En este proyecto lo utilizaremos para aprender una forma de trabajo cercana a la utilizada en equipos reales de desarrollo:

```text
Línea base
    ↓
Rama de trabajo
    ↓
Desarrollo
    ↓
Commits
    ↓
Pruebas
    ↓
Correcciones
    ↓
Merge
    ↓
Nueva línea base
```

La regla que debes recordar durante todo el proyecto es:

> **`main` debe representar siempre una versión estable, funcional y limpia del proyecto.**

Las ramas son el espacio donde desarrollamos cambios.

Cuando el cambio está listo y probado, lo integramos.

De esta manera, el proyecto puede crecer semana tras semana sin perder el control de su historia ni de su estabilidad.
