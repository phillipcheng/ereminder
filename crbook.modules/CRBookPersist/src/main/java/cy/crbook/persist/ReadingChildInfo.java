package cy.crbook.persist;

import cy.common.entity.Reading;

public class ReadingChildInfo{
	Reading me;
	Reading firstChild;
	long totalChildBookCount;
	
	public ReadingChildInfo(Reading me, Reading firstChild, long tcbc){
		this.me = me;
		this.firstChild =firstChild ;
		this.totalChildBookCount = tcbc;
	}
	public ReadingChildInfo(Reading me){
		this.me = me;
	}
}