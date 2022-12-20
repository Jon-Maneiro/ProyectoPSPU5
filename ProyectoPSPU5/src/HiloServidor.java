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

    /**
     * Constructor de la clase HiloServidor
     * @param sock el socket al que se ha conectado
     */
    public HiloServidor(Socket sock) {
        this.sock = sock;
    }

    /**
     * Metodo de ejecucion de la clase HiloServidor
     */
    @Override
    public void run() {

        System.setProperty("javax.net.ssl.keyStore","certificado/AlmacenSSL.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","12345Abcde");

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
            boolean cuentaExiste = (boolean)ois.readObject();
            if(cuentaExiste){
                boolean correcto = false;
                while(!correcto) {
                    String user = (String) ois.readObject();
                    String pass = (String) ois.readObject();
                    boolean x = AccesoInfo.checkUsuario(user, pass);
                    correcto = x;
                    oos.writeObject(x);
                }
                System.out.println("Inicio de sesion");
            }else{
                Usuario user = (Usuario)ois.readObject();
                AccesoInfo.insertarUsuario(user);
                System.out.println("Se ha recibido un Usuario Nuevo");
                oos.writeObject(true);
                double saldoInicial = (double) ois.readObject();
                System.out.println("Se ha recibido el Saldo de ese usuario");
                AccesoInfo.crearCuenta(user.getNumCuenta(),10000);
                System.out.println("Registro");
            }


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
                textoCliente = operacion;
                switch(Integer.parseInt(operacion)){
                    case 1:
                        boolean correcto = false;
                        String cuenta = "";
                        while(!correcto){
                            System.out.println("Se procede con una consulta de saldo");
                            byte[] cuentaCifrada =(byte[])ois.readObject();
                            System.out.println(cuentaCifrada);
                            cuenta = AccesoInfo.descifrarMensaje("RSA",cuentaCifrada,pvkServidor);
                            //Comprobacion de que la cuenta cifrada sea la correcta
                            System.out.println(cuenta);
                            if(AccesoInfo.cuentaExiste(cuenta)) {//Comprobar que la cuenta exista
                                correcto = true;
                                oos.writeObject(true);
                            }else{
                                oos.writeObject(false);
                            }
                        }


                        //Recogemos el saldo de esa cuenta y se lo enviamos de vuelta
                        double saldo = AccesoInfo.obtenerSaldoCuenta(Integer.parseInt(cuenta));
                        oos.writeObject(saldo);
                        break;
                    case 2:
                        System.out.println("Se procede con una Transferencia");
                        byte[] cuentaPropiaC;
                        String cuentaPropia = "";
                        byte[] cuentaAjenaC;
                        String cuentaAjena = "";

                        boolean correcto2 = false;
                        while(!correcto2) {//Cuenta Propia
                            cuentaPropiaC = (byte[]) ois.readObject();
                            cuentaPropia = AccesoInfo.descifrarMensaje("RSA", cuentaPropiaC, pvkServidor);

                            oos.writeObject(true);
                            correcto2 = (boolean) ois.readObject();
                        }

                        boolean correcto3 = false;
                        while(!correcto3) {
                            //Cuenta Ajena
                            cuentaAjenaC = (byte[]) ois.readObject();
                            cuentaAjena = AccesoInfo.descifrarMensaje("RSA", cuentaAjenaC, pvkServidor);
                            //Comprobacion de que la cuenta existe
                            oos.writeObject(true);
                            correcto3 = (boolean) ois.readObject();
                        }

                        boolean correcto4 = false;
                        while(!correcto4){
                            //Nos llega el dinero

                            double dinero = (double) ois.readObject();
                            String codigo = String.valueOf(AccesoInfo.generarCodigo());
                            byte[] codigoCifrado = AccesoInfo.cifrarMensaje("RSA",codigo,pkCliente);
                            oos.writeObject(codigoCifrado);

                            if((boolean)ois.readObject()){
                                correcto4 = true;
                                AccesoInfo.cambiarValorCuenta(cuentaPropia,-dinero);
                                AccesoInfo.cambiarValorCuenta(cuentaAjena,dinero);

                            }else{
                            }
                        }
                        oos.writeObject(new String("Se ha transferido el dinero"));

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

    /**
     * Genera un par de Claves para ser usadas en cifrado y descifrado de datos
      * @return
     */
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

    /**
     * El texto del menu principal, se manda desde el servidor al cliente
     * @return
     */
    public static String textoMenuPrincipal(){
        return "Bienvenido, que deseas hacer?:\n" +
                "(1) - Ver Saldo\n" +
                "(2) - Hacer Transferencia\n" +
                "Escribe \"Salir\" para salir";
    }


}
