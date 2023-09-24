import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    private JButton exeButton;
    private JButton swfButton;
    private JButton createButton;
    private JButton resetButton;

    private File exeFile;
    private File swfFile;

    public Main() {
        setTitle("SWF To EXE Converter");
        setSize(400, 300);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel titleLabel = new JLabel("SWF To EXE Converter");
        titleLabel.setBounds(10, 10, 320, 20);
        add(titleLabel);

        JLabel detailsLabel = new JLabel("<html><h3>This tool allows you to convert SWF files into EXE using your preffered Flash Player.</h3></html>");
        detailsLabel.setBounds(10, 30, 320, 60);
        add(detailsLabel);

        int buttonWidth = 150; // Width of buttons
        int buttonX = (350 - buttonWidth) / 2; // Center horizontally

        exeButton = new JButton("Load Flash Player EXE");
        exeButton.setBounds(buttonX, 100, 150, 25);
        exeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openEXE();
            }
        });
        add(exeButton);


        

        swfButton = new JButton("Load SWF File");
        swfButton.setBounds(buttonX, 135, 150, 25);
        swfButton.setEnabled(false);
        swfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSWF();
            }
        });
        add(swfButton);

        createButton = new JButton("Create Projector");
        createButton.setBounds(buttonX, 170, 150, 25);
        createButton.setEnabled(false);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                create();
            }
        });
        add(createButton);

        resetButton = new JButton("Reset");
        resetButton.setBounds(buttonX, 205, 150, 25);
        resetButton.setEnabled(false);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        add(resetButton);
    }

    private void openEXE() {
        FileDialog fileDialog = new FileDialog(this, "Select Flash Player EXE", FileDialog.LOAD);
        fileDialog.setVisible(true);
        String filename = fileDialog.getFile();
        if (filename != null) {
            exeFile = new File(fileDialog.getDirectory() + filename);
            swfButton.setEnabled(true);
            exeButton.setEnabled(false);
        }
    }

    private void openSWF() {
        FileDialog fileDialog = new FileDialog(this, "Select SWF File", FileDialog.LOAD);
        fileDialog.setVisible(true);
        String filename = fileDialog.getFile();
        if (filename != null) {
            swfFile = new File(fileDialog.getDirectory() + filename);
            createButton.setEnabled(true);
            swfButton.setEnabled(false);
        }
    }

    private void create() {
        byte[] exeData = readBytesFromFile(exeFile);
        byte[] swfData = readBytesFromFile(swfFile);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(exeData);
            outputStream.write(swfData);
            outputStream.write(new byte[] { 0x56, 0x34, 0x12, (byte) 0xFA });

            int swfSize = swfData.length;
            for (int i = 0; i < 4; i++) {
                outputStream.write((byte) (swfSize & 0xFF));
                swfSize >>= 8;
            }

            byte[] outputData = outputStream.toByteArray();
            outputStream.close();

            FileDialog saveDialog = new FileDialog(this, "Save Projector", FileDialog.SAVE);
            saveDialog.setFile("Projector.exe");
            saveDialog.setVisible(true);
            String saveFilename = saveDialog.getFile();
            if (saveFilename != null) {
                String savePath = saveDialog.getDirectory() + saveFilename;
                FileOutputStream fileOutputStream = new FileOutputStream(savePath);
                fileOutputStream.write(outputData);
                fileOutputStream.close();

                JOptionPane.showMessageDialog(this, "Projector created successfully!");
                resetButton.setEnabled(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readBytesFromFile(File file) {
        try {
            return Files.readAllBytes(Paths.get(file.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void reset() {
        exeButton.setEnabled(true);
        swfButton.setEnabled(false);
        createButton.setEnabled(false);
        resetButton.setEnabled(false);
        exeFile = null;
        swfFile = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
