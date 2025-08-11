package thread_pool.waitable_pq;

import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitablePQ<E extends Comparable<E>> {
    private final PriorityQueue<E> queue = new PriorityQueue<>();
    Semaphore writers = new Semaphore(1);
    Semaphore blocking = new Semaphore(0);
    Semaphore readers = new Semaphore(1);
    AtomicInteger numReaders = new AtomicInteger(0);

    public void enqueue(E element) throws InterruptedException {
        tryWrite();
        queue.add(element);
        unblock();
        endWrite();
    }

    public E dequeue() throws InterruptedException {
        tryRemove();
        tryWrite();
        E element = queue.poll();
        endWrite();
        return element;
    }

    public boolean remove(E element) throws InterruptedException {
        tryWrite();
        boolean removed = queue.remove(element);
        if (removed) {
            blocking.acquire();
        }
        endWrite();
        return removed;
    }

    public E peek() throws InterruptedException {
        tryRead();
        E top = queue.peek();
        endRead();
        return top;
    }

    public boolean isEmpty() throws InterruptedException {
        tryRead();
        boolean result = queue.isEmpty();
        endRead();
        return result;
    }

    public int size() throws InterruptedException {
        tryRead();
        int size = queue.size();
        endRead();
        return size;
    }

    private void tryRead() throws InterruptedException {
        readers.acquire();
        if (numReaders.getAndIncrement() == 0) {
            writers.acquire();
        }
        readers.release();
    }

    private void tryWrite() throws InterruptedException {
        writers.acquire();
    }

    private void endRead() throws InterruptedException {
        readers.acquire();
        if (numReaders.getAndDecrement() == 1) {
            writers.release();
        }
        readers.release();
    }

    private void endWrite() {
        writers.release();
    }

    private void tryRemove() throws InterruptedException {
        blocking.acquire();
    }

    private void unblock() {
        blocking.release();
    }
}
