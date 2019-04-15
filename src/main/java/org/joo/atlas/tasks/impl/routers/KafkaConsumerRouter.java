package org.joo.atlas.tasks.impl.routers;

import org.joo.atlas.models.TaskResult;
import org.joo.atlas.tasks.TaskNotifier;
import org.joo.promise4j.Deferred;

import io.gridgo.bean.BObject;
import io.gridgo.connector.Connector;
import io.gridgo.connector.Consumer;
import io.gridgo.connector.impl.factories.DefaultConnectorFactory;
import io.gridgo.connector.kafka.KafkaConstants;
import io.gridgo.framework.impl.NonameComponentLifecycle;
import io.gridgo.framework.support.Message;

public class KafkaConsumerRouter extends NonameComponentLifecycle {

    private String endpoint;

    private transient Connector connector;

    private transient Consumer consumer;

    private TaskNotifier notifier;

    public KafkaConsumerRouter(String endpoint, TaskNotifier notifier) {
        this.endpoint = endpoint;
        this.notifier = notifier;
    }

    @Override
    public void onStart() {
        this.connector = new DefaultConnectorFactory().createConnector(endpoint);
        this.consumer = this.connector.getConsumer().orElseThrow();
        this.consumer.subscribe(this::processMessage);
        this.connector.start();
    }

    private void processMessage(Message msg, Deferred<Message, Exception> deferred) {
        var batchId = msg.headers().getString(KafkaConstants.KEY);
        var body = msg.body().asObject();
        var taskId = body.getString("taskId");
        var result = body.getObject("taskResult");
        notifier.notifyJobComplete(batchId, taskId, toTaskResult(result));
    }

    private TaskResult toTaskResult(BObject result) {
        return result.toPojo(TaskResult.class);
    }

    @Override
    public void onStop() {
        this.connector.stop();
    }
}
