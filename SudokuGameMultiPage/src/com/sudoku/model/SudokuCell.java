/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.model;

/**
 *
 * @author Solkhann
 */

/*
 * Merepresentasikan satu sel dalam papan Sudoku.
 * Mengelola nilai sel, status apakah sel tetap (given), dan apakah ada konflik.
 */
public class SudokuCell {
    private int value;
    private boolean isFixed;
    private boolean isConflicting;

    /*
     * Konstruktor untuk SudokuCell.
     * @param value Nilai awal sel (0-9).
     * @param isFixed Apakah sel ini tetap.
     */
    public SudokuCell(int value, boolean isFixed) {
        this.value = value;
        this.isFixed = isFixed;
        this.isConflicting = false;
    }

    /**
     * Mendapatkan nilai sel.
     * @return Nilai sel (0-9).
     */
    public int getValue() {
        return value;
    }

    /*
     * Mengatur nilai sel. Hanya bisa diatur jika sel tidak tetap.
     * @param value Nilai baru untuk sel (0-9).
     */
    public void setValue(int value) {
        if (!isFixed) {
            this.value = value;
        }
    }

    /*
     * Memeriksa apakah sel ini tetap (bagian dari puzzle awal).
     * @return true jika sel tetap, false jika dapat diubah.
     */
    public boolean isFixed() {
        return isFixed;
    }

    /*
     * Mengatur status tetap sel.
     * @param fixed Status tetap baru.
     */
    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    /*
     * Memeriksa apakah sel ini sedang dalam kondisi konflik.
     * @return true jika konflik, false jika tidak.
     */
    public boolean isConflicting() {
        return isConflicting;
    }

    /*
     * Mengatur status konflik sel.
     * @param conflicting Status konflik baru.
     */
    public void setConflicting(boolean conflicting) {
        this.isConflicting = conflicting;
    }

    /*
     * Mengatur ulang sel ke kondisi kosong dan tidak konflik jika tidak tetap.
     */
    public void reset() {
        if (!isFixed) {
            this.value = 0;
            this.isConflicting = false;
        }
    }
}
