package org.joo.atlas.tasks.impl.queue;

import java.io.Serializable;

import org.joo.atlas.models.ExecutionContext;
import org.joo.atlas.models.Job;
import org.joo.atlas.tasks.TaskRouter;

import lombok.Data;

@Data
public class HazelcastRunnableJob implements Serializable, Runnable {

    private static final long serialVersionUID = -2552737194281028688L;

    private Job job;

    private ExecutionContext context;
    
    private TaskRouter router;

    @Override
    public void run() {
        
    }
}
