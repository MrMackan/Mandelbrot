/*
  Author: Marcus Linné
  Id: ai8851
  Study program: DT
*/

package com.mrmackan;

public class MultithreadingTest extends Thread
{
    private Thread t;
    private String threadName;

    /**
     * creates a thread
     * @param name of thread
     */
    MultithreadingTest(String name)
    {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    /**
     * when threads are running
     */
    public void run()
    {
        System.out.println("Running " + threadName);
        try
        {
            for (int i = 4; i > 0; i--)
            {
                System.out.println("Thread: " + threadName + ", " + i);
                // Let the thread sleep for a while.
                Thread.sleep(50);
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    /**
     * starts (initializes) the thread
     */
    public void start()
    {
        System.out.println("Starting " + threadName);
        if (t == null)
        {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}

class TestThread
{

    public static void main(String args[])
    {
        MultithreadingTest T1 = new MultithreadingTest("Thread-1");
        T1.start();

        MultithreadingTest T2 = new MultithreadingTest("Thread-2");
        T2.start();
    }
}

