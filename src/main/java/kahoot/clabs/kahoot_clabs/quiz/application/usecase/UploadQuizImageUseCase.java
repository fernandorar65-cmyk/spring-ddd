package kahoot.clabs.kahoot_clabs.quiz.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.quiz.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.shared.application.port.ImageStorage;
import kahoot.clabs.kahoot_clabs.quiz.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.quiz.domain.repository.QuizRepository;
import kahoot.clabs.kahoot_clabs.quiz.domain.valueobject.MediaType;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

@Service
public class UploadQuizImageUseCase {

    private static final int MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024;

    private final QuizRepository quizRepository;
    private final ImageStorage imageStorage;

    public UploadQuizImageUseCase(QuizRepository quizRepository, ImageStorage imageStorage) {
        this.quizRepository = quizRepository;
        this.imageStorage = imageStorage;
    }

    @Transactional
    public QuizResponse execute(
            UUID organizationId,
            UUID quizId,
            UUID questionId,
            byte[] content,
            String contentType,
            String originalFilename,
            String altText) {
        Quiz quiz = requireOwnedQuiz(organizationId, quizId);
        validateImage(content, contentType);
        String key = "quizzes/%s/questions/%s/%s%s".formatted(
                quizId, questionId, UUID.randomUUID(), extension(originalFilename, contentType));
        String url = imageStorage.upload(key, content, contentType);
        quiz.attachAsset(questionId, MediaType.IMAGE, url, null, altText, null);
        return QuizResponse.from(quizRepository.save(quiz));
    }

    private Quiz requireOwnedQuiz(UUID organizationId, UUID quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new DomainException("Quiz not found: " + quizId));
        if (!quiz.getOrganizationId().equals(organizationId)) {
            throw new DomainException("Quiz does not belong to the organization");
        }
        return quiz;
    }

    private void validateImage(byte[] content, String contentType) {
        if (content == null || content.length == 0 || content.length > MAX_IMAGE_SIZE_BYTES) {
            throw new DomainException("Image must be between 1 byte and 10 MB");
        }
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)
                && !"image/webp".equals(contentType) && !"image/gif".equals(contentType)) {
            throw new DomainException("Only JPEG, PNG, WebP, and GIF images are allowed");
        }
    }

    private String extension(String filename, String contentType) {
        if (filename != null && filename.lastIndexOf('.') >= 0) {
            return filename.substring(filename.lastIndexOf('.')).toLowerCase();
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".gif";
        };
    }
}
