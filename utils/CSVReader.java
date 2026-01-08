package utils;

import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;

public class CSVReader {

    public static List<String[]> read(String filePath) {
       

        List<String[]> rows = new ArrayList<>();

        try {

            List<String> line = Files.readAllLines(Path.of(filePath));

            if (line.isEmpty()) {

                return rows;
            }

            int startIndex = 1; //skip header line

            for (int i = startIndex; i < line.size(); i++) {

                String token = line.get(i).trim();

                if (token.isEmpty()) {
                    continue;
                }


                String[] parts = token.split(",", -1); //keep empty fields
                rows.add(parts);
            }
            
        } catch (Exception e) {
            
            System.out.println("Error reading file: " + filePath);
            System.out.println(e.getMessage());
        }

        return rows;
    }
 
    
}
