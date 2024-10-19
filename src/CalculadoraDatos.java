import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CalculadoraDatos {
    private static final Logger bitacora = Logger.getLogger(CalculadoraDatos.class.getName());
    private String nombreArchivo;
    private int numFrames;
    private int numReferencias;
    private float tiempo;
    private float tiempoRAM;
    private float   tiempoFallas;

    // Simula los marcos de página con un tamaño igual a numFrames
    private List<Integer> memoriaPaginas = new ArrayList<>();

    // Registra todas las referencias {página: [bitAccion, bitRef]}
    private Map<Integer, List<Object>> refAcciones = new HashMap<>(); 

    private static int hits = 0;
    private static int fallos = 0;
    private int[][] paresClases = {}; // Uso interno para el algoritmo NRU {página, clase}
    public static boolean primerThreadTerminado = false;

    public CalculadoraDatos(int numFrames, String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        this.numFrames = numFrames;
        nombreArchivo = nombreArchivo; //TODO: cambiar cuando tengamos direccion real

        // Inicializa los marcos de página en la lista con valores vacíos
        while (memoriaPaginas.size() < numFrames) {
            memoriaPaginas.add(-1);
        }

        // Configura el logger para registrar eventos
        try {
            FileHandler manejador = new FileHandler("bitacora.log");
            bitacora.addHandler(manejador);
            SimpleFormatter formato = new SimpleFormatter();
            manejador.setFormatter(formato);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Inicia el primer hilo (lectura del archivo y procesamiento de fallos)
        ProcesoLectura proceso = new ProcesoLectura(nombreArchivo, numFrames);
        proceso.start();

        // Inicia el segundo hilo (actualización de bits de referencia)
        ActualizadorBits actualizador = new ActualizadorBits();
        actualizador.start();
    }

    public int obtenerhits() {
        return hits;
    }

    public int obtenerfallos() {
        return fallos;
    }

    public int obtenernumReferencias() {
        return numReferencias;
    }

    // Primer hilo: Lee el archivo, verifica la página y actualiza el fallo o hit
    public class ProcesoLectura extends Thread {
        private String nombreArchivo;
        private int numFrames;
        private int paginaVirtual;
        private String bitAccion;

        public ProcesoLectura(String nombreArchivo, int numFrames) {
            this.nombreArchivo = nombreArchivo;
            this.numFrames = numFrames;
        }

        @Override
        public void run() {
            BufferedReader lector = null;
            try {
                lector = new BufferedReader(new FileReader(nombreArchivo));
                for (int i = 0; i < 4; i++) { // Ignora las primeras líneas
                    lector.readLine();
                }

                String line = lector.readLine(); // Lee el número de registros
                int registros = Integer.parseInt(line.split("=")[1].trim());
                numReferencias = registros;
                tiempoFallas = registros * 10;
                tiempoRAM = (float) (registros * 0.0000025);

                for(int i=0; i<registros; i++){
                    String linea = lector.readLine();
                    if(linea == null){
                        System.out.println("Finalizo archivo sin terminar registros.");
                        break;
                    }
                    
                    String[] each = linea.split(",");
                    
                    if (each.length <4){
                        System.out.println("el registro no contiene suficientes elementos: " + linea);
                        continue;
                    }
                    
                    paginaVirtual = Integer.parseInt(each[1]); //pagina
                    bitAccion = each[3]; //bit de acción
                    
                    if(!refAcciones.containsKey(paginaVirtual)){
                        bitacora.info("Fallo de página para la referencia: "+ paginaVirtual);
                        fallos +=1;
                        tiempo +=10;
                    
                    int referenciaReemplazar = ejecutarNRU(refAcciones, bitAccion, paginaVirtual);
                    bitacora.info("Reemplazando referencia en índice: " + referenciaReemplazar);
                    
                    memoriaPaginas.remove(Integer.valueOf(referenciaReemplazar));
                    
                    memoriaPaginas.add(paginaVirtual);
                    
                    registrarReferencia(paginaVirtual, bitAccion, 1);
                                        
                    } else {
                        bitacora.info("Página " + paginaVirtual + " en memoria (hit).");
                        hits++;
                        tiempo += 0.0000025;
                    }

                    primerThreadTerminado = true;

                    synchronized (CalculadoraDatos.this) {
                        CalculadoraDatos.this.notify();
                    }

                    try {
                        sleep(1); // El thread se pausa 1ms
                    } catch (InterruptedException e) {
                    }
                }

                bitacora.info("Número de registros procesados: " + registros);

            } catch (IOException | NumberFormatException e) {
                bitacora.severe("Error: " + e.getMessage());
            }

            bitacora.info("hits: " + hits);
            bitacora.info("Fallos: " + fallos);
            ActualizadorBits.detenerHilo();

            //imprimir
            System.out.println("Resultados Obtenidos: ");
            System.out.println("Total de referencias analizadas: " + Integer.toString(numReferencias));
            System.out.println("hits: " + hits);
            System.out.println("Fallos de pagina: " + fallos);
            System.out.println("Tiempo total que se tomo el sistema: " + tiempo + " ms");
            System.out.println("Tiempo total si todas las referencias estuvieran en RAM: " + tiempoRAM + " ms");
            System.out.println("Tiempo total si todas las referencias dieran una falla: " + tiempoFallas + " ms");
        }
    }

    public synchronized int ejecutarNRU(Map<Integer, List<Object>> refAcciones, String bitAccion, int paginaVirtual) {
        int paginaParaRemplazo;

        if (memoriaPaginas.contains(-1)) {
            bitacora.info("Memoria disponible");
            int indice = memoriaPaginas.indexOf(-1);
            int[] nuevoPar = {paginaVirtual, 1};
            paresClases = Arrays.copyOf(paresClases, paresClases.length + 1);
            paresClases[paresClases.length - 1] = nuevoPar;
            eliminarPar(indice);
            return indice;
        }

        if (!memoriaPaginas.contains(-1)) {
            for (int pagMemoria : memoriaPaginas) {
                for (Map.Entry<Integer, List<Object>> entrada : refAcciones.entrySet()) {
                    Integer pag = entrada.getKey();
                    List<Object> bits = entrada.getValue();

                    if (pagMemoria == pag) {
                        if ((bits.get(0) == "R") && (bits.get(1).equals(0))) {
                            int[] nuevoPar = {pag, 0};
                            paresClases = Arrays.copyOf(paresClases, paresClases.length + 1);
                            paresClases[paresClases.length - 1] = nuevoPar;
                        }
                        if ((bits.get(0) == "W") && (bits.get(1).equals(0))) {
                            int[] nuevoPar = {pag, 1};
                            paresClases = Arrays.copyOf(paresClases, paresClases.length + 1);
                            paresClases[paresClases.length - 1] = nuevoPar;
                        }
                        if ((bits.get(0) == "R") && (bits.get(1).equals(1))) {
                            int[] nuevoPar = {pag, 2};
                            paresClases = Arrays.copyOf(paresClases, paresClases.length + 1);
                            paresClases[paresClases.length - 1] = nuevoPar;
                        }
                        if ((bits.get(0) == "W") && (bits.get(1).equals(1))) {
                            int[] nuevoPar = {pag, 3};
                            paresClases = Arrays.copyOf(paresClases, paresClases.length + 1);
                            paresClases[paresClases.length - 1] = nuevoPar;
                        }
                    }
                }
            }
        }

        int menorClase = Integer.MAX_VALUE;
        int indiceClaseMenor = -1;

        for (int i = 0; i < paresClases.length; i++) {
            int claseActual = paresClases[i][1];
            if (claseActual < menorClase) {
                menorClase = claseActual;
                indiceClaseMenor = i;
            }
        }

        paginaParaRemplazo = paresClases[indiceClaseMenor][0];
        eliminarPar(indiceClaseMenor);
        return paginaParaRemplazo;
    }

    public synchronized void registrarReferencia(int pagina, Object bitAccion, Object bitReferencia) {
        if (refAcciones.containsKey(pagina)) {
            List<Object> valores = refAcciones.get(pagina);
            valores.add(bitAccion);
            valores.add(bitReferencia);
        } else {
            List<Object> valores = new ArrayList<>();
            valores.add(bitAccion);
            valores.add(bitReferencia);
            refAcciones.put(pagina, valores);
        }
    }

    public void eliminarPar(int valor) {
        int contador = 0;
        for (int[] par : paresClases) {
            if (par[0] != valor) {
                contador++;
            }
        }
        int[][] nuevaLista = new int[contador][2];
        int nuevoIndice = 0;
        for (int[] par : paresClases) {
            if (par[0] != valor) {
                nuevaLista[nuevoIndice] = par;
                nuevoIndice++;
            }
        }
        paresClases = nuevaLista;
    }

    public class ActualizadorBits extends Thread {
        private static boolean enEjecucion = true;

        @Override
        public void run() {
            while (enEjecucion) {
                synchronized (this) {
                    try {
                        while (!primerThreadTerminado && enEjecucion) {
                            CalculadoraDatos.this.wait();
                        }
                        actualizarReferencias();
                        try {
                            bitacora.info("Actualización de bits completada");
                            sleep(4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        primerThreadTerminado = false;
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public static void detenerHilo() {
            enEjecucion = false;
        }
    }

    public synchronized void actualizarReferencias() {
        for (Map.Entry<Integer, List<Object>> entrada : refAcciones.entrySet()) {
            List<Object> lista = entrada.getValue();
            lista.set(1, 0);
        }
    }
}

