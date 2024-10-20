import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ReferenceGenerator {

    private BMPReader bmpReader; // Clase que lee la imagen BMP
    private int pageSize; // Tamaño de la página en bytes
    private ArrayList<String> referencias = new ArrayList<>();
    private ArrayList<String> referenciasFinales = new ArrayList<>();
    private ArrayList<String> primerasReferencias = new ArrayList<>();

    public ReferenceGenerator(BMPReader bmpReader, int pageSize) {
        this.bmpReader = bmpReader;
        this.pageSize = pageSize;
    }

    // Método para generar las referencias de páginas y guardarlas en un archivo
    public void generateReferences(String outputFile) throws IOException {
        // Inicializar contadores
        int referenceCount = 0;

        // Obtener los datos de la imagen BMP
        byte[][][] pixels = bmpReader.getPixels();
        int width = bmpReader.getWidth();
        int height = bmpReader.getHeight();

        // Leer longitud del mensaje escondido
        int messageLength = bmpReader.leerLongitud(); // Obtener longitud del mensaje oculto

        // Calcular el número total de píxeles
        int numberOfPixels = width * height;

        // Calcular el total de bytes ocupados por la matriz de imagen
        int totalBytesForImage = numberOfPixels * 3; // 3 bytes por píxel (RGB)

        // Calcular el total de bytes ocupados (incluyendo el mensaje oculto)
        int totalBytes = totalBytesForImage + messageLength + 16; // Sumando los bytes de la matriz, el mensaje oculto y los 16 bits para la longitud

        // Calcular el número de páginas necesarias
        int pageCount = (int) Math.ceil((double) totalBytes / pageSize); // Usar Math.ceil para redondear hacia arriba

        // Generar las primeras referencias para la longitud del mensaje (primeros 16 bits)
        for (int i = 0; i < 16; i++) {
            int fila = i / (width * 3);
            int col = (i % (width * 3)) / 3;
            int color = (i % (width * 3)) % 3;
            int direccionByte = fila * width * 3 + col * 3 + color;
            int paginaVirtual = direccionByte / pageSize;
            int desplazamiento = direccionByte % pageSize;
            String componenteColor = (color == 0) ? "R" : (color == 1) ? "G" : "B";

            String referenciaImagen = "Imagen[" + fila + "][" + col + "]." + componenteColor + "," + paginaVirtual + "," + desplazamiento + ",R";
            referencias.add(referenciaImagen);
            referenceCount++;
        }

        // Generar referencias para el mensaje oculto y la imagen
        int totalBytesImagen = height * width * 3;

        for (int posCaracter = 0; posCaracter < messageLength; posCaracter++) {
            // Registrar la escritura de los caracteres del mensaje
            int direccionByteMensaje = totalBytesImagen + posCaracter;
            int paginaVirtualEscritura = direccionByteMensaje / pageSize;
            int desplazamientoEscritura = direccionByteMensaje % pageSize;
            String referenciaMensaje = "Mensaje[" + posCaracter + "]," + paginaVirtualEscritura + "," + desplazamientoEscritura + ",W";
            referencias.add(referenciaMensaje);
            referenceCount++;

            // Registrar las referencias de lectura de la imagen
            for (int i = 0; i < 8; i++) {
                int numeroByte = 16 + (posCaracter * 8) + i; // Saltando los primeros 16 bits (longitud)
                int fila = numeroByte / (width * 3);
                int col = (numeroByte % (width * 3)) / 3;
                int color = (numeroByte % (width * 3)) % 3;
                int direccionByteLectura = fila * width * 3 + col * 3 + color;
                int paginaVirtualLectura = direccionByteLectura / pageSize;
                int desplazamientoLectura = direccionByteLectura % pageSize;

                // Registrar la referencia de lectura de la imagen
                String componenteColor = (color == 0) ? "R" : (color == 1) ? "G" : "B";
                String referenciaImagen = "Imagen[" + fila + "][" + col + "]." + componenteColor + "," + paginaVirtualLectura + "," + desplazamientoLectura + ",R";
                referencias.add(referenciaImagen);
                referenceCount++;

                // Registrar nuevamente la escritura del mensaje
                referencias.add(referenciaMensaje); // Se repite la escritura del mensaje después de leer cada bit
                referenceCount++;
            }
        }

        // Escribir las configuraciones iniciales al principio del archivo
        primerasReferencias.add("P=" + pageSize);
        primerasReferencias.add("NF=" + height);
        primerasReferencias.add("NC=" + width);
        primerasReferencias.add("NR=" + referenceCount);
        primerasReferencias.add("NP=" + pageCount);
        //primerasReferencias.add("LENGHT=" + messageLength);


        // Guardar las referencias en el archivo
        guardarReferencias(outputFile);
    }

    // Método para guardar las referencias generadas en un archivo
    public void guardarReferencias(String rutaArchivo) {
        referenciasFinales.addAll(primerasReferencias);
        referenciasFinales.addAll(referencias);
        try (FileWriter fw = new FileWriter(rutaArchivo)) {
            for (String referencia : referenciasFinales) {
                fw.write(referencia + "\n");
            }
            System.out.println("Archivo de referencias generado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo de referencias: " + e.getMessage());
        }
    }

    // Método auxiliar para obtener el nombre del color según el índice
    private String getColorName(int colorIndex) {
        switch (colorIndex) {
            case 0: return "R"; // Rojo (Red)
            case 1: return "G"; // Verde (Green)
            case 2: return "B"; // Azul (Blue)
            default: return "Unknown";
        }
    }
}
