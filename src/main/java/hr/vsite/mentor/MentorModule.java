package hr.vsite.mentor;

import javax.inject.Singleton;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MentorModule extends AbstractModule {

	@Override 
	protected void configure() {
		bind(MentorEventBus.class);
		bind(JobFactory.class).to(GuiceJobFactory.class);
		bind(Garbageman.class);
		bind(MentorStatus.class);
	}

	@Provides
	@Singleton
	Scheduler provideScheduler(JobFactory jobFactory) throws SchedulerException {
		Scheduler scheduler = new StdSchedulerFactory("quartz.properties").getScheduler();
    	scheduler.setJobFactory(jobFactory);
		return scheduler;
	}

}
