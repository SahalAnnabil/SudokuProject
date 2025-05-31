/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.view;
import com.sudoku.util.SudokuConstant; // << IMPORT KONSTANTA

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
/**
 *
 * @author Solkhann
 */

/*
 * Representasi visual satu sel Sudoku, turunan dari JTextField.
 * Mengelola tampilan (warna, font, border) berdasarkan status sel (tetap, bisa diisi, konflik, aktif).
 */
public class CellView extends JTextField {
    private int actualValue = 0;
    private final int row;
    private final int col;
    private boolean isFixedCell; // Apakah sel ini dari puzzle awal (given)
    private boolean isConflictingCell; // Apakah sel ini sedang berkonflik
    private boolean isActiveCell; // Apakah sel ini sedang aktif/dipilih

    /*
     * Konstruktor untuk CellView.
     * @param row Posisi baris sel (0-8).
     * @param col Posisi kolom sel (0-8).
     */
    public CellView(int row, int col) {
        super("");
        this.row = row;
        this.col = col;
        this.isFixedCell = false;
        this.isConflictingCell = false;
        this.isActiveCell = false;

        setHorizontalAlignment(JTextField.CENTER);
        setOpaque(true);
        // Atur font awal, akan disesuaikan lagi di updateAppearance
        setPreferredSize(new Dimension(50, 50)); // Berikan ukuran preferensi agar GridLayout bekerja baik
        setFont(SudokuConstant.CELL_FONT); 
        // Ukuran sel akan diatur oleh GridLayout di BoardPanel
        // setPreferredSize(new Dimension(45, 45)); 
        

        // Filter input hanya untuk angka 1-9 dan kosong
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (string.matches("[1-9]?") && (fb.getDocument().getLength() + string.length()) <= 1) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                 if (text.matches("[1-9]?") && (fb.getDocument().getLength() - length + text.length()) <= 1) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (text.isEmpty()) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        updateAppearance(); // Atur tampilan awal
    }

    /* Mendapatkan posisi baris sel ini. */
    public int getRowVal() { return row; }
    /* Mendapatkan posisi kolom sel ini. */
    public int getColVal() { return col; }

    /*
     * Mengatur apakah sel ini adalah sel tetap (dari sistem).
     * @param fixed true jika sel tetap, false jika bisa diisi pengguna.
     */
    public void setFixed(boolean fixed) {
        this.isFixedCell = fixed;
        setEditable(!fixed); // Sel tetap tidak bisa diedit
        updateAppearance();
    }

    /* Memeriksa apakah sel ini tetap. */
    public boolean isCellFixed() { return isFixedCell; }

    /*
     * Mengatur apakah sel ini berkonflik.
     * @param conflicting true jika konflik, false jika tidak.
     */
    public void setConflicting(boolean conflicting) {
        boolean changed = this.isConflictingCell != conflicting;
        this.isConflictingCell = conflicting;
        if (changed) {
            updateAppearance();
        } else if (conflicting) { // Jika sudah konflik dan dipanggil lagi untuk konflik
            // Ini bisa terjadi jika "Check" dipanggil berulang kali tanpa perubahan.
            // Kita mungkin tetap ingin memastikan tampilannya benar.
            updateAppearance();
        }
    }

    /* Memeriksa apakah sel ini berkonflik. */
    public boolean isCellConflicting() { return isConflictingCell; }

    /*
     * Mengatur apakah sel ini sedang aktif (dipilih).
     * @param active true jika aktif, false jika tidak.
     */
    public void setActive(boolean active) {
        boolean changed = this.isActiveCell != active;
        this.isActiveCell = active;
        if (changed) {
            updateAppearance();
        }
    }

