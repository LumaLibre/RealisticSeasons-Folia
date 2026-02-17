// Luma - Folia
package me.casperge.realisticseasons1_21_R7;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.casperge.interfaces.PaperNmsDelegate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PaperNmsDelegate1_21_R7 implements PaperNmsDelegate {
    // Taken from: https://github.com/TechnicallyCoded/FoliaLib/blob/main/platform/folia/src/main/java/com/tcoded/folialib/impl/FoliaImplementation.java
    @Override
    public @Nullable List<ScheduledTask> getAllScheduledTasks() {
        GlobalRegionScheduler globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
        AsyncScheduler asyncScheduler = Bukkit.getAsyncScheduler();

        try {
            // Global tasks
            Class<? extends GlobalRegionScheduler> globalClass = globalRegionScheduler.getClass();

            Field tasksByDeadlineField = globalClass.getDeclaredField("tasksByDeadline");
            boolean wasAccessible = tasksByDeadlineField.canAccess(globalRegionScheduler);
            tasksByDeadlineField.setAccessible(true);

            // noinspection unchecked
            Long2ObjectOpenHashMap<List<ScheduledTask>> globalTasksMap = (Long2ObjectOpenHashMap<List<ScheduledTask>>) tasksByDeadlineField.get(globalRegionScheduler);
            tasksByDeadlineField.setAccessible(wasAccessible);

            // Async tasks
            Class<? extends AsyncScheduler> asyncClass = asyncScheduler.getClass();

            Field asyncTasksField = asyncClass.getDeclaredField("tasks");
            wasAccessible = asyncTasksField.canAccess(asyncScheduler);
            asyncTasksField.setAccessible(true);

            // noinspection unchecked
            Set<ScheduledTask> asyncTasks = (Set<ScheduledTask>) asyncTasksField.get(asyncScheduler);
            asyncTasksField.setAccessible(wasAccessible);

            // Combine global tasks
            List<ScheduledTask> globalTasks = new ArrayList<>();
            for (List<ScheduledTask> list : globalTasksMap.values()) {
                globalTasks.addAll(list);
            }

            // Combine all tasks
            List<ScheduledTask> allTasks = new ArrayList<>(globalTasks.size() + asyncTasks.size());
            allTasks.addAll(globalTasks);
            allTasks.addAll(asyncTasks);
            return allTasks;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
