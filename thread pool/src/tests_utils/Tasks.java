package tests_utils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class Tasks {

    private static int getThreadNumber() {
        String threadName = Thread.currentThread().getName();
        String number = threadName.replaceAll("[^0-9]", "");
        return Integer.parseInt(number);
    }

    public static class RunnableTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int index = getThreadNumber() % 6;
            System.out.println(MessageFormat.format(
                    "Task from {0}{1}{2} runs",Colors.values()[index + 1].value,
                    Thread.currentThread().getName(), Colors.RESET.value
            ));
        }
    }

    public static class CallableTask<T extends String> implements Callable<T> {
        private final int taskNumber;

        public CallableTask(int taskNumber) {
            this.taskNumber = taskNumber;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T call() throws Exception {
            return (T)MessageFormat.format("Task {0} is running", taskNumber);
        }
    }

    public static class ThreadNumberTask implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            return getThreadNumber();
        }
    }

    public static class CallableTaskT<T extends TaskResult> implements Callable<T> {
        private final int taskNumber;

        public CallableTaskT(int taskNumber) {
            this.taskNumber = taskNumber;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T call() throws Exception {
            Thread.sleep(100);
            return (T)new TaskResult(taskNumber);
        }
    }

    public static class TaskResult implements Comparable<TaskResult> {
        private final LocalDateTime executionTime = LocalDateTime.now();
        private final int taskNumber;

        public TaskResult(int taskNumber) {
            this.taskNumber = taskNumber;
        }

        @Override
        public int compareTo(TaskResult other) {
            return executionTime.compareTo(other.executionTime);
        }

        @Override
        public String toString() {
            return MessageFormat.format("Task {0} is running", taskNumber);
        }
    }

    public static class TimedTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

