package tests_utils;


import thread_pool.waitable_pq.WaitablePQ;

public class DequeueTester<E extends Comparable<E>> implements Runnable {
    WaitablePQ<E> pq;

    public DequeueTester(WaitablePQ<E> pq) {
        this.pq = pq;
    }

    @Override
    public void run() {
        try {
            pq.dequeue();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

