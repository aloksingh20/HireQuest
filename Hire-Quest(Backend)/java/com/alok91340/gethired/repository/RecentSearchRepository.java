/**
 * 
 */
package com.alok91340.gethired.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.alok91340.gethired.entities.RecentSearch;
/**
 * @author aloksingh
 *
 */
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long>{
	
	List<RecentSearch> findDistinctTop8ByUserIdOrderByIdDesc(Long userId);
	
	@Modifying
	@Query("DELETE FROM RecentSearch r WHERE r.userId = :userId")
	void deleteAllByUsername(Long userId);

}
