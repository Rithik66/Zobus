package service;

import com.adventnet.taskengine.Task;
import com.adventnet.taskengine.TaskContext;
import com.adventnet.taskengine.TaskExecutionException;
import log.MyLogger;

import java.util.logging.Level;

public class SampleTask implements Task {
    @Override
    public void executeTask(TaskContext taskContext) throws TaskExecutionException{
        MyLogger.run("TASK RUN", Level.INFO);
    }
    @Override
    public void stopTask() throws TaskExecutionException {

    }
    @Override
    public int remindTask(TaskContext taskContext) throws TaskExecutionException {
        return Task.super.remindTask(taskContext);
    }
}
