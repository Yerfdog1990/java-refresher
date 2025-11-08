package day9;

public class TextBlocks {
    public static void main(String[] args) {
        String html = """
    <html>
        <body>
            <h1>Hello, Java!</h1>
        </body>
    </html>
    """;

        System.out.println(html);

        /*
        Features:
        1.Maintain natural formatting
        2.Automatically handle line breaks
        3.Reduce need for escape sequences
         */
        // Before Java 13
        String json = "{\n\t\"name\": \"Alice\",\n\t\"age\": 25\n}";
        System.out.println("Before Java 13:\n" +json);
// Using Text Blocks
        String jsonBlock = """
            {
                "name": "Alice",
                "age": 25
            }
            """;
        System.out.println("After Java 13:\n" +jsonBlock);
    }
}
