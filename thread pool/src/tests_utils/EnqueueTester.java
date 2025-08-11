package tests_utils;

import thread_pool.waitable_pq.WaitablePQ;

public class EnqueueTester<E extends Comparable<E>> implements Runnable {
    WaitablePQ<E> pq;
    E element;

    public EnqueueTester(WaitablePQ<E> pq, E element) {
        this.pq = pq;
        this.element = element;
    }

    @Override
    public void run() {
        try {
            pq.enqueue(element);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

