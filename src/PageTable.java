import java.util.ArrayList;

public class PageTable {
    private ArrayList<Page> pages; // Todas las páginas virtuales del proceso
    private int framesInRAM;       // Número de marcos disponibles en la RAM

    public PageTable(int totalPages, int pageSize, int framesInRAM) {
        pages = new ArrayList<>();
        this.framesInRAM = framesInRAM;
        // Inicializar todas las páginas
        for (int i = 0; i < totalPages; i++) {
            pages.add(new Page(i, pageSize));
        }
    }

    // Método para verificar si la página está en RAM
    public synchronized Page accessPage(int pageNumber) {
        Page page = pages.get(pageNumber);
        if (!page.isInRAM()) {
            // Falla de página: cargar la página a RAM
            handlePageFault(page);
        }
        page.updateLastAccessTime(); // Actualizar el tiempo de acceso
        return page;
    }

    // Manejar una falla de página
    private void handlePageFault(Page page) {
        System.out.println("Falla de página en la página: " + page.getPageNumber());
        if (countPagesInRAM() >= framesInRAM) {
            // Si la RAM está llena, se necesita reemplazar una página
            replacePage(page);
        }
        page.loadPage(new byte[page.getData().length]); // Simulación de cargar la página
    }

    // Contar cuántas páginas están en RAM
    private int countPagesInRAM() {
        int count = 0;
        for (Page p : pages) {
            if (p.isInRAM()) {
                count++;
            }
        }
        return count;
    }

    // Reemplazar una página en RAM usando el algoritmo NUR
    private void replacePage(Page newPage) {
        Page pageToReplace = findPageToReplace();
        pageToReplace.unloadPage();
        newPage.loadPage(new byte[newPage.getData().length]); // Simulación de carga
        System.out.println("Reemplazando página " + pageToReplace.getPageNumber() + " por " + newPage.getPageNumber());
    }

    // Encontrar una página para reemplazar usando el bit de referencia R
    private Page findPageToReplace() {
        // Implementar el algoritmo NUR basado en el bit R
        for (Page p : pages) {
            if (p.isInRAM() && !p.getReferenceBit()) {
                return p; // Encontrar la primera página con bit R en 0
            }
        }
        // Si todas tienen R en 1, escoger la más antigua (basado en lastAccessTime)
        Page oldestPage = null;
        for (Page p : pages) {
            if (p.isInRAM()) {
                if (oldestPage == null || p.getLastAccessTime() < oldestPage.getLastAccessTime()) {
                    oldestPage = p;
                }
            }
        }
        return oldestPage;
    }

    // Método para que el thread de actualización del bit R lo use
    public synchronized void updateBitR() {
        for (Page page : pages) {
            if (page.isInRAM()) {
                page.setReferenceBit(false); // Reiniciar el bit R después de la actualización
            }
        }
    }
}
