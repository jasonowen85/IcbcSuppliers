package com.grgbanking.supplier.common.infra;

public class TraceTaskScheduler extends WrapTaskScheduler {
	public TraceTaskScheduler(com.grgbanking.supplier.common.infra.TaskScheduler wrap) {
		super(wrap);
	}

	@Override
	public void reschedule(com.grgbanking.supplier.common.infra.Task task) {
		trace("reschedule " + task.dump(true));
		
		super.reschedule(task);
	}
	
	private final void trace(String msg) {

	}
}
