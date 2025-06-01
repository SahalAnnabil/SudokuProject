/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.controller;

// Tambahkan import yang kurang di sini:
import java.awt.event.KeyListener; // Untuk error: cannot find symbol class KeyListener
import javax.swing.JTextArea;    // Untuk error: cannot find symbol class JTextArea
import javax.swing.JScrollPane;  // Untuk error: cannot find symbol class JScrollPane
import java.awt.Dimension;       // Untuk error: cannot find symbol class Dimension
import java.awt.Point;           // Untuk error: cannot find symbol class Point
import java.util.ArrayList;      // Untuk error: cannot find symbol class ArrayList (meskipun List sudah ada, ArrayList perlu eksplisit jika membuat instance)

// Import yang sudah ada sebelumnya (pastikan tetap ada)
import com.sudoku.model.SudokuBoard;
import com.sudoku.model.SudokuCell;
import com.sudoku.model.GameHistoryEntry;
import com.sudoku.model.MoveRecord;
import com.sudoku.view.MainMenuPage;
import com.sudoku.view.BoardGamePage;
import com.sudoku.view.CellView;
import com.sudoku.util.SudokuGenerator;
import com.sudoku.util.SudokuSolver;
import com.sudoku.db.DatabaseManager;

import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.List;
import java.util.Stack;
import java.util.Random;
/**
 *
 * @author Solkhann
 */

/*
 * Controller utama untuk game Sudoku.
 * Mengelola interaksi antara MainMenuPage, BoardGamePage, model data, dan database.
 * Menangani logika permainan, navigasi antar window, dan fitur-fitur seperti save, load, undo, hint.
 */
public class GameController {
    private MainMenuPage mainMenuPage;
    private BoardGamePage boardGamePage;
    private SudokuBoard currentBoard;    // Papan yang sedang dimainkan di BoardGamePage
    private SudokuBoard initialBoard;    // Papan awal dari puzzle yang digenerate (untuk reset, hint, history)
    private SudokuBoard solvedBoard;     // Papan solusi (untuk hint dan check cepat)
    private SudokuGenerator generator;
    private SudokuSolver solver;
    private DatabaseManager dbManager;

    private Timer gameTimer;
    private int timeElapsedSeconds;
    private String currentPlayingLevel; // Level yang sedang dimainkan atau dipilih
    private boolean gameCurrentlyActive; // Status apakah permainan di BoardGamePage sedang aktif

    private Stack<MoveRecord> moveHistory; // Untuk fitur Undo

    /*
     * Konstruktor untuk GameController.
     * @param mainMenuPage Instance dari MainMenuPage.
     */
    public GameController(MainMenuPage mainMenuPage) {
        this.mainMenuPage = mainMenuPage;
        this.generator = new SudokuGenerator();
        this.solver = new SudokuSolver();
        this.dbManager = new DatabaseManager();
        this.moveHistory = new Stack<>();

        initGameTimer();
        attachListenersToMainMenu();
        updateResumeButtonVisibility(this.mainMenuPage.getSelectedDifficulty()); // Cek saat awal
    }

    /*
     * Menginisialisasi game timer.
     */
    private void initGameTimer() {
        timeElapsedSeconds = 0;
        gameTimer = new Timer(1000, e -> {
            if (gameCurrentlyActive && boardGamePage != null && boardGamePage.isVisible()) {
                timeElapsedSeconds++;
                boardGamePage.updateTimerDisplay(timeElapsedSeconds);
            }
        });
    }

    /*
     * Memasang listener ke komponen di MainMenuPage.
     */
    private void attachListenersToMainMenu() {
        mainMenuPage.addLevelComboBoxListener(e -> 
            updateResumeButtonVisibility(mainMenuPage.getSelectedDifficulty())
        );
        mainMenuPage.addNewGameListener(e -> handleNewGameRequest());
        mainMenuPage.addResumeGameListener(e -> handleResumeGameRequest());
        mainMenuPage.addHistoryListener(e -> handleHistoryRequest());
    }

