package de.tum.cit.ase.maze.helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PropertiesFileParser {

    //String filePath = "maps/level-1.properties";

    public static Map<Tuple, Integer> parsePropertiesFile(String filePath) throws IOException {
        Map<Tuple, Integer> resultMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    Tuple key = parseTuple(parts[0]);
                    int value = Integer.parseInt(parts[1]);
                    resultMap.put(key, value);
                }
            }
        }

        return resultMap;
    }

    public static Tuple parseTuple(String input) {
        String[] coordinates = input.split(",");
        if (coordinates.length == 2) {
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            return new Tuple(x, y);
        }
        return null;
    }


}