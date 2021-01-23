# Mandelbrot in java

## Table of contents
- [Mandelbrot in java](#mandelbrot-in-java)
  - [Table of contents](#table-of-contents)
  - [Some pretty cool color settings](#some-pretty-cool-color-settings)
  - [About](#about)
    - [Story behind the project](#story-behind-the-project)

## Some pretty cool color settings

```Java 
int red = (int) ((Math.sin(40*frequency*n-Math.PI/2+1)*0.5+0.5)*255);
int green = (int) ((Math.sin(10*frequency*n-Math.PI/2+1)*0.5+0.5)*255);
int blue = (int) ((Math.sin(5*frequency*n-Math.PI/2+1)*0.5+0.5)*255);
```

## About

This project is my way of both improving my knowledge in Java but also a way for me to finally get interested in programming more on my sparetime and try to learn how to develop multithreaded applications prior to the time we will do it in one of the current courses at university that im currently enrolled to.


### Story behind the project

The idea to do this came after my friend showed me his version of this in C++, which at that time did not have any multithreaded support. He showed me what all parts of the code did and we tried it out a bit. Eventually he also added multithreading to it which was really fun to later get to try out and compare the speed and results on his computer versus mine only to see how big difference not only the single core clock but the amount of threads actually can have on a process like this. That and then that we have to use Java at university made me feel like I want to try do something similar which has now gotten me to where this project is now. I can't say I have done everything on my own as my friend have helped me out as well but it is a really fun project and my goal is to understand multithreading much better in the future and hopefully manage to make this program run on as many threads as your computer has and not a set amount.

