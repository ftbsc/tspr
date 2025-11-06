package ftbsc.tspr.core;

import java.util.ArrayList;
import java.util.HashSet;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class Scheduler {

	private HashSet<Scheduler.Task> tasks;

	public Scheduler() {
		this.tasks = new HashSet<Scheduler.Task>();
	}

	public void schedule(int ticks, Runnable task) {
		this.tasks.add(new Task(ticks, task));
	}

	@SubscribeEvent
	void onTick(ClientTickEvent.Pre event) {
		ArrayList<Scheduler.Task> toRemove = new ArrayList<Scheduler.Task>();
		for (Scheduler.Task task : this.tasks) {
			task.ticks -= 1;
			if (task.ticks == 0) {
				toRemove.add(task);
				task.task.run();
			}
		}

		for (Scheduler.Task task : toRemove) {
			this.tasks.remove(task);
		}
	}

	public class Task {
		private int ticks;
		private Runnable task;

		public Task(int ticks, Runnable task) {
			this.ticks = ticks;
			this.task = task;
		}
	}
}
