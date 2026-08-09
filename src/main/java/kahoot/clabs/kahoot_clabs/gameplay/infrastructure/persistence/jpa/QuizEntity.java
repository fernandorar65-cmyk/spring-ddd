package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.persistence.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
public class QuizEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    // estado de draft - published - archived - off entre otros considerar volverlo una tabla
    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;

    @Column(name = "play_count", nullable = false)
    private int playCount;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "is_template", nullable = false)
    private boolean template;

    @Column(name = "random_questions", nullable = false)
    private boolean randomQuestions;

    @Column(name = "random_answers", nullable = false)
    private boolean randomAnswers;

    @Column(name = "show_correct_answer", nullable = false)
    private boolean showCorrectAnswer;

    @Column(name = "show_ranking", nullable = false)
    private boolean showRanking;

    @Column(name = "allow_retry", nullable = false)
    private boolean allowRetry;

    @Column(name = "show_timer", nullable = false)
    private boolean showTimer;

    @Column(name = "music_enabled", nullable = false)
    private boolean musicEnabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuestionEntity> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<QuizCategoryEntity> categories = new HashSet<>();
}
