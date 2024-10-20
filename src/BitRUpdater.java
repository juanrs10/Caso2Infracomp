import java.util.concurrent.locks.ReentrantLock;

public class BitRUpdater extends Thread {
    private PageTable pageTable;
    private long intervalMillis; // Intervalo en milisegundos

    public BitRUpdater(PageTable pageTable, long intervalMillis) {
        this.pageTable = pageTable;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        while (true) {
            // Ejecutar el algoritmo de actualización del bit R
            pageTable.updateBitR();
            // Esperar el intervalo antes de volver a ejecutar
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
