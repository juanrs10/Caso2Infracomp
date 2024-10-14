import java.util.concurrent.locks.ReentrantLock;

public class PageTableUpdater extends Thread {
    private PageTable pageTable;
    private long intervalMillis; // Intervalo en milisegundos

    public PageTableUpdater(PageTable pageTable, ReentrantLock lock, long intervalMillis) {
        this.pageTable = pageTable;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        while (true) {
            // Actualizar la tabla de páginas y los marcos de página
            pageTable.updatePageTable(); //Definir método
            // Aquí también podrías realizar otras acciones relacionadas con los marcos en RAM

            // Esperar el intervalo antes de volver a ejecutar
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
}
