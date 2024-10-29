public class Test {

    public static void main(String[] args) {
        SimpleNLE.debug = true;

        testJoin();
    }

    private static void testJoin(){
        String inputPath1 = "C:/Users/noahl/Desktop/DJI_0342.MP4";
        String inputPath2 = "C:/Users/noahl/Desktop/DJI_0356.MP4";
        String outputPath = "C:/Users/noahl/Desktop/Drone Test Composite.MP4";

        if(SimpleNLE.joinVideos(inputPath1,inputPath2,outputPath)){
            System.out.println("Render successful! File can be found at " + outputPath);
        } else {
            System.out.println("Render unsuccessful.");
        }
    }

    private static void testTrim(){
        String inputPath = "C:/Users/noahl/Desktop/DJI_0342.MP4";
        String outputPath = "C:/Users/noahl/Desktop/DJI_0342 - Trimmed (1).MP4";
        double start = 6.0; double end = 13.5;

        if(SimpleNLE.trimVideo(start,end,inputPath,outputPath)){
            System.out.println("Trim successful! File can be found at " + outputPath);
        } else {
            System.out.println("Trim unsuccessful.");
        }
    }

}
