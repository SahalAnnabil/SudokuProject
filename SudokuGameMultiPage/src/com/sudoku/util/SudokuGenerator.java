/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.util;
import com.sudoku.model.SudokuBoard;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List; // Import List
/**
 *
 * @author Solkhann
 */

/*
 * Utilitas untuk menghasilkan puzzle Sudoku baru dengan berbagai tingkat kesulitan.
 */
public class SudokuGenerator {

    private static final int SIZE = SudokuBoard.SIZE;
    private final Random random = new Random();

    // Papan Sudoku yang sudah terpecahkan sebagai dasar
    // Anda bisa memiliki beberapa papan dasar atau algoritma generasi yang lebih canggih
    private final int[][] baseSolvedBoard = {
        {5, 3, 4, 6, 7, 8, 9, 1, 2},
        {6, 7, 2, 1, 9, 5, 3, 4, 8},
        {1, 9, 8, 3, 4, 2, 5, 6, 7},
        {8, 5, 9, 7, 6, 1, 4, 2, 3},
        {4, 2, 6, 8, 5, 3, 7, 9, 1},
        {7, 1, 3, 9, 2, 4, 8, 5, 6},
        {9, 6, 1, 5, 3, 7, 2, 8, 4},
        {2, 8, 7, 4, 1, 9, 6, 3, 5},
        {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    /*
     * Menghasilkan puzzle Sudoku baru berdasarkan tingkat kesulitan yang diberikan.
     * Metode ini mengosongkan sejumlah sel dari papan yang sudah terpecahkan.
     * @param difficulty Tingkat kesulitan: "Beginner", "Easy", "Medium", "Hard", "Extreme".
     * @return Objek SudokuBoard dengan puzzle baru.
     */
    public SudokuBoard generatePuzzle(String difficulty) {
        int[][] puzzle = new int[SIZE][SIZE];
        // Salin papan dasar yang sudah terpecahkan
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(baseSolvedBoard[i], 0, puzzle[i], 0, SIZE);
        }

        // Acak papan dasar sedikit (opsional, untuk variasi lebih)
        // Implementasi pengacakan papan yang valid bisa kompleks, untuk sederhana kita lewati dulu
        // shuffleBoard(puzzle); 

        int cellsToRemove;
        switch (difficulty.toLowerCase()) {
            case "beginner":
                cellsToRemove = 25; // Sangat sedikit yang dihapus
                break;
            case "easy":
                cellsToRemove = 35;
                break;
            case "medium":
                cellsToRemove = 45;
                break;
            case "hard":
                cellsToRemove = 50;
                break;
            case "extreme":
                cellsToRemove = 55; // Paling banyak dihapus
                break;
            default:
                cellsToRemove = 45; // Default ke medium
        }

        removeCells(puzzle, cellsToRemove);

        return new SudokuBoard(puzzle);
    }

    /*
     * Menghapus sejumlah sel secara acak dari papan yang diberikan.
     * @param board Papan 2D integer.
     * @param count Jumlah sel yang akan dihapus.
     */
    private void removeCells(int[][] board, int count) {
        List<Integer> cellIndices = new ArrayList<>();
        for (int i = 0; i < SIZE * SIZE; i++) {
            cellIndices.add(i);
        }
        Collections.shuffle(cellIndices, random);

        int removedCount = 0;
        for (int i = 0; i < cellIndices.size() && removedCount < count; i++) {
            int r = cellIndices.get(i) / SIZE;
            int c = cellIndices.get(i) % SIZE;

            if (board[r][c] != 0) {
                // Simpan nilai sementara jika kita ingin memastikan puzzle tetap unik & punya solusi tunggal
                // int temp = board[r][c];
                // board[r][c] = 0;
                // Jika setelah dihapus jadi tidak unik atau tidak solvable, kembalikan (lebih advanced)
                // Untuk sekarang, kita hapus saja.
                board[r][c] = 0;
                removedCount++;
            }
        }
    }

    // Metode shuffleBoard yang lebih canggih (opsional, diluar cakupan dasar)
    // private void shuffleBoard(int[][] board) { ... }
}