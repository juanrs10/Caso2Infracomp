public class Vector {
    private int longitud;
    private byte[] datos;

    public Vector(int longitud) {
        this.longitud = longitud;
        datos = new byte[longitud];
    }

    // Obtener la longitud del vector
    public int getLongitud() {
        return longitud;
    }

    // Obtener un valor del vector
    public byte get(int index) {
        return datos[index];
    }

    // Establecer un valor en el vector
    public void set(int index, byte valor) {
        datos[index] = valor;
    }
}
