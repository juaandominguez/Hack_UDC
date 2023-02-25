import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;



public class PlayaWeather {
    public static void showBeaches(Map<String, Integer> map) {
        System.out.println("Estas son los mejores playas: ");
        int i = 0;
        if(map.isEmpty()) {System.out.println("No provinces"); return;}
        for (String str : map.keySet()) {
            System.out.println("-" + i++ + " " + str);
        }
    }

    public static int getValue(int estadoCielo, int oleaje, int viento, int tAgua, boolean sol, boolean surf, boolean kitesurf, boolean swim) {
        int val = 0;
        val = (sol && estadoCielo <= 100) ? val + 1 : val;
        val = (surf && oleaje <= 310) ? val + 1 : val;
        val = (kitesurf && viento >= 210) ? val + 1 : val;
        val = (swim && tAgua >= 18) ? val + 1 : val;
        return val;
    }

    public static Map<String, Integer> getBeaches(String provincia, int tiempo, int momento, boolean sol, boolean surf, boolean kitesurf, boolean swim) throws IOException {
        System.out.println("Eligiendo las mejores playas segun tus intereses...");
        Map<String, List<Integer>> map = CsvParser.getMap();
        int i = 1;
        String apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJFc2thaXdvbGtlckBnbWFpbC5jb20iLCJqdGkiOiIxNDRjN2M1Yy1iZDA4LTQ5NGUtYTU4Yi02ZjhiYzhlNzI3ZDAiLCJpc3MiOiJBRU1FVCIsImlhdCI6MTY3NzI1ODkxMCwidXNlcklkIjoiMTQ0YzdjNWMtYmQwOC00OTRlLWE1OGItNmY4YmM4ZTcyN2QwIiwicm9sZSI6IiJ9.oldQPE_m68ind-TVeYlfPyTuh5_SouogXvTfU5FIBEs";
        Map<String, Integer> beaches = new HashMap();
        if (!map.containsKey(provincia)) {
            System.out.println(provincia + " no existe");
            return null;
        }
        for (Integer integer : map.get(provincia)) {
            //System.out.println(i++ +" petitions");
            String playa = String.format("%07d", integer);
            //System.out.println("number is " + playa);

            String url = "https://opendata.aemet.es/opendata/api/prediccion/especifica/playa/" + playa + "?api_key=" + apiKey;

            // Realiza una solicitud GET a la API
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setRequestMethod("GET");

                // Lee la respuesta de la API y decodifica el JSON
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                //in.close();
                //con.disconnect();
                in.close();
                con.disconnect();
                JSONObject json = new JSONObject(content.toString());

                if (json.has("datos")) {


                    //json = new JSONObject(content.toString());
                    //System.out.println(json.toString(4));
                    HttpURLConnection con2 = (HttpURLConnection) new URL(json.getString("datos")).openConnection();
                    con2.setRequestMethod("GET");

                    BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                    String inputLine2;
                    StringBuffer content2 = new StringBuffer();
                    while ((inputLine2 = in2.readLine()) != null) {
                        content2.append(inputLine2);
                    }
                    in2.close();
                    con2.disconnect();

                    JSONObject json2 = new JSONObject(content2.substring(1).toString());

                    JSONObject prediccion = json2.getJSONObject("prediccion");
                    JSONArray aux = prediccion.getJSONArray("dia");
                    JSONObject dia = aux.getJSONObject(0);
                    JSONObject dia2 = aux.getJSONObject(1);
                    JSONObject dia3 = aux.getJSONObject(2);
                    if (momento == 0) {
                        addBeaches(tiempo, sol, surf, kitesurf, swim, beaches, json2, dia);
                    } else if (momento == 1) {
                        addBeaches(tiempo, sol, surf, kitesurf, swim, beaches, json2, dia2);
                    } else {
                        addBeaches(tiempo, sol, surf, kitesurf, swim, beaches, json2, dia3);
                    }

                } else {
                    System.out.println("No se encontraron datos para la playa " + playa);
                }
            } catch (Exception e) {
                break;
            }
        }
        return beaches.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    private static void addBeaches(int tiempo, boolean sol, boolean surf, boolean kitesurf, boolean swim, Map<String, Integer> beaches, JSONObject json2, JSONObject dia3) {
        if (tiempo == 0) {
            beaches.put(json2.getString("nombre"), getValue(dia3.getJSONObject("estadoCielo").getInt("f1"), dia3.getJSONObject("oleaje").getInt("f1"), dia3.getJSONObject("viento").getInt("f1"), dia3.getJSONObject("tAgua").getInt("valor1"), sol, surf, kitesurf, swim));
        } else if (tiempo == 1) {
            beaches.put(json2.getString("nombre"), getValue(dia3.getJSONObject("estadoCielo").getInt("f2"), dia3.getJSONObject("oleaje").getInt("f2"), dia3.getJSONObject("viento").getInt("f2"), dia3.getJSONObject("tAgua").getInt("valor2"), sol, surf, kitesurf, swim));
        } else
            beaches.put(json2.getString("nombre"), getValue((dia3.getJSONObject("estadoCielo").getInt("f1") + (dia3.getJSONObject("estadoCielo").getInt("f2"))) / 2
                    , (dia3.getJSONObject("oleaje").getInt("f1") + dia3.getJSONObject("oleaje").getInt("f2")) / 2
                    , (dia3.getJSONObject("viento").getInt("f1") + dia3.getJSONObject("viento").getInt("f2")) / 2
                    , (dia3.getJSONObject("tAgua").getInt("valor1") + dia3.getJSONObject("tAgua").getInt("valor2")) / 2
                    , sol, surf, kitesurf, swim));
    }

    private static int strToInt(String input) {
        if (input.equals("0"))
            return 0;
        else if (input.equals("1"))
            return 1;
        else
            return 2;

    }

    private static boolean strToBool(String input) {
        if (input.equals("0"))
            return false;
        else if (input.equals("1"))
            return true;
        else
            return false;
    }

    public static void main(String[] args) throws IOException {
        User user = new User();
        showBeaches(getBeaches(user.getProvincia(), user.getDia(), user.getHorario(), user.isTomarSol(), user.isSurf(), user.isKiteSurf(), user.isSwim()));
    }
}

