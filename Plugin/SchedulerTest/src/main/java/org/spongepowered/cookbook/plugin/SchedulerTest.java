package org.spongepowered.cookbook.plugin;


import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.scheduler.AsynchronousScheduler;
import org.spongepowered.api.service.scheduler.SynchronousScheduler;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.util.event.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Plugin(id = "SchedulerTest", name = "Test Plugin", version = "1.0")
public class SchedulerTest {

    Game game;
    final int MAX_TASKS = 15;

    RunnableTaskBody watchDog = new RunnableTaskBody("WatchDog", 250, RunnableTaskBody.TimeBase.TICK);


    @Subscribe
    public void handler(ServerStartedEvent evt) {
        game = evt.getGame();

        Optional<PluginContainer> result = evt.getGame().getPluginManager().getPlugin("TestPlugin");
        PluginContainer pluginContainer = result.get();

        AsynchronousScheduler async = game.getAsyncScheduler();
        SynchronousScheduler sync = game.getSyncScheduler();

        System.out.println("On ServerStartedEvent");
        Random r = new Random();
        int Low = 20;
        int High = 100;

        List<Task> myTasks = new ArrayList<Task>();

        // setup the watch dog:
        watchDog.setVerbose(false);
        sync.runRepeatingTask(pluginContainer, watchDog, 250);

        // make some Synchronous Tasks
        for(int i = 0; i < MAX_TASKS; i++) {
            long tickPeriod = r.nextInt(High - Low) + Low;
            String label = "TestTask (SYNC)" + i + " [Period:" + tickPeriod + "] ";
            RunnableTaskBody task = new RunnableTaskBody(label, tickPeriod, RunnableTaskBody.TimeBase.TICK);
            task.setVerbose(true);

            Optional<Task> tmp =   sync.runRepeatingTask(pluginContainer, (Runnable) task, tickPeriod);
            if (tmp.isPresent()) {
                myTasks.add( (Task) tmp.of(tmp));
            }
        }

        Low = 3000;
        High = 8000;

        // make some Asynchronous Tasks
        for(int i = MAX_TASKS; i < ( MAX_TASKS *2); i++) {
            long period = r.nextInt(High - Low) + Low;
            String label = "TestTask (ASYNC)" + i + " [Period:" + period + "] ";
            RunnableTaskBody task = new RunnableTaskBody(label, period, RunnableTaskBody.TimeBase.WALLCLOCK);
            task.setVerbose(true);
            Optional<Task> tmp = async.runRepeatingTask(pluginContainer, (Runnable) task, TimeUnit.MILLISECONDS, period);
            if ( tmp.isPresent()) {
                myTasks.add( (Task) tmp.of(tmp));
            }

        }
        System.out.println("Ending ServerStartedEvent");
    }
}
