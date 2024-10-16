public class Page {
    private int pageNumber;     // Número de la página virtual
    private byte[] data;        // Datos almacenados en la página (imagen/mensaje)
    private boolean inRAM;      // Indica si la página está en la RAM
    private boolean referenceBit; // Bit de referencia R (para el algoritmo NUR)
    private long lastAccessTime;  // Marca de tiempo de acceso para implementar envejecimiento
    private byte agingCounter;    // Contador de envejecimiento (8 bits)
    
    // Constructor
    public Page(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.data = new byte[pageSize]; // Crear una página del tamaño especificado (en bytes)
        this.inRAM = false;            // Inicialmente, la página no está en la RAM
        this.referenceBit = false;     // Bit de referencia R inicial
        this.agingCounter = 0;         // Inicializar el contador de envejecimiento en 0
        this.lastAccessTime = System.currentTimeMillis(); // Inicializa el tiempo de acceso
    }

    // Métodos para manipular la página

    public void loadPage(byte[] newData) {
        System.arraycopy(newData, 0, data, 0, newData.length);
        inRAM = true; // Marca la página como cargada en RAM
    }

    public void unloadPage() {
        inRAM = false; // Marca la página como no presente en RAM
    }

    public void setReferenceBit(boolean referenceBit) {
        this.referenceBit = referenceBit;
    }

    public boolean isInRAM() {
        return inRAM;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public byte[] getData() {
        return data;
    }

    public boolean getReferenceBit() {
        return referenceBit;
    }

    public void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    // Método para actualizar el contador de envejecimiento
    public void updateAgingCounter() {
        // Desplazar a la derecha el contador de envejecimiento
        agingCounter = (byte) (agingCounter >> 1);

        // Si el bit R está en 1, añadirlo en el bit más significativo del contador
        if (referenceBit) {
            agingCounter = (byte) (agingCounter | 0x80); // Añadir 1 en el bit más significativo
        }

        // Reiniciar el bit R después de usarlo
        referenceBit = false;
    }

    public byte getAgingCounter() {
        return agingCounter;
    }
}
