/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   IngestaSensores - VERSION 0.3 (Semana 3)

   Novedades frente a la Semana 2:
   - Integración con BancoDePruebas para análisis de algoritmos de búsqueda.
   - Conserva único punto de entrada (main) para todo el proyecto.
   ============================================================ */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IngestaSensores {

    private static final String ARCHIVO = "lecturas_ampliadas.csv";
    private static final int CAMPOS_ESPERADOS = 5;

    private static int descartadasPorFormato = 0;
    private static int descartadasPorRango = 0;

    public static void main(String[] args) throws IOException {

        RepositorioLecturas repositorio = new RepositorioLecturas();
        AnalizadorMatriz analizador = new AnalizadorMatriz();

        cargarArchivo(repositorio, analizador);

        System.out.println();
        System.out.println("=== INGESTA ===");
        System.out.println("Lecturas almacenadas:      " + repositorio.tamano());
        System.out.println("Descartadas por formato:   " + descartadasPorFormato);
        System.out.println("Descartadas por rango:     " + descartadasPorRango);
        System.out.printf("PM2.5 promedio:            %.2f%n", repositorio.promedioPm25());
        System.out.println();
        System.out.println("=== PERFIL HORARIO DE LA CIUDAD ===");
        for (int h = 0; h < 24; h++) {
            System.out.printf("Hora %02d -> PM2.5 promedio: %.2f%n", h, analizador.promedioDeHora(h));
        }

        System.out.println();
        System.out.println("=== SEMANA 3 ===");
        BancoDePruebas.experimentoUno();
        BancoDePruebas.experimentoDos();
        BancoDePruebas.experimentoTres();
        BancoDePruebas.experimentoCuatro();
    }

    /**
     * Resuelve la ruta del archivo de datos según desde dónde se ejecute el programa
     * (raíz del proyecto, carpeta src o carpeta data).
     */
    private static String resolverRuta(String nombreArchivo) {
        if (new File(nombreArchivo).exists()) {
            return nombreArchivo;
        }
        if (new File("data/" + nombreArchivo).exists()) {
            return "data/" + nombreArchivo;
        }
        if (new File("../data/" + nombreArchivo).exists()) {
            return "../data/" + nombreArchivo;
        }
        return nombreArchivo;
    }

    /**
     * Lee el archivo linea por linea y alimenta el repositorio y la matriz.
     */
    private static void cargarArchivo(RepositorioLecturas repositorio,
                                      AnalizadorMatriz analizador) throws IOException {
        String ruta = resolverRuta(ARCHIVO);
        BufferedReader lector = new BufferedReader(new FileReader(ruta));
        lector.readLine(); // encabezado

        String linea;
        while ((linea = lector.readLine()) != null) {
            LecturaSensor lectura = construirLectura(linea);
            if (lectura == null) {
                continue;
            }
            if (!lectura.esValida()) {
                descartadasPorRango++;
                continue;
            }
            repositorio.agregar(lectura);
            analizador.registrar(lectura);
        }
        lector.close();
    }

    /**
     * Convierte una linea del CSV en un objeto LecturaSensor.
     * @return la lectura, o null si la linea esta mal formada
     */
    private static LecturaSensor construirLectura(String linea) {
        String[] campos = linea.split(",");
        if (campos.length != CAMPOS_ESPERADOS) {
            descartadasPorFormato++;
            return null;
        }
        try {
            double temperatura = Double.parseDouble(campos[2]);
            double humedad = Double.parseDouble(campos[3]);
            double pm25 = Double.parseDouble(campos[4]);
            return new LecturaSensor(campos[0], campos[1], temperatura, humedad, pm25);
        } catch (NumberFormatException e) {
            descartadasPorFormato++;
            return null;
        }
    }
}
