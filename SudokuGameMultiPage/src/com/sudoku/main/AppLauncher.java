/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.sudoku.main;

/**
 *
 * @author Solkhann
 */
import com.sudoku.view.MainMenuPage;
import com.sudoku.controller.GameController; // Import GameController
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Kelas utama untuk meluncurkan aplikasi game Sudoku.
 * Menginisialisasi MainMenuPage dan GameController.
 */
public class AppLauncher {
    public static void main(String[] args) {
        // Menjalankan GUI di Event Dispatch Thread (EDT) Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Mengatur Look and Feel agar lebih sesuai dengan sistem operasi pengguna
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Gagal mengatur Look and Feel sistem: " + e.getMessage());
                // Bisa juga coba Nimbus atau Metal sebagai fallback jika diperlukan
                // try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } 
                // catch (Exception ex) { System.err.println("Gagal mengatur Look and Feel default: " + ex.getMessage()); }
            }

            MainMenuPage mainMenu = new MainMenuPage();
            // GameController sekarang diinisialisasi dengan MainMenuPage
            new GameController(mainMenu); 
            mainMenu.setVisible(true);
        });
    }
}
