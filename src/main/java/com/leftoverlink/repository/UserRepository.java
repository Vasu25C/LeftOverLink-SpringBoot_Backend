package com.leftoverlink.repository;

import com.leftoverlink.model.Role;
import com.leftoverlink.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findTop10ByOrderByCreditPointsDesc();
    List<User> findTop5ByRoleOrderByCreditPointsDesc(Role role);
    List<User> findByRole(Role role);
    long countByRole(Role role); // DONOR or NGO

    @Query("SELECT COUNT(DISTINCT u.city) FROM User u WHERE u.city IS NOT NULL")
    long countDistinctCities();
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);
}
