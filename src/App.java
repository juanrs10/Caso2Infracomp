import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class App {

    public static void main(String[] args) {    
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            // Mostrar el menú
            System.out.println("\n--- MENÚ ---");
            System.out.println("1. Generar archivo de referencias");
            System.out.println("2. Calcular datos (fallas de página, hits, tiempos)");
            System.out.println("3. Salir");
            System.out.print("Elija una opción: ");
            
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    // Opción 1: Generación de referencias
                    System.out.print("Ingrese el tamaño de la página: ");
                    int pageSize = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea

                    System.out.print("Ingrese el nombre del archivo de la imagen BMP: ");
                    String imageName = scanner.nextLine();

                    // Generar archivo de referencias (Faltaría implementar la lógica completa)
                    generarArchivoDeReferencias(pageSize, imageName);
                    break;

                case 2:
                    // Opción 2: Calcular datos buscados (fallas de página, hits, tiempos)
                    System.out.print("Ingrese el número de marcos de página: ");
                    int numFrames = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea

                    System.out.print("Ingrese el nombre del archivo de referencias: ");
                    String refFileName = scanner.nextLine();

                    // Simular el proceso y calcular los datos resultantes (Faltaría implementar la lógica completa)
                    calcularDatos(numFrames, refFileName);
                    break;

                case 3:
                    // Salir del programa
                    System.out.println("Saliendo del programa...");
                    salir = true;
                    break;

                default:
                    System.out.println("Opción no válida, por favor elija una opción del menú.");
            }
        }
        scanner.close();
    }

    // Método para generar el archivo de referencias
    public static void generarArchivoDeReferencias(int pageSize, String imageName) {
        // Leer la imagen BMP
        BMPReader bmpReader = new BMPReader(imageName);
        byte[][][] pixels = bmpReader.getPixels();
        int width = bmpReader.getWidth();
        int height = bmpReader.getHeight();

        // Calcular los valores de referencia (falta la lógica de distribución en páginas)
        int totalBytes = width * height * 3; // Cada píxel son 3 bytes
        int totalPages = (int) Math.ceil((double) totalBytes / pageSize); // Calcular cuántas páginas se necesitan

        // Guardar los datos en un archivo de referencias
        try (FileWriter writer = new FileWriter("referencias.txt")) {
            writer.write("TP: " + pageSize + "\n");
            writer.write("NF: " + height + "\n");
            writer.write("NC: " + width + "\n");
            writer.write("NR: " + totalBytes + "\n"); // Este sería el total de referencias de la imagen
            writer.write("NP: " + totalPages + "\n");
            
            // Generar las referencias (Faltaría implementar la lógica de las referencias reales)
            // Se deberían generar las referencias a las páginas de la matriz y el vector del mensaje.
            
            System.out.println("Archivo de referencias generado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al generar el archivo de referencias: " + e.getMessage());
        }
    }

    // Método para calcular los datos buscados (falla de página, hits, tiempos)
    public static void calcularDatos(int numFrames, String refFileName) {
        // Simular el comportamiento de paginación (Faltaría implementar la lógica)
        // Leer las referencias del archivo y simular el acceso a páginas, contando hits y fallas
        System.out.println("Simulando el proceso con " + numFrames + " marcos de página y archivo de referencias: " + refFileName);
        
        // (1) Simular el comportamiento del proceso cargando las referencias una por una.
        // (2) Simular el comportamiento del sistema de paginación, incluyendo fallas de página y reemplazo.
        // (3) Llevar el conteo de hits y fallas de página.
        
        // Resultados simulados
        System.out.println("Resultados simulados: ");
        System.out.println("Número de fallas de página: X");
        System.out.println("Porcentaje de hits: Y%");
        System.out.println("Tiempos de acceso simulados: Z ms");
    }
}
