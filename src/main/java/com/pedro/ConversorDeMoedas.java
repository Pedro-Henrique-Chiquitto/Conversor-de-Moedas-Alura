package com.pedro;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ConversorDeMoedas {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String apiKey = "ca2c18266ec653c08958080e";

        while (true) {
            System.out.println("### Conversor de Moedas ###");
            System.out.println("1. USD -> BRL");
            System.out.println("2. EUR -> BRL");
            System.out.println("3. BRL -> USD");
            System.out.println("4. BRL -> EUR");
            System.out.println("5. JPY -> USD");
            System.out.println("6. CAD -> EUR");
            System.out.println("7. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            if (opcao == 7) {
                System.out.println("Saindo do conversor. Até mais!");
                break;
            }

            System.out.print("Digite o valor a ser convertido: ");
            double valor = scanner.nextDouble();

            String moedaBase = "";
            String moedaAlvo = "";

            switch (opcao) {
                case 1:
                    moedaBase = "USD";
                    moedaAlvo = "BRL";
                    break;
                case 2:
                    moedaBase = "EUR";
                    moedaAlvo = "BRL";
                    break;
                case 3:
                    moedaBase = "BRL";
                    moedaAlvo = "USD";
                    break;
                case 4:
                    moedaBase = "BRL";
                    moedaAlvo = "EUR";
                    break;
                case 5:
                    moedaBase = "JPY";
                    moedaAlvo = "USD";
                    break;
                case 6:
                    moedaBase = "CAD";
                    moedaAlvo = "EUR";
                    break;
                default:
                    System.out.println("Opção inválida.");
                    continue;
            }

            try {
                double taxaDeCambio = obterTaxaDeCambio(moedaBase, moedaAlvo, apiKey);
                double valorConvertido = valor * taxaDeCambio;
                System.out.printf("%.2f %s equivalem a %.2f %s%n", valor, moedaBase, valorConvertido, moedaAlvo);
            } catch (IOException | InterruptedException e) {
                System.err.println("Erro ao obter a taxa de câmbio: " + e.getMessage());
            }
            System.out.println("----------------------------------------");
        }
        scanner.close();
    }

    private static double obterTaxaDeCambio(String base, String alvo, String apiKey) throws IOException, InterruptedException {
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + base;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Gson gson = new Gson();
            RespostaAPI resposta = gson.fromJson(response.body(), RespostaAPI.class);

            if (resposta != null && resposta.getConversion_rates() != null) {
                Double taxa = resposta.getConversion_rates().get(alvo);
                if (taxa != null) {
                    return taxa;
                }
            }
        }
        throw new IOException("Não foi possível obter a taxa de câmbio para a moeda " + alvo);
    }
}