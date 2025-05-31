/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
/**
 *
 * @author Solkhann
 */

/*
 * JFrame yang berfungsi sebagai halaman menu utama game Sudoku.
 * Memungkinkan pemain memilih level, memulai game baru, melanjutkan game, atau melihat riwayat.
 */
public class MainMenuPage extends JFrame {
    private JComboBox<String> levelComboBox;
    private JButton newGameButton;
    private JButton resumeGameButton;
    private JButton historyButton;
    private JLabel titleLabel;

    // Daftar level kesulitan yang tersedia
    private final String[] difficultyLevels = {"Beginner", "Easy", "Medium", "Hard", "Extreme"};

    /*
     * Konstruktor untuk MainMenuPage.
     */
    public MainMenuPage() {
        initComponents();
        this.setTitle("Sudoku - Main Menu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 350); // Ukuran window yang sesuai
        this.setLocationRelativeTo(null); // Tampilkan di tengah layar
        this.setResizable(false);
    }

    /*
     * Menginisialisasi dan mengatur tata letak komponen UI.
     */
    private void initComponents() {
        // Panel utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // Judul
        titleLabel = new JLabel("Sudoku Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel untuk pilihan level dan tombol
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Jarak antar komponen

        // Label dan ComboBox untuk Level
        JLabel levelLabel = new JLabel("Pilih Level:");
        levelLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(levelLabel, gbc);

        levelComboBox = new JComboBox<>(difficultyLevels);
        levelComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        ((JLabel)levelComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER); // Teks ComboBox ke tengah
        centerPanel.add(levelComboBox, gbc);

        // Tombol-tombol
        newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        newGameButton.setPreferredSize(new Dimension(150, 40));
        centerPanel.add(newGameButton, gbc);

        resumeGameButton = new JButton("Resume Game");
        resumeGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        resumeGameButton.setPreferredSize(new Dimension(150, 40));
        resumeGameButton.setVisible(false); // Awalnya tidak terlihat, diatur oleh controller
        centerPanel.add(resumeGameButton, gbc);

        historyButton = new JButton("History");
        historyButton.setFont(new Font("Arial", Font.BOLD, 16));
        historyButton.setPreferredSize(new Dimension(150, 40));
        centerPanel.add(historyButton, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        this.add(mainPanel);
    }

    /* Mendapatkan level kesulitan yang dipilih. */
    public String getSelectedDifficulty() {
        return (String) levelComboBox.getSelectedItem();
    }

    /* Mengatur visibilitas tombol Resume Game. */
    public void setResumeButtonVisible(boolean visible) {
        resumeGameButton.setVisible(visible);
    }

    // Metode untuk menambahkan ActionListener ke tombol
    /* Menambahkan ActionListener ke ComboBox level. */
    public void addLevelComboBoxListener(ActionListener listener) {
        levelComboBox.addActionListener(listener);
    }
    /* Menambahkan ActionListener ke tombol New Game. */
    public void addNewGameListener(ActionListener listener) {
        newGameButton.addActionListener(listener);
    }
    /* Menambahkan ActionListener ke tombol Resume Game. */
    public void addResumeGameListener(ActionListener listener) {
        resumeGameButton.addActionListener(listener);
    }
    /* Menambahkan ActionListener ke tombol History. */
    public void addHistoryListener(ActionListener listener) {
        historyButton.addActionListener(listener);
    }

    /* Menampilkan pesan konfirmasi. */
    public boolean showConfirmDialog(String message, String title) {
        int response = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return response == JOptionPane.YES_OPTION;
    }

    /* Menampilkan pesan informasi. */
    public void showMessageDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}
