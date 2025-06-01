/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.model;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
/**
 *
 * @author Solkhann
 */

/*
 * Merepresentasikan satu entri dalam riwayat permainan.
 * Menyimpan detail seperti tanggal, durasi, status penyelesaian, dan tingkat kesulitan.
 */
public class GameHistoryEntry {
    private int historyId;
    private Timestamp gameDate;
    private int durationSeconds;
    private boolean solvedByUser;
    private String difficulty;
    private String initialBoard; // Untuk referensi atau memuat ulang puzzle spesifik

    /*
     * Konstruktor untuk membuat entri riwayat baru sebelum disimpan ke DB (tanpa historyId).
     * @param gameDate Waktu permainan selesai.
     * @param durationSeconds Durasi permainan dalam detik.
     * @param solvedByUser Apakah diselesaikan oleh pengguna (true) atau solver (false).
     * @param difficulty Tingkat kesulitan permainan.
     * @param initialBoard Representasi string dari papan awal.
     */
    public GameHistoryEntry(Timestamp gameDate, int durationSeconds, boolean solvedByUser, String difficulty, String initialBoard) {
        this.gameDate = gameDate;
        this.durationSeconds = durationSeconds;
        this.solvedByUser = solvedByUser;
        this.difficulty = difficulty;
        this.initialBoard = initialBoard;
    }

    /*
     * Konstruktor untuk membuat entri riwayat dari data yang diambil dari DB (dengan historyId).
     * @param historyId ID unik entri riwayat.
     * @param gameDate Waktu permainan selesai.
     * @param durationSeconds Durasi permainan dalam detik.
     * @param solvedByUser Apakah diselesaikan oleh pengguna atau solver.
     * @param difficulty Tingkat kesulitan permainan.
     * @param initialBoard Representasi string dari papan awal.
     */
    public GameHistoryEntry(int historyId, Timestamp gameDate, int durationSeconds, boolean solvedByUser, String difficulty, String initialBoard) {
        this(gameDate, durationSeconds, solvedByUser, difficulty, initialBoard); // Panggil konstruktor lain
        this.historyId = historyId;
    }

    // Getter methods
    public int getHistoryId() { return historyId; }
    public Timestamp getGameDate() { return gameDate; }
    public int getDurationSeconds() { return durationSeconds; }
    public boolean isSolvedByUser() { return solvedByUser; }
    public String getDifficulty() { return difficulty; }
    public String getInitialBoard() { return initialBoard; }

    /*
     * Mengembalikan representasi string dari entri riwayat untuk ditampilkan di UI.
     * @return String yang diformat berisi detail riwayat.
     */
    @Override
    public String toString() {
        // Locale.ENGLISH digunakan agar format tanggal konsisten jika sistem pengguna berbeda
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH);
        String solvedByText = solvedByUser ? "User" : "Solver";
        long minutes = durationSeconds / 60;
        long seconds = durationSeconds % 60;
        return String.format("Kesulitan: %s | Selesai: %s | Durasi: %02dm %02ds | Oleh: %s",
                difficulty, sdf.format(gameDate), minutes, seconds, solvedByText);
    }
}
