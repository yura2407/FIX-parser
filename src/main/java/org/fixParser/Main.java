package org.fixParser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String[] command = {
                "xjc",            // The xjc command
                "-d", ".", // Output directory
                "-p", "org.fixParser.generated", // Package name for the generated classes
                "sbe-2.0rc3.xsd"     // Path to your XSD file
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        try {
            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("xjc executed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}