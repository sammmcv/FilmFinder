package net.codejavaspring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    // Cambiar a List<SearchHistory> para devolver múltiples resultados
    @Query("SELECT a FROM SearchHistory a WHERE a.userId = ?1")
    List<SearchHistory> findByUserId(Long userId);  // Devolver una lista

     // Eliminar el historial de búsqueda de un usuario dado su userId
    @Modifying
    @Transactional
    @Query("DELETE FROM SearchHistory s WHERE s.userId = ?1")
     void deleteByUserId(Long userId);  // Método para eliminar todas las entradas asociadas a un userId
}
