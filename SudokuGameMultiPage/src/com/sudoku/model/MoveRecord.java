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
 * Merepresentasikan satu langkah (move) yang dilakukan oleh pengguna.
 * Digunakan untuk implementasi fitur Undo.
 */
public class MoveRecord {
    private final int row;
    private final int col;
    private final int previousValue; // Nilai sebelum langkah ini
    private final int newValue;      // Nilai setelah langkah ini

    /*
     * Konstruktor untuk MoveRecord.
     * @param row Baris dari sel yang diubah.
     * @param col Kolom dari sel yang diubah.
     * @param previousValue Nilai sel sebelum diubah.
     * @param newValue Nilai sel setelah diubah.
     */
    public MoveRecord(int row, int col, int previousValue, int newValue) {
        this.row = row;
        this.col = col;
        this.previousValue = previousValue;
        this.newValue = newValue;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getPreviousValue() {
        return previousValue;
    }

    public int getNewValue() {
        return newValue;
    }
}