package com.mycompany.javacv;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import org.bytedeco.javacv.FFmpegFrameRecorder;

public class Demo {

    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException, IOException {

        //Initialize our decoder
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("Fish.mp4");
        //Tell the decoder to start processing
        grabber.start();

        //Initialize our encoder
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("Out.mp4", grabber.getImageWidth(), grabber.getImageHeight());
        //We can set a target bitrate (Mb/s) or a quality target. Quality targets range from 0 (lossless, largest size) to 51 (really bad, smallest size)
        //There does not appear to be a major impact on runtime, just on filesize
        recorder.setVideoQuality(0);
        //The default framerate for FFmpegFrameRecorder is 30 fps

        //Tell the recorder are done setting parameters.
        recorder.start();

        // Reference for each frame we decode
        Frame frame;
        
        //Loop until we have processed every frame
        while ((frame = grabber.grabImage()) != null) {
            // Get the frame dimensions
            int width = frame.imageWidth;
            int height = frame.imageHeight;

            //Read the frame as a buffer of bytes (RGB)
            
            //Set the buffer to start reading at the begging of the frame
            ByteBuffer bb = (ByteBuffer) frame.image[0].position(0);
            //Create a byte buffer of the right size
            byte[] allBytes = new byte[3 * width * height];
            
            //Read values from the decoder into the byte array
            bb.get(allBytes, 0, 3 * width * height);

            //Loop over each pixel (byte triple)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    
                    //Figure where in our 1D array our x,y pixel is.
                    int start = 3 * (x + y * width);
                    
                    byte r = allBytes[start];
                    byte g = allBytes[start+1];
                    byte b = allBytes[start+2];
                    
                    allBytes[start] = allBytes[start];      //Set r
                    allBytes[start + 1] = allBytes[start];  //Set g
                    allBytes[start + 2] = allBytes[start];  //Set b

                }
            }

            //Resest our position in the frame
            bb = (ByteBuffer) frame.image[0].position(0);

            //And set our buffer
            bb.put(allBytes, 0, 3 * width * height);

            //Save the frame to our encoder
            recorder.record(frame);
           
        }

        //Tell the encoder we are done, so it can do the final steps
        recorder.stop();

        // Release our resources
        grabber.release();
        recorder.release();
        
        //Indication on the console that we have finished.
        System.out.println("Done");

    }

}
