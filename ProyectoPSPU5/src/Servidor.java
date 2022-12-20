import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    final static int puerto = 6790;

    public static void main(String[] args) {

        /**
         * Gestionar conexiones y lanzar hilos, que
         * sean los hilos los que se encarguen de la carga pesada
         * Porque aqui poco se va a hacer
         */

        ServerSocket s;
        Socket c;

        try {
            s = new ServerSocket(puerto);
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error al crear el servidor");
            throw new RuntimeException(e);
        }

        while(true){
            try {
                c = s.accept();
            } catch (IOException e) {
                System.out.println("Ha ocurrido un error al crear una conexion con un cliente");
                throw new RuntimeException(e);
            }
            HiloServidor hilo = new HiloServidor(c);
            System.out.println("Se ha conectado un nuevo usuario");
            hilo.start();
        }


    }
}