package cy.crbook.wserver.jobs;


import javax.servlet.ServletContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cy.crbook.persist.JDBCPersistService;
import cy.crbook.persist.SetVolumeCover;
import cy.crbook.wsserver.InitListener;

public class SetVolumeCoverJob implements Job {
	
	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		ServletContext servletContext = (ServletContext) jec.getMergedJobDataMap().get(InitListener.PARAM_SERVLET_CONTEXT);
		SetVolumeCover svc = new SetVolumeCover((JDBCPersistService) servletContext.getAttribute(InitListener.PERSIST_MANAGER_KEY));
		svc.setAllCoverUrl();
	}

}
