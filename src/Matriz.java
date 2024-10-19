public class Matriz {
    private int filas;
    private int columnas;
    private byte[][][] datos; // Almacenamiento en formato RGB

    public Matriz(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        datos = new byte[filas][columnas][3]; // 3 bytes por píxel (RGB)
    }

    // Obtener el número de filas
    public int getFilas() {
        return filas;
    }

    // Obtener el número de columnas
    public int getColumnas() {
        return columnas;
    }

    // Obtener un píxel
    public byte[] getPixel(int fila, int columna) {
        return datos[fila][columna];
    }

    // Establecer un píxel
    public void setPixel(int fila, int columna, byte[] pixel) {
        datos[fila][columna] = pixel;
    }
}
