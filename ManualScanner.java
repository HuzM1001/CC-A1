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

        while (!isAtEnd()) {
            skipWhitespace();

            if (isAtEnd()) break;

            int startLine = line;
            int startColumn = column;
            char c = peek();

            //start with upercase letter means identifier
            if (Character.isUpperCase(c)) {
                tokens.add(scanIdentifier(startLine, startColumn));
                continue;
            }

            //throwing error for unexpected characters
            tokens.add(new Token(
                    TokenType.ERROR,
                    "Unexpected character: " + c,
                    startLine,
                    startColumn
            ));
            advance();
        }

        return tokens;
    }

//HELPERS
    private boolean isAtEnd() {
        return index >= input.length();
    }

    private char peek() {
        return input.charAt(index);
    }

    private char advance() {
        char c = input.charAt(index++);
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\t') {
                advance();
            } else if (c == '\n') {
                advance();
            } else {
                break;
            }
        }
    }
//HELPERS END


//SCANNING

    private Token scanIdentifier(int startLine, int startColumn) {
        StringBuilder lexeme = new StringBuilder();

        lexeme.append(advance());

        while (!isAtEnd()) {
            char c = peek();
            if (Character.isLowerCase(c) || Character.isDigit(c) || c == '_') {
                lexeme.append(advance());
            } else {
                break;
            }
        }

        return new Token(TokenType.IDENTIFIER, lexeme.toString(), startLine, startColumn);
    }


    



    public static void main(String[] args) {
        String testInput = "Hello World\nTest123 _invalid";
        ManualScanner scanner = new ManualScanner(testInput);
        List<Token> tokens = scanner.scan();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
