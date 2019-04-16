package org.joo.atlas.models;

import org.joo.atlas.models.impl.results.CanceledTaskResult;
import org.joo.atlas.models.impl.results.DefaultTaskResult;
import org.joo.atlas.models.impl.results.FailedTaskResult;
import org.joo.atlas.support.exceptions.RemoteException;

import io.gridgo.bean.BElement;
import io.gridgo.bean.BObject;

public interface TaskResult {

    String getId();

    TaskResultStatus getStatus();

    boolean isSuccessful();

    Throwable getCause();

    BElement getResult();

    default BObject toBObject() {
        return BObject.of("id", getId()) //
                      .setAny("result", getResult()) //
                      .setAny("status", getStatus().name()) //
                      .setAny("cause", getCause() != null ? getCause().toString() : null);
    }

    static TaskResult fromPojo(BObject result) {
        var id = result.getString("id");
        var status = TaskResultStatus.valueOf(result.getString("status"));
        switch (status) {
        case FINISHED:
            var resultObject = result.get("result");
            return new DefaultTaskResult(id, resultObject);
        case CANCELED:
            return new CanceledTaskResult(id);
        case FAILED:
            var cause = result.getString("cause", null);
            return new FailedTaskResult(id, new RemoteException(cause));
        }
        return null;
    }
}
