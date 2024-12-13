package generic;

import javax.sound.sampled.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;

public class AudioBookPlayer {
    private JFrame frame;
    private JLabel imageLabel, audioNameLabel, currentTimeLabel, totalTimeLabel;
    private JButton playPauseButton, nextButton, prevButton;
    private JSlider seekSlider;
    private JList<String> audioList;
    private DefaultListModel<String> listModel;

    private List<String> audioFiles;  // List to hold audio files
    private List<String> imageFiles;  // List to hold image files
    private int currentChapterIndex;
    private Clip audioClip;
    private long clipPosition;
    private Timer timer;
    private boolean isPaused;

    public AudioBookPlayer() {
        audioFiles = loadFiles("C:\\Users\\Sunil\\eclipse-workspace\\Audiobook\\audio", ".wav");  // Load audio files
        imageFiles = loadFiles("C:\\Users\\Sunil\\eclipse-workspace\\Audiobook\\images", ".jpg");  // Load image files
        currentChapterIndex = 0;

        // Set up main frame
        frame = new JFrame("Hearme");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Spotify-Inspired Layout with Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250); // Left side width for playlist
        frame.add(splitPane, BorderLayout.CENTER);

        // Left Panel (Playlist)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        listModel = new DefaultListModel<>();
        audioFiles.forEach(file -> listModel.addElement(new File(file).getName()));

        audioList = new JList<>(listModel);
        audioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        audioList.setFont(new Font("Arial", Font.PLAIN, 14));
        audioList.setBackground(new Color(50, 50, 50));
        audioList.setForeground(Color.WHITE);
        audioList.setFixedCellHeight(50);
        audioList.setCellRenderer(new AudioListCellRenderer());  // Custom renderer for playlist

