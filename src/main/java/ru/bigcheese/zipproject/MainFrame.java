package ru.bigcheese.zipproject;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("ZipProject \u00a9 by BigCheese");
        setLayout(new MigLayout());

        JLabel label = new JLabel("Select");
        JTextField textField = new JTextField(35);

        JFileChooser fc = new JFileChooser(".");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);

        JButton browseButton = new JButton("...");
        browseButton.addActionListener(e -> {
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        JTextArea textArea = new JTextArea(15, 35);

        JButton zipButton = new JButton("Zip!");
        zipButton.addActionListener(e -> {
            textArea.setText(null);
            String rootPath = textField.getText();
            validateRootPath(rootPath);
            zipProject(rootPath);
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        MessageConsole mc = new MessageConsole(textArea);
        mc.redirectOut();
        mc.redirectErr();

        add(label);
        add(textField);
        add(browseButton);
        add(zipButton, "wrap");
        add(scrollPane, "span, grow");
    }

    private void validateRootPath(String path) {
        String error = null;
        if (path == null || path.trim().isEmpty()) {
            error = "Error! Select a root directory";
        } else if (!Files.exists(Paths.get(path))) {
            error = "Invalid parameter. Directory " + path + " not exists";
        }
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalArgumentException(error);
        }
    }

    private void zipProject(String path) {
        new Thread(() -> {
            try {
                new Zipper(path).zip(path + ".zip");
            } catch (IOException e) {
                throw new RuntimeException("I/O error while zip project", e);
            }
        }).start();
    }
}
