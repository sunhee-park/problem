package org.example.problem1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AnalysisCVS {

    public static void main(String[] args) {

        String currentDirectory = System.getProperty("user.dir");
        File file = new File(currentDirectory, "sample.csv");
        String filePath = file.getAbsolutePath();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // 첫 번째 라인 (헤더) 읽고 무시
            br.readLine();

            String line;
            int totalLines = 0;
            int calculatedLines = 0;
            ArrayList<String[]> invalidRows = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                totalLines++;
                String[] values = line.split(",");

                // Check if all values are numeric
                boolean isValid = Arrays.stream(values).allMatch(AnalysisCVS::isNumeric);

                if (isValid) {
                    calculatedLines++;
                    ArrayList<Double> numericValues = new ArrayList<>();

                    // Convert to double
                    for (String value : values) {
                        numericValues.add(Double.parseDouble(value));
                    }

                    // Calculate statistics
                    double min = Collections.min(numericValues);
                    double max = Collections.max(numericValues);
                    double sum = numericValues.stream().mapToDouble(Double::doubleValue).sum();
                    double avg = sum / numericValues.size();
                    double stdDev = calculateStandardDeviation(numericValues, avg);
                    double median = calculateMedian(numericValues);

                    // Print the result for this row
                    System.out.printf("%.1f %.1f %.1f %.1f %.2f %.1f\n", min, max, sum, avg, stdDev, median);
                } else {
                    // Add invalid row to the list
                    invalidRows.add(values);
                }
            }

            // Summary
            System.out.println("---------------------------------------------");
            System.out.println("The total number of lines: " + totalLines);
            System.out.println("The calculated lines: " + calculatedLines);
            System.out.println("The error values: " + invalidRows.size());

            // Print invalid rows
            for (String[] invalidRow : invalidRows) {
                System.out.println(Arrays.toString(invalidRow));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to check if a string is numeric
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper method to calculate standard deviation
    private static double calculateStandardDeviation(ArrayList<Double> values, double mean) {
        double sum = 0.0;
        for (double value : values) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / values.size());
    }

    // Helper method to calculate median
    private static double calculateMedian(ArrayList<Double> values) {
        Collections.sort(values);
        int size = values.size();
        if (size % 2 == 0) {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            return values.get(size / 2);
        }
    }
}
