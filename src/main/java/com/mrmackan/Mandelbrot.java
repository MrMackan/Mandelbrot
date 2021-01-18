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
    private final int width = 8196;
    private final int height = 8196;

    private final int maxIterations = 1000;
    private final double radius = 2;
    private final double scale = 2;

    public Mandelbrot()
    {
        int[][] mandelbrot = new int[height][width];

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++)
        {
            var mapY_value = map(y, 0, height, -1, 1);

            for (int x = 0; x < width; x++)
            {
                var mapX_value = map(x, 0, width, -1, 1);
                mandelbrot[y][x] = iteration(mapX_value, mapY_value);
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
                    //float hue= (float) map(count,0,maxIterations,0,50);
                    //var color = Color.getHSBColor(hue, 1, 1);
                    //bufferedImage.setRGB(x, y, color.getRGB());
                    float n = ((float) count/maxIterations);
                    float frequency = 3;
                    int red = (int) ((Math.sin(40*frequency*n-Math.PI/2+1)*0.5+0.5)*255);
                    int green = (int) ((Math.sin(10*frequency*n-Math.PI/2+1)*0.5+0.5)*255);
                    int blue = (int) ((Math.sin(5*frequency*n-Math.PI/2+1)*0.5+0.5)*255);
                    var color = new Color(red, green, blue);

                    bufferedImage.setRGB(x, y, color.getRGB());
                }
                else
                {
                    bufferedImage.setRGB(x, y, 0);
                }
            }
        }

        // Saves mandelbrot as PNG
        File file = new File("mandelbrot.png");

        try
        {
            System.out.println("saving");
            ImageIO.write(bufferedImage, "png", file);
        }
        catch (IOException exception)
        {
            System.out.println("Failed to save");
        }
    }

    public int iteration(double x, double y)
    {
        double z_Real = 0;
        double z_Imaginary = 0;
        double z_TempReal = z_Real;

        double c_Real = x*scale;
        double c_Imaginary = y*scale;

        int iterations;


        for (iterations = 0; iterations < maxIterations; iterations++)
        {
            z_TempReal = (z_Real*z_Real)-(z_Imaginary*z_Imaginary)+c_Real;
            z_Imaginary = (2*z_Real*z_Imaginary)+c_Imaginary;
            z_Real = z_TempReal;

            if (Math.sqrt((z_Real*z_Real)+(z_Imaginary*z_Imaginary)) > radius)
            {
                break;
            }
        }
        return iterations;
    }

    public static double map(double input, double minIn, double maxIn, double minOut, double maxOut)
    {
        double output = (input-minIn)/(maxIn-minIn)*(maxOut-minOut)+minOut;

        return output;
    }

    public static void main(String[] args)
    {
        new Mandelbrot();
    }
}
