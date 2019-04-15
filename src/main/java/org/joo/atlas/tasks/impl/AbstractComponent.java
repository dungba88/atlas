package org.joo.atlas.tasks.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

import org.joo.atlas.tasks.Component;

public abstract class AbstractComponent implements Component {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private volatile boolean running = false;

    public boolean isStarted() {
        busySpin(10, () -> {
            return this.started.get() ^ this.running;
        });
        return this.running;
    }

    private void busySpin(int waitNanos, BooleanSupplier continueUntilFalse) {
        while (continueUntilFalse.getAsBoolean()) {
            LockSupport.parkNanos(waitNanos);
        }
    }

    @Override
    public final void start() {
        if (!this.isStarted() && started.compareAndSet(false, true)) {
            try {
                this.onStart();
                this.running = true;
            } catch (Exception ex) {
                this.started.set(false);
                this.running = false;
                throw ex;
            }
        }
    }

    @Override
    public final void stop() {
        if (this.isStarted() && started.compareAndSet(true, false)) {
            try {
                this.onStop();
                this.running = false;
            } catch (Exception e) {
                this.running = false;
                throw e;
            }
        }
    }

    protected void onStart() {
        
    }

    protected void onStop() {
        
    }
}
