package org.cld.taskmgr;

import java.util.concurrent.FutureTask;

public class RerunableFutureTask<V> extends FutureTask<V> implements  Comparable<RerunableFutureTask<V>>{

	private RerunableTask rrTask;
	
	public RerunableTask getRRTask(){
		return rrTask;
	}
	
	public RerunableFutureTask(RerunableTask rerunnable, V result) {
		super(rerunnable, result);
		this.rrTask = rerunnable;
	}

	@Override
	public int compareTo(RerunableFutureTask<V> o) {
		return rrTask.compareTo(o.getRRTask());
	}

}
