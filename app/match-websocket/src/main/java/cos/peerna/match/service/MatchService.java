package cos.peerna.match.service;

import cos.peerna.domain.match.model.MatchTicket;
import cos.peerna.domain.match.repository.MatchTicketRepository;
import cos.peerna.domain.user.model.Category;
import cos.peerna.domain.user.model.User;
import cos.peerna.domain.user.repository.UserRepository;
import cos.peerna.match.job.MatchJob;
import cos.peerna.support.event.room.CreateRoomEvent;
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

    public MatchTicket addTicket(Long userId, Category category) {
        if (matchTicketRepository.existsById(userId)) {
            return null;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        MatchTicket matchTicket = MatchTicket.builder()
                .id(userId)
                .interest1(category)
                .interest2(category)
                .score(user.getScore())
                .createdAt(LocalDateTime.now())
                .build();

        return matchTicketRepository.save(matchTicket);
    }

    @Async
    public void duoMatchingV3(Category category) {
        List<MatchTicket> matchTicketList = matchTicketRepository.findByInterest1OrderByScore(category);
        if (matchTicketList.size() < 2) {
            return;
        }
        while (matchTicketList.size() >= 2) {
            MatchTicket matchTicket = matchTicketList.remove(0);
            for (int i = 0; i < matchTicketList.size(); i++) {
                MatchTicket target = matchTicketList.get(i);
                if (!matchTicket.isMatchable(target))
                    break;
                if (target.isMatchable(matchTicket)) {
                    matchTicketList.remove(i);
                    matchTicketRepository.delete(matchTicket);
                    matchTicketRepository.delete(target);
//                    kafkaTemplate.send("peerna:matched", CreateRoomEvent.of(List.of(matchTicket.getId(), target.getId()), category));
                    break;
                }
            }
            matchTicket = matchTicketRepository.findById(matchTicket.getId()).orElse(null);
            if (matchTicket != null && matchTicket.isLongTermWaiting()) {
                matchTicket.rotateInterests();
                matchTicketRepository.save(matchTicket);
            }
        }
    }

    @Async
    public void duoMatchingV2(Category category) {
        List<MatchTicket> matchTicketList = matchTicketRepository.findByInterest1OrderByScore(category);
        if (matchTicketList.size() < 2) {
            return;
        }
        while (matchTicketList.size() >= 2) {
            MatchTicket matchTicket = matchTicketList.remove(0);
            for (int i = 0; i < matchTicketList.size(); i++) {
                MatchTicket target = matchTicketList.get(i);
                if (!matchTicket.isMatchable(target))
                    break;
                if (target.isMatchable(matchTicket)) {
                    matchTicketList.remove(i);
                    matchTicketRepository.delete(matchTicket);
                    matchTicketRepository.delete(target);
//                    kafkaTemplate.send("peerna:matched", CreateRoomEvent.of(List.of(matchTicket.getId(), target.getId()), category));
                    break;
                }
            }
        }
    }

    public void duoMatchingV1(Category category) {
        List<MatchTicket> matchTicketList = matchTicketRepository.findByInterest1OrderByScore(category);
        matchTicketList.addAll(matchTicketRepository.findByInterest2OrderByScore(category));
        if (matchTicketList.size() < 2) {
            return;
        }
        while (matchTicketList.size() >= 2) {
            MatchTicket matchTicket = matchTicketList.remove(0);
            for (int i = 0; i < matchTicketList.size(); i++) {
                MatchTicket target = matchTicketList.get(i);
                if (!matchTicket.isMatchable(target))
                    break;
                if (target.isMatchable(matchTicket)) {
                    matchTicketList.remove(i);
                    matchTicketRepository.delete(matchTicket);
                    matchTicketRepository.delete(target);
//                    kafkaTemplate.send("peerna:matched", CreateRoomEvent.of(List.of(matchTicket.getId(), target.getId()), category));
                    break;
                }
            }
        }
    }

    public void cancelTicket(Long userId) {
        MatchTicket matchTicket = matchTicketRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Standby not found"));
        matchTicketRepository.delete(matchTicket);
    }
}