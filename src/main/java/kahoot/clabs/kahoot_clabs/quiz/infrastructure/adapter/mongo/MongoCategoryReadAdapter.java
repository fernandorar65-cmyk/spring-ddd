package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import kahoot.clabs.kahoot_clabs.quiz.application.port.CategoryProjectionPort;
import kahoot.clabs.kahoot_clabs.quiz.application.port.CategoryReadPort;
import kahoot.clabs.kahoot_clabs.quiz.application.readmodel.CategoryReadModel;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.persistence.mongo.CategoryDocument;
import kahoot.clabs.kahoot_clabs.quiz.infrastructure.repository.mongo.SpringCategoryMongoRepository;

@Repository
@Profile("!test")
public class MongoCategoryReadAdapter implements CategoryReadPort, CategoryProjectionPort {

    private final SpringCategoryMongoRepository categoryMongoRepository;

    public MongoCategoryReadAdapter(SpringCategoryMongoRepository categoryMongoRepository) {
        this.categoryMongoRepository = categoryMongoRepository;
    }

    @Override
    public Optional<CategoryReadModel> findById(UUID id) {
        return categoryMongoRepository.findById(id).map(this::toReadModel);
    }

    @Override
    public List<CategoryReadModel> findByOrganizationId(UUID organizationId) {
        return categoryMongoRepository.findByOrganizationId(organizationId).stream()
                .map(this::toReadModel)
                .toList();
    }

    @Override
    public void save(CategoryReadModel readModel) {
        CategoryDocument document = new CategoryDocument();
        document.setId(readModel.id());
        document.setOrganizationId(readModel.organizationId());
        document.setName(readModel.name());
        document.setDescription(readModel.description());
        document.setColor(readModel.color());
        document.setIcon(readModel.icon());
        LocalDateTime now = LocalDateTime.now();
        document.setCreatedAt(now);
        document.setUpdatedAt(now);
        categoryMongoRepository.save(document);
    }

    @Override
    public void deleteById(UUID id) {
        categoryMongoRepository.deleteById(id);
    }

    private CategoryReadModel toReadModel(CategoryDocument document) {
        return new CategoryReadModel(
                document.getId(),
                document.getOrganizationId(),
                document.getName(),
                document.getDescription(),
                document.getColor(),
                document.getIcon());
    }
}
