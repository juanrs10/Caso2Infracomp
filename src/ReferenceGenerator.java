import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReferenceGenerator {

    private BMPReader bmpReader; // Clase que lee la imagen BMP
    private int pageSize; // Tamaño de la página en bytes

    public ReferenceGenerator(BMPReader bmpReader, int pageSize) {
        this.bmpReader = bmpReader;
        this.pageSize = pageSize;
    }

    // Método para generar las referencias de páginas y guardarlas en un archivo
    public void generateReferences(String outputFile) {
        try {
            // Inicializar contadores
            int referenceCount = 0;
            int pageCount = 0;

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
            int totalBytes = totalBytesForImage + messageLength; // Sumando los bytes de la matriz y el mensaje oculto

            // Calcular el número de páginas necesarias
            pageCount =(int) Math.ceil((double) totalBytes / pageSize); // Usar Math.ceil para redondear hacia arriba

            // Generar referencias a la imagen (almacenada por filas, row-major order)
            StringBuilder references = new StringBuilder();

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    for (int color = 0; color < 3; color++) { // 3 colores (RGB)
                        int reference = (i * width * 3) + (j * 3) + color;
                        int virtualPage = reference / pageSize;
                        int offset = reference % pageSize;

                        // Agregar referencia a la cadena
                        references.append(String.format("Imagen[%d][%d].%s,%d,%d,R%n", i, j, getColorName(color), virtualPage, offset));
                        referenceCount++;
                    }
                }
            }

            // Calcular la página del mensaje
            int messagePage = totalBytesForImage / pageSize; // Página donde comienza el mensaje

            // Generar referencias para el vector de mensajes
            for (int m = 0; m < messageLength; m++) {
                references.append(String.format("Mensaje[0],%d,%d,W%n", messagePage, m)); // Desplazamiento dentro de la página
                referenceCount++;
            }

            // Escribir el archivo al final, con las variables de configuración al principio
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)))) {
                // Escribir las configuraciones al inicio
                writer.println("P=" + pageSize); // Tamaño de página
                writer.println("NF=" + height); // Número de filas de la imagen
                writer.println("NC=" + width); // Número de columnas de la imagen
                writer.println("NR=" + referenceCount); // Número total de referencias
                writer.println("NP=" + pageCount); // Número total de páginas virtuales

                // Escribir las referencias
                writer.write(references.toString());
            }

            System.out.println("Archivo de referencias generado exitosamente.");

        } catch (IOException e) {
            e.printStackTrace();
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
