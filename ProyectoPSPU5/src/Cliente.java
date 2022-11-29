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

public class Cliente {

    /**
     * Conectarse al banco, introducir datos
     * clave publica, leer cosas cifradas
     * verificar identidad
     */
    final static int puerto = 6790;

    public static void main(String[] args) {
        //Conectar con el servidor
        Socket socket;
        try {
            socket = new Socket("localhost", puerto);


            ObjectOutputStream oos;
            ObjectInputStream ois;

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            PublicKey pks;

            pks = (PublicKey) ois.readObject();


            System.out.println(pks);
            //Habria que enviar la clave publica del cliente, y/o utilizar una clave simetrica
            //Falta la logica de preguntas/respuestas
            KeyPair kp;

            kp = generarClaves();

            PublicKey pkc = kp.getPublic();
            PrivateKey pvc = kp.getPrivate();

            oos.writeObject(pkc);
        } catch (IOException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        }



    }

    public static KeyPair generarClaves() {
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
