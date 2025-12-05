package org.uvhnael.mpbe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_recipe_favorites", 
       indexes = {
           @Index(name = "idx_user_recipe", columnList = "user_id,recipe_id"),
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_recipe_id", columnList = "recipe_id")
       },
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "recipe_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecipeFavorite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
