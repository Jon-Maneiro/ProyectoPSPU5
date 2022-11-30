import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloServidor extends Thread {

    Socket sock = new Socket();

    public HiloServidor(Socket sock) {//Posibilidad de más parametros, como una referencia a la clase que accede a la info
        this.sock = sock;
    }

    @Override
    public void run() {


        ObjectOutputStream oos;
        ObjectInputStream ois;
        PublicKey pkCliente;
        PublicKey pkServidor;
        PrivateKey pvkServidor;
        SecretKey sk;
        try {
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());

            pkCliente = (PublicKey) ois.readObject();//Leemos la clave publica del cliente

            KeyPair kp = generarClaves();

            pkServidor = kp.getPublic();
            pvkServidor = kp.getPrivate();

            oos.writeObject(pkServidor);
            /*
            Empieza la logica de Iniciar Sesion
             */
            //-------------------------------------
            /*
            Se termina el Inicio de Sesion
             */

            /*
            Empieza la logica del operaciones
             */
            String textoCliente ="";
            while(!textoCliente.equalsIgnoreCase("Salir")){
                oos.writeObject(textoMenuPrincipal());
            }
            /*
            Termina logica de operaciones
             */

        } catch (IOException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        }

    }

    public KeyPair generarClaves() {
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("El algoritmo de creacion no existe");
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        }
        KeyPair par = keygen.generateKeyPair();
        return par;
    }

    public SecretKey generarClave() {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        }
        SecretKey sk = keygen.generateKey();
        return sk;
    }

    public static String descifrarMensaje(String algoritmo, String mensaje, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cp = Cipher.getInstance(algoritmo);
        cp.init(Cipher.DECRYPT_MODE, key);
        String msgDes = new String(cp.doFinal(mensaje.getBytes()));

        return msgDes;
    }

    public static byte[] cifrarMensaje(String algoritmo, String mensaje, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cp = Cipher.getInstance(algoritmo);
        cp.init(Cipher.ENCRYPT_MODE, key);
        byte[] msgCF = cp.doFinal(mensaje.getBytes());

        return msgCF;
    }

    public static String textoMenuPrincipal(){
        return "Bienvenido, que deseas hacer?:\n" +
                "(1) - Ver Saldo\n" +
                "(2) - Hacer Transferencia";
    }
    private static String obtenerStringCompleto(String texto, int longitud) {
        String modif = texto;
        if (modif.length() < longitud) {
            while (modif.length() < longitud) {
                modif = modif + " ";
            }
        } else if (modif.length() > longitud) {
            modif = modif.substring(0, (longitud - 1));
        }

        return modif;
    }
    public static boolean isInt(String check){
        try{
            Integer.parseInt(check);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

}
