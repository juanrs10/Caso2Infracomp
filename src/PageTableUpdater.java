import java.util.concurrent.locks.ReentrantLock;

public class PageTableUpdater extends Thread {
    private PageTable pageTable;
    private long intervalMillis; // Intervalo en milisegundos

    public PageTableUpdater(PageTable pageTable, long intervalMillis) {
        this.pageTable = pageTable;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        while (true) {
            // Simulación de acceso a una página (podría ser aleatorio o determinado)
            int pageToAccess = (int) (Math.random() * pageTable.getTotalPages());
            pageTable.accessPage(pageToAccess); // Acceder a la página, puede haber un hit o una falla de página

            // Esperar el intervalo antes de volver a ejecutar
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
