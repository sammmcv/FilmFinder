package net.codejavaspring;

import java.util.List;
import java.io.IOException;
import java.util.HashMap;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Map;

@Controller
public class AppController {
    @Autowired
    private CustomUserDetailsService userRepository;

    @Autowired
    private ApiMoviesRepository apiMoviesRepository;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ApiMoviesService apiMoviesService;
    @Autowired
    private ApiBooksService ApiBooksService;

        @SuppressWarnings("unchecked") // por el cambio a hashmap
        @GetMapping("/consumeApiMovies")
        public String searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        
            // verifica si el usuario no ingresó un título y muestra solo el formulario
            if (title == null || title.trim().isEmpty()) {
                model.addAttribute("movieData", null);
                model.addAttribute("title", "");
                return "api_movies"; // vuelve al formulario sin mensaje
            }
        
            // llama al servicio para buscar películas
            HashMap<String, Object> response = apiMoviesService.searchMoviesWithPagination(title, page);
        
            // obtiene los datos de películas
            List<HashMap<String, Object>> movies = (List<HashMap<String, Object>>) response.get("Search");
            Integer totalResults = response.get("totalResults") != null
                ? Integer.parseInt((String) response.get("totalResults"))
                : 0;
        
            int totalPages = (int) Math.ceil(totalResults / 10.0);
        
            // agrega los atributos al modelo
            model.addAttribute("movieData", movies);
            model.addAttribute("title", title);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
        
