package org.example;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try{
            String monedaUsuario = "Bitcoin";

            String idMoneda = obtenerIdMoneda(monedaUsuario);

            if(idMoneda == null){
                System.err.println("La moneda no existe");
                return;
            }else {
                System.out.println("La moneda sí existe");
            }

            HttpClient cliente = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.coinlore.net/api/ticker/?id="))
                    .GET()
                    .build();

            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Moneda info: ");
            System.out.println(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static String obtenerIdMoneda(String monedaUsuario){

        try{
            HttpClient cliente = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.coinlore.net/api/tickers/"))
                    .GET()
                    .build();

            HttpResponse response = cliente.send(request, HttpResponse.BodyHandlers.ofString());


            JSONObject json = new JSONObject(response.body());
            JSONArray array = json.getJSONArray("data");

            for(int i=0; i<array.length(); i++){

                JSONObject moneda = array.getJSONObject(i);

                String name = moneda.getString("name");

                if(name.equals(monedaUsuario)){
                    return moneda.getString("id");
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return null;
    }
}