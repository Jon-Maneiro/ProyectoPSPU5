import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;
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
        ObjectOutputStream oos;
        ObjectInputStream ois;
        PublicKey pkCliente;
        PublicKey pkServidor;
        PrivateKey pvkCliente;

        try {
            socket = new Socket("localhost", puerto);




            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            //Habria que enviar la clave publica del cliente, y/o utilizar una clave simetrica
            //Falta la logica de preguntas/respuestas
            KeyPair kp;

            kp = generarClaves();

             pkCliente = kp.getPublic();
             pvkCliente = kp.getPrivate();

            oos.writeObject(pkCliente);

            pkServidor = (PublicKey) ois.readObject();


            /*
            Logica de Inicio de Sesion
             */
            //------------------------------------
            /*
            Fin de Inicio de Sesion
             */

            /*
            Logica de operaciones
             */
            String textoServidor = "";
            Scanner sc = new Scanner(System.in);
            String respuesta = "";
            while(!textoServidor.equalsIgnoreCase("desconexion")){
                System.out.println(ois.readObject());
                respuesta = sc.nextLine();
                if(isInt(respuesta) && (Integer.parseInt(respuesta) >= 1 && Integer.parseInt(respuesta)<= 2 )){
                    String conv = ois.readObject().toString();
                    while(!conv.equalsIgnoreCase("volver")){

                    }
                }else{
                    System.out.println("Parece que lo que hayas introducido no es valido");
                }

            }
            /*
            Fin de Logica de Operaciones
             */

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
