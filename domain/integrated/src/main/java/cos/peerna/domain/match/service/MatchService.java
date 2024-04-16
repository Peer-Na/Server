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
import org.apache.commons.math3.distribution.NormalDistribution;
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
            addDummyTickets();
            scheduler.scheduleJob(jobDetail, MatchJob.buildJobTrigger());
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    private void addDummyTickets() {
        NormalDistribution scoreNormalDistribution = new NormalDistribution(1000, 500);
        NormalDistribution timeNormalDistribution = new NormalDistribution(300, 100);

        for (int i = 0; i < 1000; i++) {
            double score = scoreNormalDistribution.sample();
            double time = timeNormalDistribution.sample();
            int interest = (int) (Math.random() * Category.values().length);
            Category interest1 = Category.values()[interest];
            Category interest2 = Category.values()[(interest+1) % Category.values().length];

            MatchTicket matchTicket = MatchTicket.builder()
                    .id((long) i)
                    .interest1(interest1)
                    .interest2(interest2)
                    .score((int) score)
                    .createdAt(LocalDateTime.now().minusSeconds((long) time))
                    .build();
            matchTicketRepository.save(matchTicket);
        }
        log.debug("Dummy tickets added");
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