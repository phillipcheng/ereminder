package cy.crbook.wsserver;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.DataSourceParams;
import org.cld.util.jdbc.DataSourcePool;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import cy.crbook.persist.JDBCPersistService;
import cy.crbook.wserver.jobs.SetBookNumJob;
import cy.crbook.wserver.jobs.SetVolumeCoverJob;

public class InitListener implements ServletContextListener {
	
	public static final String PERSIST_MANAGER_KEY="CRBookPersistMgr";
	public static final String PARAM_SERVLET_CONTEXT="servletContext";
	public static final String PARAM_SEARCH_STRING="searchString";
	public static final String PARAM_READING_TYPE="readingType";
	
	private static Logger logger = LogManager.getLogger("crbook.jsp");
	public static JDBCPersistService pService;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		DataSourceParams dsp = DataSourcePool.initFromProperties("mgrpersist.properties");
		pService = new JDBCPersistService(DataSourcePool.setupDataSource(dsp));
		ctx.setAttribute(PERSIST_MANAGER_KEY, pService);
		
		try{
			//Create & start the scheduler.
	        StdSchedulerFactory factory = new StdSchedulerFactory();
	        factory.initialize(sce.getServletContext().getResourceAsStream("/WEB-INF/quartz.properties"));
	        Scheduler scheduler = factory.getScheduler();
	        //pass the servlet context to the job
	        JobDataMap jobDataMap = new JobDataMap();
	        jobDataMap.put(PARAM_SERVLET_CONTEXT, sce.getServletContext());
	        //
	        JobDetail setBookNumJob = JobBuilder.newJob(SetBookNumJob.class).withIdentity("SetBookNumJob", "group1").usingJobData(jobDataMap).build();
	        Trigger trigger1 = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startNow()
	              .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(600000L).repeatForever()).build();
	        scheduler.scheduleJob(setBookNumJob, trigger1);
	        //
	        JobDetail setVolumeCoverJob = JobBuilder.newJob(SetVolumeCoverJob.class).withIdentity("SetVolumeCoverJob", "group2").usingJobData(jobDataMap).build();
	        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("trigger2", "group2").startNow()
		              .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(600000L).repeatForever()).build();
	        scheduler.scheduleJob(setVolumeCoverJob, trigger2);
	        // and start it off
	        scheduler.start();
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		
	}
}
