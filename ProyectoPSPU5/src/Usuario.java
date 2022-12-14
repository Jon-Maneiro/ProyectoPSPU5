import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre; // 50char
    private String apellido; // 50char
    private int edad;
    private String email;//Validar con expresiones regulares //  50 chars
    private String usuario;// 20char
    private byte[] contrasenna;//La contraseña se guarda hasheada, 20Bytes

    private int numCuenta;//4Bytes//20 Digitos

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

    /**
     * Constructor de la clase usuario
     * @param nombre
     * @param apellido
     * @param edad
     * @param email
     * @param usuario
     * @param contrasenna
     * @param numCuenta
     */
    public Usuario(String nombre, String apellido, int edad, String email, String usuario, byte[] contrasenna, int numCuenta) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        this.contrasenna = contrasenna;
        this.numCuenta = numCuenta;
    }

    /**
     * Constructor vacio
     */
    public Usuario(){};

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public byte[] getContrasenna() {
        return contrasenna;
    }

    public void setContrasenna(byte[] contrasenna) {
        this.contrasenna = contrasenna;
    }

    public int getNumCuenta() {
        return numCuenta;
    }

    public void setNumCuenta(int numCuenta) {
        this.numCuenta = numCuenta;
    }
}
