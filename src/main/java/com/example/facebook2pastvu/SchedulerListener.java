package com.example.facebook2pastvu;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SchedulerListener implements ServletContextListener {
    private ScheduledExecutorService executor;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        long interval = 60;
        try (Database db = new Database()) {
            String val = db.getConfig("SCHEDULE_MINUTES");
            if (val != null) {
                interval = Long.parseLong(val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new SyncTask(), 0, interval, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
