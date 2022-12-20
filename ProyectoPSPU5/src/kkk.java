import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class kkk {
    public static void main(String[] args) throws IOException {
        File file = new File("Usuarios.dat");
        RandomAccessFile fichero = new RandomAccessFile(file, "rw");

        while(true){
            for (int x = 0;x<50;x++){
                System.out.println(fichero.readChar());
            }
            for (int x = 0;x<50;x++){
                System.out.println(fichero.readChar());
            }
            System.out.println(fichero.readInt());
            for (int x = 0;x<20;x++){
                System.out.println(fichero.readChar());
            }
            for (int x = 0;x<64;x++){
                System.out.println(fichero.readChar());
            }
            System.out.println(fichero.readInt());
        }

    }
}
