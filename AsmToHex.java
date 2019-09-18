import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Endrius on 14/09/2017.
 */
public class AsmToHex {

    public static void main(String args[]) {

        System.out.println(args[0]);

        File file = new File(args[0]);
        T1 trab = new T1();

        try {
            Scanner sc = new Scanner(file);
            trab.readIn(sc);
            trab.createInstructions();
            trab.generateOutPut(args[0]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("FINISH CORRECT!");

    }

}
