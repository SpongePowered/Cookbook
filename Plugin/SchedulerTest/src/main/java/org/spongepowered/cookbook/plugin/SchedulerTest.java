package org.spongepowered.cookbook.plugin;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Plugin(id = "SchedulerTest", name = "Test Plugin", version = "1.1")
public class SchedulerTest {

    private final int MAX_TASKS = 15;

    private RunnableTaskBody watchDog = new RunnableTaskBody("WatchDog", 250, RunnableTaskBody.TimeBase.TICK);


    @Listener
    public void handler(GameStartedServerEvent evt) {
        System.out.println("On StartedServerEvent");
        Random r = new Random();
        int Low = 20;
        int High = 100;

        List<Task> myTasks = new ArrayList<Task>();

        // setup the watch dog:
        this.watchDog.setVerbose(false);
        Sponge.getScheduler().createTaskBuilder()
                .execute(this.watchDog)
                .intervalTicks(250)
                .submit(this);

        // make some Synchronous Tasks
        for (int i = 0; i < this.MAX_TASKS; i++) {
            long tickPeriod = r.nextInt(High - Low) + Low;
            String label = "TestTask (SYNC)" + i + " [Period:" + tickPeriod + "] ";
            RunnableTaskBody runnable = new RunnableTaskBody(label, tickPeriod, RunnableTaskBody.TimeBase.TICK);
            runnable.setVerbose(true);

            Task task = Sponge.getScheduler().createTaskBuilder()
                    .execute(runnable)
                    .intervalTicks(tickPeriod)
                    .submit(this);
            myTasks.add(task);
        }

        Low = 3000;
        High = 8000;

        // make some Asynchronous Tasks
        for (int i = this.MAX_TASKS; i < (this.MAX_TASKS * 2); i++) {
            long period = r.nextInt(High - Low) + Low;
            String label = "TestTask (ASYNC)" + i + " [Period:" + period + "] ";
            RunnableTaskBody runnable = new RunnableTaskBody(label, period, RunnableTaskBody.TimeBase.WALLCLOCK);
            runnable.setVerbose(true);
            Task task = Sponge.getScheduler().createTaskBuilder()
                    .async()
                    .execute(runnable)
                    .interval(period, TimeUnit.MILLISECONDS)
                    .submit(this);
            myTasks.add(task);

        }
        System.out.println("Ending ServerStartedEvent");
    }
}