    /*
     * Memasang listener ke komponen di BoardGamePage.
     * Dipanggil setiap kali BoardGamePage dibuat.
     */
    private void attachListenersToBoardGamePage() {
        if (boardGamePage == null) return;

        // Listener untuk input sel
        CellView[][] cellViews = boardGamePage.getBoardPanel().getCellViews();
        for (int r = 0; r < SudokuBoard.SIZE; r++) {
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                final CellView cellView = cellViews[r][c];
                final int row = r;
                final int col = c;

                // Hapus KeyListener lama jika ada sebelum menambah yang baru
                for(KeyListener kl : cellView.getKeyListeners()){
                    cellView.removeKeyListener(kl);
                }

                cellView.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (cellView.isCellFixed() || !gameCurrentlyActive) return;
                        int newValueFromTextField;
                        try {
                            String text = cellView.getText();
                            if (text.isEmpty()) {
                                newValueFromTextField = 0;
                            } else {
                                newValueFromTextField = Integer.parseInt(text);
                                if (newValueFromTextField < 1 || newValueFromTextField > 9) { // Validasi input dasar
                                // Jika input tidak valid (misal pengguna mengetik 0 atau >9 secara manual meski filter ada)
                                // Kembalikan ke nilai sebelumnya di model atau kosongkan
                                    int oldValueInModel = currentBoard.getCell(row, col).getValue();
                                    cellView.setValueInView(oldValueInModel, false); // Set view ke nilai model lama
                                    return; // Hentikan proses lebih lanjut
                                }
                            }
                        } catch (NumberFormatException ex) {
                            // Jika parse gagal (misalnya pengguna menghapus semua teks jadi kosong, sudah ditangani di atas)
                            // atau jika ada karakter non-numerik lolos filter (seharusnya tidak)
                            int oldValueInModel = currentBoard.getCell(row, col).getValue();
                            cellView.setValueInView(oldValueInModel, false); // Kembalikan ke nilai model lama
                            return;
                        }
                        int oldValueInModel = currentBoard.getCell(row, col).getValue();
                        cellView.setValueInView(newValueFromTextField, false); 

                        if (currentBoard.setCellValue(row, col, newValueFromTextField)) {
                            // Jika nilai benar-benar berubah di model, catat untuk undo
                            if (oldValueInModel != newValueFromTextField) {
                                moveHistory.push(new MoveRecord(row, col, oldValueInModel, newValueFromTextField));
                            }
                            boardGamePage.setStatusMessage("Cell (" + (row + 1) + "," + (col + 1) + ") = " + (newValueFromTextField == 0 ? " " : newValueFromTextField), false);
                        } else {
                            // Jika setCellValue gagal (misal karena sel fixed, seharusnya sudah dicek)
                            cellView.setValueInView(oldValueInModel, false); // Kembalikan ke nilai lama di view
                        }
                    }
                });
            }
        }

        // Listener untuk tombol-tombol di BoardGamePage
        boardGamePage.addResetListener(e -> handleResetBoard());
        boardGamePage.addUndoListener(e -> handleUndoMove());
        boardGamePage.addHintListener(e -> handleHintRequest());
        boardGamePage.addSaveListener(e -> handleSaveGame());
        boardGamePage.addCheckListener(e -> handleCheckBoard());
        boardGamePage.addSolveListener(e -> handleSolveBoard());
        boardGamePage.addBackToMenuListener(e -> handleBackToMainMenu());

        // Listener untuk event window closing di BoardGamePage
        boardGamePage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleBackToMainMenu(); // Perlakukan sama seperti tombol "Back to Main Menu"
            }
        });
    }

    /*
     * Memperbarui visibilitas tombol "Resume Game" berdasarkan apakah ada
     * data tersimpan untuk level yang dipilih.
     * @param level Tingkat kesulitan yang dipilih.
     */
    private void updateResumeButtonVisibility(String level) {
        String[] savedData = dbManager.loadGameProgress(level);
        mainMenuPage.setResumeButtonVisible(savedData != null);
    }

    /*
     * Menangani permintaan untuk memulai permainan baru.
     */
    private void handleNewGameRequest() {
        currentPlayingLevel = mainMenuPage.getSelectedDifficulty();
        String[] existingSave = dbManager.loadGameProgress(currentPlayingLevel);

        if (existingSave != null) {
            boolean proceed = mainMenuPage.showConfirmDialog(
                "Memulai 'New Game' akan menghapus progres tersimpan untuk level " + currentPlayingLevel + ".\nLanjutkan?",
                "Konfirmasi New Game"
            );
            if (!proceed) {
                return; // Pengguna membatalkan
            }
            dbManager.deleteSavedGame(currentPlayingLevel); // Hapus save lama
        }

        startNewGameInternal(currentPlayingLevel, null);
    }

    /*
     * Menangani permintaan untuk melanjutkan permainan.
     */
    private void handleResumeGameRequest() {
        currentPlayingLevel = mainMenuPage.getSelectedDifficulty();
        String[] loadedData = dbManager.loadGameProgress(currentPlayingLevel);

        if (loadedData != null && loadedData.length == 3) {
            startNewGameInternal(currentPlayingLevel, loadedData);
        } else {
            mainMenuPage.showMessageDialog(
                "Tidak ada data permainan tersimpan untuk level " + currentPlayingLevel + ".",
                "Resume Gagal", JOptionPane.ERROR_MESSAGE
            );
            updateResumeButtonVisibility(currentPlayingLevel); // Update jika data korup/hilang
        }
    }

    /*
     * Logika internal untuk memulai atau melanjutkan permainan dan menampilkan BoardGamePage.
     * @param level Tingkat kesulitan.
     * @param loadedData Array data yang dimuat (untuk resume), atau null (untuk new game).
     * Format: {initialBoardString, currentBoardString, timeElapsedSecondsString}
     */
    private void startNewGameInternal(String level, String[] loadedData) {
        currentPlayingLevel = level;
        moveHistory.clear(); // Bersihkan riwayat langkah untuk game baru/resume

        if (loadedData != null) { // Resume game
            String initialBoardStr = loadedData[0];
            String currentBoardStr = loadedData[1];
            timeElapsedSeconds = Integer.parseInt(loadedData[2]);

            initialBoard = new SudokuBoard(); // Papan puzzle asli
            initialBoard.loadBoardFromStrings(initialBoardStr, initialBoardStr); // Fixed cells from initial string

            currentBoard = new SudokuBoard(); // Papan yang dimainkan user
            currentBoard.loadBoardFromStrings(currentBoardStr, initialBoardStr); // Load current state, fixed from initial

        } else { // New game
            initialBoard = generator.generatePuzzle(currentPlayingLevel);
            currentBoard = new SudokuBoard(); // Buat papan kosong
            initialBoard.copyTo(currentBoard); // Salin puzzle awal ke papan permainan
            timeElapsedSeconds = 0;
        }

        // Buat papan solusi untuk fitur Hint dan Check Cepat
        solvedBoard = new SudokuBoard();
        initialBoard.copyTo(solvedBoard); // Salin puzzle awal
        if (!solver.solve(solvedBoard)) {
            // Seharusnya tidak terjadi jika generator menghasilkan puzzle valid
            System.err.println("PERINGATAN: Puzzle yang digenerate untuk level " + level + " tidak memiliki solusi!");
            // Handle error ini, mungkin dengan generate ulang atau pesan error
            mainMenuPage.showMessageDialog("Gagal membuat puzzle yang valid. Coba lagi.", "Error Puzzle", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (boardGamePage != null && boardGamePage.isVisible()) {
            boardGamePage.dispose(); // Tutup board game lama jika ada
        }
        boardGamePage = new BoardGamePage(currentPlayingLevel);
        attachListenersToBoardGamePage(); // Pasang listener ke window baru

        if (boardGamePage != null && boardGamePage.getBoardPanel() != null) {
            boardGamePage.getBoardPanel().updateBoardView(currentBoard);
        }
        
        boardGamePage.updateTimerDisplay(timeElapsedSeconds);
        boardGamePage.setStatusMessage("Game started for level: " + currentPlayingLevel, false);

        gameCurrentlyActive = true;
        if (!gameTimer.isRunning()) gameTimer.start(); else gameTimer.restart();

        mainMenuPage.setVisible(false); // Sembunyikan menu utama
        boardGamePage.setVisible(true);
    }

    /*
     * Menangani permintaan untuk melihat riwayat permainan.
     */
    private void handleHistoryRequest() {
        String selectedLevelForHistory = mainMenuPage.getSelectedDifficulty();
        List<GameHistoryEntry> history = dbManager.getGameHistory(selectedLevelForHistory);

        if (history.isEmpty()) {
            mainMenuPage.showMessageDialog(
                "Tidak ada riwayat permainan untuk level " + selectedLevelForHistory + ".",
                "Riwayat Permainan", JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            // Tampilkan history dalam dialog baru atau panel khusus
            // Untuk sederhana, kita pakai JTextArea dalam JOptionPane
            StringBuilder sb = new StringBuilder("Riwayat Permainan - Level: " + selectedLevelForHistory + "\n\n");
            for (GameHistoryEntry entry : history) {
                sb.append(entry.toString()).append("\n");
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(mainMenuPage, scrollPane, "Riwayat Permainan", JOptionPane.PLAIN_MESSAGE);
        }
    }

    // --- Metode Handler untuk BoardGamePage ---

    /*
     * Mengosongkan semua input pengguna di papan permainan.
     */
    private void handleResetBoard() {
        if (!gameCurrentlyActive || currentBoard == null || initialBoard == null) return;
        // Konfirmasi sebelum reset
        int response = JOptionPane.showConfirmDialog(boardGamePage,
            "Apakah Anda yakin ingin mereset semua isian Anda?",
            "Konfirmasi Reset", JOptionPane.YES_NO_OPTION);
        if (response != JOptionPane.YES_OPTION) return;

        initialBoard.copyTo(currentBoard); // Kembalikan currentBoard ke kondisi initialBoard
        moveHistory.clear(); // Hapus riwayat langkah
        boardGamePage.getBoardPanel().updateBoardView(currentBoard);
        boardGamePage.setStatusMessage("Papan direset ke puzzle awal.", false);
    }

    /*
     * Kembali ke satu langkah sebelumnya (Undo).
     */
    private void handleUndoMove() {
        if (!gameCurrentlyActive || moveHistory.isEmpty()) {
            boardGamePage.setStatusMessage("Tidak ada langkah untuk di-undo.", true);
            return;
        }
        MoveRecord lastMove = moveHistory.pop();
        currentBoard.setCellValue(lastMove.getRow(), lastMove.getCol(), lastMove.getPreviousValue());
        // Update tampilan sel yang di-undo
        boardGamePage.getBoardPanel().updateSingleCellView(lastMove.getRow(), lastMove.getCol(), lastMove.getPreviousValue(), false);
        // Validasi ulang papan untuk menghapus tanda konflik jika ada
        currentBoard.isValidBoard();
        boardGamePage.getBoardPanel().updateBoardView(currentBoard); // Update seluruh papan untuk status konflik
        boardGamePage.setStatusMessage("Langkah terakhir di-undo.", false);
    }

    /*
     * Memberikan satu petunjuk angka di sel kosong.
     */
    private void handleHintRequest() {
        if (!gameCurrentlyActive || currentBoard == null || solvedBoard == null) return;

        List<Point> emptyCells = new ArrayList<>();
        for (int r = 0; r < SudokuBoard.SIZE; r++) {
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                if (currentBoard.getCell(r, c).getValue() == 0) {
                    emptyCells.add(new Point(c, r)); // x adalah col, y adalah row
                }
            }
        }

        if (emptyCells.isEmpty()) {
            boardGamePage.setStatusMessage("Tidak ada sel kosong untuk diberi petunjuk.", false);
            return;
        }

        Random random = new Random();
        Point randomEmptyCell = emptyCells.get(random.nextInt(emptyCells.size()));
        int r = randomEmptyCell.y;
        int c = randomEmptyCell.x;
        int solutionValue = solvedBoard.getCell(r, c).getValue();

        if (solutionValue != 0) {
            int oldValue = currentBoard.getCell(r, c).getValue();
            currentBoard.setCellValue(r, c, solutionValue);
            moveHistory.push(new MoveRecord(r, c, oldValue, solutionValue)); // Catat hint sebagai langkah
            boardGamePage.getBoardPanel().updateSingleCellView(r, c, solutionValue, true); // isHint = true
            boardGamePage.setStatusMessage("Petunjuk diberikan di (" + (r + 1) + "," + (c + 1) + ").", false);
        } else {
            boardGamePage.setStatusMessage("Gagal mendapatkan petunjuk (solusi tidak ditemukan untuk sel).", true);
        }
    }

    /*
     * Menyimpan progres permainan saat ini.
     */
    private void handleSaveGame() {
        if (currentBoard == null || currentPlayingLevel == null) {
             boardGamePage.showErrorNotification("Tidak ada game aktif untuk disimpan.", "Save Error");
             return;
        }
        // Sync model dari view sebelum save, jika ada input yang belum ter-trigger keyReleased
        syncGameBoardModelFromView();

        String initialBoardStr = initialBoard.getBoardString();
        String currentBoardStr = currentBoard.getBoardString();

        if (dbManager.saveOrUpdateGameProgress(currentPlayingLevel, initialBoardStr, currentBoardStr, timeElapsedSeconds)) {
            boardGamePage.showInfoNotification("Progres permainan berhasil disimpan!", "Game Saved");
            boardGamePage.setStatusMessage("Game disimpan untuk level: " + currentPlayingLevel, false);
            updateResumeButtonVisibility(currentPlayingLevel); // Update di menu jika masih terbuka
        } else {
            boardGamePage.showErrorNotification("Gagal menyimpan progres permainan.", "Save Error");
            boardGamePage.setStatusMessage("Gagal menyimpan game.", true);
        }
    }

    /*
     * Helper untuk memastikan model papan game sinkron dengan input terakhir di view.
     */
    private void syncGameBoardModelFromView() {
        if (boardGamePage == null || currentBoard == null) return;
        CellView[][] cellViews = boardGamePage.getBoardPanel().getCellViews();
        for (int r = 0; r < SudokuBoard.SIZE; r++) {
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                if (!currentBoard.getCell(r,c).isFixed()) {
                    currentBoard.setCellValue(r, c, cellViews[r][c].getIntValue());
                }
            }
        }
    }


    /*
     * Memeriksa apakah papan Sudoku sudah terisi dengan benar.
     */
    private void handleCheckBoard() {
        if (!gameCurrentlyActive || currentBoard == null) return;
        syncGameBoardModelFromView(); // Pastikan model sinkron

        boolean isValidNow = currentBoard.isValidBoard(); // Ini akan update flag konflik di model
        boardGamePage.getBoardPanel().updateBoardView(currentBoard); // Update view dengan status konflik

        // Panggilan revalidate dan repaint pada BoardPanel setelah semua sel diperbarui
        if (boardGamePage != null && boardGamePage.getBoardPanel() != null) {
            boardGamePage.getBoardPanel().revalidate();
            boardGamePage.getBoardPanel().repaint();
        }

        if (currentBoard.isSolved()) {
            gameCurrentlyActive = false;
            gameTimer.stop();
            boardGamePage.showInfoNotification(
                "Selamat! Sudoku berhasil dipecahkan dalam " + formatTime(timeElapsedSeconds) + "!",
                "Permainan Selesai!"
            );
            boardGamePage.setStatusMessage("Sudoku terpecahkan!", false);
            dbManager.deleteSavedGame(currentPlayingLevel); // Hapus save game karena sudah selesai
            saveGameToHistoryDb(true); // Simpan ke riwayat (solved by user)
            updateResumeButtonVisibility(currentPlayingLevel); // Resume button hilang
        } else if (isValidNow) {
            boardGamePage.showInfoNotification("Papan saat ini valid, tetapi belum selesai.", "Hasil Cek");
            boardGamePage.setStatusMessage("Papan valid, lanjutkan!", false);
        } else {
            boardGamePage.showErrorNotification("Terdapat kesalahan/konflik di papan. Periksa sel yang ditandai.", "Hasil Cek");
            boardGamePage.setStatusMessage("Papan mengandung kesalahan!", true);
        }
    }

    /*
     * Langsung mengisi semua sel kosong dengan solusi yang benar.
     */
    private void handleSolveBoard() {
        if (!gameCurrentlyActive || currentBoard == null || solvedBoard == null) return;

        int response = JOptionPane.showConfirmDialog(boardGamePage,
            "Ini akan menampilkan solusi dan mengakhiri permainan Anda saat ini.\nLanjutkan?",
            "Konfirmasi Tampilkan Solusi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (response != JOptionPane.YES_OPTION) return;

        solvedBoard.copyTo(currentBoard); // Salin solusi ke papan permainan
        moveHistory.clear(); // Tidak ada undo setelah solusi ditampilkan

        gameCurrentlyActive = false;
        gameTimer.stop();
        boardGamePage.getBoardPanel().updateBoardView(currentBoard);
        boardGamePage.showInfoNotification("Solusi Sudoku ditampilkan.", "Solusi Ditampilkan");
        boardGamePage.setStatusMessage("Solusi ditampilkan. Permainan selesai.", false);
        dbManager.deleteSavedGame(currentPlayingLevel); // Hapus save game
        saveGameToHistoryDb(false); // Simpan ke riwayat (solved by system/solver)
        updateResumeButtonVisibility(currentPlayingLevel); // Resume button hilang
    }

    /*
     * Kembali ke halaman Main Menu.
     */
    private void handleBackToMainMenu() {
        if (boardGamePage != null && boardGamePage.isVisible()) {
            if (gameCurrentlyActive) { // Jika game masih aktif, tanya mau save atau tidak
                int choice = JOptionPane.showConfirmDialog(boardGamePage,
                        "Apakah Anda ingin menyimpan progres sebelum kembali ke menu?",
                        "Simpan Permainan?",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    handleSaveGame();
                } else if (choice == JOptionPane.CANCEL_OPTION) {
                    return; // Batal kembali ke menu
                }
                // Jika NO_OPTION, lanjut tanpa save
            }
            gameCurrentlyActive = false;
            if (gameTimer.isRunning()) gameTimer.stop();
            boardGamePage.dispose(); // Tutup window board game
        }
        updateResumeButtonVisibility(mainMenuPage.getSelectedDifficulty()); // Update tombol resume di menu
        mainMenuPage.setVisible(true); // Tampilkan kembali menu utama
    }

    /*
     * Menyimpan data permainan yang selesai ke database riwayat.
     * @param solvedByUser true jika diselesaikan pengguna, false jika oleh solver.
     */
    private void saveGameToHistoryDb(boolean solvedByUser) {
        if (initialBoard == null || currentPlayingLevel == null) return;
        GameHistoryEntry entry = new GameHistoryEntry(
                new Timestamp(System.currentTimeMillis()),
                timeElapsedSeconds,
                solvedByUser,
                currentPlayingLevel,
                initialBoard.getBoardString()
        );
        if (!dbManager.saveGameToHistory(entry)) {
             if(boardGamePage != null && boardGamePage.isVisible()) {
                boardGamePage.showErrorNotification("Gagal menyimpan ke riwayat permainan.", "Database Error");
             } else if (mainMenuPage != null && mainMenuPage.isVisible()) {
                mainMenuPage.showMessageDialog("Gagal menyimpan ke riwayat permainan.", "Database Error", JOptionPane.ERROR_MESSAGE);
             }
        }
    }

    /*
     * Format waktu dari detik ke MM:SS.
     */
    private String formatTime(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
