package br.gov.se.setc.scheduler;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ReceitaConvenioExecutionLock {
    private final AtomicBoolean running = new AtomicBoolean(false);

    public boolean tryStart() {
        return running.compareAndSet(false, true);
    }

    public void finish() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }
}
