package com.mrmackan;

public class FractalWorker implements Runnable
{
    int width;
    int height;
    int rows;
    int cols;

    int startRow, endRow;
    int startCol, endCol;

    int[][] data;

    private int iterations;
    private double radius;
    private double scale;
    private double posX;
    private double posY;

    FractalWorker(int startRow, int endRow, int startCol, int endCol, int width, int height)
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
            var mapY_value = Mandelbrot.map(y + startRow, 0, height, -1, 1);

            for (int x = 0; x < cols; x++)
            {
                var mapX_value = Mandelbrot.map(x + startCol, 0, width, -1, 1);
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

            if (Math.sqrt((z_Real * z_Real) + (z_Imaginary * z_Imaginary)) > radius)
            {
                break;
            }
        }
        return iterations;
    }

    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }

    public void setScale(double scale)
    {
        this.scale = scale;
    }

    public void setPosition(double posX, double posY)
    {
        this.posX = posX;
        this.posY = posY;
    }
}