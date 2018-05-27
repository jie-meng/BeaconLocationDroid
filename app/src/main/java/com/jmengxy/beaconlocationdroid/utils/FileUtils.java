package com.jmengxy.beaconlocationdroid.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {

    public static String readTextFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }

        return sb.toString();
    }

    public static void writeTextFile(String filePath, String text) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(filePath)) {
            out.println(text);
        }
    }
}
