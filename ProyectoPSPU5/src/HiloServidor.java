import java.net.Socket;

public class HiloServidor extends Thread{

    Socket sock = new Socket();

    public HiloServidor(Socket sock){//Posibilidad de más parametros, como una referencia a la clase que accede a la info
        this.sock = sock;
    }

    @Override
    public void run(){

    }

}
