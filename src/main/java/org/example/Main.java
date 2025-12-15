package org.example;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
            Scanner scan = new Scanner(System.in);

            System.out.println("Introduce el nombre de la moneda que deseas buscar");

            String nomeMoneda = scan.nextLine().toLowerCase();

            JsonObject moneda = buscarMoneda(nomeMoneda);

            if(moneda == null){
                System.err.println("La moneda no existe");
            }else {
                mostrarInfo(moneda);
            }
    scan.close();
    }
    private static int totalMonedas(){
        try{
            HttpClient cliente = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.coinlore.net/api/global/"))
                    .GET()
                    .build();

            HttpResponse<String> json = cliente.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new Gson();
            JsonArray array = gson.fromJson(json.body(),JsonArray.class);
            JsonObject datos = array.get(0).getAsJsonObject();

            return datos.get("coins_count").getAsInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }catch (JsonSyntaxException e){
            throw new RuntimeException(e);
        }
    }
    public static JsonObject buscarMoneda(String nombre){
        try {
            HttpClient cliente = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            int totalMonedas = totalMonedas();

            for(int i=0; i<=totalMonedas; i+=100){
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.coinlore.net/api/tickers/?start="+i+"&limit=100"))
                        .GET()
                        .build();

                HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

                Gson gson = new Gson();
                JsonObject respuestaJson = gson.fromJson(response.body(), JsonObject.class);
                JsonArray datos = respuestaJson.getAsJsonArray("data");

                for(int m=0; m<datos.size(); m++){
                    JsonObject moneda = datos.get(m).getAsJsonObject();
                    String nombreMoneda = moneda.get("name").getAsString().toLowerCase();
                    String simboloMoneda = moneda.get("symbol").getAsString().toLowerCase();

                    if(nombreMoneda.equals(nombre) || simboloMoneda.equals(nombre)){
                        return moneda;
                    }
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static void mostrarInfo(JsonObject moneda){
        String nombreMoneda = moneda.get("name").getAsString();
        String simboloMoneda = moneda.get("symbol").getAsString();
        int rankMoneda = moneda.get("rank").getAsInt();
        String usdMoneda = moneda.get("price_usd").getAsString();
        String tiempoMoneda = moneda.get("percent_change_24h").getAsString();

        System.out.println("Nombre: " + nombreMoneda);
        System.out.println("Símbolo: " + simboloMoneda);
        System.out.println("Rank: " + rankMoneda);
        System.out.println("Precio: " + usdMoneda);
        System.out.println("Variación del último día: " + tiempoMoneda);

    }
}