            return "api_movies"; // vuelve a la vista
        }
        

        @GetMapping("/movie/details/{id}")
        public String getMovieDetails(@PathVariable("id") String imdbID, Model model) {
            // Obtener los detalles de la película
            HashMap<String, Object> movieDetails = apiMoviesService.getMovieByImbdId(imdbID);
            model.addAttribute("movieDetails", movieDetails);
        
            // Obtener datos del usuario
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = null;
        
            if (principal instanceof CustomUserDetails) { // user local
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                userId = userDetails.getId();
            } else if (principal instanceof DefaultOAuth2User) {// user con Google
                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;
                String email = oauth2User.getAttribute("email");
                // Obtener el usuario desde la base de datos para obtener el userId para guardar en la foreingkey de la tabla favs
                User user = userRepo.findByEmail(email);
                if (user != null) {
                    userId = user.getId(); // id guardada en la BD local
                }
            } else {
                throw new IllegalStateException("tpo de usuario no soportado: " + principal.getClass().getName());
            }
        
            // ID del usuario al modelo para que esté disponible en la vista
            model.addAttribute("userId", userId);
        
            return "api_movie_details"; // vsta con los detalles de la película individual
        }

        @GetMapping("/apiMovieFavorites")
        public String showFavoritesPage(Model model) {
            
            // Obtener datos del usuario
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = null;
            if (principal instanceof CustomUserDetails) { // user local
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                userId = userDetails.getId();
            } else if (principal instanceof DefaultOAuth2User) {// user con Google
                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;
                String email = oauth2User.getAttribute("email");
                // Obtener el usuario desde la base de datos para obtener el userId para guardar en la foreingkey de la tabla favs
                User user = userRepo.findByEmail(email);
                if (user != null) {
                    userId = user.getId(); // id guardada en la BD local
                }
            } else {
                throw new IllegalStateException("tpo de usuario no soportado: " + principal.getClass().getName());
            }
            // Obtener las películas favoritas del usuario desde la base de datos
            List<ApiMovies> favoriteMovies = apiMoviesRepository.findByUserId(userId);
            
            // Pasar los datos al modelo
            model.addAttribute("favoriteMovies", favoriteMovies);
            
            // ID del usuario al modelo para que esté disponible en la vista
            model.addAttribute("userId", userId);
            // Aquí podrías agregar lógica si es necesario (como comprobar si el usuario tiene favoritos).
            return "api_movie_favorites"; // Redirige a la vista de favoritos.
        }

        @GetMapping("/apiMovieFavoritesGeneral")
        public String showFavoritesGeneralPage(Model model) {
            // Obtener todas las películas favoritas de todos los usuarios, ordenadas por userId
            List<ApiMovies> favoriteMovies = apiMoviesRepository.findAllByOrderByUserIdAsc();
            
            // Pasar las películas al modelo
            model.addAttribute("favoriteMovies", favoriteMovies);
            
            // No es necesario pasar el userId porque ya no lo estamos filtrando por el usuario actual
            return "api_movie_favorites_general"; // Redirige a la vista de favoritos generales
        }

        
        @SuppressWarnings("unchecked")
        @GetMapping("/consumeApiBooks")
        public String searchBooks(
                @RequestParam(required = false) String title,
                Model model) {
        
            // Verifica si el usuario no ingresó un título y muestra solo el formulario
            if (title == null || title.trim().isEmpty()) {
                model.addAttribute("bookData", null);
                model.addAttribute("title", "");
                return "api_books"; // vuelve al formulario sin mensaje
            }
        
            // Llama al servicio para buscar libros
            HashMap<String, Object> response = ApiBooksService.searchBooksWithPagination(title);
        
            // Obtiene los datos de libros
            List<HashMap<String, Object>> books = (List<HashMap<String, Object>>) response.get("docs");
        
            // Agrega los atributos al modelo
            model.addAttribute("bookData", books);
            model.addAttribute("title", title);
        
            return "api_books"; // vuelve a la vista
        }
        
        @GetMapping("/book/details/{id}")
        public String getBookDetails(@PathVariable("id") String bookId, Model model) {
            // Llamar al servicio para obtener los detalles del libro
            HashMap<String, Object> bookDetails = ApiBooksService.getBookDetails(bookId);

            // Agregar los detalles del libro al modelo
            model.addAttribute("bookDetails", bookDetails);

            // Retornar el nombre del archivo HTML para los detalles del libro
            return "api_book_details"; // Vista con los detalles del libro
        }

    
    @GetMapping("") // redireccion a la pagina de inicio (sin iniciar sesion)
        public String viewHomePage() {
            return "index";
        }
    
        @GetMapping("/dashboard")
        public String dashboard(Model model, @AuthenticationPrincipal OAuth2User principal) {
            // obtener los detalles del usuario de google
            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
            model.addAttribute("picture", principal.getAttribute("picture"));
            return "dashboard"; // vista para usuarios autenticados
        }

    @GetMapping("/register") // redireccion a la pagina de registro
        public String showRegistrationForm(Model model) {
            model.addAttribute("user", new User()); // mandamos un modelo de usuario al formulario de registro
            return "signup_form";
        }

    @GetMapping("/login")
        public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
            if (error != null) {
                model.addAttribute("loginError", "User o password incorrectos, intenta de nuevo");
            }
            return "login";
        }

        @PostMapping("/saveMovie")
        public String saveMovie(@RequestParam("imbdID") String imdbID, 
                                @RequestParam("userId") Long userId,
                                @RequestParam("title") String title, 
                                @RequestParam("year") int year,
                                @RequestParam("director") String director,
                                @RequestParam("genre") String genre,
                                @RequestParam("plot") String plot,
                                @RequestParam("posterUrl") String posterUrl) {
            // Llamar al servicio para guardar la película con todos los datos
            apiMoviesService.saveApiMovie(imdbID, userId, title, year, director, genre, plot, posterUrl);
        
            // Redirigir a la página de detalles de la película
            return "redirect:/movie/details/" + imdbID; 
        }

        @PostMapping("/removeFavorite")
        public String removeFavorite(@RequestParam("id") Long favoriteId, Model model) {
            // Eliminar la película favorita por su id
            apiMoviesRepository.deleteById(favoriteId);
            return "redirect:/apiMovieFavorites"; // Redirigir a la página de favoritos
        }


    @PostMapping("/process_register") // redireccion a la pagina de registro
        public String processRegister(User user, @RequestParam("profilePicture") MultipartFile imageFile) { // para la foto de perfil
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // encoder de la contra
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setRole("USER"); // user por default
            user.setEnabled(true); // cuenta activada por default

            try {
                if (!imageFile.isEmpty()) {
                    user.setPhoto(imageFile.getBytes()); // guarda la imagen sin compresión
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            userRepo.save(user);
            return "register_success";
        }

        @GetMapping("/welcome")
        public String showWelcomePage(Model model) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof DefaultOAuth2User) {
                // Caso de usuario autenticado con Google OAuth2
                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;

                String email = oauth2User.getAttribute("email");
                String name = oauth2User.getAttribute("name");
                String photoBase64 = oauth2User.getAttribute("photoBase64");

                // Guarda los datos del usuario en la base de datos
                userRepository.saveOAuth2User(email, name);

                model.addAttribute("photoBase64", photoBase64 != null ? photoBase64 : null); // Foto o nulo
                model.addAttribute("fullName", name);
                model.addAttribute("email", email);

            } else if (principal instanceof CustomUserDetails) {
                // Caso de usuario autenticado localmente
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                String email = userDetails.getUsername();
                User user = userRepo.findByEmail(email);

                String photoBase64 = user.getPhoto() != null ? Base64.getEncoder().encodeToString(user.getPhoto()) : null;
                model.addAttribute("photoBase64", photoBase64);
                model.addAttribute("fullName", userDetails.getFullName());
                model.addAttribute("email", email);
            }

            return "welcome"; // Retorna la vista de bienvenida
        }

        @GetMapping("/users") // redireccion a la pagina de usuarios (solo admins)
        public String listUsers(Model model) {
            // Obtener todos los usuarios de la base de datos
            List<User> listUsers = userRepo.findAll();
        
            // Convertir las fotos en base64 para todos los usuarios
            listUsers.forEach(user -> {
                if (user.getPhoto() != null) {
                    String photoBase64 = Base64.getEncoder().encodeToString(user.getPhoto());
                    user.setPhotoBase64(photoBase64);
                }
            });
        
            model.addAttribute("listUsers", listUsers); // Añadir la lista de usuarios al modelo
        
            // Obtener el usuario autenticado actual
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String fullName;
        
            if (principal instanceof CustomUserDetails) {
                // Caso de usuario autenticado localmente
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                fullName = userDetails.getFullName(); // Obtener el nombre completo desde CustomUserDetails
            } else if (principal instanceof DefaultOAuth2User) {
                // Caso de usuario autenticado con Google OAuth2
                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;
                fullName = oauth2User.getAttribute("name"); // Obtener el nombre desde los atributos de OAuth2
            } else {
                // Manejo de casos inesperados
                throw new IllegalStateException("Tipo de usuario no soportado: " + principal.getClass().getName());
            }
        
            model.addAttribute("fullName", fullName); // Pasar el nombre completo al modelo
            return "users"; // Retornar la vista HTML de usuarios
        }
        

    @PostMapping("/updateUserRole/{id}") // tampoco hay pagina para actualizar usuarios
        public String updateUserRole(@PathVariable("id") Long id, @RequestParam("role") String newRole) {
            try {
                User user = userRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
                user.setRole(newRole);
                userRepo.save(user);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // si el user cambia su rol, actualiza
                if (authentication.getName().equals(user.getEmail())) {
                    CustomUserDetails updatedUserDetails = new CustomUserDetails(user);
                    Authentication newAuth = new UsernamePasswordAuthenticationToken( // nueva autenticacion debido a la actualizacion
                            updatedUserDetails,
                            authentication.getCredentials(),
                            updatedUserDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(newAuth);
                }
                return "redirect:/welcome"; // redireccion a la página de bienvenida
            } catch (Exception e) {
                return "error";
            }
        }

    @PostMapping("/deleteUser/{id}") // no hay pagina
        public String deleteUser(@PathVariable("id") Long id) {
            userRepo.deleteById(id);  // eliminar el usuario
            return "redirect:/users";  // redirige de nuevo a la lista de usuarios
        }

    @GetMapping("/editUser/{id}")
        public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userRepo.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

                    if (user.getPhoto() != null) {
                        String photoBase64 = Base64.getEncoder().encodeToString(user.getPhoto());
                        model.addAttribute("photoBase64", photoBase64);
                    }

        model.addAttribute("user", user);  // pasar el usuario al modelo
        return "edit_user";  // redirige a la página de edición
        }

    @PostMapping("/editUser/{id}")
        public String updateUser(@PathVariable("id") Long id,
                                User userDetails,
                                @RequestParam("profilePicture") MultipartFile imageFile) {
            User user = userRepo.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

            // Actualiza los datos del usuario
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail());

            // Si se ha cargado una nueva foto, actualízala
            try {
                if (!imageFile.isEmpty()) {
                    user.setPhoto(imageFile.getBytes());  // guarda la imagen sin compresión
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            userRepo.save(user);  // Guarda el usuario actualizado
            return "redirect:/users";  // Redirige de nuevo a la lista de usuarios
        }

        @PostMapping("/update_photo")
        public String updatePhoto(@RequestParam("profilePicture") MultipartFile imageFile) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                // Usuario autenticado localmente
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                String email = userDetails.getUsername();
                updateUserPhoto(email, imageFile);
            } else if (principal instanceof DefaultOAuth2User) {
                // Usuario autenticado con Google
                DefaultOAuth2User oauth2User = (DefaultOAuth2User) principal;
                String email = oauth2User.getAttribute("email");
                updateUserPhoto(email, imageFile);

                // Actualizar el SecurityContext con la nueva foto
                updateOAuth2UserPhotoInSession(oauth2User, email);
            }

            return "redirect:/welcome";
        }

        /**
         * Actualiza la foto del usuario en la base de datos.
         */
        private void updateUserPhoto(String email, MultipartFile imageFile) {
            try {
                if (!imageFile.isEmpty()) {
                    User user = userRepo.findByEmail(email);
                    if (user != null) {
                        user.setPhoto(imageFile.getBytes());
                        userRepo.save(user);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Manejar error si es necesario
            }
        }

        /**
         * Actualiza la foto de perfil en el SecurityContext para los usuarios autenticados con Google.
         */
        private void updateOAuth2UserPhotoInSession(DefaultOAuth2User oauth2User, String email) {
            User user = userRepo.findByEmail(email);
            if (user != null) {
                String photoBase64 = user.getPhoto() != null
                        ? Base64.getEncoder().encodeToString(user.getPhoto())
                        : null;

                // Clonar los atributos del usuario actual y agregar la foto actualizada
                Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
                attributes.put("photoBase64", photoBase64);

                // Crear un nuevo DefaultOAuth2User con los atributos actualizados
                DefaultOAuth2User updatedOAuth2User = new DefaultOAuth2User(
                        oauth2User.getAuthorities(),
                        attributes,
                        "email"
                );

                // Reemplazar el usuario actual en el SecurityContext
                Authentication newAuth = new OAuth2AuthenticationToken(
                        updatedOAuth2User,
                        oauth2User.getAuthorities(),
                        ((OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getAuthorizedClientRegistrationId()
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }

    }
