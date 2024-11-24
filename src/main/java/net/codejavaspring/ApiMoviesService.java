package net.codejavaspring;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class ApiMoviesService {

    private final RestTemplate restTemplate;
    private static final String API_KEY = "3afbf06";
    private static final String API_URL = "https://www.omdbapi.com/";

    @SuppressWarnings("unchecked")
    public HashMap<String, Object> searchMoviesWithPagination(String title, int page) {
        String url = API_URL + "?s=" + title + "&page=" + page + "&apikey=" + API_KEY;
        return restTemplate.getForObject(url, HashMap.class);
    }
    

    public ApiMoviesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @SuppressWarnings("unchecked") // para evitar warnings
    public List<HashMap<String, Object>> searchMovies(String title, int page) { // acepta titulo y pagina
        String url = API_URL + "?s=" + title + "&page=" + page + "&apikey=" + API_KEY; // incluye el parametro de pagina
        HashMap<String, Object> response = restTemplate.getForObject(url, HashMap.class);
        return response != null && response.get("Search") != null
            ? (List<HashMap<String, Object>>) response.get("Search")
            : new ArrayList<>();
    }


    @SuppressWarnings("unchecked") // para que no salga el warning por el cambio de objeto a hashmap
    public HashMap<String, Object> getMovieByImbdId(String imdbID) { // informacion de una sola pelicula
        //URL usando el imdbID
        String url = API_URL +  "?apikey=" + API_KEY + "&i=" + imdbID;
        
        // GET para datos de la película
        return restTemplate.getForObject(url, HashMap.class);
    }
}