# 🧵 ThreadPool

A custom Java Thread Pool implementation that implements the standard `Executor` interface, featuring:

- Task prioritization using a **Waitable Priority Queue**  
- **Dynamic thread scaling** (increase/decrease worker count at runtime)  
- **Pause & Resume** functionality  
- **Graceful shutdown** with task completion waiting, implemented via the **Poison Pill** technique  
- **Future** support for task results and cancellation

---

## Table of Contents

- [Features](#features)  
- [Project Structure](#project-structure)  
- [API Overview](#api-overview)  
  - [ThreadPool API](#threadpool-api)  
  - [WaitablePQ API](#waitablepq-api)  
- [UML Class Diagram](#uml-class-diagram)  
- [Testing](#testing)  
- [Usage Example](#usage-example)  
- [Credits](#credits)  

---

## Features

- **Custom Priority Queue**  
  Tasks are stored in a `WaitablePQ` with multiple priority levels: `LOW`, `MEDIUM`, `HIGH`.

- **Dynamic Thread Management**  
  Increase or decrease worker threads while running.

- **Pause & Resume Execution**  
  Temporarily halt task execution and later resume without losing tasks.

- **Graceful Shutdown with Poison Pill Technique**  
  Special “poison pill” tasks are enqueued to signal threads to stop. When a thread dequeues one, it terminates cleanly after finishing current tasks, ensuring no new tasks start after shutdown.

- **Future-based API**  
  Submit tasks and retrieve results asynchronously via `Future`.

---

## Project Structure

- 📁 thread pool/
  - 📁 src/
    - 📁 tests_utils/ `helper classes for testing`
    - 📁 thread_pool/ `main thread pool source code`
      - 📄 ThreadPool.java
      - 📁 waitable_pq/
        - 📄 WaitablePQ.java
  - 📁 tests/ `JUnit test classes`
    - 📄 TestThreadPool.java
    - 📄 TestWPQ.java

---

## API Overview

### ThreadPool API

```java
ThreadPool(int numberOfThreads)
```
- Creates a thread pool with the specified number of worker threads.

```java
public void execute(Runnable command)  //Legacy method from the Executor interface.  
```
- Submits a Runnable task with high priority for execution.

```java
public <T> Future<T> submit(Runnable runnable, TasksPriority priority)
``` 
 - Submits a Runnable task with the given priority and returns a Future.

```java
public <T> Future<T> submit(Runnable runnable, TasksPriority priority, T value)
```
 - Submits a Runnable task with the given priority and a result value.

```java
public <T> Future<T> submit(Callable<T> callable, TasksPriority priority)
```
 - Submits a Callable task with the specified priority.

```java
public <T> Future<T> submit(Callable<T> callable)`
``` 
- Submits a Callable task with medium priority.

```java
public void setNumOfThreads(int numOfThreads)
```  
 - Adjusts the number of worker threads dynamically.

```java
public void pause()
```  
 - Pauses the execution of tasks.

```java
public void resume()
```  
 - Resumes execution of paused tasks.

```java
public void shutDown()
```  
 - Initiates a graceful shutdown, stopping new tasks and finishing queued ones.

```java
public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
```  
-  Waits up to the specified timeout for all tasks to complete after shutdown.

---

### WaitablePQ API

```java
public void enqueue(E element) throws InterruptedException
```  
 - Adds an element to the priority queue, blocking if necessary.

```java
public E dequeue() throws InterruptedException
```  
 - Removes and returns the highest priority element, blocking if the queue is empty.

```java
public boolean remove(E element) throws InterruptedException
```  
 - Removes a specific element from the queue if present.

---

## UML Class Diagram

### [View Class Diagram (PDF)](docs/Thread%20Pool%20Class%20Diagram.pdf)
<img width="894" height="806" alt="image" src="https://github.com/user-attachments/assets/325daec0-b945-4ba7-b4f6-17007968996c" />

---

## Testing

This project includes **JUnit tests** to verify the correctness of the thread pool and its components.

- Test classes are located in the `tests/` directory.
- Use your IDE or build tool (e.g., Maven, Gradle) to run the tests.

### Example Test Classes

- `TestThreadPool.java` — tests core thread pool functionality  
- `TestWPQ.java` — tests the waitable priority queue behavior

---

## Usage Example

```java
import thread_pool.ThreadPool;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws Exception {
        ThreadPool pool = new ThreadPool(4); // Start with 4 worker threads

        // Submit a simple task
        Future<String> result = pool.submit(() -> {
            Thread.sleep(1000);
            return "High priority task";
        }, ThreadPool.TasksPriority.HIGH);

        System.out.println(result.get()); // Wait for result

        // Pause execution
        pool.pause();

        // Resume execution
        pool.resume();

        // Change number of threads
        pool.setNumOfThreads(6);

        // Shutdown gracefully
        pool.shutDown();
        pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
    }
}
```

---

## Credits

- Developed by **Roy Goldhar**  
- Inspired by Java’s standard `Executor` framework.
