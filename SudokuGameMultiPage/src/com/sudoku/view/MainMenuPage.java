/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.view;
import com.sudoku.util.SudokuConstant;
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
        initComponents(); // Panggil initComponents dulu
        applyCustomStyles(); // Panggil metode baru untuk styling
        this.setTitle("NAKudoku - Main Menu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500); // Sesuaikan ukuran jika perlu
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    /*
     * Menginisialisasi dan mengatur tata letak komponen UI.
     */
    private void initComponents() {
        // Panel utama dengan BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        // mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // Judul
        String titleText = "<html><center>NAKudoku<br>Nawawi Anabil Kent<br>Sudoku Game</center></html>";
        titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding atas dan bawah lebih besar
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel untuk pilihan level dan tombol
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5); // Jarak antar komponen disesuaikan

        // Label dan ComboBox untuk Level
        JLabel levelLabel = new JLabel("Pilih Level:");
        // levelLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Style di applyCustomStyles
        centerPanel.add(levelLabel, gbc);

        levelComboBox = new JComboBox<>(difficultyLevels);
        // ((JLabel)levelComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER); // Style di applyCustomStyles
        centerPanel.add(levelComboBox, gbc);

        // Tombol-tombol
        newGameButton = new JButton("New Game");
        // newGameButton.setPreferredSize(new Dimension(180, 45)); // Ukuran disesuaikan
        centerPanel.add(newGameButton, gbc);

        resumeGameButton = new JButton("Resume Game");
        // resumeGameButton.setPreferredSize(new Dimension(180, 45));
        resumeGameButton.setVisible(false);
        centerPanel.add(resumeGameButton, gbc);
        
        historyButton = new JButton("History");
        // historyButton.setPreferredSize(new Dimension(180, 45));
        centerPanel.add(historyButton, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        this.add(mainPanel);

        // Simpan referensi komponen untuk di-style
        this.mainPanelForStyle = mainPanel;
        this.centerPanelForStyle = centerPanel;
        this.levelLabelForStyle = levelLabel;
    }
    
    // Variabel anggota untuk menyimpan referensi komponen yang akan di-style
    private JPanel mainPanelForStyle;
    private JPanel centerPanelForStyle;
    private JLabel levelLabelForStyle;
    // titleLabel, levelComboBox, newGameButton, resumeGameButton, historyButton sudah jadi atribut kelas
    
    /*
     * Menerapkan gaya kustom (warna dan font) ke komponen UI.
     */
    private void applyCustomStyles() {
        // Latar Belakang Utama
        this.getContentPane().setBackground(SudokuConstant.BACKGROUND_COLOR);
        if (mainPanelForStyle != null) mainPanelForStyle.setBackground(SudokuConstant.BACKGROUND_COLOR);
        if (centerPanelForStyle != null) centerPanelForStyle.setBackground(SudokuConstant.BACKGROUND_COLOR);

        // Judul
        if (titleLabel != null) {
            titleLabel.setFont(SudokuConstant.TITLE_FONT.deriveFont(24f)); // Sedikit lebih kecil agar muat 3 baris
            titleLabel.setForeground(SudokuConstant.TEXT_COLOR);
        }

        // Label Level
        if (levelLabelForStyle != null) {
            levelLabelForStyle.setFont(SudokuConstant.PRIMARY_FONT);
            levelLabelForStyle.setForeground(SudokuConstant.TEXT_COLOR);
        }

        // ComboBox Level
        if (levelComboBox != null) {
            levelComboBox.setFont(SudokuConstant.COMBOBOX_FONT);
            levelComboBox.setForeground(SudokuConstant.BACKGROUND_COLOR);
            levelComboBox.setBackground(SudokuConstant.TEXT_COLOR);
            ((JLabel)levelComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        }

        // Tombol-tombol
        JButton[] buttons = {newGameButton, resumeGameButton, historyButton};
        for (JButton button : buttons) {
            if (button != null) {
                button.setFont(SudokuConstant.BUTTON_FONT);
                button.setBackground(SudokuConstant.PRIMARY_ACCENT_COLOR);
                button.setForeground(SudokuConstant.BACKGROUND_COLOR);
                button.setFocusPainted(false);
                button.setPreferredSize(new Dimension(180, 45));
            }
        }
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
