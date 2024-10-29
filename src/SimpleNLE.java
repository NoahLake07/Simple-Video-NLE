import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import javax.swing.*;

public class SimpleNLE {

    public static boolean debug = false;
    public static boolean doDialog = true;

    public static boolean trimVideo(double start, double end, String inputPath, String outputPath){
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
        FFmpegFrameRecorder recorder = null;

        try {
            grabber.start();

            // setup recorder with same settings
            recorder = new FFmpegFrameRecorder(outputPath, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
            recorder.setFormat(grabber.getFormat());
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setVideoCodec(grabber.getVideoCodec());
            recorder.setVideoBitrate(grabber.getVideoBitrate());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioCodec(grabber.getAudioCodec());
            recorder.start();

            // move grabber to start
            grabber.setTimestamp((long) start * 1_000_000);

            JProgressBar progressBar = null;
            if(doDialog){
                JDialog progressDialog = new JDialog();
                progressDialog.setTitle("Render Progress");
                progressBar = new JProgressBar();
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                progressBar.setString("Preparing...");
                progressDialog.add(progressBar);
                progressDialog.setVisible(true);
                progressDialog.setSize(200,100);
            }

            Frame f;
            double currentTimestamp, endTimestampInMs = end * 1_000_000;
            while((f = grabber.grab()) != null && (currentTimestamp = grabber.getTimestamp()) <= endTimestampInMs){
                recorder.record(f);
                if(doDialog) {
                    progressBar.setValue((int) ((currentTimestamp/endTimestampInMs) * 100));
                    progressBar.setString(String.format("%.2f",(currentTimestamp/endTimestampInMs) * 100) + "% COMPLETE");
                }
            }

            progressBar.setValue(100);
            progressBar.setString("100% COMPLETE");

            if(debug) System.out.println("Video trimming completed successfully.");
            return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean joinVideos(String inputPath1, String inputPath2, String outputPath){
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(inputPath1), grabber2 = null;
        FFmpegFrameRecorder recorder = null;
        JProgressBar progressBar = null;

        try {
            grabber1.start();

            // setup recorder with same settings as grabber 1
            recorder = new FFmpegFrameRecorder(outputPath, grabber1.getImageWidth(), grabber1.getImageHeight(), grabber1.getAudioChannels());
            recorder.setFormat(grabber1.getFormat());
            recorder.setFrameRate(grabber1.getFrameRate());
            recorder.setVideoCodec(grabber1.getVideoCodec());
            recorder.setVideoBitrate(grabber1.getVideoBitrate());
            recorder.setSampleRate(grabber1.getSampleRate());
            recorder.setAudioCodec(grabber1.getAudioCodec());
            recorder.start();

            if (doDialog) {
                JDialog progressDialog = new JDialog();
                progressDialog.setTitle("Join Progress");
                progressBar = new JProgressBar();
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                progressDialog.add(progressBar);
                progressDialog.setSize(300, 100);
                progressDialog.setVisible(true);
            }

            Frame f;

            // record input1
            long totalLength1 = grabber1.getLengthInTime();
            while ((f = grabber1.grab()) != null) {
                recorder.record(f);
                if (doDialog) {
                    double currentProgress = (double) grabber1.getTimestamp() / totalLength1;
                    progressBar.setValue((int) (currentProgress * 50));  // Up to 50% for the first video
                    progressBar.setString(String.format("Joining First Video: %.2f%% COMPLETE", currentProgress * 50));
                }
            }

            // record input2
            grabber2 = new FFmpegFrameGrabber(inputPath2);
            grabber2.start();
            long totalLength2 = grabber2.getLengthInTime();
            while ((f = grabber2.grab()) != null) {
                recorder.record(f);
                if (doDialog) {
                    double currentProgress = (double) grabber2.getTimestamp() / totalLength2;
                    progressBar.setValue(50 + (int) (currentProgress * 50));  // 50% to 100% for the second video
                    progressBar.setString(String.format("Joining Second Video: %.2f%% COMPLETE", 50 + currentProgress * 50));
                }
            }

            if (doDialog) {
                progressBar.setValue(100);
                progressBar.setString("100% COMPLETE");
            }

            if(debug) System.out.println("Video joining completed successfully.");
            return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (grabber1 != null) {
                    grabber1.stop();
                    grabber1.release();
                }
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
