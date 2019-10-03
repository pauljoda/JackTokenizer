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
        symbols.add(';');
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

    // Boolean to hold current comment reading state
    private static boolean readingComment = false;

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

        // Write Output
        // Write root tag
        writeBuffer.add('<');
        // Tag itself
        for(Character letter : "tokens".toCharArray())
            writeBuffer.add(letter);
        // Close tag
        writeBuffer.add('>');
        writeBuffer.add('\n');

        for(String currentLine : inputFileQueue)
            analyzeLine(currentLine);

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

    private static void analyzeLine(String line) {
        String currentToken = "", tokenType = "";
        boolean writeToken = false, checkNext = false;

        for(Character lexeme : line.toCharArray()) {

            /***********************************************************************************************************
             * Comments                                                                                                *
             ***********************************************************************************************************/
            // Currently running comment check
            if(readingComment) {
                currentToken += lexeme;
                // We've been told to check next, if it fits bill reset state and move on
                if(checkNext && lexeme == '/') {
                    readingComment = false;
                    currentToken = tokenType = "";
                    writeToken = checkNext = false;
                    continue;
                }
                checkNext = lexeme == '*';
                continue;
            }

            // Handle Comments start
            // Haven't started a new token and starts with /
            if(currentToken.equals("") && lexeme == '/') {
                currentToken += lexeme;
                continue;
            }
            // Single /, check for start of comment state
            if(currentToken.equals("/")) {
                // Go to end of line, we can skip the entire line at this point
                if(lexeme == '/') {
                    return;
                }
                else if(lexeme == '*') {
                    // Starting block comment
                    readingComment = true;
                    currentToken += lexeme;
                    continue;
                } else { // We are just a divide symbol, write tag and move on
                    writeTag("symbol", currentToken);
                    readingComment = false;
                    currentToken = tokenType = "";
                    writeToken = checkNext = false;
                }
            }

            /***********************************************************************************************************
             * Constants, keywords, ids                                                                               *
             ***********************************************************************************************************/

            // New tag, find out what it is
            if(tokenType.equals("")) {
                switch (lexeme) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        tokenType = "integerConstant";
                        currentToken += lexeme;
                        continue;
                    case '"' :
                        tokenType = "stringConstant";
                        continue;
                    case '\t' :
                    case ' ' :
                        // No need to do anything, move on
                        continue;
                    case '_' :
                        tokenType = "identifier";
                        currentToken += lexeme;
                        continue;
                    default:
                        if(symbols.contains(lexeme)) {
                            tokenType = "symbol";
                            currentToken += lexeme;
                            writeToken = true;
                            break;
                        }
                        currentToken += lexeme;
                        tokenType = "UNKNOWN"; // We don't know yet
                }
            } else {
                switch (lexeme) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        currentToken += lexeme;
                        continue;
                    case '"' :
                        writeToken = true;
                        break;
                    case '\t' :
                    case ' ' :
                        // If working on string constant
                        if(tokenType.equals("stringConstant")) {
                            currentToken += lexeme;
                            continue;
                        }

                        // Lookup if reserved word
                        if(reservedWords.contains(currentToken))
                            tokenType = "keyword";
                        else
                            tokenType = "identifier";

                        writeToken = true;
                        break;
                    default:
                        if(tokenType.equals("stringConstant")) {
                            currentToken += lexeme;
                            continue;
                        }

                        // Handle symbol next to other
                        if(symbols.contains(lexeme)) {
                            // Lookup if reserved word
                            if(reservedWords.contains(currentToken))
                                tokenType = "keyword";
                            else if(!tokenType.equals("integerConstant"))
                                tokenType = "identifier";

                            // Dump what we have
                            writeTag(tokenType, currentToken);
                            // Write Symbol
                            writeTag("symbol", String.valueOf(lexeme));
                            tokenType = currentToken = "";
                            writeToken = false;
                            continue;
                        }

                        if(!tokenType.equals("integerConstant"))
                            currentToken += lexeme;
                        else
                            writeToken = true;
                }
            }

            if(writeToken) {
                writeTag(tokenType, currentToken);
                tokenType = currentToken = "";
                writeToken = false;
            }
        }
    }

    /**
     * Writes the given tag and value
     * @param tagName Name for xml tag
     * @param value Value to print
     */
    private static void writeTag(String tagName, String value) {
        // Open tag
        writeBuffer.add('<');
        // Tag itself
        for(Character letter : tagName.toCharArray())
            writeBuffer.add(letter);
        // Close tag
        writeBuffer.add('>');

        // Write the value
        writeBuffer.add(' ');
        for(Character letter : value.toCharArray()) {
            if(letter == '<') {
                for(Character temp : "&lt;".toCharArray())
                    writeBuffer.add(temp);
            }
            else
                writeBuffer.add(letter);
        }
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
