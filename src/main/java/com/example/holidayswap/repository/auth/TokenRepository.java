package com.example.holidayswap.repository.auth;

import com.example.holidayswap.domain.entity.auth.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query(value = """
        SELECT t FROM Token t
        INNER JOIN User u ON t.user.userId = u.userId
        WHERE u.userId = :id AND t.status = 'VALID'
    """)
    List<Token> findAllValidTokenByUser(Long id);
    Optional<Token> findByValueEquals(String token);
}
