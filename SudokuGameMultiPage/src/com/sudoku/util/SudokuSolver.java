/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.util;
import com.sudoku.model.SudokuBoard;
import com.sudoku.model.SudokuCell;
/**
 *
 * @author Solkhann
 */

/*
 * Mengimplementasikan algoritma backtracking untuk menyelesaikan puzzle Sudoku.
 */
public class SudokuSolver {

    private static final int SIZE = SudokuBoard.SIZE;

    /*
     * Mencoba menyelesaikan papan Sudoku yang diberikan.
     * Papan akan dimodifikasi secara langsung jika solusi ditemukan.
     * @param board Objek SudokuBoard yang akan diselesaikan.
     * @return true jika solusi ditemukan, false jika tidak ada solusi.
     */
    public boolean solve(SudokuBoard board) {
        SudokuCell[][] grid = board.getGrid(); // Dapatkan grid dari papan

        // Cari sel kosong pertama
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col].getValue() == 0) { // Hanya proses sel yang belum terisi
                    // Coba semua angka yang mungkin (1-9)
                    for (int number = 1; number <= SIZE; number++) {
                        if (isSafeToPlace(grid, row, col, number)) {
                            grid[row][col].setValue(number); // Tempatkan angka

                            if (solve(board)) { // Rekursif ke sel berikutnya
                                return true; // Solusi ditemukan di jalur ini
                            } else {
                                grid[row][col].setValue(0); // Batalkan jika jalur ini buntu (backtrack)
                            }
                        }
                    }
                    return false; // Tidak ada angka valid untuk sel ini, backtrack dari pemanggil
                }
            }
        }
        return true; // Semua sel terisi, solusi ditemukan
    }

    /*
     * Memeriksa apakah aman untuk menempatkan angka pada sel tertentu di grid.
     * Aman jika angka tersebut belum ada di baris, kolom, dan blok 3x3 yang sama.
     * @param grid Grid Sudoku (array SudokuCell[][]).
     * @param row Baris dari sel yang diperiksa.
     * @param col Kolom dari sel yang diperiksa.
     * @param num Angka yang akan ditempatkan.
     * @return true jika aman untuk menempatkan angka, false jika tidak.
     */
    public boolean isSafeToPlace(SudokuCell[][] grid, int row, int col, int num) {
        // Cek baris
        for (int c = 0; c < SIZE; c++) {
            if (grid[row][c].getValue() == num) {
                return false;
            }
        }

        // Cek kolom
        for (int r = 0; r < SIZE; r++) {
            if (grid[r][col].getValue() == num) {
                return false;
            }
        }

        // Cek blok 3x3
        int blockRowStart = row - row % 3;
        int blockColStart = col - col % 3;

        for (int r = blockRowStart; r < blockRowStart + 3; r++) {
            for (int c = blockColStart; c < blockColStart + 3; c++) {
                if (grid[r][c].getValue() == num) {
                    return false;
                }
            }
        }
        return true; // Aman untuk ditempatkan
    }
}