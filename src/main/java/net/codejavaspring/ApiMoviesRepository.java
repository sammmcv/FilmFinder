package net.codejavaspring;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ApiMoviesRepository extends JpaRepository<ApiMovies, Long> {

    // Consulta para obtener una película por su movieId (que es el IMDB ID)
    @Query("SELECT a FROM ApiMovies a WHERE a.movieId = ?1")
    ApiMovies findByMovieId(String movieId);

    @Query("SELECT a FROM ApiMovies a WHERE a.userId = ?1 AND a.movieId = ?2")
    ApiMovies findByUserIdAndMovieId(Long userId, String movieId);   

     // Obtener todas las películas guardadas por un usuario
    List<ApiMovies> findByUserId(Long userId);
    // Obtener todas las películas favoritas de todos los usuarios ordenadas por userId
    List<ApiMovies> findAllByOrderByUserIdAsc();
 
    @Transactional
    @Modifying
    @Query("DELETE FROM ApiMovies a WHERE a.userId = ?1")
    void deleteByUserId(Long userId);  // Método para eliminar todas las películas asociadas a un userId

}
