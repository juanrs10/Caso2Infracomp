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

    // Método para que el thread de actualización del bit R lo use
    public synchronized void updateBitR() {
        for (Page page : pages) {
            if (page.isInRAM()) {
                // Actualizar el contador de envejecimiento
                page.updateAgingCounter();
            }
        }
    }
    // Método para que el thread de actualización del bit R lo use
    public synchronized void updatePageTable() {
    }

    // Manejar una falla de página
    private void handlePageFault(Page page) {
        System.out.println("Falla de página en la página: " + page.getPageNumber());

        // Verificar si la RAM está llena (ya se han asignado todos los marcos)
        if (countPagesInRAM() >= framesInRAM) {
            // Si la RAM está llena, se necesita reemplazar una página
            replacePage(page);
        } else {
            // Si hay espacio en RAM, cargar la nueva página sin reemplazar
            page.loadPage(new byte[page.getData().length]); // Simulación de cargar la página
        }
    }

    // Reemplazar una página en RAM usando el algoritmo de envejecimiento (NUR)
    private void replacePage(Page newPage) {
        // Encontrar la página con el contador de envejecimiento más bajo
        Page pageToReplace = findPageToReplace();

        // Descargar la página seleccionada para reemplazo
        pageToReplace.unloadPage();

        // Cargar la nueva página en su lugar
        newPage.loadPage(new byte[newPage.getData().length]); // Simulación de carga de la nueva página
        System.out.println("Reemplazando página " + pageToReplace.getPageNumber() + " por " + newPage.getPageNumber());
    }

    // Encontrar la página con el contador de envejecimiento más bajo
    private Page findPageToReplace() {
        Page oldestPage = null;

        // Buscar la página con el menor contador de envejecimiento
        for (Page page : pages) {
            if (page.isInRAM()) {
                if (oldestPage == null || page.getAgingCounter() < oldestPage.getAgingCounter()) {
                    oldestPage = page;
                }
            }
        }

        return oldestPage; // Devolver la página candidata para ser reemplazada
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

    public int getTotalPages(){
        return pages.size();
    }
}
