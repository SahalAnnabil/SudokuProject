/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.model;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author Solkhann
 */
import java.util.HashSet;
import java.util.Set;

/*
 * Merepresentasikan papan Sudoku 9x9 dan logika permainannya.
 * Bertanggung jawab untuk validasi baris, kolom, blok, dan status keseluruhan papan.
 */
public class SudokuBoard {
    /* Ukuran standar papan Sudoku (9x9). */
    public static final int SIZE = 9;
    private SudokuCell[][] grid;

    /*
     * Konstruktor default, menginisialisasi papan kosong.
     */
    public SudokuBoard() {
        grid = new SudokuCell[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = new SudokuCell(0, false);
            }
        }
    }

    /*
     * Konstruktor untuk menginisialisasi papan dari array integer 2D.
     * Angka 0 dianggap sel kosong, angka lain dianggap sel tetap.
     * @param initialGrid Array integer 9x9 untuk puzzle awal.
     * @throws IllegalArgumentException jika ukuran grid awal tidak 9x9.
     */
    public SudokuBoard(int[][] initialGrid) {
        this(); // Panggil konstruktor default untuk inisialisasi grid dasar
        if (initialGrid == null || initialGrid.length != SIZE || initialGrid[0].length != SIZE) {
            throw new IllegalArgumentException("Ukuran grid awal harus 9x9 dan tidak null.");
        }
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = initialGrid[row][col];
                if (value >= 1 && value <= 9) {
                    grid[row][col] = new SudokuCell(value, true); // Sel tetap
                } else {
                    grid[row][col] = new SudokuCell(0, false); // Sel kosong
                }
            }
        }
    }

    /*
     * Mendapatkan objek SudokuCell pada posisi baris dan kolom tertentu.
     * @param row Baris (0-8).
     * @param col Kolom (0-8).
     * @return Objek SudokuCell pada posisi tersebut.
     * @throws IllegalArgumentException jika baris atau kolom di luar batas.
     */
    public SudokuCell getCell(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException("Baris atau kolom di luar batas: row=" + row + ", col=" + col);
        }
        return grid[row][col];
    }

    /*
     * Mengatur nilai pada sel tertentu jika sel tersebut tidak tetap.
     * @param row Baris (0-8).
     * @param col Kolom (0-8).
     * @param value Nilai (1-9), atau 0 untuk mengosongkan.
     * @return true jika nilai berhasil diatur, false jika sel adalah tetap (fixed).
     * @throws IllegalArgumentException jika nilai di luar rentang 0-9 atau baris/kolom di luar batas.
     */
    public boolean setCellValue(int row, int col, int value) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException("Baris atau kolom di luar batas saat mengatur nilai.");
        }
        if (grid[row][col].isFixed()) {
            return false;
        }
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Nilai sel harus antara 0 dan 9.");
        }
        grid[row][col].setValue(value);
        return true;
    }

    /*
     * Memeriksa apakah seluruh papan Sudoku valid saat ini dan menandai sel yang berkonflik.
     * Papan valid jika tidak ada duplikasi angka di setiap baris, kolom, dan blok 3x3.
     * Metode ini juga akan mengatur flag isConflicting pada SudokuCell.
     * @return true jika seluruh papan valid (tidak ada konflik), false jika ada.
     */
    public boolean isValidBoard() {
        // Reset semua status konflik sebelum validasi ulang
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] != null) { // Pastikan sel sudah diinisialisasi
                    grid[r][c].setConflicting(false);
                }
            }
        }

        boolean boardIsValidOverall = true;

        // Validasi baris
        for (int r = 0; r < SIZE; r++) {
            Set<Integer> seenInRow = new HashSet<>();
            Set<Integer> conflictingValuesInRow = new HashSet<>();
            for (int c = 0; c < SIZE; c++) {
                int val = grid[r][c].getValue();
                if (val != 0) {
                    if (seenInRow.contains(val)) {
                        conflictingValuesInRow.add(val);
                        boardIsValidOverall = false;
                    } else {
                        seenInRow.add(val);
                    }
                }
            }
            // Tandai sel yang konflik di baris ini
            if (!conflictingValuesInRow.isEmpty()) {
                for (int c = 0; c < SIZE; c++) {
                    if (conflictingValuesInRow.contains(grid[r][c].getValue())) {
                        grid[r][c].setConflicting(true);
                    }
                }
            }
        }

        // Validasi kolom
        for (int c = 0; c < SIZE; c++) {
            Set<Integer> seenInCol = new HashSet<>();
            Set<Integer> conflictingValuesInCol = new HashSet<>();
            for (int r = 0; r < SIZE; r++) {
                int val = grid[r][c].getValue();
                if (val != 0) {
                    if (seenInCol.contains(val)) {
                        conflictingValuesInCol.add(val);
                        boardIsValidOverall = false;
                    } else {
                        seenInCol.add(val);
                    }
                }
            }
            // Tandai sel yang konflik di kolom ini
            if (!conflictingValuesInCol.isEmpty()) {
                for (int r = 0; r < SIZE; r++) {
                    if (conflictingValuesInCol.contains(grid[r][c].getValue())) {
                        grid[r][c].setConflicting(true);
                    }
                }
            }
        }

        // Validasi blok 3x3
        for (int blockRowStart = 0; blockRowStart < SIZE; blockRowStart += 3) {
            for (int blockColStart = 0; blockColStart < SIZE; blockColStart += 3) {
                Set<Integer> seenInBlock = new HashSet<>();
                Set<Integer> conflictingValuesInBlock = new HashSet<>();
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        int val = grid[blockRowStart + r][blockColStart + c].getValue();
                        if (val != 0) {
                            if (seenInBlock.contains(val)) {
                                conflictingValuesInBlock.add(val);
                                boardIsValidOverall = false;
                            } else {
                                seenInBlock.add(val);
                            }
                        }
                    }
                }
                // Tandai sel yang konflik di blok ini
                if (!conflictingValuesInBlock.isEmpty()) {
                    for (int r = 0; r < 3; r++) {
                        for (int c = 0; c < 3; c++) {
                            if (conflictingValuesInBlock.contains(grid[blockRowStart + r][blockColStart + c].getValue())) {
                                grid[blockRowStart + r][blockColStart + c].setConflicting(true);
                            }
                        }
                    }
                }
            }
        }
        return boardIsValidOverall;
    }

    /*
     * Memeriksa apakah papan Sudoku telah terisi penuh dan semua aturan terpenuhi.
     * @return true jika papan selesai dan benar, false jika belum atau ada kesalahan.
     */
    public boolean isSolved() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == null || grid[row][col].getValue() == 0) {
                    return false; // Ada sel kosong atau null
                }
            }
        }
        return isValidBoard(); // Cek validitas jika semua terisi
    }

    /*
     * Mengosongkan semua sel yang dapat diubah oleh pengguna (bukan sel tetap).
     * Status konflik juga direset untuk sel-sel tersebut.
     */
    public void resetUserEntries() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != null && !grid[row][col].isFixed()) {
                    grid[row][col].setValue(0);
                    grid[row][col].setConflicting(false);
                }
            }
        }
    }

    /**
     * Mengembalikan representasi String dari papan untuk disimpan ke database.
     * Terdiri dari 81 karakter, dengan '0' untuk sel kosong.
     * @return String representasi papan.
     */
    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                sb.append(grid[r][c] != null ? grid[r][c].getValue() : '0');
            }
        }
        return sb.toString();
    }

    /*
     * Memuat konfigurasi papan dari representasi String.
     * String papan saat ini dan string papan awal (untuk menentukan sel tetap) diperlukan.
     * @param currentBoardString String 81 karakter untuk status papan saat ini.
     * @param initialBoardString String 81 karakter untuk status papan awal (menentukan sel tetap).
     * @throws IllegalArgumentException jika panjang string tidak 81.
     */
    public void loadBoardFromStrings(String currentBoardString, String initialBoardString) {
        if (currentBoardString.length() != SIZE * SIZE || initialBoardString.length() != SIZE * SIZE) {
            throw new IllegalArgumentException("String papan (saat ini dan awal) harus 81 karakter.");
        }
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int currentValue = Character.getNumericValue(currentBoardString.charAt(r * SIZE + c));
                int initialValue = Character.getNumericValue(initialBoardString.charAt(r * SIZE + c));
                boolean isFixed = (initialValue != 0); // Sel dianggap tetap jika di papan awal tidak nol

                // Jika sel tetap, nilainya harus dari papan awal
                // Jika tidak tetap, nilainya dari papan saat ini
                int finalValue = isFixed ? initialValue : currentValue;

                grid[r][c] = new SudokuCell(finalValue, isFixed);
            }
        }
    }

    /*
     * Menyalin kondisi papan saat ini ke papan lain.
     * Berguna untuk membuat backup atau untuk solver.
     * @param targetBoard Papan tujuan tempat data akan disalin.
     */
    public void copyTo(SudokuBoard targetBoard) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                SudokuCell sourceCell = this.getCell(r, c);
                targetBoard.getCell(r,c).setValue(sourceCell.getValue());
                targetBoard.getCell(r,c).setFixed(sourceCell.isFixed());
                targetBoard.getCell(r,c).setConflicting(sourceCell.isConflicting());
            }
        }
    }

    /*
     * Mengembalikan grid internal dari sel-sel Sudoku.
     * Digunakan terutama oleh SudokuSolver.
     * @return Array 2D dari SudokuCell.
     */
    public SudokuCell[][] getGrid() {
        return grid;
    }
}