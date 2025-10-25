package assignments;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCount {
    public static void main(String[] args) {
        // Method 1: Using String replaceAll() method
        String text = """
                What is Lorem Ipsum?
                Lorem Ipsum is a simple simulation of content. that are used in the printing or typesetting business It has been the standard simulation of this business since the 16th century, when a unknown printer used a typewriter to switch letter positions to create a sample book. Lorem Ipsum has been invincible for not just five centuries, butUntil the era of transformation into typesetting using electronic means. and remains in its original condition unchanged It became more popular in the era of CProf 1960, when the Letraset was released with the text on it being Lorem Ipsum, and more recently That is, when print media software (Desktop Publishing) like Aldus PageMaker included various versions of Lorem Ipsum. into the software as well
                
                Why must it be used?
                There is factual evidence confirming this for a long time. That the content that is read will distract the reader from the layout. We use Lorem Ipsum because it has a moderate distribution of simple letters, which is used instead of writing ‘here as content, This is content' and also makes it look like normal English. Currently, there are packages of print media software. and many web page creation software (Web Page Editor) that use Lorem Ipsum as a default content model. and when searching with the word 'lorem ipsum' The results of the search will not find any websites that are still in the early stages of creation. Over the years, different versions of Lorem Ipsum have been invented, some by chance. Some are intentional (such as secretly inserting jokes)
                """;
        //System.out.println("Original string: " + text);

        // Replace all white spaces characters with empty string
        String[] result1 = text.split("\\s");
        int counter1 = 0;
        for (int i = 1; i < result1.length; i++) {
            counter1++;
        }

        System.out.println("Word count (method 1): " + counter1);

        // Method 2: Using Marcher split() method based on the \\s (white space)
        Pattern pattern = Pattern.compile("\\s");
        String[] result2 = pattern.split(text.trim());

        int counter2 = 0;
        for (int i = 1; i < result2.length; i++) {
            counter2++;
        }
        System.out.println("Word count (method 2): " + counter2);

        // Method 3: Using Pattern with word boundaries
        Pattern wordPattern = Pattern.compile("\\b\\w+\\b");
        Matcher matcher = wordPattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        System.out.println("Word count (method 3): " + count);
    }
}
