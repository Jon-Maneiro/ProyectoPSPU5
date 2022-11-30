import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre; // 50char
    private String apellido; // 50char
    private int edad;
    private String email;//Validar con expresiones regulares //  50 chars
    private String usuario;// 20char
    private String contrasenna;//La contraseña se guarda hasheada, 20Bytes

    private boolean normasAceptadas;
/*
    Nombre 50 chars - 100Bytes
    Apellido 50 chars - 100Bytes
    edad int - 4Bytes
    email 50chars - 100Bytes
    usuario 20 chars - 40Bytes
    contrasenna 20Bytes

    Total = 364Bytes

 */

    public Usuario(String nombre, String apellido, int edad, String email, String usuario, String contrasenna, boolean normasAceptadas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        this.contrasenna = contrasenna;
        this.normasAceptadas = normasAceptadas;
    }

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

    public String getContrasenna() {
        return contrasenna;
    }

    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }

    public boolean isNormasAceptadas() {
        return normasAceptadas;
    }

    public void setNormasAceptadas(boolean normasAceptadas) {
        this.normasAceptadas = normasAceptadas;
    }
}
