import java.io.FileInputStream;
import java.io.IOException;

public class BMPReader {
    private byte[] header = new byte[54]; // Cabecera BMP
    private byte[][][] pixels; // Matriz de píxeles
    private int width, height;
    private int padding;

    public BMPReader(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);

            // Leer los primeros 54 bytes de la cabecera
            fis.read(header);

            // Extraer ancho y alto de la imagen desde la cabecera
            width = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) |
                    ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
            height = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) |
                    ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);

            System.out.println("Ancho: " + width + " px, Alto: " + height + " px");

            // La cantidad de bytes por fila (sin padding)
            int rowSizeWithoutPadding = width * 3; // 3 bytes por píxel (RGB)

            // Calcular el padding (las filas deben ser múltiplos de 4 bytes)
            padding = (4 - (rowSizeWithoutPadding % 4)) % 4;

            // Inicializar la matriz para almacenar los píxeles [alto][ancho][3 colores]
            pixels = new byte[height][width][3];

            byte[] pixel = new byte[3]; // Para leer cada píxel
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    // Leer los 3 bytes del píxel (BGR)
                    fis.read(pixel);
                    pixels[i][j][0] = pixel[0]; // Azul
                    pixels[i][j][1] = pixel[1]; // Verde
                    pixels[i][j][2] = pixel[2]; // Rojo
                }
                // Saltar el padding al final de cada fila
                fis.skip(padding);
            }

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[][][] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void writeMessage(String message) {
        byte[] messageBytes = message.getBytes(); // Convertir el mensaje en bytes
        int messageBitIndex = 0; // Índice del bit dentro del mensaje
        int totalMessageBits = messageBytes.length * 8; // Total de bits en el mensaje
    
        outerLoop:
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int color = 0; color < 3; color++) { // Recorrer BGR (3 bytes por píxel)
                    if (messageBitIndex < totalMessageBits) {
                        // Obtener el bit actual del mensaje (usamos operaciones de bit)
                        int byteIndex = messageBitIndex / 8; // Índice del byte en el mensaje
                        int bitIndex = messageBitIndex % 8; // Índice del bit dentro del byte
                        int bit = (messageBytes[byteIndex] >> (7 - bitIndex)) & 1; // Extraer el bit más significativo
    
                        // Limpiar el LSB del byte actual y asignar el bit del mensaje
                        pixels[i][j][color] = (byte) ((pixels[i][j][color] & 0xFE) | bit); // Reemplazar el LSB con el bit del mensaje
    
                        messageBitIndex++; // Avanzar al siguiente bit del mensaje
                    } else {
                        break outerLoop; // Salir cuando se han escrito todos los bits del mensaje
                    }
                }
            }
        }
        
        System.out.println("Mensaje oculto escrito correctamente en la imagen.");
    }
    
    public String readMessage(int messageLength) {
        byte[] messageBytes = new byte[messageLength]; // Almacenar los bytes del mensaje
        int messageBitIndex = 0; // Índice del bit dentro del mensaje
        int totalMessageBits = messageLength * 8; // Total de bits a leer
    
        outerLoop:
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int color = 0; color < 3; color++) { // Recorrer BGR (3 bytes por píxel)
                    if (messageBitIndex < totalMessageBits) {
                        // Obtener el LSB del byte actual del píxel
                        int bit = pixels[i][j][color] & 1; // Extraer el LSB
    
                        // Insertar el bit en el byte correspondiente del mensaje
                        int byteIndex = messageBitIndex / 8; // Índice del byte en el mensaje
                        int bitIndex = messageBitIndex % 8; // Índice del bit dentro del byte
                        messageBytes[byteIndex] = (byte) ((messageBytes[byteIndex] << 1) | bit); // Insertar el bit
    
                        messageBitIndex++; // Avanzar al siguiente bit del mensaje
                    } else {
                        break outerLoop; // Salir cuando se han leído todos los bits del mensaje
                    }
                }
            }
        }
        
        return new String(messageBytes); // Convertir los bytes leídos a cadena
    }
    
    
}
