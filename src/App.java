import java.util.concurrent.locks.ReentrantLock;

public class App {
    public static void main(String[] args) {
        PageTable pageTable = new PageTable(4,4,4); //RAND VALUES
        ReentrantLock lock = new ReentrantLock();

        // Crear los threads
        PageTableUpdater updaterThread = new PageTableUpdater(pageTable, lock, 1); // 1 ms
        BitRUpdater bitRThread = new BitRUpdater(pageTable, lock, 2); // 2 ms

        // Iniciar los threads
        updaterThread.start();
        bitRThread.start();
    }

}
