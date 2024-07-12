import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Busca {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner leitura = new Scanner(System.in);
        String busca = "";
        List<Titulo> titulos = new ArrayList<>(); //Toda vez que for rodado vai sendo adicionado a lista

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while (!busca.equalsIgnoreCase("sair")) { //Enquanto for diferente de sair continua buscando filme
            System.out.println("Digite o nome do filme: ");
            busca = leitura.nextLine();

            if(busca.equalsIgnoreCase("sair")){ //sair
                break;
            }

            //solicita
            try {
                String endereco = "https://www.omdbapi.com/?t=" + busca.replace(" ", "+") + "&apikey=6585022c";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endereco))
                        .build();

                HttpResponse<String> response = client //resposta
                        .send(request, HttpResponse.BodyHandlers.ofString());

                String json = response.body();
                System.out.println(json); //imprimi o resultado

                //Titulo meuTitulo = gson.fromJson(json, Titulo.class);
                TituloOmdb meuTituloOmdb = gson.fromJson(json, TituloOmdb.class);
                System.out.println(meuTituloOmdb);
                //try { //Sem Exception
                Titulo meuTitulo = new Titulo(meuTituloOmdb);
                System.out.println("Titulo já convertido");
                System.out.println(meuTitulo);

                titulos.add(meuTitulo);
            }catch (NumberFormatException e) { //Se acontecer uma Exception
                System.out.println("Aconteceu um erro: " + e.getMessage());
            }catch (IllegalArgumentException e) {
                System.out.println("Algum erro de argumento na busca, verifique o endereço" + e.getMessage());
            }catch (ErroDeConversaoDeAnoException e) {
                System.out.println(e.getMensagem());
            }
        }

        System.out.println(titulos); //imprimindo a lista

        FileWriter escrita = new FileWriter("filmes.json");
        escrita.write(gson.toJson(titulos));
        escrita.close();
        System.out.println("O programa finalizou corretamente");

    }
}
