package com.sleepkqq.sololeveling.player.service.service.player;

import static com.sleepkqq.sololeveling.player.service.model.player.enums.Assessment.E;

import com.sleepkqq.sololeveling.player.service.exception.ModelNotFoundException;
import com.sleepkqq.sololeveling.player.service.model.player.Level;
import com.sleepkqq.sololeveling.player.service.model.player.LevelDraft;
import com.sleepkqq.sololeveling.player.service.repository.player.LevelRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LevelService {

  private static final int BASE_FIRST_LEVEL = 1;
  private static final int BASE_BEGIN_EXPERIENCE = 0;

  private final LevelRepository levelRepository;
  private final CountExperienceService countExperienceService;

  @Transactional
  public Level get(UUID id) {
    return find(id).orElseThrow(() -> new ModelNotFoundException(Level.class, id));
  }

  @Transactional
  public Optional<Level> find(UUID id) {
    return levelRepository.findById(id);
  }

  public void initializePlayerLevel(LevelDraft draft) {
    initializeBaseLevel(draft);
    draft.setExperienceToNextLevel(
        countExperienceService.countPlayerExperienceToNextLevel(BASE_FIRST_LEVEL)
    );
  }

  public void initializeTopicLevel(LevelDraft draft) {
    initializeBaseLevel(draft);
    draft.setExperienceToNextLevel(
        countExperienceService.countTopicExperienceToNextLevel(BASE_FIRST_LEVEL)
    );
  }

  private void initializeBaseLevel(LevelDraft draft) {
    draft.setId(UUID.randomUUID());
    draft.setLevel(BASE_FIRST_LEVEL);
    draft.setTotalExperience(BASE_BEGIN_EXPERIENCE);
    draft.setCurrentExperience(BASE_BEGIN_EXPERIENCE);
    draft.setAssessment(E);
  }
}
