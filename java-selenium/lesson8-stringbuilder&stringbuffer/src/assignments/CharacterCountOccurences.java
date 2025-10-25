package assignments;

public class CharacterCountOccurences {
    public static void main(String[] args) {
        String text = """
            What is Lorem Ipsum?
            Lorem Ipsum is a simple simulation of content. that are used in the printing or typesetting business It has been the standard simulation of this business since the 16th century, when a unknown printer used a typewriter to switch letter positions to create a sample book. Lorem Ipsum has been invincible for not just five centuries, butUntil the era of transformation into typesetting using electronic means. and remains in its original condition unchanged It became more popular in the era of CProf 1960, when the Letraset was released with the text on it being Lorem Ipsum, and more recently That is, when print media software (Desktop Publishing) like Aldus PageMaker included various versions of Lorem Ipsum. into the software as well
            
            Why must it be used?
            There is factual evidence confirming this for a long time. That the content that is read will distract the reader from the layout. We use Lorem Ipsum because it has a moderate distribution of simple letters, which is used instead of writing 'here as content, This is content' and also makes it look like normal English. Currently, there are packages of print media software. and many web page creation software (Web Page Editor) that use Lorem Ipsum as a default content model. and when searching with the word 'lorem ipsum' The results of the search will not find any websites that are still in the early stages of creation. Over the years, different versions of Lorem Ipsum have been invented, some by chance. Some are intentional (such as secretly inserting jokes)
            """;

        // Method 1: Using string length
        int count1 = text.length();
        System.out.println("Character count (method 1): " + count1);

        // Method 2: Using a loop (if you need to process each character)
        int count2 = 0;
        for (int i = 0; i < text.length(); i++) {
            count2++;  // Increment count for each character
        }
        System.out.println("Character count (method 2): " + count2);
    }
}
