import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class Main {

	public static void main(String[] args) throws SchedulerException {
		
		// Specify the job' s details..
		JobDetail job = JobBuilder.newJob(ScheduledTestJob.class)
		.withIdentity("testJob").build();
		
		// Specify the running period of the job
		Trigger trigger = TriggerBuilder.newTrigger()
		.withSchedule(SimpleScheduleBuilder.simpleSchedule()
		.withIntervalInSeconds(60).repeatForever())
		.build();

		// Schedule the job
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		sched.start();
		sched.scheduleJob(job, trigger);

	}

}
