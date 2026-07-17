package cream.cli;

import javax.swing.*;
import java.awt.*;

public class Console extends JFrame {

    private final JTextArea outputArea;

    public Console() {
        super("Cream CLI Console");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GRAY);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public void append(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    public void appendLine(String line) {
        append(line + "\n");
    }

    public void clear() {
        SwingUtilities.invokeLater(() -> {
            outputArea.setText("");
        });
    }

    public void redirectSystemOut() {
        java.io.PrintStream printStream = new java.io.PrintStream(new java.io.OutputStream() {
            @Override
            public void write(int b) {
                append(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                append(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) {
                append(new String(b));
            }
        });

        System.setOut(printStream);
        System.setErr(printStream);
    }
}
