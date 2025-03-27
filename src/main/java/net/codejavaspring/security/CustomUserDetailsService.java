package net.codejavaspring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import net.codejavaspring.model.User;
import net.codejavaspring.repository.UserRepository;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
//import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService, OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Cargar el usuario desde Google con DefaultOAuth2UserService
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);
    
        // Obtener datos básicos del usuario autenticado por Google
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
    
        // Guardar o actualizar el usuario en la base de datos
        saveOAuth2User(email, name);
    
        // Obtener al usuario desde la base de datos (esto es lo importante)
        User user = userRepo.findByEmail(email); // Aquí obtenemos al usuario desde nuestra base de datos local
    
        // Depuración: Imprimir el ID y rol del usuario almacenado
        System.out.println("Saved User ID: " + user.getId() + " | Role: " + user.getRole());
    
        // Crear una lista de roles (autoridades)
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    
        // Crear un mapa mutable para los atributos
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        if (user.getPhoto() != null) {
            String photoBase64 = Base64.getEncoder().encodeToString(user.getPhoto());
            attributes.put("photoBase64", photoBase64); // Agregar la foto al mapa
        }
    
        // **Agregar el ID de la base de datos local (no el ID de Google) a los atributos**
        attributes.put("userId", user.getId()); // Aquí agregamos el ID local a los atributos
    
        // Retornar un DefaultOAuth2User con las autoridades y atributos actualizados
        return new DefaultOAuth2User(authorities, attributes, "email");
    }
    

    public void saveOAuth2User(String email, String name) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(name != null ? name.split(" ")[0] : "Nombre");
            user.setLastName(name != null && name.split(" ").length > 1 ? name.split(" ")[1] : "Lastname");
            user.setRole("ADMIN"); // Asignar rol por defecto
            user.setPassword("defaultPassword"); // Contraseña por defecto (puede ser encriptada)
            userRepo.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }
}
