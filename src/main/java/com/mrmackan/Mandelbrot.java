package com.mrmackan;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Mandelbrot set in  Java!
 */
public class Mandelbrot
{
    //picture size
    private final int WIDTH = 10240;
    private final int HEIGHT = 10240;

    private final int MAX_ITERATIONS = 5120;

    private final double RADIUS = 2;
    private final double SCALE = 2;

    //quick position changing
    private final double POS_X = 0;
    private final double POS_Y = 0;

    //quick color access
    private final double rgbRed = 40;
    private final double rgbGreen = 10;
    private final double rgbBlue = 5;

    int[][] mandelbrot;

    BufferedImage bufferedImage;

    class Worker implements Runnable
    {
        int width;
        int height;
        int rows;
        int cols;

        int startRow, endRow;
        int startCol, endCol;

        int[][] data;

        int iterations = 512;
        double scale = 1.0;
        double posX = 0, posY = 0;

        Worker(int startRow, int endRow, int startCol, int endCol, int width, int height)
        {
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
            this.width = width;
            this.height = height;

            rows = endRow - startRow;
            cols = endCol - startCol;

            data = new int[rows][cols];
        }

        @Override
        public void run()
        {
            compute();

            System.out.printf("Thread: %d DONE!\n", Thread.currentThread().getId());
        }

        private void compute()
        {
            for (int y = 0; y < rows; y++)
            {
                var mapY_value = map(y + startRow, 0, height, -1, 1);

                for (int x = 0; x < cols; x++)
                {
                    var mapX_value = map(x + startCol, 0, width, -1, 1);
                    data[y][x] = fractals(mapX_value + posX, mapY_value + posY, scale, iterations);
                }
            }
        }

        private int fractals(double x, double y, double scale, int maxIterations)
        {
            double z_Real = 0;
            double z_Imaginary = 0;
            double z_TempReal;

            double c_Real = x * scale;
            double c_Imaginary = y * scale;

            int iterations;

            for (iterations = 0; iterations < maxIterations; iterations++)
            {
                z_TempReal = (z_Real * z_Real) - (z_Imaginary * z_Imaginary) + c_Real;
                z_Imaginary = (2 * z_Real * z_Imaginary) + c_Imaginary;
                z_Real = z_TempReal;

                if (Math.sqrt((z_Real * z_Real) + (z_Imaginary * z_Imaginary)) > RADIUS)
                {
                    break;
                }
            }

            return iterations;
        }
    }

    /**
     * each worker handles a part of the image and then split on same amount of threads
     */
    public Mandelbrot()
    {
        mandelbrot = new int[WIDTH][HEIGHT];

        bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        Worker worker = new Worker(0, HEIGHT, 0, WIDTH / 2, WIDTH, HEIGHT);

        Worker worker2 = new Worker(0, HEIGHT, WIDTH / 2, WIDTH, WIDTH, HEIGHT);

        Thread thread1 = new Thread(worker);
        Thread thread2 = new Thread(worker2);

        //calculate setup time
        long startTime = System.currentTimeMillis();

        //launches the threads, the join is to sync them with the main thread to avoid exit
        thread1.start();
        thread2.start();
        try
        {
            thread1.join();
            thread2.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        // Joins the computed image part from each thread into one, y-axis is same on both threads but not x-axis

        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                if (x < WIDTH / 2)
                {
                    mandelbrot[y][x] = worker.data[y][x];
                }
                else
                {
                    mandelbrot[y][x] = worker2.data[y][x - WIDTH / 2];
                }
            }
        }

        // TODO: Join the data when using multiple threads, so far only 2 thread, next job is 4 threads
        long endTime = System.currentTimeMillis();
        System.out.println("Completion time was: " + ((double) (endTime - startTime) / 1000) + "s");

        generateImage();
        save();
    }

    private void generateImage()
    {
        int k = (HEIGHT * WIDTH) / 10;
        int index;
        double progress;

        //sets random color for the png
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                int count = mandelbrot[y][x];

                if (count < MAX_ITERATIONS)
                {
                    float n = ((float) count / MAX_ITERATIONS);
                    float frequency = 3;
                    int red = (int) ((Math.sin(rgbRed * frequency * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    int green = (int) ((Math.sin(rgbGreen * frequency * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    int blue = (int) ((Math.sin(rgbBlue * frequency * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    var color = new Color(red, green, blue);

                    bufferedImage.setRGB(x, y, color.getRGB());
                }
                else { bufferedImage.setRGB(x, y, 0); }

                progress = ((double) (y * WIDTH + x) / (HEIGHT * WIDTH));
                index = (y * WIDTH + x);
                if (index % k == 0)
                { System.out.printf("Fixing colors: %.0f%% \n", progress * 100); }
            }
        }
    }

    private void save()
    {
        // Saves mandelbrot as PNG
        File file = new File("mandelbrot.png");
        try
        {
            System.out.println("Saving picture");
            ImageIO.write(bufferedImage, "png", file);
        }
        catch (IOException exception) { System.out.println("Failed to save"); }
    }

    public static double map(double input, double minIn, double maxIn, double minOut, double maxOut)
    {
        return (input - minIn) / (maxIn - minIn) * (maxOut - minOut) + minOut;
    }

    public static void main(String[] args) { new Mandelbrot(); }
}