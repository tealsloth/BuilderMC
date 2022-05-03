package me.rowan.plugin.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {

    /**
     * Loads a file and adds it into an arraylist of Strings
     *
     *
     * @param filename filename of required file
     * @return file converted to arraylist
     * @throws IOException
     */
    public ArrayList<String> load(String filename) throws IOException {
        ArrayList<String> instructions = new ArrayList<>();

        File file = new File(filename);
        Scanner scanner = new Scanner(file);

        while(scanner.hasNextLine()){
            instructions.add(scanner.nextLine());
        }
        return instructions;
    }
}
