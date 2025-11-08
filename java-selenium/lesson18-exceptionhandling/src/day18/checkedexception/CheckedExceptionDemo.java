package day18.checkedexception;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class CheckedExceptionDemo {
    public static void main(String[] args) throws FileNotFoundException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("lesson18-exceptionhandling/file.txt"));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
