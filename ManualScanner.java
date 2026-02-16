import java.util.*;

public class ManualScanner {
    private final String input;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    // keywords
    private static final Set<String> KEYWORDS = Set.of(
            "start", "finish", "loop", "condition", "declare",
            "output", "input", "function", "return",
            "break", "continue", "else");

    private static final Set<String> BOOLEAN_LITERALS = Set.of(
            "true", "false");

    // operators simple wale
    private static final Set<Character> OPERATORS = Set.of(
            '+', '-', '*', '/', '=', '<', '>', '!', '&', '|');

    // delimiters simple wale
    private static final Set<Character> DELIMITERS = Set.of(
            '(', ')', '{', '}', ';', ',');

    public ManualScanner(String input) {
        this.input = input;
    }

    public List<Token> scan() {
        List<Token> tokens = new ArrayList<>();

        while (!isAtEnd()) {
            skipWhitespace();

            if (isAtEnd())
                break;

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

            // string literal logic new stuff
            if (c == '"') {
                tokens.add(scanString(startLine, startColumn));
                continue;
            }

            // char literal logic new stuff
            if (c == '\'') {
                tokens.add(scanChar(startLine, startColumn));
                continue;
            }

            // comment logic pehle check karo warna // ko operator samjhega
            if (c == '/' && peekNext() == '/') {
                tokens.add(scanComment(startLine, startColumn));
                continue;
            }

            // delimiters simple wale
            if (DELIMITERS.contains(c)) {
                tokens.add(scanDelimiter(startLine, startColumn));
                continue;
            }

            // operators simple + relational
            if (OPERATORS.contains(c)) {
                tokens.add(scanOperator(startLine, startColumn));
                continue;
            }

            // unexpected character pe phat na jaye
            tokens.add(new Token(
                    TokenType.ERROR,
                    "Unexpected character: " + c,
                    startLine,
                    startColumn));
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

    // NUMBER LOGIC NEW STUFF YAY
    // changed it up to handle . for floats
    private Token scanNumber(int startLine, int startColumn) {

        StringBuilder number = new StringBuilder();

        while (!isAtEnd() && Character.isDigit(peek())) {
            number.append(advance());
        }

        if (!isAtEnd() && peek() == '.') {

            number.append(advance());

            if (isAtEnd() || !Character.isDigit(peek())) {
                return new Token(
                        TokenType.ERROR,
                        number.toString(),
                        startLine,
                        startColumn);
            }

            while (!isAtEnd() && Character.isDigit(peek())) {
                number.append(advance());
            }

            return new Token(
                    TokenType.FLOAT_LITERAL,
                    number.toString(),
                    startLine,
                    startColumn);
        }

        return new Token(
                TokenType.INTEGER_LITERAL,
                number.toString(),
                startLine,
                startColumn);
    }

    // string literal logic simple hi hai
    private Token scanString(int startLine, int startColumn) {

        advance(); // consume opening "

        StringBuilder value = new StringBuilder();

        while (!isAtEnd() && peek() != '"') {

            if (peek() == '\n') {
                return new Token(
                        TokenType.ERROR,
                        "Unterminated string",
                        startLine,
                        startColumn);
            }

            value.append(advance());
        }

        if (isAtEnd()) {
            return new Token(
                    TokenType.ERROR,
                    "Unterminated string",
                    startLine,
                    startColumn);
        }

        advance(); // consume closing "

        return new Token(
                TokenType.STRING_LITERAL,
                value.toString(),
                startLine,
                startColumn);
    }

    // char literal logic thora strict hai
    private Token scanChar(int startLine, int startColumn) {

        advance(); // consume opening '

        if (isAtEnd() || peek() == '\n') {
            return new Token(
                    TokenType.ERROR,
                    "Invalid char literal",
                    startLine,
                    startColumn);
        }

        char value = advance();

        if (isAtEnd() || peek() != '\'') {
            return new Token(
                    TokenType.ERROR,
                    "Invalid char literal",
                    startLine,
                    startColumn);
        }

        advance(); // consume closing '

        return new Token(
                TokenType.CHAR_LITERAL,
                String.valueOf(value),
                startLine,
                startColumn);
    }

    // operator logic thori si advanced now, checks for && and || gave me a stroke
    private Token scanOperator(int startLine, int startColumn) {

        char current = advance();

        if (!isAtEnd()) {
            char next = peek();

            if ((current == '=' && next == '=') ||
                    (current == '!' && next == '=') ||
                    (current == '<' && next == '=') ||
                    (current == '>' && next == '=')) {

                advance();

                return new Token(
                        TokenType.RELATIONAL_OPERATOR,
                        "" + current + next,
                        startLine,
                        startColumn);
            }

            if ((current == '&' && next == '&') ||
                    (current == '|' && next == '|')) {

                advance();

                return new Token(
                        TokenType.RELATIONAL_OPERATOR,
                        "" + current + next,
                        startLine,
                        startColumn);
            }
        }

        if (current == '<' || current == '>') {
            return new Token(
                    TokenType.RELATIONAL_OPERATOR,
                    String.valueOf(current),
                    startLine,
                    startColumn);
        }

        return new Token(
                TokenType.OPERATOR,
                String.valueOf(current),
                startLine,
                startColumn);
    }

    // comment logic simple sa
    private Token scanComment(int startLine, int startColumn) {

        advance();
        advance();

        StringBuilder comment = new StringBuilder();

        while (!isAtEnd() && peek() != '\n') {
            comment.append(advance());
        }

        return new Token(
                TokenType.SINGLE_LINE_COMMENT,
                comment.toString(),
                startLine,
                startColumn);
    }

    // delimiter logic simple sa
    private Token scanDelimiter(int startLine, int startColumn) {

        char d = advance();

        return new Token(
                TokenType.DELIMITER,
                String.valueOf(d),
                startLine,
                startColumn);
    }

    // helpers
    private boolean isAtEnd() {
        return index >= input.length();
    }

    private char peek() {
        return input.charAt(index);
    }

    private char peekNext() {
        if (index + 1 >= input.length())
            return '\0';
        return input.charAt(index + 1);
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

        String testInput =
                // identifiers + keywords
                "Count = 10;\n" +
                        "start finish loop condition declare output input function return break continue else\n" +

                        // boolean literals
                        "Flag = true;\n" +
                        "OtherFlag = false;\n" +

                        // integers and floats
                        "IntValue = 123;\n" +
                        "FloatValue = 45.67;\n" +
                        "ZeroFloat = 0.001;\n" +

                        // invalid numbers
                        ".25\n" +
                        "10.\n" +

                        // string literals
                        "Message = \"Hello World\";\n" +
                        "EmptyString = \"\";\n" +

                        // unterminated string
                        "\"This string never ends\n" +

                        // char literals
                        "Letter = 'A';\n" +
                        "DigitChar = '5';\n" +

                        // invalid char literals
                        "'AB'\n" +
                        "'\n" +

                        // operators
                        "A + B - C * D / E;\n" +
                        "A == B;\n" +
                        "A != B;\n" +
                        "A <= B;\n" +
                        "A >= B;\n" +
                        "A < B;\n" +
                        "A > B;\n" +
                        "A && B;\n" +
                        "A || B;\n" +

                        // delimiters
                        "Function Add(A, B) {\n" +
                        "   Return A + B;\n" +
                        "}\n" +

                        // comment
                        "// this is a single line comment\n" +

                        // lowercase invalid identifiers
                        "invalidIdentifier\n" +
                        "anotherOne\n";

        ManualScanner scanner = new ManualScanner(testInput);
        List<Token> tokens = scanner.scan();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
