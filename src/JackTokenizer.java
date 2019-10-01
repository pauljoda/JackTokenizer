import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Paul Davis
 * @since 9/30/19
 */
public class JackTokenizer {
    // List of symbols
    private static List<Character> symbols = new ArrayList<>();
    static {
        symbols.add('(');
        symbols.add(')');
        symbols.add('[');
        symbols.add(']');
        symbols.add('{');
        symbols.add('}');
        symbols.add(',');
        symbols.add('=');
        symbols.add('.');
        symbols.add('+');
        symbols.add('-');
        symbols.add('*');
        symbols.add('&');
        symbols.add('|');
        symbols.add('~');
        symbols.add('<');
        symbols.add('>');
    }

    // Reserved Words
    private static List<String> reservedWords = new ArrayList<>();
    static {
        reservedWords.add("class");
        reservedWords.add("constructor");
        reservedWords.add("method");
        reservedWords.add("function");
        reservedWords.add("int");
        reservedWords.add("boolean");
        reservedWords.add("char");
        reservedWords.add("void");
        reservedWords.add("var");
        reservedWords.add("static");
        reservedWords.add("field");
        reservedWords.add("let");
        reservedWords.add("do");
        reservedWords.add("if");
        reservedWords.add("else");
        reservedWords.add("while");
        reservedWords.add("return");
        reservedWords.add("true");
        reservedWords.add("false");
        reservedWords.add("null");
        reservedWords.add("this");
    }

    // Input file converted to queue
    private static List<Character> readQueue = new ArrayList<>();

    // Output file ready for writing
    private static List<Character> writeQueue = new ArrayList<>();

    /**
     * Main function call
     */
    public static void main(String[] args) {
        writeQueue.add('h');
        writeQueue.add('e');
        writeQueue.add('l');
        writeQueue.add('l');
        writeQueue.add('o');

        writeQueue.add(' ');
        writeQueue.add('w');
        writeQueue.add('o');
        writeQueue.add('r');
        writeQueue.add('l');
        writeQueue.add('d');


        try {
            writeFile("Test.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for(Character character : writeQueue) {
            writer.write(character);
        }
        writer.close();
    }
}
