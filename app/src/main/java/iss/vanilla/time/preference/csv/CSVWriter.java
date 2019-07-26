package iss.vanilla.time.preference.csv;

import java.io.PrintWriter;
import java.io.Writer;

public class CSVWriter {

    // character for escaping quotes
    private static final char DEFAULT_ESCAPE_CHARACTER = '"';
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE_CHARACTER = '"';
    private static final char NO_QUOTE_CHARACTER = '\u0000';
    private static final char NO_ESCAPE_CHARACTER = '\u0000';
    private static final String DEFAULT_LINE_END = "\n";

    private PrintWriter printWriter;
    private char separator;
    private char quote;
    private char escape;
    private String lineEnd;

    public CSVWriter(Writer writer) {
        this(writer, DEFAULT_SEPARATOR, NO_QUOTE_CHARACTER, NO_ESCAPE_CHARACTER, DEFAULT_LINE_END);
    }

    public CSVWriter(Writer writer, char separator) {
        this(writer, separator, NO_QUOTE_CHARACTER, NO_ESCAPE_CHARACTER, DEFAULT_LINE_END);
    }

    public CSVWriter(Writer writer, char separator, char quote, char escape, String lineEnd) {
        this.printWriter = new PrintWriter(writer);
        this.separator = separator;
        this.quote = quote;
        this.escape = escape;
        this.lineEnd = lineEnd;
    }

    public void writeNext(String ... args) {

        if(args == null)
            return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {

            if(i != 0)
                sb.append(separator);

            String element = args[i];
            if(element == null)
                continue;
            if(quote != NO_QUOTE_CHARACTER)
                sb.append(quote);

            for(char c : element.toCharArray()) {
                if(escape != NO_ESCAPE_CHARACTER && c == quote)
                    sb.append(escape).append(c);
                else if(escape != NO_ESCAPE_CHARACTER && c == escape)
                    sb.append(escape).append(c);
                else
                    sb.append(c);
            }

            if(quote != NO_QUOTE_CHARACTER)
                sb.append(quote);

        }

        sb.append(lineEnd);
        printWriter.write(sb.toString());

    }

    public void flush() { printWriter.flush(); }

    public void close() {
        flush();
        printWriter.close();
    }

}
