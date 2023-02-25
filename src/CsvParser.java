import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CsvParser {
    public static Map<String, List<Integer>> getMap() throws IOException {
        // Crear un objeto CSVParser y especificar el formato del archivo CSV
        CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(new FileReader(System.getProperty("user.dir")+ "/codigos.csv", StandardCharsets.UTF_8));
        Map<String ,List<String>> mapa_comunidad = new HashMap<>();
        Map<String, List<Integer>> playas = new HashMap<>();
        String nombre_provincia = "";
        String comunidad ="";
        String id_playa = "";
// Iterar sobre cada registro en el archivo CSV
        for (CSVRecord record : parser) {
            // Acceder a los valores de cada campo del registro por su nombre
            nombre_provincia = record.get("NOMBRE_PROVINCIA");
            comunidad = record.get("COMUNIDAD");
            id_playa = record.get("ID_PLAYA");
            if(!playas.containsKey(nombre_provincia)){
                playas.put(nombre_provincia,new ArrayList<>());
            }
            playas.get(nombre_provincia).add(Integer.parseInt(id_playa));
            if(!mapa_comunidad.containsKey(comunidad)){
                mapa_comunidad.put(comunidad,new ArrayList<>());
            }
            else if(!mapa_comunidad.get(comunidad).contains(nombre_provincia)){
                mapa_comunidad.get(comunidad).add(nombre_provincia);
            }
        }
        return playas;

    }

}