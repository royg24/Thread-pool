
import org.junit.jupiter.api.*;
import tests_utils.Video;
import tests_utils.Tasks.*;
import thread_pool.ThreadPool;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;

import static tests_utils.Members.*;
import static thread_pool.ThreadPool.TasksPriority.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestThreadPool {
    ThreadPool pool = new ThreadPool(3);
    ThreadPool modifiedPool = new ThreadPool(6);
    List<Future<String>> stringTasks = new ArrayList<>();
    List<Future<Video>> videoTasks = new ArrayList<>();
    List<Future<?>> nonParamTasks = new ArrayList<>();
    List<Future<TimedTask>> timedTasks = new ArrayList<>();
    List<Future<Integer>> futures = new ArrayList<>();
    List<Integer> threadNumbers = new ArrayList<>();
    List<Future<TaskResult>> results = new ArrayList<>();

    @BeforeAll
    public static void announceTest() {
        System.out.println("Testing thread pool...");
    }

    @BeforeEach
    public void setup() {
        stringTasks.clear();
        videoTasks.clear();
        nonParamTasks.clear();
        timedTasks.clear();
        futures.clear();
        threadNumbers.clear();
    }

    @Test
    @Order(4)
    public void testSubmit1() throws InterruptedException, ExecutionException {
        nonParamTasks.add(pool.submit(new RunnableTask(), LOW));
        nonParamTasks.add(pool.submit(new RunnableTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new RunnableTask(), HIGH));
        nonParamTasks.add(pool.submit(new RunnableTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new RunnableTask(), LOW));
        nonParamTasks.add(pool.submit(new RunnableTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new RunnableTask(), HIGH));
        nonParamTasks.add(pool.submit(new RunnableTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new RunnableTask(), LOW));
        nonParamTasks.add(pool.submit(new RunnableTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new RunnableTask(), HIGH));
        nonParamTasks.add(pool.submit(new RunnableTask(), MEDIUM));

        var task1 = nonParamTasks.get(0);
        var task2 = nonParamTasks.get(1);
        var task3 = nonParamTasks.get(2);
        var task4 = nonParamTasks.get(3);
        var task5 = nonParamTasks.get(4);
        var task6 = nonParamTasks.get(5);
        var task7 = nonParamTasks.get(6);
        var task8 = nonParamTasks.get(7);
        var task9 = nonParamTasks.get(8);
        var task10 = nonParamTasks.get(9);
        var task11 = nonParamTasks.get(10);
        var task12 = nonParamTasks.get(11);

        Assertions.assertNull(task1.get());
        Assertions.assertNull(task2.get());
        Assertions.assertNull(task3.get());
        Assertions.assertNull(task4.get());
        Assertions.assertNull(task5.get());
        Assertions.assertNull(task6.get());
        Assertions.assertNull(task7.get());
        Assertions.assertNull(task8.get());
        Assertions.assertNull(task9.get());
        Assertions.assertNull(task10.get());
        Assertions.assertNull(task11.get());
        Assertions.assertNull(task12.get());
        System.out.println();
    }

    @Test
    @Order(5)
    public void testSubmit2() throws InterruptedException, ExecutionException {
        videoTasks.add(pool.submit(new RunnableTask(), HIGH, zoo));
        videoTasks.add(pool.submit(new RunnableTask(), MEDIUM, charlie));
        videoTasks.add(pool.submit(new RunnableTask(), LOW, leaveAlone));
        videoTasks.add(pool.submit(new RunnableTask(), MEDIUM, titanium));
        videoTasks.add(pool.submit(new RunnableTask(), HIGH, harlemShake));
        videoTasks.add(pool.submit(new RunnableTask(), MEDIUM, dance));

        var task1 = videoTasks.get(0);
        var task2 = videoTasks.get(1);
        var task3 = videoTasks.get(2);
        var task4 = videoTasks.get(3);
        var task5 = videoTasks.get(4);
        var task6 = videoTasks.get(5);

        Assertions.assertEquals(titanium, task4.get());
        Assertions.assertEquals(charlie, task2.get());
        Assertions.assertEquals(harlemShake, task5.get());
        Assertions.assertEquals(leaveAlone, task3.get());
        Assertions.assertEquals(dance, task6.get());
        Assertions.assertEquals(zoo, task1.get());
        System.out.println();
    }

    @Test
    @Order(6)
    public void testSubmit3() throws ExecutionException, InterruptedException {
        stringTasks.add(pool.submit(new CallableTask<>(1), MEDIUM));
        stringTasks.add(pool.submit(new CallableTask<>(2), LOW));
        stringTasks.add(pool.submit(new CallableTask<>(3), HIGH));
        stringTasks.add(pool.submit(new CallableTask<>(-19), MEDIUM));
        stringTasks.add(pool.submit(new CallableTask<>(15), HIGH));

        var task1 = stringTasks.get(0);
        var task2 = stringTasks.get(1);
        var task3 = stringTasks.get(2);
        var task4 = stringTasks.get(3);
        var task5 = stringTasks.get(4);

        Assertions.assertEquals(getMessage(1), task1.get());
        Assertions.assertEquals(getMessage(2), task2.get());
        Assertions.assertEquals(getMessage(3), task3.get());
        Assertions.assertEquals(getMessage(-19), task4.get());
        Assertions.assertEquals(getMessage(15), task5.get());
    }

    @Test
    @Order(7)
    public void testSubmit4() throws InterruptedException, ExecutionException {
        stringTasks.add(pool.submit(new CallableTask<>(543)));
        stringTasks.add(pool.submit(new CallableTask<>(89)));
        stringTasks.add(pool.submit(new CallableTask<>(-121)));
        stringTasks.add(pool.submit(new CallableTask<>(0)));
        stringTasks.add(pool.submit(new CallableTask<>(324534)));
        stringTasks.add(pool.submit(new CallableTask<>(-1)));
        stringTasks.add(pool.submit(new CallableTask<>(-999)));
        stringTasks.add(pool.submit(new CallableTask<>(17)));

        var task1 = stringTasks.get(0);
        var task2 = stringTasks.get(1);
        var task3 = stringTasks.get(2);
        var task4 = stringTasks.get(3);
        var task5 = stringTasks.get(4);
        var task6 = stringTasks.get(5);
        var task7 = stringTasks.get(6);
        var task8 = stringTasks.get(7);

        Assertions.assertEquals(getMessage(543), task1.get());
        Assertions.assertEquals(getMessage(89), task2.get());
        Assertions.assertEquals(getMessage(-121), task3.get());
        Assertions.assertEquals(getMessage(0), task4.get());
        Assertions.assertEquals(getMessage(324534), task5.get());
        Assertions.assertEquals(getMessage(-1), task6.get());
        Assertions.assertEquals(getMessage(-999), task7.get());
        Assertions.assertEquals(getMessage(17), task8.get());
    }

    @Test
    @Order(1)
    public void testThreadsLimit() throws InterruptedException, ExecutionException {
        checkPoolLimit(6);
    }

    @Test
    @Order(2)
    public void testSetNumOfThreadsLower() throws InterruptedException, ExecutionException {
        modifiedPool.setNumOfThreads(3);
        Thread.sleep(500);
        checkPoolLimit(3);
    }

    @Test
    @Order(3)
    public void testSetNumOfThreadsUpper() throws InterruptedException, ExecutionException {
        modifiedPool.setNumOfThreads(8);
        checkPoolLimit(8);
    }

    @Test
    @Order(8)
    public void testCancellation() {
        timedTasks.add(pool.submit(new TimedTask(), HIGH));
        timedTasks.add(pool.submit(new TimedTask(), MEDIUM));
        timedTasks.add(pool.submit(new TimedTask(), LOW));
        timedTasks.add(pool.submit(new TimedTask(), MEDIUM));
        timedTasks.add(pool.submit(new TimedTask(), LOW));
        timedTasks.add(pool.submit(new TimedTask(), MEDIUM));

        var task1 = timedTasks.get(0);
        var task2 = timedTasks.get(1);
        var task3 = timedTasks.get(2);
        var task4 = timedTasks.get(3);

        Assertions.assertFalse(task1.isCancelled());
        Assertions.assertFalse(task2.isCancelled());
        Assertions.assertFalse(task3.isCancelled());
        Assertions.assertFalse(task4.isCancelled());

        task1.cancel(false);
        task2.cancel(false);
        task3.cancel(false);
        task4.cancel(true);

        Assertions.assertTrue(task1.isCancelled());
        Assertions.assertTrue(task2.isCancelled());
        Assertions.assertTrue(task3.isCancelled());
        Assertions.assertTrue(task4.isCancelled());

        Assertions.assertThrows(CancellationException.class, task4::get);
    }

    @Test
    @Order(9)
    public void testIsDone() throws InterruptedException, ExecutionException {
        timedTasks.add(pool.submit(new TimedTask(), HIGH));
        timedTasks.add(pool.submit(new TimedTask(), MEDIUM));
        timedTasks.add(pool.submit(new TimedTask(), LOW));
        timedTasks.add(pool.submit(new TimedTask(), MEDIUM));
        timedTasks.add(pool.submit(new TimedTask(), LOW));
        timedTasks.add(pool.submit(new TimedTask(), MEDIUM));

        var task1 = timedTasks.get(0);
        var task2 = timedTasks.get(1);
        var task3 = timedTasks.get(2);
        var task4 = timedTasks.get(3);
        var task5 = timedTasks.get(4);
        var task6 = timedTasks.get(5);

        Assertions.assertFalse(task1.isDone());
        Assertions.assertFalse(task2.isDone());
        Assertions.assertFalse(task3.isDone());
        Assertions.assertFalse(task4.isDone());
        Assertions.assertFalse(task5.isDone());
        Assertions.assertFalse(task6.isDone());

        task1.get();
        task2.get();
        task3.get();
        task4.get();
        task5.get();
        task6.get();

        Assertions.assertTrue(task1.isDone());
        Assertions.assertTrue(task2.isDone());
        Assertions.assertTrue(task3.isDone());
        Assertions.assertTrue(task4.isDone());
        Assertions.assertTrue(task5.isDone());
        Assertions.assertTrue(task6.isDone());
    }

    @Test
    @Order(10)
    public void testGet1() throws InterruptedException, ExecutionException {
        stringTasks.add(pool.submit(new CallableTask<>(4), HIGH));
        stringTasks.add(pool.submit(new CallableTask<>(5), MEDIUM));
        stringTasks.add(pool.submit(new CallableTask<>(6), LOW));
        stringTasks.add(pool.submit(new CallableTask<>(7), MEDIUM));
        stringTasks.add(pool.submit(new CallableTask<>(8), LOW));
        stringTasks.add(pool.submit(new CallableTask<>(9), MEDIUM));
        stringTasks.add(pool.submit(new CallableTask<>(10), LOW));
        stringTasks.add(pool.submit(new CallableTask<>(11), MEDIUM));

        var task1 = stringTasks.get(0);
        var task2 = stringTasks.get(1);
        var task3 = stringTasks.get(2);
        var task4 = stringTasks.get(3);
        var task5 = stringTasks.get(4);
        var task6 = stringTasks.get(5);
        var task7 = stringTasks.get(6);
        var task8 = stringTasks.get(7);

        String value = task1.get();
        Assertions.assertTrue(task1.isDone());
        Assertions.assertEquals(getMessage(4), value);

        value = task2.get();
        Assertions.assertTrue(task2.isDone());
        Assertions.assertEquals(getMessage(5), value);

        value = task3.get();
        Assertions.assertTrue(task3.isDone());
        Assertions.assertEquals(getMessage(6), value);

        value = task4.get();
        Assertions.assertTrue(task4.isDone());
        Assertions.assertEquals(getMessage(7), value);

        value = task5.get();
        Assertions.assertTrue(task5.isDone());
        Assertions.assertEquals(getMessage(8), value);

        value = task6.get();
        Assertions.assertTrue(task6.isDone());
        Assertions.assertEquals(getMessage(9), value);

        value = task7.get();
        Assertions.assertTrue(task7.isDone());
        Assertions.assertEquals(getMessage(10), value);

        value = task8.get();
        Assertions.assertTrue(task8.isDone());
        Assertions.assertEquals(getMessage(11), value);
    }

    @Test
    @Order(11)
    public void testGet2() throws InterruptedException, ExecutionException, TimeoutException {
        nonParamTasks.add(pool.submit(new TimedTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new TimedTask(), LOW));
        nonParamTasks.add(pool.submit(new TimedTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new TimedTask(), MEDIUM));
        nonParamTasks.add(pool.submit(new TimedTask(), LOW));
        nonParamTasks.add(pool.submit(new TimedTask(), MEDIUM));

        var task1 = nonParamTasks.get(0);
        var task2 = nonParamTasks.get(1);
        var task3 = nonParamTasks.get(2);
        var task4 = nonParamTasks.get(3);
        var task5 = nonParamTasks.get(4);
        var task6 = nonParamTasks.get(5);

        Assertions.assertThrows(TimeoutException.class, () -> task6.get(50, TimeUnit.MILLISECONDS));
        Assertions.assertNull(task1.get(600, TimeUnit.MILLISECONDS));
        Assertions.assertNull(task2.get(1500, TimeUnit.MILLISECONDS));
        Assertions.assertNull(task3.get(1, TimeUnit.SECONDS));
        Assertions.assertNull(task4.get(1200000, TimeUnit.MICROSECONDS));
        Assertions.assertNull(task5.get(1000000000, TimeUnit.NANOSECONDS));
    }

    @Test
    @Order(12)
    public void testPriority() throws InterruptedException, ExecutionException {

        results.add(pool.submit(new CallableTaskT<>(3), HIGH));
        results.add(pool.submit(new CallableTaskT<>(4), MEDIUM));
        results.add(pool.submit(new CallableTaskT<>(5), HIGH));
        results.add(pool.submit(new CallableTaskT<>(6), HIGH));
        results.add(pool.submit(new CallableTaskT<>(7), LOW));
        results.add(pool.submit(new CallableTaskT<>(8), MEDIUM));
        results.add(pool.submit(new CallableTaskT<>(9), HIGH));

        var task4 = results.get(3).get();
        var task5 = results.get(4).get();
        var task6 = results.get(5).get();
        var task7 = results.get(6).get();

        Assertions.assertTrue(task4.compareTo(task5) < 0);
        Assertions.assertTrue(task4.compareTo(task6) < 0);
        Assertions.assertTrue(task7.compareTo(task5) < 0);
        Assertions.assertTrue(task7.compareTo(task6) < 0);
        Assertions.assertTrue(task6.compareTo(task5) < 0);

    }

    @Test
    @Order(13)
    public void testPauseAndResume() throws InterruptedException, ExecutionException {

        results.add(pool.submit(new CallableTaskT<>(4), MEDIUM));
        results.add(pool.submit(new CallableTaskT<>(5), MEDIUM));
        results.add(pool.submit(new CallableTaskT<>(6), MEDIUM));

        pool.pause();

        results.add(pool.submit(new CallableTaskT<>(40), MEDIUM));
        results.add(pool.submit(new CallableTaskT<>(55), MEDIUM));
        results.add(pool.submit(new CallableTaskT<>(60), MEDIUM));

        var task1 = results.get(0);
        var task2 = results.get(1);
        var task3 = results.get(2);

        Assertions.assertEquals(getMessage(4), task1.get().toString());
        Assertions.assertTrue(task1.isDone());
        Assertions.assertEquals(getMessage(5), task2.get().toString());
        Assertions.assertTrue(task2.isDone());
        Assertions.assertEquals(getMessage(6), task3.get().toString());
        Assertions.assertTrue(task3.isDone());

        Thread.sleep(1000);

        var task4 = results.get(3);
        var task5 = results.get(4);
        var task6 = results.get(5);

        Assertions.assertFalse(task4.isDone());
        Assertions.assertFalse(task5.isDone());
        Assertions.assertFalse(task6.isDone());

        pool.resume();

        Assertions.assertEquals(getMessage(40), task4.get().toString());
        Assertions.assertTrue(task4.isDone());
        Assertions.assertEquals(getMessage(55), task5.get().toString());
        Assertions.assertTrue(task5.isDone());
        Assertions.assertEquals(getMessage(60), task6.get().toString());
        Assertions.assertTrue(task6.isDone());
    }

    @Test
    @Order(14)
    public void testShutdown() throws InterruptedException, ExecutionException{
        stringTasks.add(pool.submit(new CallableTask<>(1), MEDIUM));
        stringTasks.add(pool.submit(new CallableTask<>(2), LOW));
        stringTasks.add(pool.submit(new CallableTask<>(3), HIGH));

        pool.shutDown();

        Assertions.assertThrows(RejectedExecutionException.class,
                () -> pool.submit(new CallableTask<>(4), MEDIUM));
        Assertions.assertThrows(RejectedExecutionException.class,
                () -> pool.submit(new CallableTask<>(5), LOW));
        Assertions.assertThrows(RejectedExecutionException.class,
                () -> pool.submit(new CallableTask<>(6), HIGH));

        var task1 = stringTasks.get(0);
        var task2 = stringTasks.get(1);
        var task3 = stringTasks.get(2);

        Assertions.assertEquals(getMessage(1), task1.get());
        Assertions.assertEquals(getMessage(2), task2.get());
        Assertions.assertEquals(getMessage(3), task3.get());
    }

    @Test
    @Order(15)
    public void testAwaitTermination() throws InterruptedException {
        ThreadPool pool1 = new ThreadPool(3);
        ThreadPool pool2 = new ThreadPool(3);

        timedTasks.add(pool1.submit(new TimedTask(), MEDIUM));
        timedTasks.add(pool1.submit(new TimedTask(), LOW));
        timedTasks.add(pool1.submit(new TimedTask(), MEDIUM));

        var task1 = timedTasks.get(0);
        var task2 = timedTasks.get(1);
        var task3 = timedTasks.get(2);

        pool1.shutDown();
        boolean success = pool1.awaitTermination(1, TimeUnit.SECONDS);
        Assertions.assertTrue(task1.isDone());
        Assertions.assertTrue(task2.isDone());
        Assertions.assertTrue(task3.isDone());
        Assertions.assertTrue(success);
        timedTasks.clear();

        timedTasks.add(pool2.submit(new TimedTask(), MEDIUM));
        timedTasks.add(pool2.submit(new TimedTask(), LOW));
        timedTasks.add(pool2.submit(new TimedTask(), MEDIUM));

        var task4 = timedTasks.get(0);
        var task5 = timedTasks.get(1);
        var task6 = timedTasks.get(2);

        pool2.shutDown();
        success = pool2.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assertions.assertFalse(task4.isDone());
        Assertions.assertFalse(task5.isDone());
        Assertions.assertFalse(task6.isDone());
        Assertions.assertFalse(success);

    }

    @Test
    @Order(16)
    public void testPauseAndSetNumOfThreads1() throws ExecutionException, InterruptedException {
        modifiedPool.pause();
        modifiedPool.setNumOfThreads(3);
        modifiedPool.resume();
        checkPoolLimit(6);
    }

    @Test
    @Order(17)
    public void testPauseAndSetNumOfThreads2() throws ExecutionException, InterruptedException {
        modifiedPool.pause();
        modifiedPool.setNumOfThreads(3);
        modifiedPool.resume();
        modifiedPool.setNumOfThreads(3);
        Thread.sleep(500);
        checkPoolLimit(6);
    }

    private void checkPoolLimit(int limit) throws InterruptedException, ExecutionException {
        futures.add(modifiedPool.submit(new ThreadNumberTask(), HIGH));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), MEDIUM));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), LOW));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), MEDIUM));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), LOW));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), MEDIUM));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), LOW));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), MEDIUM));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), LOW));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), MEDIUM));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), LOW));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), MEDIUM));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), LOW));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), MEDIUM));
        futures.add(modifiedPool.submit(new ThreadNumberTask(), LOW));

        threadNumbers.add(futures.get(0).get());
        threadNumbers.add(futures.get(1).get());
        threadNumbers.add(futures.get(2).get());
        threadNumbers.add(futures.get(3).get());
        threadNumbers.add(futures.get(4).get());
        threadNumbers.add(futures.get(5).get());
        threadNumbers.add(futures.get(6).get());
        threadNumbers.add(futures.get(7).get());
        threadNumbers.add(futures.get(8).get());
        threadNumbers.add(futures.get(9).get());
        threadNumbers.add(futures.get(10).get());
        threadNumbers.add(futures.get(11).get());
        threadNumbers.add(futures.get(12).get());
        threadNumbers.add(futures.get(13).get());
        threadNumbers.add(futures.get(14).get());

        checkDifference(threadNumbers, limit);
    }

    private String getMessage(int number) {
        return MessageFormat.format("Task {0} is running", number);
    }

    private static void checkDifference(List<Integer> numbers, int k) {

        numbers.sort(Integer::compareTo);

        int max = Collections.max(numbers);
        int min = Collections.min(numbers);

        Assertions.assertTrue(k > max - min);
    }
}
