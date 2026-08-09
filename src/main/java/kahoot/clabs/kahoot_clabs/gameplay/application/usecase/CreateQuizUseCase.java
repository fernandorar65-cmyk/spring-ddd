package kahoot.clabs.kahoot_clabs.gameplay.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kahoot.clabs.kahoot_clabs.gameplay.application.command.CreateQuizCommand;
import kahoot.clabs.kahoot_clabs.gameplay.application.dto.QuizResponse;
import kahoot.clabs.kahoot_clabs.gameplay.domain.aggregate.Quiz;
import kahoot.clabs.kahoot_clabs.gameplay.domain.repository.QuizRepository;

@Service
public class CreateQuizUseCase {

    private final QuizRepository quizRepository;

    public CreateQuizUseCase(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizResponse execute(UUID organizationId, CreateQuizCommand command) {
        Quiz quiz = Quiz.create(organizationId, command.title(), command.createdById());
        return QuizResponse.from(quizRepository.save(quiz));
    }
}
