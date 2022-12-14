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
                String operacion = ois.readObject().toString();
                switch(Integer.parseInt(operacion)){
                    case 1:
                        boolean correcto = false;
                        while(!correcto){//Hacer la logica jaja
                            System.out.println("hola");
                            byte[] cuentaCifrada =(byte[])ois.readObject();
                            System.out.println(cuentaCifrada);
                            String cuenta = descifrarMensaje("RSA",cuentaCifrada,pvkServidor);
                            //Comprobacion de que la cuenta cifrada sea la correcta
                            System.out.println(cuenta);
                            if(true) {//Comprobar que la cuenta exista

                                correcto = true;
                            }else{
                                oos.writeBoolean(true);
                            }


                            //Recogemos el saldo de esa cuenta y se lo enviamos de vuelta
                            double saldo = 10000.00;//Dato de prueba
                            oos.writeDouble(saldo);
                        }
                        break;
                    case 2:
                        boolean correcto2 = false;
                        while(!correcto2) {//Hacer la logica jeje
                            byte[] cuentaPropiaC = (byte[]) ois.readObject();
                            String cuentaPropia = descifrarMensaje("RSA", cuentaPropiaC, pvkServidor);

                            //Comprobacion de que la cuenta es correcta
                            oos.writeBoolean(true);
                            //Cuenta Ajena
                            byte[] cuentaAjenaC = (byte[]) ois.readObject();
                            String cuentaAjena = descifrarMensaje("RSA" , cuentaAjenaC, pvkServidor);
                            //Comprobacion de que la cuenta existe
                            oos.writeBoolean(true);

                            //Nos llega el dinero
                            double dinero = ois.readDouble();

                            //Comprobamos bien todito
                            oos.writeBoolean(true);


                        }
                        break;
                }
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
        } catch (NoSuchPaddingException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, e);
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
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

    public static String descifrarMensaje(String algoritmo, byte[] mensaje, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cp = Cipher.getInstance(algoritmo);
        cp.init(Cipher.DECRYPT_MODE, key);
        String msgDes = new String(cp.doFinal(mensaje));

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
                "(2) - Hacer Transferencia\n" +
                "Escribe \"Salir\" para salir";
    }


}
