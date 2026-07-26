package kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.PlayerAnswer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionPlayer;
import kahoot.clabs.kahoot_clabs.gameplay.domain.entity.SessionQuestion;
// import kahoot.clabs.kahoot_clabs.gameplay.domain.event.GameStartedEvent; // reserved: no publisher/consumers yet
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GamePin;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.GameStatus;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.AnswerOptionSnapshot;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.PlayerRank;
import kahoot.clabs.kahoot_clabs.gameplay.domain.valueobject.ResponseTime;
import kahoot.clabs.kahoot_clabs.shared.domain.AggregateRoot;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Live execution of a published quiz. This is the core domain: it owns players,
 * the question sequence and the score. It only references other contexts by id
 * (organizationId, quizId, hostUserId, quizQuestionId).
 */
public class GameSession extends AggregateRoot {

    private static final int NO_QUESTION = -1;

    private final UUID organizationId;
    private final UUID quizId;
    private final UUID hostUserId;
    private GamePin pin;

    private GameStatus status;
    private int currentQuestionIndex = NO_QUESTION;

    private final List<SessionPlayer> players = new ArrayList<>();
    private final List<SessionQuestion> questions = new ArrayList<>();

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private GameSession(UUID id, UUID organizationId, UUID quizId, UUID hostUserId, GamePin pin,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, organizationId, quizId, hostUserId, pin, false, createdAt, updatedAt);
    }

    private GameSession(
            UUID id,
            UUID organizationId,
            UUID quizId,
            UUID hostUserId,
            GamePin pin,
            boolean releasedPinAllowed,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (quizId == null) {
            throw new DomainException("Quiz id is required");
        }
        if (hostUserId == null) {
            throw new DomainException("Host user id is required");
        }
        if (pin == null && !releasedPinAllowed) {
            throw new DomainException("Game pin is required");
        }
        this.organizationId = organizationId;
        this.quizId = quizId;
        this.hostUserId = hostUserId;
        this.pin = pin;
        this.status = GameStatus.LOBBY;
    }

    public static GameSession create(UUID organizationId, UUID quizId, UUID hostUserId) {
        return new GameSession(null, organizationId, quizId, hostUserId, GamePin.random(), null, null);
    }

    public static GameSession rehydrate(
            UUID id,
            UUID organizationId,
            UUID quizId,
            UUID hostUserId,
            GamePin pin,
            GameStatus status,
            int currentQuestionIndex,
            List<SessionPlayer> players,
            List<SessionQuestion> questions,
            LocalDateTime startedAt,
            LocalDateTime finishedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        GameStatus restoredStatus = status != null ? status : GameStatus.LOBBY;
        boolean releasedPinAllowed = restoredStatus == GameStatus.FINISHED
                || restoredStatus == GameStatus.CANCELLED;
        GameSession session = new GameSession(
                id,
                organizationId,
                quizId,
                hostUserId,
                pin,
                releasedPinAllowed,
                createdAt,
                updatedAt);
        session.status = restoredStatus;
        session.currentQuestionIndex = currentQuestionIndex;
        if (players != null) {
            session.players.addAll(players);
        }
        if (questions != null) {
            session.questions.addAll(questions);
        }
        session.startedAt = startedAt;
        session.finishedAt = finishedAt;
        return session;
    }

    public SessionQuestion addQuestionSnapshot(
            UUID quizQuestionId,
            String title,
            String description,
            String questionType,
            int points,
            int timeLimitSeconds,
            List<AnswerOptionSnapshot> options) {
        ensureStatus(GameStatus.LOBBY, "Questions can only be added while the session is in the lobby");
        SessionQuestion question = SessionQuestion.snapshot(
                getId(),
                quizQuestionId,
                title,
                description,
                questionType,
                questions.size() + 1,
                points,
                timeLimitSeconds,
                options);
        questions.add(question);
        touch();
        return question;
    }

    public SessionPlayer join(UUID userId, String nickname) {
        ensureStatus(GameStatus.LOBBY, "Players can only join while the session is in the lobby");
        if (nickname != null && findPlayerByNickname(nickname).isPresent()) {
            throw new DomainException("Nickname is already taken in this session: " + nickname);
        }
        SessionPlayer player = SessionPlayer.join(getId(), userId, nickname);
        players.add(player);
        touch();
        return player;
    }

    public void start() {
        ensureStatus(GameStatus.LOBBY, "Only a session in the lobby can start");
        if (questions.isEmpty()) {
            throw new DomainException("Cannot start a session without questions");
        }
        if (players.isEmpty()) {
            throw new DomainException("Cannot start a session without players");
        }
        this.status = GameStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
        this.currentQuestionIndex = 0;
        questions.get(currentQuestionIndex).open();
        touch();
        // Domain event prepared but not wired (no pullDomainEvents / publisher / listeners yet).
        // registerEvent(new GameStartedEvent(getId(), organizationId, quizId, hostUserId, pin.value()));
    }

    public PlayerAnswer submitAnswer(UUID playerId, UUID sessionQuestionId, UUID selectedOptionId) {
        ensureStatus(GameStatus.RUNNING, "Answers are only accepted while the session is running");
        SessionQuestion question = requireOpenQuestion();
        if (!question.getId().equals(sessionQuestionId)) {
            throw new DomainException("Answers can only be submitted for the current question");
        }
        SessionPlayer player = requirePlayer(playerId);
        if (!player.isConnected()) {
            throw new DomainException("Disconnected players cannot submit answers");
        }

        boolean correct = question.isCorrectOption(selectedOptionId);
        ResponseTime responseTime = question.responseTimeAt(LocalDateTime.now());
        int awardedPoints = correct ? question.getPoints() : 0;
        PlayerAnswer answer = PlayerAnswer.of(
                question.getId(),
                player.getId(),
                selectedOptionId,
                correct,
                responseTime,
                awardedPoints);
        question.register(answer);
        player.award(awardedPoints);
        touch();
        return answer;
    }

    public void closeCurrentQuestion() {
        ensureStatus(GameStatus.RUNNING, "Only a running session has questions to close");
        requireOpenQuestion().close();
        touch();
    }

    /**
     * Opens the next question after the current one was explicitly closed.
     */
    public Optional<SessionQuestion> nextQuestion() {
        ensureStatus(GameStatus.RUNNING, "Only a running session can advance to the next question");
        SessionQuestion current = questions.get(currentQuestionIndex);
        if (current.isOpen()) {
            throw new DomainException("Current question must be closed before advancing");
        }
        if (currentQuestionIndex + 1 >= questions.size()) {
            throw new DomainException("There is no next question; finish the session instead");
        }
        currentQuestionIndex++;
        SessionQuestion next = questions.get(currentQuestionIndex);
        next.open();
        touch();
        return Optional.of(next);
    }

    public void finish() {
        if (status != GameStatus.RUNNING) {
            throw new DomainException("Only a running session can finish");
        }
        if (currentQuestion().map(SessionQuestion::isOpen).orElse(false)) {
            throw new DomainException("Current question must be closed before finishing");
        }
        this.status = GameStatus.FINISHED;
        this.finishedAt = LocalDateTime.now();
        this.pin = null;
        touch();
    }

    public void cancel() {
        if (status != GameStatus.LOBBY && status != GameStatus.RUNNING) {
            throw new DomainException("Only a lobby or running session can be cancelled");
        }
        this.status = GameStatus.CANCELLED;
        this.pin = null;
        touch();
    }

    public List<PlayerRank> leaderboard() {
        List<SessionPlayer> ordered = new ArrayList<>(players);
        ordered.sort(Comparator.comparingInt((SessionPlayer player) -> player.getScore().value()).reversed()
                .thenComparing(Comparator.comparingInt(this::correctAnswersFor).reversed())
                .thenComparingLong(this::totalResponseTimeMillisFor)
                .thenComparing(SessionPlayer::getJoinedAt));

        List<PlayerRank> ranks = new ArrayList<>(ordered.size());
        for (int index = 0; index < ordered.size(); index++) {
            SessionPlayer player = ordered.get(index);
            ranks.add(new PlayerRank(index + 1, player.getId(), player.getNickname(), player.getScore()));
        }
        return ranks;
    }

    public Optional<SessionQuestion> currentQuestion() {
        if (currentQuestionIndex == NO_QUESTION || currentQuestionIndex >= questions.size()) {
            return Optional.empty();
        }
        return Optional.of(questions.get(currentQuestionIndex));
    }

    private SessionQuestion requireOpenQuestion() {
        return currentQuestion()
                .filter(SessionQuestion::isOpen)
                .orElseThrow(() -> new DomainException("There is no open question in this session"));
    }

    private SessionPlayer requirePlayer(UUID playerId) {
        return players.stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new DomainException("Player is not part of this session: " + playerId));
    }

    private int correctAnswersFor(SessionPlayer player) {
        return (int) questions.stream()
                .flatMap(question -> question.getAnswers().stream())
                .filter(answer -> answer.getSessionPlayerId().equals(player.getId()) && answer.isCorrect())
                .count();
    }

    private long totalResponseTimeMillisFor(SessionPlayer player) {
        return questions.stream()
                .flatMap(question -> question.getAnswers().stream())
                .filter(answer -> answer.getSessionPlayerId().equals(player.getId()))
                .mapToLong(answer -> answer.getResponseTime().toMillis())
                .sum();
    }

    private Optional<SessionPlayer> findPlayerByNickname(String nickname) {
        return players.stream()
                .filter(player -> player.getNickname().equalsIgnoreCase(nickname.trim()))
                .findFirst();
    }

    private void ensureStatus(GameStatus expected, String message) {
        if (status != expected) {
            throw new DomainException(message);
        }
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getQuizId() {
        return quizId;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public GamePin getPin() {
        return pin;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public List<SessionPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<SessionQuestion> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }
}
