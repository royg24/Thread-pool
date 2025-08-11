package thread_pool;

import org.jetbrains.annotations.*;
import thread_pool.waitable_pq.WaitablePQ;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class ThreadPool implements Executor {
    private final WaitablePQ<Task<?>> tasksQueue = new WaitablePQ<>();
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private final AtomicBoolean isPaused = new AtomicBoolean(false);
    private final AtomicInteger threadsCounter = new AtomicInteger(0);
    private final Semaphore pauseSemaphore = new Semaphore(0);
    private final Task<?> poisonPill;
    private final Runnable pauseTask;
    private final Consumer<Void> resumeTask;
    private final Object awaitObject = new Object();
    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = Integer.MAX_VALUE;

    {
        pauseTask = () -> {
            try {
                pauseSemaphore.acquire();
            } catch (InterruptedException e) {
                // ignore
            }
        };

        resumeTask = (_) -> pauseSemaphore.release();

        poisonPill = new Task<>(() -> {
            ((TaskingThread)Thread.currentThread()).isPoisoned.set(true);return null;},
                ExtendedTasksPriority.FIRST);
    }

    public ThreadPool(@Range(from = MIN_RANGE, to = MAX_RANGE) int numOfThreads) {
        createThreads(numOfThreads);
    }

    @Override
    public void execute(@NotNull Runnable command) {
        submit(command, TasksPriority.HIGH);
    }

    public enum TasksPriority {
        LOW(ExtendedTasksPriority.LOW)
        , MEDIUM(ExtendedTasksPriority.MEDIUM)
        , HIGH(ExtendedTasksPriority.HIGH);

        private final ExtendedTasksPriority priority;

        TasksPriority(ExtendedTasksPriority priority) {
            this.priority = priority;
        }

        private ExtendedTasksPriority convert() {
            return priority;
        }

    }

    public <T> Future<T> submit(@NotNull Runnable runnable, @NotNull TasksPriority priority) {
        checkIfShutDown();
        return submit(runnable, priority , null);
    }

    public <T> Future<T> submit(@NotNull Runnable runnable, @NotNull TasksPriority priority, T value) {
        checkIfShutDown();
        return submit(Executors.callable(runnable, value), priority);
    }

    public <T> Future<T> submit(@NotNull Callable<T> callable, @NotNull TasksPriority priority) {
        checkIfShutDown();
        return addTaskToPool(callable, priority.convert());
    }

    public <T> Future<T> submit(@NotNull Callable<T> callable) {
        checkIfShutDown();
        return submit(callable, TasksPriority.MEDIUM);
    }

    public void setNumOfThreads(@Range(from = MIN_RANGE, to = MAX_RANGE)int numOfThreads) {
        checkIfShutDown();

        if (isPaused.get()) {
            return;
        }

        int diff = numOfThreads - threadsCounter.get();
        if (diff > 0) {
            createThreads(diff);
        } else {
            stopThreads(-diff, ExtendedTasksPriority.FIRST);
        }
    }

    public void pause() {
        isPaused.set(true);
        Consumer<Void> pauser = (_) ->
                addTaskToPool(Executors.callable(pauseTask, null),
                        ExtendedTasksPriority.FIRST);

        forEachThreads(pauser, null, threadsCounter.get());
    }

    public void resume() {
        isPaused.set(false);
        forEachThreads(resumeTask, null, threadsCounter.get());
    }

    public void shutDown() {
        resume();
        stopThreads(threadsCounter.get(), ExtendedTasksPriority.LAST);
        isShutdown.set(true);
    }

    public boolean awaitTermination(@Range(from = MIN_RANGE, to = MAX_RANGE) long timeout,
                                    @NotNull TimeUnit unit) throws InterruptedException {
        synchronized (awaitObject) {
            Predicate<Void> condition = (_) -> {
                boolean empty = false;
                try {
                    empty = tasksQueue.isEmpty();
                } catch (InterruptedException e) {
                    // ignore
                }
                return empty;
            };

            return waitTillCondition(condition, timeout, unit, awaitObject);
        }
    }

    private void checkIfShutDown() {
        if (isShutdown.get()) {
            throw new RejectedExecutionException("thread_pool.ThreadPool is shut down");
        }
    }

    private void createThreads(int numOfThreads) {
        threadsCounter.set(threadsCounter.get() + numOfThreads);

        Consumer<Void> threadsCreator = (_) ->{
            Thread thread = new TaskingThread();
            thread.start();
        };

        forEachThreads(threadsCreator, null, numOfThreads);
    }

    private void stopThreads(int numOfThreads, ExtendedTasksPriority pillPriority) {
        threadsCounter.set(threadsCounter.get() - numOfThreads);
        poisonPill.priority = pillPriority;
        Consumer<Task<?>> stopper = (poisonPill) -> {
            try {
                tasksQueue.enqueue(poisonPill);
            } catch (InterruptedException e) {
                // ignore
            }
        };

        forEachThreads(stopper, poisonPill, numOfThreads);
    }

    private <T> void forEachThreads(Consumer<T> operation, T parameter, int numOfThreads) {
        for (int i = 0; i < numOfThreads; i++) {
            operation.accept(parameter);
        }
    }

    private <T> Future<T> addTaskToPool(Callable<T> callable, ExtendedTasksPriority priority) {
        Task<T> task = new Task<>(callable, priority);
        try {
            tasksQueue.enqueue(task);
        } catch (InterruptedException e) {
            // ignore
        }
        return task.new FutureIMP();
    }

    private boolean waitTillCondition(Predicate<?> condition, long timeout, TimeUnit unit,
                                      Object lockObject) throws InterruptedException {
        long taskTimeout = System.nanoTime() + unit.toNanos(timeout);
        long remaining = taskTimeout - System.nanoTime();

        while (!condition.test(null) && remaining > 0) {

            remaining = taskTimeout - System.nanoTime();
            TimeUnit.NANOSECONDS.timedWait(lockObject, remaining);
        }

        return remaining > 0;
    }

    private class TaskingThread extends Thread {
        private final AtomicBoolean isPoisoned = new AtomicBoolean(false);
        @Override
        public void run() {
            while (!isPoisoned.get()) {
                try {
                    Task<?> task = tasksQueue.dequeue();
                    task.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class Task<T> implements Comparable<Task<?>> {
        private final Callable<T> taskOperation;
        private ExtendedTasksPriority priority;
        private final AtomicReference<T> returnValue = new AtomicReference<>();
        private final AtomicBoolean cancelFlag = new AtomicBoolean(false);
        private final AtomicBoolean doneFlag = new AtomicBoolean(false);
        private Exception taskFailure = null;

        public Task(@NotNull Callable<T> task, @NotNull ExtendedTasksPriority priority) {
            this.taskOperation = task;
            this.priority = priority;
        }

        @Override
        public int compareTo(Task other) {
            return Integer.compare(other.priority.value, priority.value);
        }

        public void start() {
            if (!cancelFlag.get()) {
                try {
                    returnValue.set(taskOperation.call());
                } catch (Exception e) {
                    taskFailure = e;
                }
                doneFlag.set(true);
            }
        }

        private class FutureIMP implements Future<T> {
            private final Object lockObject = new Object();

            @Override
            public T get() {
                synchronized (lockObject) {
                    checkIfTaskFailed();
                    while (!doneFlag.get() && !cancelFlag.get()) {
                        Thread.onSpinWait();
                    }
                    if (cancelFlag.get()) {
                        throw new CancellationException();
                    }
                }
                return returnValue.get();
            }

            @Override
            public T get(@Range(from = MIN_RANGE, to = MAX_RANGE) long timeout, @NotNull TimeUnit unit)
                    throws InterruptedException, TimeoutException {
                checkIfTaskFailed();
                synchronized (lockObject) {
                    boolean isTaskDone = ThreadPool.this.waitTillCondition(
                            (_) -> isDone() || isCancelled(), timeout, unit, lockObject);

                    if (isCancelled()) {
                        throw new CancellationException();
                    }
                    if (!isTaskDone) {
                        throw new TimeoutException();
                    }
                    return returnValue.get();
                }
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                synchronized (lockObject) {
                    cancelFlag.set(true);
                    try {
                        if (tasksQueue.remove(Task.this)) {
                            return true;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                }
            }

            @Override
            public boolean isCancelled() {
                synchronized (lockObject) {
                    return cancelFlag.get();
                }
            }

            @Override
            public boolean isDone() {
                synchronized (lockObject) {
                    return doneFlag.get();
                }
            }

            private void checkIfTaskFailed() {
                if (taskFailure != null) {
                    throw new RuntimeException(taskFailure);
                }
            }
        }
    }

    private enum ExtendedTasksPriority {
        LAST(Integer.MIN_VALUE) ,
        FIRST(Integer.MAX_VALUE),
        LOW(10),
        MEDIUM(20),
        HIGH(30);

        private final int value;

        ExtendedTasksPriority(int value) {
            this.value = value;
        }
    }
}