    /*
     * Mengatur nilai yang ditampilkan di sel.
     * @param value Angka 0-9 (0 untuk kosong).
     * @param isHint Apakah nilai ini hasil dari hint (untuk pewarnaan berbeda).
     */
    public void setValueInView(int value, boolean isHint) {
        this.actualValue = (value >= 1 && value <= 9) ? value : 0;
        // Teks dan gaya akan diatur oleh updateAppearance
        updateAppearance();
        if (isHint && !isFixedCell) {
            // Pastikan warna hint diterapkan setelah gaya dasar
            setForeground(SudokuConstant.HINT_CELL_FOREGROUND_COLOR);
        }
    }

    /*
     * Mendapatkan nilai integer dari teks di sel.
     * @return Angka 1-9, atau 0 jika sel kosong atau input tidak valid.
     */
    public int getIntValue() {
        return this.actualValue;
    }

    /*
     * Memperbarui tampilan visual sel (warna latar, warna teks, font)
     * berdasarkan statusnya saat ini (tetap, bisa diisi, konflik, aktif).
     */
    private void updateAppearance() {
        // 1. Tentukan teks yang akan ditampilkan
        String displayText;
        if (!isFixedCell && isConflictingCell) {
            // Jika Anda ingin tetap menampilkan angka pengguna yang salah saat konflik,
            // bukan "X", maka gunakan actualValue.
            // Jika ingin "X", gunakan: displayText = "X";
            displayText = (actualValue != 0) ? String.valueOf(actualValue) : "";
        } else {
            displayText = (actualValue != 0) ? String.valueOf(actualValue) : "";
        }

        // Hanya panggil setText jika teksnya berbeda atau jika kita ingin "memaksa" refresh.
        // Untuk masalah rendering, kadang lebih aman untuk selalu panggil jika ada perubahan status visual.
        if (!getText().equals(displayText)) {
             super.setText(displayText);
        }


        // 2. Atur gaya visual (warna, font)
        if (isFixedCell) {
            setBackground(SudokuConstant.FIXED_CELL_BACKGROUND_COLOR);
            setForeground(SudokuConstant.FIXED_CELL_FOREGROUND_COLOR);
            setFont(SudokuConstant.CELL_FONT.deriveFont(Font.BOLD));
        } else {
            // Untuk sel yang bisa diedit
            setFont(SudokuConstant.CELL_FONT.deriveFont(Font.PLAIN)); // Atur font dulu

            if (isConflictingCell) {
                setBackground(SudokuConstant.CONFLICT_CELL_BG_COLOR); // Ini akan warna MERAH SOLID
                // Tentukan warna teks untuk sel konflik. Jika menampilkan angka asli,
                // mungkin ingin warna teks yang kontras dengan merah, misal putih.
                setForeground(SudokuConstant.TEXT_COLOR); // Atau warna khusus untuk teks konflik
            } else {
                // Tidak konflik
                setForeground(SudokuConstant.EDITABLE_CELL_FOREGROUND_COLOR); // Warna teks normal
                if (isActiveCell) {
                    setBackground(SudokuConstant.ACTIVE_CELL_BG_COLOR); // Warna sel aktif (HIJAU SOLID atau BIRU KEHIJAUAN SOLID)
                } else {
                    setBackground(SudokuConstant.EDITABLE_CELL_BG_COLOR);
                }
            }
        }
        // Tidak perlu repaint() eksplisit di sini jika perubahan properti Swing sudah cukup.
        // Namun, jika masalah persisten, ini adalah tempat terakhir untuk dicoba.
        // repaint();
    }

    public int getCurrentTextAsIntValue() {
        try {
            String text = getText();
            // Jika Anda menggunakan "X" untuk konflik, pastikan ini tidak mencoba mem-parse "X"
            if (text == null || text.isEmpty() || (!isFixedCell && isConflictingCell && text.equalsIgnoreCase("X")) ) {
                return 0;
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // Jika teks adalah "X" dan Anda tidak menanganinya di atas, ini akan error.
            // Lebih aman mengandalkan this.actualValue untuk logika game.
            return 0;
        }
    }
}