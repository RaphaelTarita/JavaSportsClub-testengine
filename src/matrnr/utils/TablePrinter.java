package matrnr.utils;

import java.io.PrintStream;

public class TablePrinter {
    private final PrintStream out;

    public TablePrinter(PrintStream output) {
        out = output;
    }

    public TablePrinter printHeading(int padTo, char padWith, String heading) {
        int left = (padTo - heading.length() - 2) / 2;
        int right = (padTo - heading.length() - 1) / 2;
        for (int i = 0; i < left; i++) {
            out.print(padWith);
        }
        out.print(' ');
        out.print(heading);
        out.print(' ');
        for (int i = 0; i < right; i++) {
            out.print(padWith);
        }
        out.print('\n');
        return this;
    }

    public TablePrinter printHeading(int padTo, String heading) {
        return printHeading(padTo, '=', heading);
    }

    public TablePrinter printEmptyLine() {
        out.println();
        return this;
    }

    public TablePrinter printTableRow(int padTo, char padWith, String... rowItems) {
        for (int i = 0; i < rowItems.length; i++) {
            out.print(rowItems[i]);
            if (i != rowItems.length - 1) {
                for (int j = rowItems[i].length(); j <= padTo; j++) {
                    out.print(padWith);
                }
            }
        }
        out.print('\n');
        return this;
    }

    public TablePrinter printTableRow(int padTo, String... rowItems) {
        return printTableRow(padTo, ' ', rowItems);
    }
}
