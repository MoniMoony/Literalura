package br.com.andre.literalura.main;


import br.com.andre.literalura.entity.Autor;
import br.com.andre.literalura.entity.Idioma;
import br.com.andre.literalura.entity.Livro;
import br.com.andre.literalura.repository.AutorRepository;
import br.com.andre.literalura.repository.LivroRepository;
import br.com.andre.literalura.util.ConsumoApi;
import br.com.andre.literalura.util.SalvarDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.*;


public class Principal {

    @Autowired
    LivroRepository livroRepository;

    @Autowired
    AutorRepository autorRepository;

    Scanner scan = new Scanner(System.in);
    DoubleSummaryStatistics ds = new DoubleSummaryStatistics();

    public Principal() {

    }

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void menu() {
        boolean sair = false;
        int opcao = -1;

        while (!sair) {
            imprimeMenuPrincipal();
            try {
                opcao = scan.nextInt();
                scan.nextLine();
                switch (opcao) {
                    case 1:
                        buscaDados();
                        break;
                    case 2:
                        listarLivros();
                        break;

                    case 3:
                        listarAutores();
                        break;
                    case 4:
                        buscarAutorVivoPorData();
                        break;
                    case 5:
                        listarLivrosPorIdioma();
                        break;
                    case 6:
                        top10LivrosBaixados();
                        break;
                    case 0:
                        sair = true;
                        System.out.println("Saliendo...");
                        break;

                    default:
                        System.out.println("Entrada inválida. Por favor, introduzca nuevamente una opción válida");
                }

            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, introduzca nuevamente una opción válida");
                scan.nextLine();

            }

        }


    }


    private void buscaDados() {
        ConsumoApi consumoApi = new ConsumoApi();
        SalvarDados salvarDados = new SalvarDados(livroRepository, autorRepository);

        System.out.println("Ingrese el nombre del libro para buscar");
        var nomeLivro = scan.nextLine();


        Optional<Livro> livro = livroRepository.findByTituloContainingIgnoreCase(nomeLivro);
        if (!livro.isPresent()) {
            System.out.println("\u001b[31m \u001b[3mBuscando .....\u001b[0m");
            var url = "https://gutendex.com/books/?search=" + nomeLivro.replace(" ", "+");
            String json = consumoApi.obterDados(url);

            salvarDados.converterDados(json);
        } else {
            System.out.println("\n\nEl libro ya está registrado");
        }
    }

    public static void imprimeMenuPrincipal() {
        String menuPrincipal = """
                               
                *******************************************
                        BIENVENIDO A LITERALURA
                *******************************************
                        
                        Elija una de las siguientes opciones:
                        
                        1 - Buscar libro por título
                        2 - Listar libros registrados
                        3 - Listar autores registrados
                        4 - Listar autores vivos en un año específico
                        5 - Listar libros en un idioma específico
                        6 - Top 10 libros más descargados
                        
                        0 - Salir
                        
                                
                """;

        System.out.println(menuPrincipal);
    }

    private void listarLivros() {
        List<Livro> livros = livroRepository.findAll(Sort.by("título"));
        imprimeLivros(livros);
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll(Sort.by("autor"));
        imprimeAutores(autores);
    }

    private void buscarAutorVivoPorData() {
        System.out.println("Ingrese la fecha que desea buscar");
        var data = scan.nextInt();
        scan.nextLine();
        List<Autor> autores = autorRepository.findByAutorVivoPorData(data);

        if (autores.isEmpty()) {
            System.out.println("No se encontró ningún autor vivo en el año " + data);
        } else {
            imprimeAutores(autores);

        }
    }

    public void listarLivrosPorIdioma() {

        System.out.println("""
                Ingrese el idioma en el que desea realizar la búsqueda
                  es - español
                  en - inglés
                  fr - francés
                  it - italiano
                  pt - portugués
                """);
        var idioma = scan.nextLine().toUpperCase();
        try {
            Idioma idiomaEscolhido = Idioma.valueOf(idioma);
            List<Livro> livros = livroRepository.findByIdioma(idiomaEscolhido);
            if (livros.isEmpty()){
                System.out.println("\nNo se encontraron libros en " + idiomaEscolhido.getNome());
            }else {
                imprimeLivros(livros);
                System.out.println("Se encontraron " + livros.size() + " libros en el idioma " +  idiomaEscolhido.getNome());
            }
        }
        catch (IllegalArgumentException e ){
            System.out.println("\n¡Ingrese un idioma válido!");
            listarLivrosPorIdioma();
        }
    }

    private void top10LivrosBaixados(){
        System.out.println("Lista de los 10 libros más descargados");
        List<Livro> livros = livroRepository.top10LivrosBaixados();
        imprimeLivros(livros);

    }


    private void imprimeAutores(List<Autor> autores) {
        autores.forEach(a -> {
            System.out.println();
            System.out.print(
                    "\nAutor: " + a.getAutor() +
                            "\nAño de Nacimiento: " + a.getAnoNascimento() +
                            "\nAño de Fallecimiento: " + a.getAnoFalecimento() +
                            "\nLibros :" + a.getLivros());
        });
    }

    private void imprimeLivros(List<Livro> livros) {
        livros.forEach(l -> {
            System.out.println(
                    "\n----- Libro -----" +
                            "\nTítulo: " + l.getTitulo() +
                            "\nAutor: " + l.getAutor().getAutor() +
                            "\nIdioma: " + l.getIdioma() +
                            "\nNúmero de downloads: " + l.getNumeroDownloads());
        });
    }


}
