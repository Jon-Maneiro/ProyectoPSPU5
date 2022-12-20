import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccesoInfo {

    private static final String algoritmo = "SHA-256";//Algoritmo de hasheo
    private static final String ENCODING_TYPE="UTF-8";
/**
 * Tanto funciones genericas como funciones de acceso a los datos
 * Gestionar aqui tema ficheros y como organizarlo para no permitir a más de un usuario acceder al mismo fichero
 * a la vez, por temas de persistencia de datos.
 */

    /**
     * Metodo de utilidad para devolver el texto suministrado con la longitud requerida
     *
     * @param texto    el texto a modificar
     * @param longitud la longitud deseada
     * @return texto con longitud deseada
     */
    public static String obtenerStringCompleto(String texto, int longitud) {
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

    /**
     * Metodo para hacer preguntas que requieran un (Y)es / (N)o por parte del usuario
     *
     * @return true o false
     */
    public static boolean yesNo() {
        Scanner sc = new Scanner(System.in);
        String check = "";
        boolean resp = false;
        boolean correcto = false;
        while (!correcto) {
            check = sc.nextLine();
            if (check.equalsIgnoreCase("Y")) {
                resp = true;
                correcto = true;
            } else if (check.equalsIgnoreCase("N")) {
                resp = false;
                correcto = true;
            } else {
                System.out.println("Parece que no has introducido una opcion valida, vuelve a intentarlo");
            }
        }

        return resp;
    }
    /**
     * Se comprueba si la variable suministrada se puede convertir a long
     *
     * @param check String a comprobar
     * @return boolean yes/no
     */
    public static boolean isLong(String check) {
        try {
            Long.parseLong(check);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Se comprueba si la variable suministrada se puede convertir a int
     *
     * @param check String a comprobar
     * @return boolean yes/no
     */
    public static boolean isInt(String check) {
        try {
            Integer.parseInt(check);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Pide por pantalla un valor int y lo devuelve
     * @param mensaje mensaje que se quiere mostrar para dar informacion sobre el valor a introducir
     * @return un Integro
     */
    public static int pedirInt(String mensaje){
        System.out.println(mensaje);
        Scanner sc = new Scanner(System.in);
        int valor = 0;
        boolean correcto = false;
        while(!correcto){
            String temp = sc.nextLine();
            if(isInt(temp)){
                valor = Integer.parseInt(temp);
                correcto = true;
            }else{
                System.out.println("Parece que lo que has introducido no es un valor correcto," +
                        "\n por favor, introduce un valor valido");
            }
        }

        return valor;

    }

    public static double pedirDouble(String mensaje){
        System.out.println(mensaje);
        Scanner sc = new Scanner(System.in);
        double valor = 0;
        boolean correcto = false;
        while(!correcto){
            String temp = sc.nextLine();
            if(isInt(temp)){
                valor = Double.parseDouble(temp);
                correcto = true;
            }else{
                System.out.println("Parece que lo que has introducido no es un valor correcto," +
                        "\n por favor, introduce un valor valido");
            }
        }

        return valor;

    }

    /**
     * Pide un Email por pantalla y se asegura de que este correctamente formateado
     * @return String con el Email solicitado
     */
    public static String pedirCorreo(){
        System.out.println("Por favor, introduce tu correo electronico");
        Scanner sc = new Scanner(System.in);
        String email = "";
        String regex = "^(.+)@(.+)$";
        Pattern patron = Pattern.compile(regex);
        boolean correcto = false;

        while(!correcto){
            String temp = sc.nextLine();
            Matcher matcher = patron.matcher(temp);
            if(matcher.matches()){
                email = temp;
                correcto = true;
            }else{
                System.out.println("Asegurate de que el Email esta correctamente formateado");
            }
        }
        return email;
    }

    /**
     * Pide por pantalla un dato, y lo devuelve resumido/hasheado
     * @param mensaje mensaje que se quiera mostrar al usuario
     * @return dato introducido por pantalla resumido/hasheado
     */
    public static byte[] pedirDatoYHashear(String mensaje) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Scanner sc = new Scanner(System.in);
        System.out.println(mensaje);
        byte[] valor = getDigest(sc.nextLine().getBytes(ENCODING_TYPE));
        return valor;
    }

    /*
        HASH
     */

    /**
     * Devuelve un array de Bytes resumiendo el texto suministrado
     * @param mensaje array de Bytes del mensaje a hashear
     * @return array de Bytes con mensaje Hasehado
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getDigest(byte[] mensaje) throws NoSuchAlgorithmException {
        byte[] resumen = null;
        MessageDigest alg = MessageDigest.getInstance(algoritmo);
        alg.reset();
        alg.update(mensaje);
        resumen = alg.digest();
        //System.out.println(resumen);//Solo para comprobaciones
        return resumen;
    }

    /**
     * Compara 2 resumenes, devuelve true si son iguales, false en caso contrario
     * @param res1 Uno de los resumenes
     * @param res2 Otro de los resumenes
     * @return true/false
     */
    public static boolean compararHash(byte[] res1, byte[] res2) throws NoSuchAlgorithmException {
        boolean correcto;
        MessageDigest alg = MessageDigest.getInstance(algoritmo);
        alg.reset();
        correcto = alg.isEqual(res1,res2);
        return correcto;
    }

    /**
     * Recibe un numero de cuenta y comprueba que exista
     * @param numCuenta numero de cuenta que se quiere comprobar
     * @return true/false
     */
    public static boolean cuentaExiste(String numCuenta){
        String cuenta = "cuentas/Cuenta"+numCuenta+".dat";
        File file = new File(cuenta);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }

    public static void crearCuenta(int numCuenta, double saldoInicial) throws IOException {
        String cuenta = "cuentas/Cuenta"+numCuenta+".dat";
        File file = new File(cuenta);
        RandomAccessFile fichero = new RandomAccessFile(file,"rw");
        fichero.seek(0);
        fichero.writeDouble(saldoInicial);
        fichero.close();
    }

    /**
     * Devuelve el saldo de una cuenta
     * @param numCuenta numero de cuenta que se desea consultar
     * @return saldo de esa cuenta
     * @throws IOException
     */
    public static double obtenerSaldoCuenta(String numCuenta) throws IOException {
        String cuenta = "cuentas/Cuenta"+numCuenta+".dat";
        File file = new File(cuenta);
        RandomAccessFile fichero = new RandomAccessFile(file,"r");

        double saldo = 0;

        long longitud = fichero.length();
        fichero.seek(0);

        saldo = fichero.readDouble();

        fichero.close();

        return saldo;

    }

    /**
     * Cambia el valor de una cuenta, sumando o restando el valor
     * @param numCuenta numero de la cuenta que se va a cambiar
     * @param valor cambio efectivo, si el numero es negativo, se resta
     * @throws IOException
     */
    public static void cambiarValorCuenta(String numCuenta , double valor) throws IOException {
        String cuenta = "cuentas/Cuenta"+numCuenta+".dat";
        File file = new File(cuenta);
        RandomAccessFile fichero = new RandomAccessFile(file,"rw");

        double saldoActual = 0;
        double saldoNuevo = 0;

        long longitud = fichero.length();
        fichero.seek(0);
        saldoActual = fichero.readDouble();
        fichero.close();

        saldoNuevo = saldoActual + valor;

        new FileOutputStream("cuentas/"+cuenta).close();

        fichero = new RandomAccessFile(file,"rw");
        fichero.seek(0);
        fichero.writeDouble(saldoNuevo);
        fichero.close();
    }

    /**
     * Inserta un usuario en el archivo.dat de Usuarios
     * @param user el objeto Usuario que se desea insertar
     * @throws IOException
     */
    public static void insertarUsuario(Usuario user) throws IOException {
        File file = new File("Usuarios.dat");
        RandomAccessFile fichero = new RandomAccessFile(file,"rw");

        /*
        Nombre 50 chars - 100Bytes
        Apellido 50 chars - 100Bytes
        edad int - 4Bytes
        email 50chars - 100Bytes
        usuario 20 chars - 40Bytes
        contrasenna 20Bytes
        numCuenta int 4Bytes

        Total = 368Bytes
       */

        long longitud = fichero.length();
        fichero.seek(longitud);

        fichero.writeChars(obtenerStringCompleto(user.getNombre(),50));
        fichero.writeChars(obtenerStringCompleto(user.getApellido(),50));
        fichero.writeInt(user.getEdad());
        fichero.writeChars(obtenerStringCompleto(user.getUsuario(),20));
        fichero.write(user.getContrasenna());
        fichero.writeInt(user.getNumCuenta());

        fichero.close();

    }

    /**
     * Comprueba la existencia de un Usuario en el archivo.dat, y que las contraseñas coincidan
     * @param user
     * @param pass
     * @return
     */
    public static boolean checkUsuario(String user, byte[] pass) throws IOException, NoSuchAlgorithmException {
        /*
        Nombre 50 chars - 100Bytes
        Apellido 50 chars - 100Bytes
        edad int - 4Bytes
        email 50chars - 100Bytes
        usuario 20 chars - 40Bytes
        contrasenna 20Bytes
        numCuenta int 4Bytes

        Total = 368Bytes
       */
        char[] usuario = new char[20];
        byte[] contrasenna = new byte[20];
        boolean existe = false;

        File file = new File("Usuarios.dat");
        RandomAccessFile fichero = new RandomAccessFile(file,"rw");
        long longitud = fichero.length();
        fichero.seek(0);

        boolean correcto = false;
        while(fichero.getFilePointer()<longitud && !correcto){
            fichero.seek(fichero.getFilePointer()+304);
            for(int x = 0; x < 20; x++){
                usuario[x] = fichero.readChar();
            }
            for(int y = 0; y<20;y++){
                contrasenna[y] = (byte) fichero.read();
            }
            fichero.readInt();
            if(new String(usuario).equals(user) && compararHash(contrasenna,pass)){
                correcto = true;
                existe = true;
            }else{
                System.out.println("Los datos son incorrectos");
            }
        }


        return existe;
    }

    /**
     * Genera un codigo de 6 digitos aleatorios
     * @return integer de 6 posiciones
     */
    public static int generarCodigo(){
        int random = 0;
        String temp = "";
        for(int x = 0;x<6;x++){
            Random rand = new Random();
            int upper = 10;
            temp = temp + rand.nextInt(upper);
        }
        random = Integer.parseInt(temp);
        return random;
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



}
