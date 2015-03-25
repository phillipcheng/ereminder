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
	        // define the job and tie it to our job's class
	        JobDetail job = JobBuilder.newJob(SetBookNumJob.class).withIdentity("job1", "group1").usingJobData(jobDataMap).build();
	        // Trigger the job to run now, and then repeat every 3 seconds
	        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startNow()
	              .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(600000L).repeatForever()).build();
	        // Tell quartz to schedule the job using our trigger
	        scheduler.scheduleJob(job, trigger);
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
