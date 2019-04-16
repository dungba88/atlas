package org.joo.atlas.tasks.impl.routers;

import org.joo.atlas.models.Job;
import org.joo.atlas.models.TaskResult;
import org.joo.atlas.tasks.TaskNotifier;
import org.joo.atlas.tasks.TaskRouter;
import org.joo.promise4j.Promise;

import io.gridgo.bean.BObject;
import io.gridgo.connector.Connector;
import io.gridgo.connector.Producer;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.connector.kafka.KafkaConstants;
import io.gridgo.framework.impl.NonameComponentLifecycle;

public class KafkaProducerRouter extends NonameComponentLifecycle implements TaskRouter {

    private static final long serialVersionUID = -6444390165983981404L;

    private String endpoint;

    private transient Connector connector;

    private transient Producer producer;

    public KafkaProducerRouter(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void onStart() {
        this.connector = new DefaultConnectorFactory().createConnector(endpoint);
        this.connector.start();
        this.producer = this.connector.getProducer().orElseThrow();
    }

    @Override
    public void onStop() {
        this.connector.stop();
    }

    @Override
    public Promise<Object, Throwable> routeJob(TaskNotifier notifier, String routingKey, Job job, TaskResult result) {
        var headers = BObject.of(KafkaConstants.KEY, routingKey);
        var body = BObject.of("taskId", job.getTaskTopo().getTaskId()) //
                          .setAny("taskResult", result != null ? result.toBObject() : null);
        return producer.sendAnyWithAck(headers, body) //
                       .then(Promise::of);
    }

    @Override
    public Promise<Object, Throwable> routeBatch(TaskNotifier notifier, String batchId) {
        var headers = BObject.of(KafkaConstants.KEY, batchId);
        return producer.sendAnyWithAck(headers, null) //
                       .then(Promise::of);
    }
}
