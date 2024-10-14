import java.util.concurrent.locks.ReentrantLock;

public class App {
    public static void main(String[] args) {
        // Leer la imagen BMP
        BMPReader bmpReader = new BMPReader("./res/caso2-parrots_mod.bmp");

        // Acceder a los píxeles de la imagen
        byte[][][] pixels = bmpReader.getPixels();
        int width = bmpReader.getWidth();
        int height = bmpReader.getHeight();

        // Definir el tamaño de la página (un múltiplo de 3 bytes)
        int pageSize = 12; // Por ejemplo, 12 bytes, lo que puede almacenar 4 píxeles
        int totalPages = (width * height * 3) / pageSize;

        // Crear la tabla de páginas con el número de páginas calculado
        PageTable pageTable = new PageTable(totalPages, pageSize, 4); // 4 marcos en RAM

        // Cargar los píxeles en las páginas
        pageTable.loadPixelsIntoPages(pixels);

        // Crear los threads
        PageTableUpdater updaterThread = new PageTableUpdater(pageTable, 1); // 1 ms
        BitRUpdater bitRThread = new BitRUpdater(pageTable, 2); // 2 ms

        // Iniciar los threads
        updaterThread.start();
        bitRThread.start();
    }
}

