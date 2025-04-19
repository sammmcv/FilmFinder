package net.codejavaspring.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.codejavaspring.model.ApiMovies;
import net.codejavaspring.model.SearchHistory;
import net.codejavaspring.model.User;
import net.codejavaspring.repository.ApiMoviesRepository;
import net.codejavaspring.repository.SearchHistoryRepository;
import net.codejavaspring.repository.UserRepository;
import net.codejavaspring.service.ApiMoviesService;

@RestController
@RequestMapping("/api")
public class ApiRestController {

    @Autowired
    private ApiMoviesRepository apiMoviesRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    @Autowired
    private ApiMoviesService apiMoviesService;
    
    // Endpoint para autenticar usuario
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario no encontrado"));
        }
        
        // Add password verification using BCrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contraseña incorrecta"));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("role", user.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    // Endpoint para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        String email = userData.get("email");
        String password = userData.get("password");
        String firstName = userData.get("firstName");
        String lastName = userData.get("lastName");
        
        // Verificar si el correo ya está registrado
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo electrónico ya está registrado"));
        }
        
        // Crear nuevo usuario
        User newUser = new User();
        newUser.setEmail(email);
        
        // Encriptar la contraseña
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);
        
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setRole("USER");
        newUser.setEnabled(true);
        
        // Guardar el usuario
        User savedUser = userRepository.save(newUser);
        
        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("email", savedUser.getEmail());
        response.put("firstName", savedUser.getFirstName());
        response.put("lastName", savedUser.getLastName());
        response.put("role", savedUser.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    // Endpoint para obtener todos los usuarios (solo para administradores)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    // Endpoint para actualizar el rol de un usuario
    @PostMapping("/users/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable("id") Long userId,
            @RequestBody Map<String, String> roleData) {
        
        String newRole = roleData.get("role");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        
        user.setRole(newRole);
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }
    
    // Endpoint para eliminar un usuario
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        // Eliminar el historial de búsquedas asociado al usuario
        searchHistoryRepository.deleteByUserId(userId);
        
        // Eliminar las películas favoritas asociadas al usuario
        apiMoviesRepository.deleteByUserId(userId);
        
        // Eliminar el usuario
        userRepository.deleteById(userId);
        
        return ResponseEntity.ok().build();
    }
    
    // Endpoint para buscar películas
    @GetMapping("/movies/search")
    public ResponseEntity<?> searchMovies(
            @RequestParam String title,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam Long userId) {
        
        // Guardar búsqueda en historial
        if (title != null && !title.isEmpty()) {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setSearchTerm(title);
            history.setTimestamp(LocalDateTime.now());
            searchHistoryRepository.save(history);
        }
        
        // Buscar películas
        HashMap<String, Object> response = apiMoviesService.searchMoviesWithPagination(title, page);
        return ResponseEntity.ok(response);
    }
    
    // Endpoint para obtener detalles de una película
    @GetMapping("/movies/{imdbId}")
    public ResponseEntity<?> getMovieDetails(@PathVariable String imdbId) {
        HashMap<String, Object> movieDetails = apiMoviesService.getMovieByImbdId(imdbId);
        return ResponseEntity.ok(movieDetails);
    }
    
    // Endpoint para guardar una película como favorita
    @PostMapping("/movies/favorites")
    public ResponseEntity<?> saveFavoriteMovie(@RequestBody Map<String, Object> movieData) {
        try {
            String movieId = (String) movieData.get("movieId");
            Long userId = Long.parseLong(movieData.get("userId").toString());
            String title = (String) movieData.get("title");
            int year = Integer.parseInt(movieData.get("year").toString());
            String director = (String) movieData.get("director");
            String genre = (String) movieData.get("genre");
            String plot = (String) movieData.get("plot");
            String posterUrl = (String) movieData.get("posterUrl");
            
            apiMoviesService.saveApiMovie(movieId, userId, title, year, director, genre, plot, posterUrl);
            
            return ResponseEntity.ok(Map.of("message", "Película guardada como favorita"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Endpoint para obtener películas favoritas de un usuario
    @GetMapping("/movies/favorites/{userId}")
    public ResponseEntity<?> getFavoriteMovies(@PathVariable Long userId) {
        List<ApiMovies> favoriteMovies = apiMoviesRepository.findByUserId(userId);
        return ResponseEntity.ok(favoriteMovies);
    }
    
    // Endpoint para eliminar una película favorita
    @DeleteMapping("/movies/favorites/{id}")
    public ResponseEntity<?> removeFavoriteMovie(@PathVariable Long id) {
        try {
            apiMoviesRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Película eliminada de favoritos"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Endpoint para obtener historial de búsqueda de un usuario
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getSearchHistory(@PathVariable Long userId) {
        List<SearchHistory> searchHistory = searchHistoryRepository.findByUserId(userId);
        return ResponseEntity.ok(searchHistory);
    }

    // Endpoint para obtener historial de búsqueda de todos los usuarios
    @GetMapping("/history/all")
    public ResponseEntity<?> getAllSearchHistory() {
        try {
            List<SearchHistory> allSearchHistory = searchHistoryRepository.findAll();
            return ResponseEntity.ok(allSearchHistory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al obtener historial: " + e.getMessage()));
        }
    }
    
    // Add this new endpoint to get all favorite movies from all users
    @GetMapping("/movies/favorites/all")
    public ResponseEntity<?> getAllFavoriteMovies() {
        try {
            List<ApiMovies> allFavorites = apiMoviesRepository.findAll();
            return ResponseEntity.ok(allFavorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al obtener favoritos: " + e.getMessage()));
        }
    }
    
}