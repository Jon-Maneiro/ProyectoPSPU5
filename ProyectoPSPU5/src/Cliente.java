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



            /*
            Fin de Inicio de Sesion
             */

            /*
            Logica de operaciones
             */
            String textoServidor = "";
            Scanner sc = new Scanner(System.in);
            String respuesta = "";
            while (!textoServidor.equalsIgnoreCase("desconexion")) {
                System.out.println(ois.readObject());
                respuesta = sc.nextLine();
                if (isInt(respuesta) && (Integer.parseInt(respuesta) >= 1 && Integer.parseInt(respuesta) <= 2)) {
                    oos.writeObject(respuesta);
                    switch (Integer.parseInt(respuesta)) {
                        case 1:
                            boolean correcto = false;
                            while(!correcto) {
                                String numCuenta = getCuenta(false);
                                oos.writeObject(cifrarMensaje("RSA",numCuenta,pkServidor));//Esto tiene que ir cifrado
                                boolean ok = ois.readBoolean();
                                System.out.println(ok);
                                if(!ok){
                                    System.out.println("Ese numero de cuenta no te corresponde");
                                }else{
                                    System.out.println("funciona");
                                    correcto = true;
                                }
                            }
                            //Recibimos la informacion
                            double saldo = ois.readDouble();
                            System.out.println("----------------------------------");
                            System.out.println(saldo);
                            System.out.println("----------------------------------");
                            break;
                        case 2:
                            boolean correcto2 = false;
                            while(!correcto2){
                                String numCuenta = getCuenta(false);//Pedimos cuenta propia
                                oos.writeObject(cifrarMensaje("RSA", numCuenta, pkServidor));
                                boolean ok = ois.readBoolean();
                                if(!ok){
                                    System.out.println("Ese numero de cuenta no te corresponde");
                                }else{
                                    correcto2 = true;
                                }
                            }
                            boolean correcto3 = false;
                            while(!correcto3) {//Pedimos cuenta ajena
                                String cuentaEx = getCuenta(true);
                                oos.writeObject(cifrarMensaje("RSA", cuentaEx,pkServidor));
                                boolean ok = ois.readBoolean();
                                if(!ok){
                                    System.out.println("Ese numero de cuenta no existe");
                                }else{
                                    correcto3 = true;
                                }
                            }
                            boolean correcto4 = false;
                            while(!correcto4){//Pedimos dinero
                                double dinero = cantidadDinero();
                                oos.writeDouble(dinero);
                                boolean ok = ois.readBoolean();//Se hacen comprobaciones en la parte de servidor
                                if(!ok){
                                    System.out.println(ois.readObject());//Mensaje de error devuelto por el servidor
                                    /*
                                    Solo puede haber 2 mensajes de error
                                        1 - No queda dinero en la cuenta
                                        2 - Error desconocido
                                     */
                                    correcto = false;
                                }else{
                                    correcto = true;
                                }
                            }
                            System.out.println("La transferencia se ha efectuado exitosamente");//A esto igual ponerle otra comprobacion jaja
                            break;
                    }
                } else {
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

    public static double cantidadDinero(){
        double dinero = 0;
        Scanner sc = new Scanner(System.in);
        boolean correcto = false;
        while(!correcto){
            System.out.println("Introduce la cantidad que deseas transferir, por favor usa");
            String x = sc.nextLine();
            if(isDouble(x)){
                correcto = true;
                dinero = convert(x);
            }else{
                correcto = false;
                System.out.println("Los datos introducidos no son validos");
            }
        }

        return dinero;
    }
    public static String getCuenta(boolean alter) {
        Scanner sc = new Scanner(System.in);
        String numCuenta = "";
        boolean correcto = false;
        while(!correcto) {
            if(alter){
                System.out.println("Por favor introduce el numero de cuenta al que deseas hacer la transferencia");
            }else {
                System.out.println("Por favor introduce tu numero de cuenta");
            }
            numCuenta = sc.nextLine();
            if(isInt(numCuenta) && numCuenta.length() == 10){
                correcto = true;
            }else{
                correcto = false;
            }
        }
        return numCuenta;
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

    public static String descifrarMensaje(String algoritmo, String mensaje, Key key) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cp = Cipher.getInstance(algoritmo);
        cp.init(Cipher.DECRYPT_MODE, key);
        String msgDes = new String(cp.doFinal(mensaje.getBytes()));

        return msgDes;
    }

    public static byte[] cifrarMensaje(String algoritmo, String mensaje, Key key) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cp = Cipher.getInstance(algoritmo);
        cp.init(Cipher.ENCRYPT_MODE, key);
        byte[] msgCF = cp.doFinal(mensaje.getBytes());

        return msgCF;
    }

    public static boolean isInt(String check) {
        try {
            Integer.parseInt(check);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isDouble(String check) {
        try {
            Double.parseDouble(check);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static double convert(String input) {
        input = input.replace(',', '.');
        int decimalSeperator = input.lastIndexOf('.');

        if (decimalSeperator > -1) {
            input = input.substring(0, decimalSeperator).replace(".", "") + input.substring(decimalSeperator);
        }

        return Double.valueOf(input);
    }

}
