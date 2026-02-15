import java.util.*;

public class ManualScanner {
    private final String input;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    //keywords
    private static final Set<String> KEYWORDS = Set.of(
            "start", "finish", "loop", "condition", "declare",
            "output", "input", "function", "return",
            "break", "continue", "else"
    );

    private static final Set<String> BOOLEAN_LITERALS = Set.of(
            "true", "false"
    );

    //operators simple wale
    private static final Set<Character> OPERATORS = Set.of(
            '+', '-', '*', '/', '=', '<', '>', '!'
    );

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

            // identifiers - keywords and bools
            if (Character.isUpperCase(c)) {
                tokens.add(scanWord(startLine, startColumn));
                continue;
            }

            // integer literals
            if (Character.isDigit(c)) {
                tokens.add(scanNumber(startLine, startColumn));
                continue;
            }

            // operators simple + relational
            if (OPERATORS.contains(c)) {
                tokens.add(scanOperator(startLine, startColumn));
                continue;
            }

            //unexpected character pe phat na jaye
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

    private Token scanWord(int startLine, int startColumn) {
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

        String word = lexeme.toString();

        if (KEYWORDS.contains(word)) {
            return new Token(TokenType.KEYWORD, word, startLine, startColumn);
        }

        if (BOOLEAN_LITERALS.contains(word)) {
            return new Token(TokenType.BOOLEAN_LITERAL, word, startLine, startColumn);
        }

        return new Token(TokenType.IDENTIFIER, word, startLine, startColumn);
    }

    //NUMBER LOGIC NEW STUFF YAY
    private Token scanNumber(int startLine, int startColumn) {
        StringBuilder number = new StringBuilder();

        while (!isAtEnd() && Character.isDigit(peek())) {
            number.append(advance());
        }

        return new Token(
                TokenType.INTEGER_LITERAL,
                number.toString(),
                startLine,
                startColumn
        );
    }

    //operator logic thori si advanced now, uses look to next char for stuff like <= waghera yk cool cool and 
    //necessary coz ofc it is why am i yapping sm 
    private Token scanOperator(int startLine, int startColumn) {

        char current = advance();

        //check double operators
        if (!isAtEnd()) {
            char next = peek();

            if ((current == '=' && next == '=') ||
                (current == '!' && next == '=') ||
                (current == '<' && next == '=') ||
                (current == '>' && next == '=')) {

                advance(); // consume second char

                return new Token(
                        TokenType.RELATIONAL_OPERATOR,
                        "" + current + next,
                        startLine,
                        startColumn
                );
            }
        }

        //single char relational
        if (current == '<' || current == '>') {
            return new Token(
                    TokenType.RELATIONAL_OPERATOR,
                    String.valueOf(current),
                    startLine,
                    startColumn
            );
        }

        //normal operator
        return new Token(
                TokenType.OPERATOR,
                String.valueOf(current),
                startLine,
                startColumn
        );
    }

    //helpers
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

    public static void main(String[] args) {
        String testInput = "Count == 5\nCount != 3\nCount <= 10\nCount >= 2";
        ManualScanner scanner = new ManualScanner(testInput);
        List<Token> tokens = scanner.scan();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
