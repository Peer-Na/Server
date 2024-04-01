package cos.peerna.domain.match.service;

import cos.peerna.domain.match.job.MatchJob;
import cos.peerna.domain.match.model.MatchTicket;
import cos.peerna.domain.match.repository.MatchTicketRepository;
import cos.peerna.domain.room.event.CreateRoomEvent;
import cos.peerna.domain.user.model.Category;
import cos.peerna.domain.user.model.User;
import cos.peerna.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final Scheduler scheduler;
    private final MatchTicketRepository matchTicketRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, CreateRoomEvent> kafkaTemplate;

    @PostConstruct
    public void scheduleJob() {
        JobDetail jobDetail = MatchJob.buildJobDetail();
        try {
            scheduler.scheduleJob(jobDetail, MatchJob.buildJobTrigger());
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    public MatchTicket findTicketById(Long userId) {
        return matchTicketRepository.findById(userId).orElse(null);
    }

    @Transactional(readOnly = true)
    public MatchTicket addTicket(Long userId, Category category) {
        if (matchTicketRepository.existsById(userId)) {
            return null;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        MatchTicket matchTicket = MatchTicket.builder()
                .id(userId)
                .category(category)
                .score(user.getScore())
                .createdAt(LocalDateTime.now())
                .build();

        return matchTicketRepository.save(matchTicket);
    }

    @Async
    public void duoMatching(Category category) {
        List<MatchTicket> matchTicketList = findTicketByCategory(category);
        if (matchTicketList.size() < 2) {
            return;
        }
        log.debug("Matching: {}", matchTicketList);
        while (matchTicketList.size() >= 2) {
            MatchTicket matchTicket = matchTicketList.remove(0);
            for (int i = 0; i < matchTicketList.size(); i++) {
                MatchTicket target = matchTicketList.get(i);
                if (!matchTicket.isMatchable(target))
                    break;
                if (target.isMatchable(matchTicket)) {
                    log.debug("Matched: {} and {}", matchTicket, target);
                    matchTicketList.remove(i);
                    matchTicketRepository.delete(matchTicket);
                    matchTicketRepository.delete(target);
                    kafkaTemplate.send("peerna:matched", CreateRoomEvent.of(List.of(matchTicket.getId(), target.getId()), category));
                    break;
                }
            }
        }
    }

    private List<MatchTicket> findTicketByCategory(Category category) {
        return matchTicketRepository.findByCategoryOrderByScore(category);
    }

    public void cancelTicket(Long userId) {
        MatchTicket matchTicket = matchTicketRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Standby not found"));
        matchTicketRepository.delete(matchTicket);
    }
}