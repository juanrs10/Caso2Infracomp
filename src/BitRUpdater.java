import java.util.concurrent.locks.ReentrantLock;

public class BitRUpdater extends Thread {
    private PageTable pageTable;
    private ReentrantLock lock;
    private long intervalMillis; // Intervalo en milisegundos

    public BitRUpdater(PageTable pageTable, ReentrantLock lock, long intervalMillis) {
        this.pageTable = pageTable;
        this.lock = lock;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        while (true) {
            try {
                lock.lock();
                // Ejecutar el algoritmo de actualización del bit R
                pageTable.updateBitR();
            } finally {
                lock.unlock();
            }
            // Esperar el intervalo antes de volver a ejecutar
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
