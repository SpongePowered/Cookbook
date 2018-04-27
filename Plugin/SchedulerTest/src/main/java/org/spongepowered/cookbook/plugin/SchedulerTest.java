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

@Plugin(id = "schedulertest", name = "SchedulerTest", version = "1.2", description = "A plugin to showcase the scheduler.")
public class SchedulerTest {

    private final int MAX_TASKS = 15;

    private RunnableTaskBody watchDog = new RunnableTaskBody("WatchDog", 250, RunnableTaskBody.TimeBase.TICK);

    @Listener
    public void handler(GameStartedServerEvent evt) {
        System.out.println("On StartedServerEvent");
        Random r = new Random();
        int Low = 20;
        int High = 100;

        final List<Task> myTasks = new ArrayList<>();

        // setup the watch dog:
        this.watchDog.setVerbose(false);

        // createTaskBuilder is one of the simpler ways to register custom runnables.
        Sponge.getScheduler().createTaskBuilder()
                .execute(this.watchDog)
                .intervalTicks(250) // intervalTicks lets you run code every x ticks. Use this if you don't want to rely on real time
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

            // Here we use async to specify that the task should not be
            // executed on the main thread. Be wary of whoch code you can
            // touch from threads other than the main thread.
            Task task = Sponge.getScheduler().createTaskBuilder()
                    .async()
                    .execute(runnable)
                    .interval(period, TimeUnit.MILLISECONDS)
                    .submit(this);
            myTasks.add(task);

        }

        //We can also use lambdas for our Runnables
        Task lambdaTask = Sponge.getScheduler().createTaskBuilder()
                .async()
                .interval(1, TimeUnit.MINUTES)
                .execute(() -> System.out.println("Lambda FTW!!"))
                .submit(this);
        myTasks.add(lambdaTask);

        System.out.println("Ending ServerStartedEvent");
    }
}
