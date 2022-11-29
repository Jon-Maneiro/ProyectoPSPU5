import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloServidor extends Thread{

    Socket sock = new Socket();

    public HiloServidor(Socket sock){//Posibilidad de más parametros, como una referencia a la clase que accede a la info
        this.sock = sock;
    }

    @Override
    public void run(){

        KeyPair par = generarClaves();

        PrivateKey privada = par.getPrivate();
        PublicKey publica = par.getPublic();

        ObjectOutputStream oos;
        ObjectInputStream ois;

        try {
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(sock.getInputStream());
        } catch (IOException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE,null,e);
            throw new RuntimeException(e);
        }

        try {
            oos.writeObject(publica);
        } catch (IOException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE,null,e);
            throw new RuntimeException(e);
        }




    }

    public KeyPair generarClaves(){
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("El algoritmo de creacion no existe");
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE,null,e);
            throw new RuntimeException(e);
        }
        KeyPair par = keygen.generateKeyPair();
        return par;
    }
    public static String descifrarMensaje(String algoritmo , String mensaje, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cp = Cipher.getInstance(algoritmo);
        cp.init(Cipher.DECRYPT_MODE,key);
        String msgDes = new String(cp.doFinal(mensaje.getBytes()));

        return msgDes;
    }
    public static byte[] cifrarMensaje(String algoritmo , String mensaje, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cp = Cipher.getInstance(algoritmo);
        cp.init(Cipher.ENCRYPT_MODE,key);
        byte[] msgCF = cp.doFinal(mensaje.getBytes());

        return msgCF;
    }
}
