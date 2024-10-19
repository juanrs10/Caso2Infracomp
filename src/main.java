import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nMenú de Simulación de Paginación");
            System.out.println("1. Generación de referencias");
            System.out.println("2. Calcular datos buscados: fallas de página, porcentaje de hits, tiempos");
            System.out.println("3. Analizar resultados y generar informe");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            
            int option = scanner.nextInt();
            scanner.nextLine();  // Para consumir el salto de línea

            switch (option) {
                case 1:
                    // Opción 1: Generación de referencias
                    System.out.println("Ingrese el tamaño de página:");
                    int pageSize = scanner.nextInt();
                    scanner.nextLine();  // Consumir el salto de línea

                    System.out.println("Ingrese el nombre del archivo BMP:");
                    String bmpFile = scanner.nextLine();

                    // Crear un lector BMP para obtener la longitud del mensaje
                    BMPReader bmpReader = new BMPReader(bmpFile);

                    // Nombre del archivo de salida para las referencias
                    String outputFile = "referencias.txt";

                    // Crear el generador de referencias
                    ReferenceGenerator generator = new ReferenceGenerator(bmpReader, pageSize);

                    // Generar las referencias y guardarlas en el archivo de salida
                    generator.generateReferences(outputFile); // Llamar sin longitud, ya que se maneja internamente
                    break;

                case 2:
                    // Opción 2: Calcular fallas de página, porcentaje de hits, tiempos
                    System.out.println("Funcionalidad no implementada aún.");
                    break;

                case 3:
                    // Opción 3: Analizar resultados y generar informe
                    System.out.println("Funcionalidad no implementada aún.");
                    break;

                case 4:
                    // Opción 4: Salir
                    System.out.println("Saliendo del programa...");
                    exit = true;
                    break;

                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }

        scanner.close();
    }
}
