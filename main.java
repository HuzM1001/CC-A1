import java.io.FileReader;

public class main {
    public static void main(String[] args) throws Exception {

        Lexer lexer = new Lexer(new FileReader("input.txt"));
        Token token;

        while ((token = lexer.yylex()) != null) {
            System.out.println(token);
        }
    }
}