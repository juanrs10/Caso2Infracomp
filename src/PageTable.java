import java.util.ArrayList;

public class PageTable {
    private ArrayList<Page> pages; // Todas las páginas virtuales del proceso
    private int framesInRAM;       // Número de marcos disponibles en la RAM
    private int pageSize;

    public PageTable(int totalPages, int pageSize, int framesInRAM) {
        pages = new ArrayList<>();
        this.framesInRAM = framesInRAM;
        this.pageSize = pageSize;
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

    // Método para que el thread de actualización del bit R lo use
    public synchronized void updatePageTable() {
    }

    //CARGA MATRIZ DE PIXELES; LOOP SOBRE LISTA DE PÁGINAS
    public void loadPixelsIntoPages(byte[][][] pixels) {
        int byteCounter = 0;
        int width = pixels[0].length; // Ancho de la imagen
        int height = pixels.length;   // Alto de la imagen
    
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Extraer el píxel (3 bytes) en formato BGR
                byte[] pixel = {pixels[i][j][0], pixels[i][j][1], pixels[i][j][2]};
    
                // Calcular el índice de la página donde irá este píxel
                int pageIndex = byteCounter / pageSize;
    
                if (pageIndex < pages.size()) {
                    Page page = pages.get(pageIndex);
    
                    // Almacenar el píxel en la página (si queda espacio)
                    int offset = byteCounter % pageSize;
                    if (offset + 3 <= pageSize) {
                        System.arraycopy(pixel, 0, page.getData(), offset, 3);
                    }
                }
    
                // Incrementar el contador de bytes procesados
                byteCounter += 3; // 3 bytes por píxel
            }
        }
    }
}
