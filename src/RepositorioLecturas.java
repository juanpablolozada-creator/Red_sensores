/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   TAD RepositorioLecturas - VERSION COMPLETA
   ============================================================ */

public class RepositorioLecturas {

    private static final int CAPACIDAD_INICIAL = 10;

    private LecturaSensor[] lecturas;
    private int cantidad;

    public RepositorioLecturas() {
        this.lecturas = new LecturaSensor[CAPACIDAD_INICIAL];
        this.cantidad = 0;
    }

    // ---------- OPERACIONES DEL CONTRATO ----------

    /**
     * Agrega una lectura al final del repositorio.
     * Si el arreglo alcanza su capacidad física, invoca internamente el redimensionamiento dinámico.
     *
     * @return true si se agregó, false si lectura es null
     */
    public boolean agregar(LecturaSensor lectura) {
        if (lectura == null) {
            return false;
        }
        if (cantidad == lecturas.length) {
            redimensionar();
        }
        lecturas[cantidad] = lectura;
        cantidad++;
        return true;
    }

    /**
     * Devuelve la lectura que esta en la posicion indicada.
     */
    public LecturaSensor obtener(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return null;
        }
        return lecturas[posicion];
    }

    /**
     * Cantidad de lecturas almacenadas actualmente.
     */
    public int tamano() {
        return cantidad;
    }

    /**
     * Elimina la lectura de la posicion indicada desplazando los elementos posteriores.
     */
    public void eliminar(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return;
        }

        for (int i = posicion; i < cantidad - 1; i++) {
            lecturas[i] = lecturas[i + 1];
        }

        lecturas[cantidad - 1] = null;
        cantidad--;
    }

    /**
     * Busca la primera lectura de una estacion.
     */
    public LecturaSensor buscarPorEstacion(String idSensor) {
        if (idSensor == null) {
            return null;
        }
        for (int i = 0; i < cantidad; i++) {
            if (lecturas[i].getIdSensor().equals(idSensor)) {
                return lecturas[i];
            }
        }

        return null;
    }

    /**
     * Reemplaza la lectura de una posicion por otra.
     */
    public void actualizar(int posicion, LecturaSensor nueva) {
        if (posicion < 0 || posicion >= cantidad || nueva == null) {
            return;
        }

        lecturas[posicion] = nueva;
    }

    /**
     * Duplica la capacidad interna del arreglo conservando el contenido existente.
     */
    private void redimensionar() {
        LecturaSensor[] nuevo = new LecturaSensor[lecturas.length * 2];
        for (int i = 0; i < cantidad; i++) {
            nuevo[i] = lecturas[i];
        }
        lecturas = nuevo;
    }

    /**
     * Promedio de PM2.5 de todas las lecturas almacenadas.
     */
    public double promedioPm25() {
        if (cantidad == 0) {
            return 0;
        }

        double suma = 0;

        for (int i = 0; i < cantidad; i++) {
            suma = suma + lecturas[i].getPm25();
        }

        return suma / cantidad;
    }
}
