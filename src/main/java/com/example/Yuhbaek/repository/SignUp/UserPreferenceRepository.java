package com.example.Yuhbaek.repository.SignUp;

import com.example.Yuhbaek.entity.SignUp.UserPreference;
import com.example.Yuhbaek.entity.SignUp.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    Optional<UserPreference> findByUser(UserEntity user);

    Optional<UserPreference> findByUserId(Long userId);

    boolean existsByUser(UserEntity user);
}