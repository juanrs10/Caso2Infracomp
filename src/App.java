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

                    System.out.print("Ingrese la ruta del archivo de la imagen BMP: ");
                    String imageName = scanner.nextLine();

                    // Crear un lector BMP para obtener la longitud del mensaje
                    BMPReader bmpReader = new BMPReader(imageName);

                    // Nombre del archivo de salida para las referencias
                    String outputFile = "referencias.txt";

                    // Crear el generador de referencias
                    ReferenceGenerator generator = new ReferenceGenerator(bmpReader, pageSize);

                    // Generar las referencias y guardarlas en el archivo de salida
                    generator.generateReferences(outputFile); 
                    break;

                case 2:
                    // Opción 2: Calcular datos buscados (fallas de página, hits, tiempos)
                    System.out.print("Ingrese el número de marcos de página: ");
                    int numFrames = scanner.nextInt();
                    scanner.nextLine(); // Consumir el salto de línea

                    System.out.print("Ingrese el nombre del archivo de referencias: ");
                    String refFileName = scanner.nextLine();

                    // Simular el proceso y calcular los datos resultantes (Faltaría implementar la lógica completa)
                    new CalculadoraDatos(numFrames, refFileName);
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

}
