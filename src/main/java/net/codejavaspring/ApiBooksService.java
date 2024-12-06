package net.codejavaspring;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class ApiBooksService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://openlibrary.org";

    public ApiBooksService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Busca libros en Open Library con paginación.
     *
     * @param query Término de búsqueda (título, autor, etc.).
     * @return Un mapa con los datos de los libros, página actual y total de páginas.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> searchBooksWithPagination(String query) {
        String url = API_URL + "/search.json?q=" + query;
        HashMap<String, Object> response = restTemplate.getForObject(url, HashMap.class);

        // Extraer los datos relevantes de la respuesta
        List<HashMap<String, Object>> books = response != null && response.get("docs") != null
                ? (List<HashMap<String, Object>>) response.get("docs")
                : new ArrayList<>();
        Integer totalResults = response != null && response.get("numFound") != null
                ? (Integer) response.get("numFound")
                : 0;

        // Añadir portadas a los libros
        books.forEach(book -> {
            if (book.containsKey("cover_i")) {
                String coverId = book.get("cover_i").toString();
                String thumbnailUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
                book.put("thumbnail_url", thumbnailUrl); // Agrega la portada al objeto del libro
            } else {
                book.put("thumbnail_url", "https://via.placeholder.com/100x150.png?text=No+Cover"); // Imagen por defecto
            }
        });

        // Calcular el total de páginas
        int totalPages = (int) Math.ceil(totalResults / 100.0); // Open Library devuelve hasta 100 resultados por página

        // Crear el mapa con los datos
        HashMap<String, Object> result = new HashMap<>();
        result.put("docs", books);
        result.put("totalResults", totalResults);
        result.put("totalPages", totalPages);

        return result;
    }

    /**
     * Obtiene los detalles de un libro específico utilizando su ID de Open Library.
     *
     * @param workId El ID de la obra en Open Library (work_id).
     * @return Detalles del libro como un mapa.
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getBookDetails(String workId) {
        String url = API_URL + "/works/" + workId + ".json";
        return restTemplate.getForObject(url, HashMap.class);
    }
}
