package com.mrmackan;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Mandelbrot set in  Java!
 */
public class Mandelbrot extends Thread
{
    //picture size
    private final int width = 1024;
    private final int height = 1024;

    private final int maxIterations = 1000;

    private final double radius = 2;
    private final double scale = 2;

    //quick position changing
    private final double posX = 0;
    private final double posY = 0;

    //quick color access
    private final double rgbRed = 40;
    private final double rgbGreen = 10;
    private final double rgbBlue = 5;

    public Mandelbrot(int i)
    {
        int[][] mandelbrot = new int[height][width];
        int k = (height * width) / 10;
        int index;
        double progress;

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++)
        {
            var mapY_value = map(y, 0, height, -1, 1);

            for (int x = 0; x < width; x++)
            {
                var mapX_value = map(x, 0, width, -1, 1);
                mandelbrot[y][x] = iteration(mapX_value + posX, mapY_value + posY);

                progress = ((double) (y * width + x) / (height * width));
                index = (y * width + x);
                if (index % k == 0)
                { System.out.printf("Generating mandelbrot: %.0f%% \n", progress * 100); }
            }
        }

        //sets random color for the png
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int count = mandelbrot[y][x];

                if (count < maxIterations)
                {
                    float n = ((float) count / maxIterations);
                    float frequency = 3;
                    int red = (int) ((Math.sin(rgbRed * frequency * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    int green = (int) ((Math.sin(rgbGreen * frequency * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    int blue = (int) ((Math.sin(rgbBlue * frequency * n - Math.PI / 2 + 1) * 0.5 + 0.5) * 255);
                    var color = new Color(red, green, blue);

                    bufferedImage.setRGB(x, y, color.getRGB());
                }
                else { bufferedImage.setRGB(x, y, 0); }

                progress = ((double) (y * width + x) / (height * width));
                index = (y * width + x);
                if (index % k == 0)
                { System.out.printf("Fixing colors: %.0f%% \n", progress * 100); }
            }
        }

        // Saves mandelbrot as PNG
        File file = new File("mandelbrot.png");
        try
        {
            System.out.println("Saving picture");
            ImageIO.write(bufferedImage, "png", file);
        }
        catch (IOException exception) { System.out.println("Failed to save"); }
    }

    /**
     * math method for iterations
     * @param x-coordinate
     * @param y-coordinate
     * @return amount of iterations
     */
    public int iteration(double x, double y)
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

            if (Math.sqrt((z_Real * z_Real) + (z_Imaginary * z_Imaginary)) > radius)
            {
                break;
            }
        }
        return iterations;
    }

    public static double map(double input, double minIn, double maxIn, double minOut, double maxOut)
    {
        double output = (input - minIn) / (maxIn - minIn) * (maxOut - minOut) + minOut;

        return output;
    }

    public static void main(String[] args) throws Exception
    {
        //calculate setup time
        long startTime = System.currentTimeMillis();
        int maxThreads = 24;
        int amountThreads;
        int started=0;
        for (amountThreads = 0; amountThreads < maxThreads; amountThreads++)
        {
            Mandelbrot mandelbrot = new Mandelbrot(amountThreads);

            started++;

            for (int i = 0; i <= started; i++)
            {
                mandelbrot.start();
                mandelbrot.join();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Completion time was: " + ((double) (endTime - startTime) / 1000) + "s");
    }
}