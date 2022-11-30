import java.io.Serializable;

public class Cuenta implements Serializable {

    private int numCuenta;
    private double dinero;
    /*
    * numCuenta int - 4Bytes
    * dinero double - 8Bytes
    *
    * total 12Bytes
    *
    * En este archivo, despues de estos datos, las demás lineas serán un log de transacciones
    * */

    public Cuenta(int numCuenta, double dinero) {
        this.numCuenta = numCuenta;
        this.dinero = dinero;
    }

    public int getNumCuenta() {
        return numCuenta;
    }

    public void setNumCuenta(int numCuenta) {
        this.numCuenta = numCuenta;
    }

    public double getDinero() {
        return dinero;
    }

    public void setDinero(double dinero) {
        this.dinero = dinero;
    }
}
