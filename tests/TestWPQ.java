import org.junit.jupiter.api.*;
import tests_utils.DequeueTester;
import tests_utils.EnqueueTester;
import tests_utils.Video;
import thread_pool.waitable_pq.WaitablePQ;

import static tests_utils.Members.*;

public class TestWPQ {
    private WaitablePQ<Integer> numbers;
    private WaitablePQ<Video> videos;
    private final Thread[] threads = new Thread[13];

    @BeforeAll
    public static void announceTest() {
        System.out.println("Test WPQ1...");
    }

    @BeforeEach
    public void beforeEach() {
        numbers = new WaitablePQ<>();
        videos = new WaitablePQ<>();
    }

    @Test
    public void testEnqueue() throws InterruptedException {
        threads[0] = new Thread(new EnqueueTester<>(numbers, 10));
        threads[1] = new Thread(new EnqueueTester<>(numbers, 2));
        threads[2] = new Thread(new EnqueueTester<>(numbers, 7));
        threads[3] = new Thread(new EnqueueTester<>(numbers, 12));
        threads[4] = new Thread(new EnqueueTester<>(numbers, 5));
        threads[5] = new Thread(new EnqueueTester<>(numbers, 21));
        threads[6] = new Thread(new EnqueueTester<>(numbers, -11));
        threads[7] = new Thread(new EnqueueTester<>(numbers, 33));

        for (int i = 0; i < 4; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 4; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(4, numbers.size());
        Assertions.assertEquals(2, numbers.peek());

        for (int i = 4; i < 8; i++) {
            threads[i].start();
        }

        for (int i = 4; i < 8; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(8, numbers.size());
        Assertions.assertEquals(-11, numbers.peek());

        threads[8] = new Thread(new EnqueueTester<>(videos, dance));
        threads[9] = new Thread(new EnqueueTester<>(videos, zoo));
        threads[10] = new Thread(new EnqueueTester<>(videos, harlemShake));
        threads[11] = new Thread(new EnqueueTester<>(videos, leaveAlone));
        threads[12]= new Thread(new EnqueueTester<>(videos, charlie));

        threads[8].start();
        threads[9].start();

        threads[9].join();
        threads[10].join();

        Assertions.assertEquals(2, videos.size());
        Assertions.assertEquals(dance, videos.peek());

        threads[10].start();
        threads[11].start();
        threads[12].start();

        threads[10].join();
        threads[11].join();
        threads[12].join();

        Assertions.assertEquals(5, videos.size());
        Assertions.assertEquals(leaveAlone, videos.peek());
    }

    @Test
    public void testDequeue() throws InterruptedException {
        setPQS();

        int number = 0;
        Video video = null;

        try {
            number = numbers.dequeue();
            Assertions.assertEquals(-90, number);

            number = numbers.dequeue();
            Assertions.assertEquals(-12, number);

            number = numbers.dequeue();
            Assertions.assertEquals(3, number);

            number = numbers.dequeue();
            Assertions.assertEquals(5, number);

            number = numbers.dequeue();
            Assertions.assertEquals(10, number);

            number = numbers.dequeue();
            Assertions.assertEquals(17, number);

            number = numbers.dequeue();
            Assertions.assertEquals(21, number);

            number = numbers.dequeue();
            Assertions.assertEquals(103, number);

            video = videos.dequeue();
            Assertions.assertEquals(leaveAlone, video);

            video = videos.dequeue();
            Assertions.assertEquals(harlemShake, video);

            video = videos.dequeue();
            Assertions.assertEquals(dance, video);

            video = videos.dequeue();
            Assertions.assertEquals(zoo, video);

            video = videos.dequeue();
            Assertions.assertEquals(charlie, video);
        }
        catch (Exception e) {
            System.out.println("failed to dequeue");
            e.printStackTrace();
        }
    }

    @Test
    public void testRemove() throws InterruptedException {
        setPQS();

        Assertions.assertTrue(numbers.remove(17));
        Assertions.assertEquals(7, numbers.size());
        Assertions.assertTrue(numbers.remove(10));
        Assertions.assertEquals(6, numbers.size());
        Assertions.assertFalse(numbers.remove(0));
        Assertions.assertEquals(6, numbers.size());
        Assertions.assertTrue(numbers.remove(-90));
        Assertions.assertEquals(5, numbers.size());
        Assertions.assertFalse(numbers.remove(44));
        Assertions.assertEquals(5, numbers.size());
        Assertions.assertFalse(numbers.remove(10));
        Assertions.assertEquals(5, numbers.size());

        Assertions.assertTrue(videos.remove(charlie));
        Assertions.assertEquals(4, videos.size());
        Assertions.assertTrue(videos.remove(zoo));
        Assertions.assertEquals(3, videos.size());
        Assertions.assertFalse(videos.remove(titanium));
        Assertions.assertEquals(3, videos.size());
        Assertions.assertFalse(videos.remove(reload));
        Assertions.assertEquals(3, videos.size());
        Assertions.assertTrue(videos.remove(harlemShake));
        Assertions.assertEquals(2, videos.size());
        Assertions.assertFalse(videos.remove(dontYouWorryChild));
        Assertions.assertEquals(2, videos.size());
        Assertions.assertFalse(videos.remove(harlemShake));
        Assertions.assertEquals(2, videos.size());
    }

    @Test
    public void testPeek() throws InterruptedException {
        setPQS();

        Assertions.assertEquals(-90, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(-12, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(3, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(5, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(10, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(17, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(21, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(103, numbers.peek());
        numbers.dequeue();

        Assertions.assertEquals(leaveAlone, videos.peek());
        videos.dequeue();

        Assertions.assertEquals(harlemShake, videos.peek());
        videos.dequeue();

        Assertions.assertEquals(dance, videos.peek());
        videos.dequeue();

        Assertions.assertEquals(zoo, videos.peek());
        videos.dequeue();

        Assertions.assertEquals(charlie, videos.peek());
        videos.dequeue();
    }

    @Test
    public void testSize() throws InterruptedException {
        setPQS();
        try {

            Assertions.assertEquals(8, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(7, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(6, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(5, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(4, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(3, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(2, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(1, numbers.size());
            numbers.dequeue();

            Assertions.assertEquals(0, numbers.size());

            Assertions.assertEquals(5, videos.size());
            videos.dequeue();

            Assertions.assertEquals(4, videos.size());
            videos.dequeue();

            Assertions.assertEquals(3, videos.size());
            videos.dequeue();

            Assertions.assertEquals(2, videos.size());
            videos.dequeue();

            Assertions.assertEquals(1, videos.size());
            videos.dequeue();

            Assertions.assertEquals(0, videos.size());

        } catch (Exception e) {
            System.out.println("failed to dequeue");
            e.printStackTrace();
        }
    }

    @Test
    public void testIsEmpty() {

        try {
            Assertions.assertTrue(numbers.isEmpty());

            numbers.enqueue(10);
            Assertions.assertFalse(numbers.isEmpty());

            numbers.enqueue(20);
            Assertions.assertFalse(numbers.isEmpty());

            numbers.dequeue();
            Assertions.assertFalse(numbers.isEmpty());

            numbers.enqueue(30);
            Assertions.assertFalse(numbers.isEmpty());

            numbers.dequeue();
            Assertions.assertFalse(numbers.isEmpty());

            numbers.dequeue();
            Assertions.assertTrue(numbers.isEmpty());

            Thread t = new Thread(new EnqueueTester<>(numbers, 40));
            t.start();
            t.join();
            Assertions.assertFalse(numbers.isEmpty());

            numbers.dequeue();
            Assertions.assertTrue(numbers.isEmpty());
        } catch (Exception e) {
            System.out.println("failed to dequeue");
            e.printStackTrace();
        }
    }

    @Test
    public void testSynchronization() throws InterruptedException {
        threads[0] = new Thread(new EnqueueTester<>(numbers, 10));
        threads[1] = new Thread(new DequeueTester<>(numbers));
        threads[2] = new Thread(new EnqueueTester<>(numbers, 20));
        threads[3] = new Thread(new DequeueTester<>(numbers));
        threads[4] = new Thread(new EnqueueTester<>(numbers, 30));
        threads[5] = new Thread(new EnqueueTester<>(numbers, 40));
        threads[6] = new Thread(new DequeueTester<>(numbers));

        for (int i = 0; i < 7; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 7; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(1, numbers.size());
        Assertions.assertEquals(40, numbers.peek());
    }

    @Test
    public void testBlocking() throws InterruptedException {
        threads[0] = new Thread(new EnqueueTester<>(numbers, 10));
        threads[1] = new Thread(new DequeueTester<>(numbers));

        threads[1].start();
        Assertions.assertTrue(numbers.isEmpty());
        threads[0].start();

        threads[0].join();
        threads[1].join();
        Assertions.assertTrue(numbers.isEmpty());
    }

    private void setPQS() throws InterruptedException {
        threads[0] = new Thread(new EnqueueTester<>(numbers, 5));
        threads[1] = new Thread(new EnqueueTester<>(numbers, 10));
        threads[2] = new Thread(new EnqueueTester<>(numbers, 21));
        threads[3] = new Thread(new EnqueueTester<>(numbers, -12));
        threads[4] = new Thread(new EnqueueTester<>(numbers, 103));
        threads[5] = new Thread(new EnqueueTester<>(numbers, 3));
        threads[6] = new Thread(new EnqueueTester<>(numbers, 17));
        threads[7] = new Thread(new EnqueueTester<>(numbers, -90));

        threads[8] = new Thread(new EnqueueTester<>(videos, dance));
        threads[9] = new Thread(new EnqueueTester<>(videos, zoo));
        threads[10] = new Thread(new EnqueueTester<>(videos, leaveAlone));
        threads[11] = new Thread(new EnqueueTester<>(videos, charlie));
        threads[12] = new Thread(new EnqueueTester<>(videos, harlemShake));

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
