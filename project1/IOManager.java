import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class IOManager{

    public void scanFile() throws FileNotFoundException {
        Scanner scan = new Scanner(new File("/home/torstein/Documents/ntnu/it3708/project1/Testing_Data/Data_Files/p01"));
        while (scan.hasNextLine()) {
            System.out.println(scan.nextLine());
        }
        scan.close();
    }

    public static void main(String[] args) throws FileNotFoundException{
        IOManager manager = new IOManager();
        Model model = new Model();
        manager.scanFile();

    }
}