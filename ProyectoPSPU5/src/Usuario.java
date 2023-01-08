import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre; // 50char
    private String apellido; // 50char
    private int edad;
    private String email;//Validar con expresiones regulares //  50 chars
    private String usuario;// 20char
    private String contrasenna;//La contraseña se guarda hasheada, 20Bytes

    private String numCuenta;//4Bytes//20 Digitos

/*
    Nombre 50 chars - 100Bytes
    Apellido 50 chars - 100Bytes
    edad int - 4Bytes
    email 50chars - 100Bytes
    usuario 20 chars - 40Bytes
    contrasenna 64chars 128Bytes
    numCuenta 10 chars 20bytes

    Total = 492Bytes

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
    public Usuario(String nombre, String apellido, int edad, String email, String usuario, String contrasenna, String numCuenta) {
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

    /**
     * Devuelve el nombre del Usuario
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna nombre de Usuario
     * @param nombre el nombre a asignar
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el apellido del usuario
     * @return
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Asigna un apellido
     * @param apellido el apellido a asignar
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Devuelve la edad del usuario
     * @return
     */
    public int getEdad() {
        return edad;
    }

    /**
     * Asigna la edad del usuario
     * @param edad la edad a asignar
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * Obtiene el Email del usuario
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asigna el email del usuario
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el Usuario del Usuario
     * @return
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Asigna el Usuario al Usuario
     * @param usuario
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Devuelve la contraseña del Usuario
     * @return
     */
    public String getContrasenna() {
        return contrasenna;
    }

    /**
     * Asigna la contraseña al Usuario
     * @param contrasenna
     */
    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }

    /**
     * Obtiene el numero de cuenta del Usuario
     * @return
     */
    public String getNumCuenta() {
        return numCuenta;
    }

    /**
     * Asigna el numero de cuenta del usuario
     * @param numCuenta
     */
    public void setNumCuenta(String numCuenta) {
        this.numCuenta = numCuenta;
    }
}
