/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.view;
import com.sudoku.model.GameHistoryEntry; // Jika perlu menampilkan sesuatu terkait histori di sini
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List; // Untuk parameter displayGameHistory (jika dipindah ke sini)
/**
 *
 * @author Solkhann
 */

/*
 * JFrame yang berfungsi sebagai halaman papan permainan Sudoku.
 * Menampilkan grid Sudoku, timer, status, dan tombol-tombol aksi permainan.
 */
public class BoardGamePage extends JFrame {
    private BoardPanel boardPanel;
    private JLabel titleLabel; // Menampilkan "Sudoku Level: [Level]"
    private JLabel timerLabel;
    private JLabel statusLabel;

    private JButton resetButton;
    private JButton undoButton; // Back To Previous Step
    private JButton hintButton;
    private JButton saveButton;
    private JButton checkButton;
    private JButton solveButton;
    private JButton backToMenuButton;

    /*
     * Konstruktor untuk BoardGamePage.
     * @param gameLevel Tingkat kesulitan game yang dimainkan.
     */
    public BoardGamePage(String gameLevel) {
        initComponents(gameLevel);
        this.setTitle("Sudoku Game - " + gameLevel);
        // Gunakan DISPOSE_ON_CLOSE agar saat window ini ditutup, aplikasi tidak exit
        // jika MainMenuPage masih ada atau akan ditampilkan lagi.
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        this.setMinimumSize(new Dimension(500, 650)); // Ukuran minimum
        this.pack();
        this.setLocationRelativeTo(null); // Tampilkan di tengah layar
    }

    /*
     * Menginisialisasi dan mengatur tata letak komponen UI.
     * @param gameLevel Tingkat kesulitan untuk ditampilkan di judul.
     */
    private void initComponents(String gameLevel) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Atas (Judul, Timer, Status)
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // 3 baris, 1 kolom
        titleLabel = new JLabel("Sudoku Level: " + gameLevel, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel = new JLabel("Time: 00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusLabel = new JLabel("Game Started. Good luck!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        topPanel.add(titleLabel);
        topPanel.add(timerLabel);
        topPanel.add(statusLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Board Panel (Tengah)
        boardPanel = new BoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Panel Kontrol Bawah (Tombol-tombol)
        JPanel bottomControlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        resetButton = new JButton("Reset");
        undoButton = new JButton("Undo Step");
        hintButton = new JButton("Hint");
        saveButton = new JButton("Save");
        checkButton = new JButton("Check");
        solveButton = new JButton("Solve");
        backToMenuButton = new JButton("Back to Menu");

        Font buttonFont = new Font("Arial", Font.PLAIN, 12);
        resetButton.setFont(buttonFont);
        undoButton.setFont(buttonFont);
        hintButton.setFont(buttonFont);
        saveButton.setFont(buttonFont);
        checkButton.setFont(buttonFont);
        solveButton.setFont(buttonFont);
        backToMenuButton.setFont(buttonFont);

        // Baris 1 tombol
        gbc.gridx = 0; gbc.gridy = 0; bottomControlPanel.add(resetButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0; bottomControlPanel.add(undoButton, gbc);
        gbc.gridx = 2; gbc.gridy = 0; bottomControlPanel.add(hintButton, gbc);

        // Baris 2 tombol
        gbc.gridx = 0; gbc.gridy = 1; bottomControlPanel.add(saveButton, gbc);
        gbc.gridx = 1; gbc.gridy = 1; bottomControlPanel.add(checkButton, gbc);
        gbc.gridx = 2; gbc.gridy = 1; bottomControlPanel.add(solveButton, gbc);

        // Baris 3 tombol (Back to Menu membentang)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; 
        bottomControlPanel.add(backToMenuButton, gbc);

        mainPanel.add(bottomControlPanel, BorderLayout.SOUTH);
        this.add(mainPanel);
    }

    /* Mendapatkan BoardPanel. */
    public BoardPanel getBoardPanel() { return boardPanel; }

    /* Mengupdate tampilan timer. */
    public void updateTimerDisplay(int secondsElapsed) {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    /* Mengatur pesan status. */
    public void setStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 100, 0)); // Hijau tua untuk non-error
    }

    /* Menampilkan notifikasi informasi. */
    public void showInfoNotification(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        if (boardPanel != null) boardPanel.clearActiveCellHighlighting();
    }

    /* Menampilkan notifikasi error. */
    public void showErrorNotification(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        if (boardPanel != null) boardPanel.clearActiveCellHighlighting();
    }

    /* Mengubah judul window untuk merefleksikan level saat ini. */
    public void updateGameLevelTitle(String newLevel) {
        titleLabel.setText("Sudoku Level: " + newLevel);
        this.setTitle("Sudoku Game - " + newLevel);
    }

    // Metode untuk menambahkan ActionListener ke tombol-tombol
    public void addResetListener(ActionListener listener) { resetButton.addActionListener(listener); }
    public void addUndoListener(ActionListener listener) { undoButton.addActionListener(listener); }
    public void addHintListener(ActionListener listener) { hintButton.addActionListener(listener); }
    public void addSaveListener(ActionListener listener) { saveButton.addActionListener(listener); }
    public void addCheckListener(ActionListener listener) { checkButton.addActionListener(listener); }
    public void addSolveListener(ActionListener listener) { solveButton.addActionListener(listener); }
    public void addBackToMenuListener(ActionListener listener) { backToMenuButton.addActionListener(listener); }
}