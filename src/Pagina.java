public class Pagina {
    private int fila;          // Fila de la matriz
    private int columna;       // Columna de la matriz
    private int tamanioBytes;  // Tamaño de la página en bytes

    // Constructor
    public Pagina(int fila, int columna, int tamanioBytes) {
        this.fila = fila;
        this.columna = columna;
        this.tamanioBytes = tamanioBytes;
    }

    // Métodos getter
    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public int getTamanioBytes() {
        return tamanioBytes;
    }

    // Método para generar una referencia de la página
    public String generarReferencia() {
        return "Página en (" + fila + ", " + columna + ") - Tamaño: " + tamanioBytes + " bytes";
    }
}
