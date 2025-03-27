package net.codejavaspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import net.codejavaspring.model.SearchHistory;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT a FROM SearchHistory a WHERE a.userId = ?1")
    List<SearchHistory> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SearchHistory s WHERE s.userId = ?1")
    void deleteByUserId(Long userId);
}
