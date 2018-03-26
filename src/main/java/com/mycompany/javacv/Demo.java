package com.mycompany.javacv;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import javax.swing.*;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;

public class Demo {

    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException, IOException {

        
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("FerrisWheel640.mp4");
        grabber.start();

        CanvasFrame canvasFrame = new CanvasFrame("Extracted Frame", 1);
        canvasFrame.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("Out.mp4", grabber.getImageWidth(), grabber.getImageHeight());
        recorder.setVideoBitrate(4000000);
        
        recorder.start();
        
        
        // Exit the example when the canvas frame is closed
        canvasFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        long delay = Math.round(1000d / grabber.getFrameRate());

        // Read frame by frame, stop early if the display window is closed
        Frame frame;
        
        Java2DFrameConverter j = new Java2DFrameConverter();
            
        
        
        while ((frame = grabber.grabImage()) != null && canvasFrame.isVisible()) {
            // Capture and show the frame
            canvasFrame.showImage(frame);
            
            BufferedImage bi = j.convert(frame);
            
            int[] rgbArray = new int[bi.getWidth() * bi.getHeight()];
            
            bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), rgbArray, 0, bi.getWidth() );
            
            
            for(int y = 0; y < bi.getHeight(); y++)
            {
                for(int x = 0; x < bi.getWidth(); x++)
                {
                    int index = y*bi.getWidth() + x;
                    int pixel = rgbArray[index]; 
                    Color c = new Color(pixel);
                    
                    int r = c.getRed();
                    int g = c.getGreen();
                    int b = c.getBlue();
                    
                    rgbArray[index] = new Color(r, r, r).getRGB();
                    
                }
            }
            
            bi.setRGB(0, 0, bi.getWidth(), bi.getHeight(), rgbArray, 0, bi.getWidth());
            
            Frame newFrame = j.convert(bi);
            
            recorder.record(newFrame);
            // Delay
            //Thread.sleep(delay);
        }
        
        recorder.stop();

        // Close the video file
        grabber.release();
        recorder.release();

    }

}