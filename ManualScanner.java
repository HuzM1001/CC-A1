import java.util.*;


public class ManualScanner {
    private final String input;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public ManualScanner(String input) {
        this.input = input;
    }

    public List<Token> scan() {
        List<Token> tokens = new ArrayList<>();

        while (index < input.length()) {
            char c = input.charAt(index);
            if (c == ' ' || c == '\t') {
                index++;
                column++;
                continue;
            } else if (c == '\n') {
                index++;
                line++;
                column = 1;
                continue;
            }
            tokens.add(new Token(TokenType.ERROR, "Unexpected character: " + c, line, column));
            index++;
            column++;
        }

        return tokens;
    }

    public static void main(String[] args) {
        String testInput = "hello world\n123";
        ManualScanner scanner = new ManualScanner(testInput);
        List<Token> tokens = scanner.scan();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
