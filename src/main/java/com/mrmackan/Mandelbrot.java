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
    //@formatter:off
    static
    {
        System.out.print("\n" +
                "-----------------------------------------------------\n" +
                "|                                                   |\n" +
                "|         Welcome to Marcus mandelbrot set          |\n" +
                "|                                                   |\n" +
                "-----------------------------------------------------\n\n");
    }
    //@formatter:on

    /*
     * Checks how many threads your cpu has and utilizes them all at once for the program.
     * Out commented version is if you want to choose yourself with default being 4 threads.
     */
    private final int threadCount = Runtime.getRuntime().availableProcessors();
    //private final int threadCount = 4;

    /**
     * Picture size in pixels and how many iterations should be done, iterations have an effect on the colors as well
     */
    private final int WIDTH = 10800;
    private final int HEIGHT = 10800;
    private final int MAX_ITERATIONS = 9999;

    private final double RADIUS = 2;
    private final double SCALE = 2;

    // Quick position changing
    private final double POS_X = 0;
    private final double POS_Y = 0;

    // Quick color changing and its color frequency
    private final float FREQUENCY = 20;
    private final double rgbRed = 40;
    private final double rgbGreen = 10;
    private final double rgbBlue = 5;

    int[][] mandelbrot;
    BufferedImage bufferedImage;

    // Each worker handles a part of the image and then split on same amount of threads
    public Mandelbrot()
    {
        mandelbrot = new int[WIDTH][HEIGHT];

        bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        int k = WIDTH / threadCount;

        /**
         * Creates the threads, and the loop creates and assigns each worker with
         * certain values for each worker based on the amount of workers created.
         * This is so that it can run async aka synchronized multithreading their respective workers
         */
        Thread[] threads = new Thread[threadCount];

        for (int index = 0; index < threads.length; index++)
        {
            FractalWorker worker = new FractalWorker(0, HEIGHT, k * index, k * (index + 1), index);
            worker.setIterations(MAX_ITERATIONS);
            worker.setRadius(RADIUS);
            worker.setScale(SCALE);
            worker.setPosition(POS_X, POS_Y);
            threads[index] = new Thread(worker);
        }

        // Calculates the setup time
        long startTime = System.currentTimeMillis();

        /*
         * Launches the threads basically
         */
        for (Thread t : threads)
        {
            t.start();
        }

        /**
         * The join is to sync the worker threads with the head application thread,
         * this is to avoid a wrongful termination of any of the threads while they are not async,
         * this is unless they've been synchronized so their cin this case computation has been "stored"
         */
        for (Thread t : threads)
        {
            try { t.join(); }

            catch (InterruptedException e) { e.printStackTrace(); }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n---------------------------------------------\n");
        System.out.println("Computation completion time was: " + ((double) (endTime - startTime) / 1000) + "s\n");

        generateImage();
        save();
    }

    public static double map(double input, double minIn, double maxIn, double minOut, double maxOut)
    {
        return (input - minIn) / (maxIn - minIn) * (maxOut - minOut) + minOut;
    }

    private void generateImage()
    {
        int k = (HEIGHT * WIDTH) / 10;
        int index;
        double progress;

        // Sets color for the png based on the rgb in the beginning of this file. Frequency is for the sinus-wave
        for (int y = 0; y < HEIGHT; y++)
        {
            for (int x = 0; x < WIDTH; x++)
            {
                int count = mandelbrot[y][x];

                if (count < MAX_ITERATIONS)
                {
                    float n = ((float) count / MAX_ITERATIONS);

                    int red = (int) ((Math.sin(rgbRed * FREQUENCY * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    int green = (int) ((Math.sin(rgbGreen * FREQUENCY * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    int blue = (int) ((Math.sin(rgbBlue * FREQUENCY * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    var color = new Color(red, green, blue);

                    bufferedImage.setRGB(x, y, color.getRGB());
                }
                else { bufferedImage.setRGB(x, y, 0); }

                progress = ((double) (y * WIDTH + x) / (HEIGHT * WIDTH));
                index = (y * WIDTH + x);

                if (index % k == 0) { System.out.printf("Fixing colors: %.0f%% \n", progress * 100); }
            }
        }
    }

    private void save()
    {
        // Saves mandelbrot as PNG
        File file = new File("mandelbrot.png");
        try
        {
            System.out.println("Colors fixed: 100% now saving picture");
            ImageIO.write(bufferedImage, "png", file);
            System.out.println("\nThe image has been saved");
        }
        catch (IOException exception) { System.out.println("Failed to save"); }
    }

    private class FractalWorker implements Runnable
    {
        private int startRow, endRow;
        private int startCol, endCol;
        private int id;

        private int iterations;
        private double radius;
        private double scale;
        private double posX;
        private double posY;

        FractalWorker(int startRow, int endRow, int startCol, int endCol, int id)
        {
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
            this.id = id;
        }

        @Override
        public void run()
        {
            compute();

            System.out.printf("Thread: %d DONE!\n", id);
        }

        private void compute()
        {
            for (int y = startRow; y < endRow; y++)
            {
                var mapY_value = Mandelbrot.map(y, 0, HEIGHT, -1, 1);

                for (int x = startCol; x < endCol; x++)
                {
                    var mapX_value = Mandelbrot.map(x, 0, WIDTH, -1, 1);
                    mandelbrot[y][x] = fractals(mapX_value + posX, mapY_value + posY, scale, iterations);
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

                if (Math.sqrt((z_Real * z_Real) + (z_Imaginary * z_Imaginary)) > radius) { break; }
            }
            return iterations;
        }

        public void setIterations(int iterations) { this.iterations = iterations; }

        public void setRadius(double radius) { this.radius = radius; }

        public void setScale(double scale) { this.scale = scale; }

        public void setPosition(double posX, double posY)
        {
            this.posX = posX;
            this.posY = posY;
        }
    }

    public static void main(String[] args) { new Mandelbrot(); }
}