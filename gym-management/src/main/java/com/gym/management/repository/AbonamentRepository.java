package com.gym.management.repository;

import com.gym.management.model.Abonament;
import com.gym.management.model.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AbonamentRepository extends JpaRepository<Abonament, Long> {

    @Query("SELECT COUNT(a) FROM Abonament a WHERE a.gym.id = :gymId AND a.expirationDate >= :today")
    long countActiveByGymId(@Param("gymId") Long gymId, @Param("today") LocalDate today);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Abonament a " +
           "WHERE a.customer.id = :customerId AND a.gym.id = :gymId AND a.expirationDate >= :today")
    boolean existsActiveForCustomerAndGym(
            @Param("customerId") Long customerId,
            @Param("gymId") Long gymId,
            @Param("today") LocalDate today);

    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Abonament a " +
           "WHERE a.gym.id = :gymId AND a.purchaseDate BETWEEN :start AND :end")
    BigDecimal sumRevenueByGymAndPeriod(
            @Param("gymId") Long gymId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT a.type FROM Abonament a " +
           "WHERE a.gym.id = :gymId AND a.purchaseDate BETWEEN :start AND :end " +
           "GROUP BY a.type ORDER BY COUNT(a.type) DESC")
    List<SubscriptionType> findMostPopularTypes(
            @Param("gymId") Long gymId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT DISTINCT a.customer.id FROM Abonament a " +
           "WHERE a.gym.id = :gymId AND a.purchaseDate BETWEEN :start AND :end")
    List<Long> findDistinctCustomerIdsWithPurchases(
            @Param("gymId") Long gymId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    long countByGymId(Long gymId);

    long countByCustomerId(Long customerId);

    @Query("SELECT a FROM Abonament a JOIN FETCH a.customer JOIN FETCH a.gym WHERE a.id = :id")
    Optional<Abonament> findByIdWithCustomerAndGym(@Param("id") Long id);

    @Query("SELECT a FROM Abonament a JOIN FETCH a.customer JOIN FETCH a.gym ORDER BY a.purchaseDate DESC")
    List<Abonament> findAllWithCustomerAndGym();
}
