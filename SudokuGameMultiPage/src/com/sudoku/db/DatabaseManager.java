/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.db;
import com.sudoku.model.GameHistoryEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Solkhann
 */

/*
 * Mengelola semua interaksi dengan basis data MySQL untuk game Sudoku.
 * Termasuk menyimpan dan memuat progres permainan per level, serta riwayat permainan.
 */
public class DatabaseManager {
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3307";
    private static final String DB_NAME = "sudoku_game_db_multi_page";
    private static final String DB_USER = "root"; // GANTI JIKA PERLU
    private static final String DB_PASSWORD = "Sofiatun558."; // GANTI DENGAN PASSWORD MYSQL ANDA

    private static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private Connection connection;

    /*
     * Mendapatkan koneksi ke database. Jika koneksi belum ada atau tertutup,
     * koneksi baru akan dibuat.
     * @return Objek Connection yang aktif.
     * @throws SQLException jika koneksi gagal dibuat.
     */
    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL JDBC tidak ditemukan. Pastikan Connector/J sudah ditambahkan ke library.");
                throw new SQLException("Driver JDBC tidak ditemukan: " + e.getMessage(), e);
            } catch (SQLException e) {
                System.err.println("Gagal melakukan koneksi ke database: " + e.getMessage());
                throw e; // Lempar kembali untuk ditangani oleh pemanggil
            }
        }
        return connection;
    }

    /*
     * Menutup koneksi database jika sedang terbuka.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi database: " + e.getMessage());
            } finally {
                connection = null; // Set ke null agar getConnection() bisa buat baru jika perlu
            }
        }
    }

    /*
     * Menyimpan atau memperbarui progres permainan untuk tingkat kesulitan tertentu.
     * Jika sudah ada progres untuk level tersebut, akan ditimpa.
     * @param levelDifficulty Tingkat kesulitan permainan (misalnya, "easy", "medium").
     * @param initialBoard String representasi papan awal.
     * @param currentBoard String representasi papan saat ini dengan isian pengguna.
     * @param timeElapsedSeconds Waktu bermain yang telah berlalu dalam detik.
     * @return true jika penyimpanan berhasil, false jika gagal.
     */
    public boolean saveOrUpdateGameProgress(String levelDifficulty, String initialBoard, String currentBoard, int timeElapsedSeconds) {
        String sql = "INSERT INTO saved_games (level_difficulty, initial_board, current_board, time_elapsed_seconds, last_played_at) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "initial_board = VALUES(initial_board), " +
                     "current_board = VALUES(current_board), " +
                     "time_elapsed_seconds = VALUES(time_elapsed_seconds), " +
                     "last_played_at = CURRENT_TIMESTAMP";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, levelDifficulty.toLowerCase()); // Simpan dalam lowercase agar konsisten
            pstmt.setString(2, initialBoard);
            pstmt.setString(3, currentBoard);
            pstmt.setInt(4, timeElapsedSeconds);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan progres game untuk level " + levelDifficulty + ": " + e.getMessage());
            e.printStackTrace(); // Untuk debugging lebih detail
            return false;
        }
    }

    /*
     * Memuat progres permainan yang tersimpan untuk tingkat kesulitan tertentu.
     * @param levelDifficulty Tingkat kesulitan permainan.
     * @return Array String berisi {initialBoard, currentBoard, timeElapsedSeconds}, 
     * atau null jika tidak ada progres tersimpan untuk level tersebut atau terjadi error.
     */
    public String[] loadGameProgress(String levelDifficulty) {
        String sql = "SELECT initial_board, current_board, time_elapsed_seconds FROM saved_games WHERE level_difficulty = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, levelDifficulty.toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("initial_board"),
                        rs.getString("current_board"),
                        String.valueOf(rs.getInt("time_elapsed_seconds"))
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat memuat progres game untuk level " + levelDifficulty + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Tidak ada data atau error
    }

    /*
     * Menghapus progres permainan yang tersimpan untuk tingkat kesulitan tertentu.
     * Berguna saat memulai "New Game" dan ingin menghapus save lama.
     * @param levelDifficulty Tingkat kesulitan permainan yang progresnya akan dihapus.
     * @return true jika penghapusan berhasil atau tidak ada data untuk dihapus, false jika terjadi error.
     */
    public boolean deleteSavedGame(String levelDifficulty) {
        String sql = "DELETE FROM saved_games WHERE level_difficulty = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, levelDifficulty.toLowerCase());
            pstmt.executeUpdate();
            return true; // Berhasil meskipun mungkin tidak ada baris yang terhapus
        } catch (SQLException e) {
            System.err.println("Error saat menghapus progres game untuk level " + levelDifficulty + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /*
     * Menyimpan entri baru ke tabel riwayat permainan.
     * @param entry Objek GameHistoryEntry yang berisi detail permainan yang selesai.
     * @return true jika penyimpanan berhasil, false jika gagal.
     */
    public boolean saveGameToHistory(GameHistoryEntry entry) {
        String sql = "INSERT INTO game_history (game_date, duration_seconds, solved_by_user, difficulty, initial_board) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, entry.getGameDate());
            pstmt.setInt(2, entry.getDurationSeconds());
            pstmt.setBoolean(3, entry.isSolvedByUser());
            pstmt.setString(4, entry.getDifficulty().toLowerCase());
            pstmt.setString(5, entry.getInitialBoard());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan riwayat game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Mengambil semua entri dari riwayat permainan, bisa difilter berdasarkan tingkat kesulitan.
     * @param levelDifficulty Tingkat kesulitan untuk filter. Jika null atau kosong, ambil semua riwayat.
     * @return List dari GameHistoryEntry. List kosong jika tidak ada data atau error.
     */
    public List<GameHistoryEntry> getGameHistory(String levelDifficulty) {
        List<GameHistoryEntry> historyList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT history_id, game_date, duration_seconds, solved_by_user, difficulty, initial_board FROM game_history");

        boolean useFilter = levelDifficulty != null && !levelDifficulty.trim().isEmpty();
        if (useFilter) {
            sqlBuilder.append(" WHERE difficulty = ?");
        }
        sqlBuilder.append(" ORDER BY game_date DESC");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            if (useFilter) {
                pstmt.setString(1, levelDifficulty.toLowerCase());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    historyList.add(new GameHistoryEntry(
                        rs.getInt("history_id"),
                        rs.getTimestamp("game_date"),
                        rs.getInt("duration_seconds"),
                        rs.getBoolean("solved_by_user"),
                        rs.getString("difficulty"), // Ambil difficulty dari DB
                        rs.getString("initial_board")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil riwayat game (level: " + levelDifficulty + "): " + e.getMessage());
            e.printStackTrace();
        }
        return historyList;
    }
}