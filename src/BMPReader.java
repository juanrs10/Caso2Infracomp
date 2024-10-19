import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

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

    public int leerLongitud() {
        int longitud = 0;
        // Usamos 16 bits para almacenar la longitud del mensaje.
        // Esos 16 bits se esconden en los primeros 16 bytes de la imagen.
        for (int i = 0; i < 16; i++) {
            // Cada byte almacena un bit, así que leemos los primeros 16 bytes.
            int byteValue = (pixels[0][i / 3][i % 3] & 1);
            longitud |= (byteValue << i);
        }
        return longitud;
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
    
    public void hideMessage(char[] mensaje, int longitud) {
        int contador = 0;
        byte elByte;
        writeBits(contador, longitud, 16);
        contador = 2;

        for (int i = 0; i < longitud; i++) {
            elByte = (byte) mensaje[i];
            writeBits(contador, elByte, 8);
            contador++;
        }
    }
    private void writeBits(int contador, int valor, int numbits) {
        int bytesPorFila = width * 3;
        int mascara;
        for (int i = 0; i < numbits; i++) {
            int fila = (8 * contador + i) / bytesPorFila;
            int col = ((8 * contador + i) % bytesPorFila) / 3;
            int color = ((8 * contador + i) % bytesPorFila) % 3;
            mascara = valor >> i;
            mascara = mascara & 1;
            pixels[fila][col][color] = (byte)((pixels[fila][col][color] & 0xFE) | mascara);
        }
    }
    public void saveImage(String ruta) {
        try (FileOutputStream fos = new FileOutputStream(new File(ruta))) {
            fos.write(header);
            // Escribir los píxeles y el padding
            byte[] paddingBytes = new byte[padding];  // Array vacío para el padding
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    fos.write(pixels[i][j]);  // Escribir los 3 bytes del píxel (RGB)
                }
                fos.write(paddingBytes);  
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String verifyHiddenMessage() {
        int longitudMensaje = leerLongitud();
        char[] mensajeRecuperado = new char[longitudMensaje];
        restore(mensajeRecuperado, longitudMensaje);
        return new String(mensajeRecuperado);
    }
    public void restore(char[] cadena, int longitud) {
        int bytesFila = width * 3;
        for (int posCaracter = 0; posCaracter < longitud; posCaracter++) {
            cadena[posCaracter] = 0;
            for (int i = 0; i < 8; i++) {
                int numBytes = 16 + (posCaracter * 8) + i;
                int fila = numBytes / bytesFila;
                int col = numBytes % (bytesFila) / 3;
                cadena[posCaracter] = (char)(cadena[posCaracter] | 
                        (pixels[fila][col][((numBytes % bytesFila) % 3)] & 1) << i);
            }
        }
    }
}
