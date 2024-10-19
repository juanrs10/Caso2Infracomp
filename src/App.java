import java.util.Scanner;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;


public class App {

    public static void main(String[] args) {    
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            // Mostrar el menú
            System.out.println("\n--- MENÚ ---");
            System.out.println("0. Esconder mensaje en el archivo BMP.");
            System.out.println("1. Generar archivo de referencias.");
            System.out.println("2. Calcular datos (fallas de página, hits, tiempos).");
            System.out.println("3. Recuperar mensaje oculto.");
            System.out.println("4. Salir");
            System.out.print("Elija una opción: ");
            
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 0:
                    opcion0(scanner);
                    break;
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
                    opcionVerificarMensajeOculto(scanner);
                    break;
                case 4:
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
    private static void opcion0 (Scanner scanner){
        // Solicitar archivo BMP
        System.out.print("Ingrese la ruta del archivo .bmp donde quiere ocultar el mensaje: ");
        String rutaBmp = scanner.nextLine();
        File archivoBmp = new File(rutaBmp);

        // Verificar si el archivo BMP existe
        if (archivoBmp.exists() && rutaBmp.endsWith(".bmp")) {
            System.out.println("El archivo .bmp fue encontrado.");
        } else {
            System.out.println("El archivo .bmp no existe o no tiene la extensión correcta.");
        }

        // Solicitar archivo TXT
        System.out.print("Ingrese la ruta del archivo .txt que contiene el mensaje para ocultar: ");
        String rutaTxt = scanner.nextLine();
        File archivoTxt = new File(rutaTxt);

        // Verificar si el archivo TXT existe
        if (archivoTxt.exists() && rutaTxt.endsWith(".txt")) {
            System.out.println("El archivo .txt fue encontrado.");
        } else {
            System.out.println("El archivo .txt no existe o no tiene la extensión correcta.");
        }

        BMPReader imagen= new BMPReader(rutaBmp);
        char[] contenido = leerArchivoComoCharArray(rutaTxt);

        imagen.hideMessage(contenido,contenido.length);

        // Solicitar nueva ruta BMP
        System.out.print("Ingrese el nombre del archivo .bmp donde quiere almacenar la imagen con el mensaje oculto: ");
        String nuevaBMP = scanner.nextLine();

        imagen.saveImage(nuevaBMP);

    }
    public static char[] leerArchivoComoCharArray(String ruta) {
        File archivo = new File(ruta);

        if (!archivo.exists() || !archivo.isFile()) {
            System.out.println("El archivo no existe o no es un archivo válido.");
            return null;
        }

        try (FileReader lector = new FileReader(archivo)) {
            // Crear un buffer para leer el contenido del archivo
            char[] buffer = new char[(int) archivo.length()];  // El tamaño del array es igual al tamaño del archivo
            lector.read(buffer);  // Leer todo el contenido en el array
            return buffer;
        } catch (IOException e) {
            System.out.println("Ocurrió un error al leer el archivo: " + e.getMessage());
            return null;
        }
    }

    private static void opcionVerificarMensajeOculto(Scanner scanner) {
        try {
            System.out.print("Ingrese el nombre del archivo BMP con el mensaje escondido: ");
            String archivoImagen = scanner.nextLine();
            BMPReader imagen = new BMPReader(archivoImagen);

            System.out.println("Verificando mensaje oculto...");
            String mensajeOculto = imagen.verifyHiddenMessage();
            System.out.println("Mensaje oculto verificado: " + mensajeOculto);
        } catch (Exception e) {
            System.out.println("Error al verificar el mensaje oculto: " + e.getMessage());
        }
    }

}