        audioList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                playSelectedAudio();
            }
        });

        JScrollPane scrollPane = new JScrollPane(audioList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Right Panel (Main UI with controls)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // Audio name and control panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(40, 40, 40));

        audioNameLabel = new JLabel("Audio: " + getAudioName(), SwingConstants.CENTER);
        audioNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        audioNameLabel.setForeground(Color.WHITE);

        topPanel.add(audioNameLabel, BorderLayout.NORTH);

        // Timing Panel (Current time, seek bar, total time)
        JPanel timingPanel = new JPanel();
        timingPanel.setLayout(new BorderLayout());
        timingPanel.setBackground(new Color(40, 40, 40));

        currentTimeLabel = new JLabel("00:00", SwingConstants.LEFT);
        currentTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        currentTimeLabel.setForeground(Color.WHITE);

        totalTimeLabel = new JLabel("00:00", SwingConstants.RIGHT);
        totalTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalTimeLabel.setForeground(Color.WHITE);

        seekSlider = new JSlider();
        seekSlider.setBackground(new Color(50, 50, 50));
        seekSlider.setForeground(new Color(255, 0, 0));
        seekSlider.setFocusable(false);
        seekSlider.addChangeListener(e -> {
            if (seekSlider.getValueIsAdjusting() && audioClip != null) {
                long seekTime = seekSlider.getValue() * 1_000_000L;
                audioClip.setMicrosecondPosition(seekTime);
                currentTimeLabel.setText(formatTime(seekSlider.getValue()));
            }
        });

        timingPanel.add(currentTimeLabel, BorderLayout.WEST);
        timingPanel.add(seekSlider, BorderLayout.CENTER);
        timingPanel.add(totalTimeLabel, BorderLayout.EAST);

        topPanel.add(timingPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Image/Album art section
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateImage();  // Initially update image
        mainPanel.add(imageLabel, BorderLayout.CENTER);

        // Control buttons (Previous, Play/Pause, Next)
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout());
        controlsPanel.setBackground(new Color(30, 30, 30));

        prevButton = createButton("Prev");
        playPauseButton = createButton("Play");
        nextButton = createButton("Next");

        controlsPanel.add(prevButton);
        controlsPanel.add(playPauseButton);
        controlsPanel.add(nextButton);

        mainPanel.add(controlsPanel, BorderLayout.SOUTH);

        playPauseButton.addActionListener(e -> togglePlayPause());
        nextButton.addActionListener(e -> moveToNextChapter());
        prevButton.addActionListener(e -> moveToPreviousChapter());

        splitPane.setRightComponent(mainPanel);
        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 50));
        return button;
    }

    private List<String> loadFiles(String folderPath, String extension) {
        List<String> files = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(extension)) {
                    files.add(file.getAbsolutePath());
                }
            }
            files.sort(String::compareTo);
        } else {
            JOptionPane.showMessageDialog(frame, "Folder not found: " + folderPath);
        }
        return files;
    }

    private String getAudioName() {
        if (!audioFiles.isEmpty()) {
            File file = new File(audioFiles.get(currentChapterIndex));
            return file.getName();
        }
        return "No Audio Available";
    }

    private void updateAudioAndImage() {
        updateAudioName();
        updateImage();
        resetSeekBar();
    }

    private void updateAudioName() {
        audioNameLabel.setText("Audio: " + getAudioName());
    }

    private void updateImage() {
        if (imageFiles.isEmpty()) {
            imageLabel.setText("No images available.");
        } else {
            // Update image corresponding to the current audio
            if (currentChapterIndex < imageFiles.size()) {
                ImageIcon icon = new ImageIcon(imageFiles.get(currentChapterIndex));
                Image scaledImage = icon.getImage().getScaledInstance(500, 400, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setIcon(null);  // If no image available for current audio
            }
        }
    }

    private void togglePlayPause() {
        if (audioClip == null) {
            playAudio(audioFiles.get(currentChapterIndex));
        } else if (isPaused) {
            resumeAudio();
        } else {
            pauseAudio();
        }
    }

    private void playSelectedAudio() {
        int selectedIndex = audioList.getSelectedIndex();
        if (selectedIndex != -1) {
            currentChapterIndex = selectedIndex;
            stopAudio();
            updateAudioAndImage();
            playAudio(audioFiles.get(currentChapterIndex));
        }
    }

    private void playAudio(String filePath) {
        try {
            if (audioClip == null || !audioClip.isOpen()) {
                File audioFile = new File(filePath);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                audioClip = AudioSystem.getClip();
                audioClip.open(audioStream);

                long duration = audioClip.getMicrosecondLength() / 1_000_000;
                seekSlider.setMaximum((int) duration);
                totalTimeLabel.setText(formatTime(duration));
            }
            if (clipPosition > 0) {
                audioClip.setMicrosecondPosition(clipPosition);
            }
            audioClip.start();
            isPaused = false;
            playPauseButton.setText("Pause");
            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {
        if (audioClip != null && audioClip.isRunning()) {
            clipPosition = audioClip.getMicrosecondPosition();
            audioClip.stop();
            isPaused = true;
            playPauseButton.setText("Resume");
            if (timer != null) {
                timer.stop();
            }
        }
    }

    private void resumeAudio() {
        if (audioClip != null && isPaused) {
            audioClip.setMicrosecondPosition(clipPosition);
            audioClip.start();
            isPaused = false;
            playPauseButton.setText("Pause");
            startTimer();
        }
    }

    private void stopAudio() {
        if (audioClip != null) {
            clipPosition = 0;
            audioClip.stop();
            audioClip.close();
            audioClip = null;
        }
        if (timer != null) {
            timer.stop();
        }
    }

    private void moveToNextChapter() {
        if (currentChapterIndex < audioFiles.size() - 1) {
            currentChapterIndex++;
        } else {
            currentChapterIndex = 0;  // Go to the first chapter if at the last chapter
        }
        stopAudio();
        updateAudioAndImage();
        playAudio(audioFiles.get(currentChapterIndex));
    }

    private void moveToPreviousChapter() {
        if (currentChapterIndex > 0) {
            currentChapterIndex--;
        } else {
            currentChapterIndex = audioFiles.size() - 1;  // Go to the last chapter if at the first chapter
        }
        stopAudio();
        updateAudioAndImage();
        playAudio(audioFiles.get(currentChapterIndex));
    }

    private void resetSeekBar() {
        seekSlider.setValue(0);
        currentTimeLabel.setText("00:00");
        totalTimeLabel.setText("00:00");
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (audioClip != null && audioClip.isRunning()) {
                    int currentTime = (int) (audioClip.getMicrosecondPosition() / 1_000_000);
                    seekSlider.setValue(currentTime);
                    currentTimeLabel.setText(formatTime(currentTime));
                }
            }
        });
        timer.start();
    }

    private String formatTime(long seconds) {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    public static void main(String[] args) {
        new AudioBookPlayer();
    }

    // Custom cell renderer for the audio list (playlist)
    private class AudioListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBackground(isSelected ? new Color(40, 40, 40) : new Color(50, 50, 50));
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            label.setText(value.toString());
            return label;
        }
    }
}
