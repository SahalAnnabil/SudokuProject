/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.view;
import com.sudoku.util.SudokuConstant; // << IMPORT KONSTANTA
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
        initComponents(gameLevel); // Panggil initComponents dulu
        applyCustomStyles();    // Panggil metode baru untuk styling
        this.setTitle("NAKudoku Game - " + gameLevel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(525, 680)); // Sesuaikan jika perlu
        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    /*
     * Menginisialisasi dan mengatur tata letak komponen UI.
     * @param gameLevel Tingkat kesulitan untuk ditampilkan di judul.
     */
    private void initComponents(String gameLevel) {
        mainPanelForStyle = new JPanel(new BorderLayout(10, 10));
        // Tambahkan padding di sini: 15 pixel di semua sisi
        mainPanelForStyle.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel Atas (Judul, Timer, Status)
        topPanelForStyle = new JPanel(new GridLayout(3, 1, 5, 5));
        // Tambahkan padding juga untuk panel atas jika diinginkan, atau biarkan
        topPanelForStyle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Padding bawah untuk panel atas
        titleLabel = new JLabel("NAKudoku Level: " + gameLevel, SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 00:00", SwingConstants.CENTER);
        statusLabel = new JLabel("Game Started. Good luck!", SwingConstants.CENTER);
        topPanelForStyle.add(titleLabel);
        topPanelForStyle.add(timerLabel);
        topPanelForStyle.add(statusLabel);
        mainPanelForStyle.add(topPanelForStyle, BorderLayout.NORTH);

        // Board Panel (Tengah)
        boardPanel = new BoardPanel(); // BoardPanel akan di-style di kelasnya sendiri
        mainPanelForStyle.add(boardPanel, BorderLayout.CENTER);

        // Panel Kontrol Bawah (Tombol-tombol)
        bottomControlPanelForStyle = new JPanel(new GridBagLayout());
        // Tambahkan padding atas untuk panel tombol
        bottomControlPanelForStyle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Agar tombol bisa mengisi ruang horizontal jika perlu

        resetButton = new JButton("Reset");
        undoButton = new JButton("Undo");
        hintButton = new JButton("Hint");
        saveButton = new JButton("Save");
        checkButton = new JButton("Check");
        solveButton = new JButton("Solve");
        backToMenuButton = new JButton("Back to Menu");
        
        // Baris 1 tombol
        gbc.gridx = 0; gbc.gridy = 0; bottomControlPanelForStyle.add(resetButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0; bottomControlPanelForStyle.add(undoButton, gbc);
        gbc.gridx = 2; gbc.gridy = 0; bottomControlPanelForStyle.add(hintButton, gbc);
        
        // Baris 2 tombol
        gbc.gridx = 0; gbc.gridy = 1; bottomControlPanelForStyle.add(saveButton, gbc);
        gbc.gridx = 1; gbc.gridy = 1; bottomControlPanelForStyle.add(checkButton, gbc);
        gbc.gridx = 2; gbc.gridy = 1; bottomControlPanelForStyle.add(solveButton, gbc);

        // Baris 3 tombol (Back to Menu membentang)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; 
        bottomControlPanelForStyle.add(backToMenuButton, gbc);

        mainPanelForStyle.add(bottomControlPanelForStyle, BorderLayout.SOUTH);
        this.add(mainPanelForStyle);
    }
    
    // Variabel anggota untuk menyimpan referensi komponen yang akan di-style
    private JPanel mainPanelForStyle;
    private JPanel topPanelForStyle;
    private JPanel bottomControlPanelForStyle;
    // titleLabel, timerLabel, statusLabel, boardPanel, dan tombol-tombol sudah jadi atribut kelas

    /*
     * Menerapkan gaya kustom (warna dan font) ke komponen UI.
     */
    private void applyCustomStyles() {
        // Latar Belakang Utama
        this.getContentPane().setBackground(SudokuConstant.BACKGROUND_COLOR);
        if (mainPanelForStyle != null) mainPanelForStyle.setBackground(SudokuConstant.BACKGROUND_COLOR);
        if (topPanelForStyle != null) topPanelForStyle.setBackground(SudokuConstant.BACKGROUND_COLOR);
        if (bottomControlPanelForStyle != null) bottomControlPanelForStyle.setBackground(SudokuConstant.BACKGROUND_COLOR);

        // Judul, Timer, Status
        JLabel[] textLabels = {titleLabel, timerLabel, statusLabel};
        Font[] labelFonts = {SudokuConstant.TITLE_FONT.deriveFont(20f), SudokuConstant.STATUS_FONT, SudokuConstant.STATUS_FONT}; // Sesuaikan ukuran font judul
        for (int i = 0; i < textLabels.length; i++) {
            if (textLabels[i] != null) {
                textLabels[i].setFont(labelFonts[i]);
                textLabels[i].setForeground(SudokuConstant.TEXT_COLOR);
            }
        }
        if (statusLabel != null) statusLabel.setFont(SudokuConstant.STATUS_FONT.deriveFont(Font.ITALIC));


        // Tombol-tombol
        JButton[] buttons = {resetButton, undoButton, hintButton, saveButton, checkButton, solveButton, backToMenuButton};
        for (JButton button : buttons) {
            if (button != null) {
                button.setFont(SudokuConstant.BUTTON_FONT);
                button.setBackground(SudokuConstant.PRIMARY_ACCENT_COLOR);
                button.setForeground(SudokuConstant.BACKGROUND_COLOR);
                button.setFocusPainted(false);
                // button.setPreferredSize(new Dimension(120, 35)); // Ukuran disesuaikan GridBagLayout
            }
        }
        
        // BoardPanel akan di-style di kelasnya sendiri
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
        statusLabel.setForeground(isError ? Color.RED : new Color(9, 245, 5)); // Hijau tua untuk non-error
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