import java.io.*;
import java.util.*;

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

    // Input file converted to queue of lines
    private static List<String> inputFileQueue = new ArrayList<>();

    // Current reading buffer
    private static List<Character> readBuffer = new ArrayList<>();

    // Output file ready for writing
    private static List<Character> writeBuffer = new ArrayList<>();

    /**
     * Main function call
     */
    public static void main(String[] args) {
        // Read file
        try {
            readFile(args[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        // TODO: Analyze



        // Write Output
        // Write root tag
        writeBuffer.add('<');
        // Tag itself
        for(Character letter : "tokens".toCharArray())
            writeBuffer.add(letter);
        // Close tag
        writeBuffer.add('>');
        writeBuffer.add('\n');


        // TODO: Write analyzed
        writeTag("constant", "Hello");


        writeBuffer.add('<');
        writeBuffer.add('/');
        for(Character letter : "tokens".toCharArray())
            writeBuffer.add(letter);
        writeBuffer.add('>');
        writeBuffer.add('\n');

        try {
            writeFile(args[0].substring(0, args[0].length() - 5) + "T.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the given tag and value
     * @param tagName Name for xml tag
     * @param value Value to print
     */
    private static void writeTag(String tagName, String value) {
        // Open tag
        writeBuffer.add(' ');
        writeBuffer.add('<');
        // Tag itself
        for(Character letter : tagName.toCharArray())
            writeBuffer.add(letter);
        // Close tag
        writeBuffer.add('>');

        // Write the value
        writeBuffer.add(' ');
        for(Character letter : value.toCharArray())
            writeBuffer.add(letter);
        writeBuffer.add(' ');

        // Close tag
        writeBuffer.add('<');
        writeBuffer.add('/');
        for(Character letter : tagName.toCharArray())
            writeBuffer.add(letter);
        writeBuffer.add('>');
        writeBuffer.add('\n');
    }

    /**
     * Dumps the current buffer to a file
     * @param fileName File to save
     * @throws IOException Might fail writing
     */
    private static void writeFile(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write("<?xml version = \"1.0\" encoding = \"UTF-8\" standalone = \"no\" ?>\n");
        for(Character character : writeBuffer) {
            writer.write(character);
        }
        writer.close();
    }

    /**
     * Read the file into a queue of lines
     * @param fileName The input file
     * @throws FileNotFoundException Can't find file
     */
    private static void readFile(String fileName) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        reader.lines().forEach(readLine -> {
            inputFileQueue.add(readLine);
        });
    }
}
