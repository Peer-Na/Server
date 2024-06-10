package cos.peerna.match.job;

import static org.quartz.JobBuilder.newJob;

import cos.peerna.domain.user.model.Category;
import cos.peerna.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.UnableToInterruptJobException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchJob extends QuartzJobBean implements InterruptableJob {
    private static final String DEVELOPER_NAME = "송승훈";
    private static final String JOB_DESCRIPTION = "동료 매칭";
    private static final String SCHEDULE_EXPRESSION = "*/5 * * * * ?";
    private static final String JOB_IDENTITY = "Dev";
    private static final String JOB_WORK = "Work";

    private final MatchService matchService;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        long startTime = System.currentTimeMillis();

        for (Category category : Category.values()) {
            matchService.duoMatchingV3(category);
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.debug("executeInternal 함수의 수행 시간: " + executionTime + "ms");
    }

    public static Trigger buildJobTrigger() {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(SCHEDULE_EXPRESSION)).build();
    }

    public static JobDetail buildJobDetail() {
        return newJob(MatchJob.class).withIdentity("scheduleJob")
                .usingJobData(newJobDataMap(DEVELOPER_NAME, JOB_DESCRIPTION))
                .build();
    }

    private static JobDataMap newJobDataMap(String name, String work) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_IDENTITY, name);
        jobDataMap.put(JOB_WORK, work);
        return jobDataMap;
    }
}