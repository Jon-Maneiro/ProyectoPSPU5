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

    /**
     * Funcion de ejecucion principal de la clase Cliente
     * @param args
     */
    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.trustStore", "certCliente/UsuarioAlmacenSSL");
        System.setProperty("javax.net.ssl.trustStorePassword", "12345Abcde");

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

            KeyPair kp;

            kp = generarClaves();

            pkCliente = kp.getPublic();
            pvkCliente = kp.getPrivate();

            oos.writeObject(pkCliente);

            pkServidor = (PublicKey) ois.readObject();


            /*
            Logica de Inicio de Sesion
             */

            System.out.println("Bienvenido, dispones de una cuenta?(Y/N) en caso negativo, pasaremos al registro");
            boolean cuentaExiste = AccesoInfo.yesNo();
            oos.writeObject(cuentaExiste);
            if(cuentaExiste){//Inicio Sesion
                boolean correcto = false;
                while(!correcto) {
                    System.out.println("Por favor, introduce los datos requeridos para el inicio de sesion.");
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Introduce el nombre de usuario que te corresponde");
                    String user = sc.nextLine();
                    oos.writeObject(user);
                    String pass = AccesoInfo.pedirDatoYHashear("Introduce tu contraseña");
                    oos.writeObject(pass);

                    boolean si = (boolean) ois.readObject();
                    correcto = si;
                    if(!correcto){
                        System.out.println("El usuario y contraseña introducidos son incorrectos");
                    }else{
                        System.out.println("Inicio de sesion exitoso");
                    }
                }
            }else{//Registro
                System.out.println("Vamos a proceder con el registro");
                String nombre,apellido,email,usuario;
                int edad,numCuenta;
                String pass;

                Scanner sc = new Scanner(System.in);
                System.out.println("Introduce tu nombre(max 50char)");
                nombre = AccesoInfo.obtenerStringCompleto(sc.nextLine(),50);
                System.out.println("Introduce tus apellidos(max 50char)");
                apellido = AccesoInfo.obtenerStringCompleto(sc.nextLine(),50);
                edad = AccesoInfo.pedirInt("Introduce tu edad");
                email = AccesoInfo.pedirCorreo();
                System.out.println("Introduce el nombre de usuario que quieras utilizar(max 20char)");
                usuario = AccesoInfo.obtenerStringCompleto(sc.nextLine(),20);
                pass = AccesoInfo.pedirDatoYHashear("Introduce la contraseña");

                numCuenta = AccesoInfo.pedirInt("Por ultimo, introduce el numero de cuenta que deseas usar");
                oos.writeObject(new Usuario(nombre,apellido,edad,email,usuario,pass,numCuenta));
                ois.readObject();
                System.out.println("Aaa");
                oos.writeObject(AccesoInfo.pedirDouble("Introduce el saldo inicial de la cuenta"));

            }

            /*
            Fin de Inicio de Sesion
             */

            /*
            Logica de operaciones
             */
            String textoServidor = "";
            Scanner sc = new Scanner(System.in);
            String respuesta = "";
            while (!textoServidor.equalsIgnoreCase("Salir")) {
                System.out.println(ois.readObject());
                respuesta = sc.nextLine();
                textoServidor = respuesta;
                if (AccesoInfo.isInt(respuesta) && (Integer.parseInt(respuesta) >= 1 && Integer.parseInt(respuesta) <= 2)) {
                    oos.writeObject(respuesta);
                    switch (Integer.parseInt(respuesta)) {
                        case 1:
                            boolean correcto = false;
                            while(!correcto) {
                                String numCuenta = getCuenta(false);
                                oos.writeObject(AccesoInfo.cifrarMensaje("RSA",numCuenta,pkServidor));
                                boolean ok = (boolean) ois.readObject();
                                System.out.println(ok);
                                if(!ok){
                                    System.out.println("Esa cuenta no existe");
                                }else{
                                    correcto = true;
                                }
                            }
                            //Recibimos la informacion
                            double saldo = (double) ois.readObject();
                            System.out.println("----------------------------------");
                            System.out.println(saldo);
                            System.out.println("----------------------------------");
                            break;
                        case 2:
                            boolean correcto2 = false;
                            while(!correcto2){
                                String numCuenta = getCuenta(false);//Pedimos cuenta propia
                                oos.writeObject(AccesoInfo.cifrarMensaje("RSA", numCuenta, pkServidor));
                                boolean ok = (boolean) ois.readObject();
                                if(!ok){
                                    System.out.println("Ese numero de cuenta no te corresponde");
                                    oos.writeObject(false);
                                }else{
                                    correcto2 = true;
                                    oos.writeObject(true);
                                }
                            }
                            boolean correcto3 = false;
                            while(!correcto3) {//Pedimos cuenta ajena
                                String cuentaEx = getCuenta(true);
                                oos.writeObject(AccesoInfo.cifrarMensaje("RSA", cuentaEx,pkServidor));
                                boolean ok = (boolean) ois.readObject();
                                if(!ok){
                                    System.out.println("Ese numero de cuenta no existe");
                                    oos.writeObject(false);
                                }else{
                                    correcto3 = true;
                                    oos.writeObject(true);
                                }
                            }
                            boolean correcto4 = false;
                            while(!correcto4){//Pedimos dinero
                                double dinero = cantidadDinero();
                                oos.writeObject(dinero);
                                //Apartado doble autenticacion
                                byte[] codigoC = (byte[]) ois.readObject();
                                String codigoDescifrado = AccesoInfo.descifrarMensaje("RSA",codigoC,pvkCliente);
                                System.out.println(codigoDescifrado);
                                System.out.println("Introduce el codigo mostrado por pantalla");
                                String resp = sc.nextLine();
                                if(resp.equals(codigoDescifrado)) {
                                    correcto4 = true;
                                    oos.writeObject(true);
                                }else{
                                    System.out.println("Los codigos no coinciden");
                                    oos.writeObject(false);
                                }
                            }
                            System.out.println(ois.readObject());
                            System.out.println("La transferencia se ha efectuado exitosamente");
                            break;
                    }
                } else {
                    if(!respuesta.equalsIgnoreCase("salir")){
                        System.out.println("Parece que lo que hayas introducido no es valido");
                    }
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

    /**
     * Pide por pantalla una cantidad de dinero, para cuando toque hacer transferencias
     * @return El dato insertado por pantalla
     */
    public static double cantidadDinero(){
        double dinero = 0;
        Scanner sc = new Scanner(System.in);
        boolean correcto = false;
        while(!correcto){
            System.out.println("Introduce la cantidad que deseas transferir");
            String x = sc.nextLine();
            if(AccesoInfo.isDouble(x)){
                correcto = true;
                dinero = convert(x);
            }else{
                correcto = false;
                System.out.println("Los datos introducidos no son validos");
            }
        }

        return dinero;
    }

    /**
     * Devuelve un numero de cuenta en formato String
     * @param alter False - Cuenta propia, True - Cuenta Ajena. Unicamente relevante por el mensaje que se muestra al usuario
     * @return un String con el numero de cuenta
     */
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
            if(AccesoInfo.isInt(numCuenta) && numCuenta.length() == 10){
                correcto = true;
            }else{
                correcto = false;
            }
        }
        return numCuenta;
    }

    /**
     * Genera un par de claves
     * @return el par de claves
     */
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



    /**
     * Formatea correctamente un Double desde un String la mayor parte de los casos. Hay algunas ocurrencias que no puede controlar y los datos devueltos serán ambiguos
     * @param input String que se quiere formatear a double
     * @return un double con la correcta puntuacion
     */
    public static double convert(String input) {
        input = input.replace(',', '.');
        int decimalSeperator = input.lastIndexOf('.');

        if (decimalSeperator > -1) {
            input = input.substring(0, decimalSeperator).replace(".", "") + input.substring(decimalSeperator);
        }

        return Double.valueOf(input);
    }

}
