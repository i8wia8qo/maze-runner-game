package de.tum.cit.ase.maze.helpers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.Map;
import java.util.Properties;

public class MapUtils {

    /**
     * A utility class for managing and processing tiled maps within the game. It includes methods
     * for retrieving layer indices and processing tile properties to implement game logic based on
     * the map's layout and tile metadata.
     */
    public static int getLayerIndex(TiledMap map, TiledMapTileLayer layer) {
        // Iterate over all layers in the map
        for (MapLayer mapLayer : map.getLayers()) {
            // Check if the current layer is a TiledMapTileLayer and has the same reference as the provided layer
            if (mapLayer instanceof TiledMapTileLayer && mapLayer == layer) {
                // Return the index of the matching layer
                return map.getLayers().getIndex(mapLayer);
            }
        }

        // If the layer is not found, return -1 or handle the case accordingly
        return -1;
    }

    /**
     * Iterates through all the tiles in a specified tile layer, processing each tile's properties.
     * This method can be used for setting up game logic based on tile properties, such as collision
     * detection or interactive elements within the game world.
     *
     * @param tiledLayer The tile layer to be processed.
     */
    public static void processTiles(TiledMapTileLayer tiledLayer) {
        // Iterate through all cells in the layer
        for (int y = 0; y < tiledLayer.getHeight(); y++) {
            for (int x = 0; x < tiledLayer.getWidth(); x++) {
                // Get the cell at the current coordinates
                TiledMapTileLayer.Cell cell = tiledLayer.getCell(x, y);

                //System.out.println("(" + x + ", " + y + "): ");

                // Check if the cell is not null (contains a tile)
                if (cell != null) {
                    // Get the tile from the cell
                    TiledMapTile tile = cell.getTile();

                    // Check if the tile is not null
                    if (tile != null) {
                        // Access and process the properties of the tile
                        processTileProperties(tile.getProperties());
                        if(x==tiledLayer.getWidth()-1){
                            System.out.print("\n");
                        }

                    }
                }
            }
        }
    }

    /**
     * Processes the properties of a single tile. This helper method can be used to implement
     * specific logic based on tile properties, such as identifying blocked paths or interactive
     * elements within the map.
     *
     * @param properties The properties of the tile to process.
     */
    private static void processTileProperties(MapProperties properties) {
        // Access and process the properties of the tile
        // Example: Check if a property named "blocked" exists and its value

        /*

        if (properties.containsKey("blocked")) {
        // boolean isBlocked = true;
        // System.out.println("Tile is blocked: " + isBlocked);
            System.out.print("x");
        } else {
        // boolean isBlocked = false;
        // System.out.println("Tile is blocked: " + isBlocked);
            System.out.print(" ");
        }

        */

        // You can check other properties as needed
        // ...
    }




}
