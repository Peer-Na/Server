package cos.peerna.domain.match.service;

import cos.peerna.domain.match.job.MatchJob;
import cos.peerna.domain.match.model.Standby;
import cos.peerna.domain.match.repository.StandbyRepository;
import cos.peerna.domain.room.event.CreateRoomEvent;
import cos.peerna.domain.user.model.Category;
import cos.peerna.global.security.dto.SessionUser;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final Scheduler scheduler;
    private final ApplicationEventPublisher eventPublisher;
    private final StandbyRepository standbyRepository;

    @PostConstruct
    public void scheduleJob() {
        JobDetail jobDetail = MatchJob.buildJobDetail();
        try {
            scheduler.scheduleJob(jobDetail, MatchJob.buildJobTrigger());
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    public Standby findStandbyById(Long userId) {
        return standbyRepository.findById(userId).orElse(null);
    }

    public Standby addStandby(SessionUser user, Category category) {
        if (standbyRepository.existsById(user.getId())) {
            return null;
        }
        Standby standby = Standby.builder()
                .id(user.getId())
                .score(user.getScore())
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        return standbyRepository.save(standby);
    }

    @Async
    public void duoMatching(Category category) {
        List<Standby> standbyList = findStandbyByCategory(category);
        if (standbyList.size() < 2) {
            return;
        }
        log.debug("Matching: {}", standbyList);
        while (standbyList.size() >= 2) {
            Standby standby = standbyList.remove(0);
            for (int i = 0; i < standbyList.size(); i++) {
                Standby target = standbyList.get(i);
                if (!standby.isMatchable(target))
                    break;
                if (target.isMatchable(standby)) {
                    log.debug("Matched: {} and {}", standby, target);
                    standbyList.remove(i);
                    standbyRepository.delete(standby);
                    standbyRepository.delete(target);
                    eventPublisher.publishEvent(CreateRoomEvent.of(new HashMap<>() {{
                        put(standby.getId(), standby.getScore());
                        put(target.getId(), target.getScore());
                    }}, category));
                    break;
                }
            }
        }
    }

    private List<Standby> findStandbyByCategory(Category category) {
        return standbyRepository.findByCategoryOrderByScore(category);
    }

    public void cancelStandby(Long userId) {
        Standby standby = standbyRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Standby not found"));
        standbyRepository.delete(standby);
    }
}