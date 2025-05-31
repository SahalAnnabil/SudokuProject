/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.view;
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
    private final int row;
    private final int col;
    private boolean isFixedCell; // Apakah sel ini dari puzzle awal (given)
    private boolean isConflictingCell; // Apakah sel ini sedang berkonflik
    private boolean isActiveCell; // Apakah sel ini sedang aktif/dipilih

    // Warna dan Font (bisa disesuaikan)
    /* Warna latar untuk sel yang nilainya tetap dari sistem. */
    public static final Color FIXED_BG_COLOR = new Color(220, 225, 230); // Abu-abu kebiruan muda
    /* Warna latar untuk sel yang bisa diisi pengguna. */
    public static final Color EDITABLE_BG_COLOR = Color.WHITE;
    /* Warna latar untuk sel yang sedang aktif/dipilih. */
    public static final Color ACTIVE_BG_COLOR = new Color(173, 216, 230); // Biru muda
    /* Warna latar untuk sel yang nilainya berkonflik. */
    public static final Color CONFLICT_BG_COLOR = new Color(255, 182, 193); // Pink/merah muda
    /* Warna teks untuk sel yang nilainya tetap. */
    public static final Color FIXED_FG_COLOR = Color.BLACK;
    /* Warna teks untuk sel yang diisi pengguna. */
    public static final Color EDITABLE_FG_COLOR = new Color(0, 0, 128); // Biru tua (navy)
    /* Warna teks untuk sel yang diberi petunjuk (hint). */
    public static final Color HINT_FG_COLOR = new Color(0, 100, 0); // Hijau tua

    /* Font standar untuk sel. */
    public static final Font CELL_FONT = new Font("Arial", Font.BOLD, 22);
    /* Font untuk sel yang nilainya tetap. */
    public static final Font FIXED_CELL_FONT = new Font("Arial", Font.BOLD, 22); // Bisa sama atau beda
    /* Font untuk sel yang diisi pengguna. */
    public static final Font EDITABLE_CELL_FONT = new Font("Arial", Font.PLAIN, 22);


    /*
     * Konstruktor untuk CellView.
     * @param row Posisi baris sel (0-8).
     * @param col Posisi kolom sel (0-8).
     */
    public CellView(int row, int col) {
        super(1); // Lebar untuk 1 karakter
        this.row = row;
        this.col = col;
        this.isFixedCell = false;
        this.isConflictingCell = false;
        this.isActiveCell = false;

        setHorizontalAlignment(JTextField.CENTER);
        setFont(CELL_FONT);
        // Ukuran sel akan diatur oleh GridLayout di BoardPanel
        // setPreferredSize(new Dimension(45, 45)); 

        // Filter input hanya untuk angka 1-9 dan kosong
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                // Hanya izinkan satu digit angka 1-9, atau string kosong (untuk menghapus)
                if (string.matches("[1-9]?") && (fb.getDocument().getLength() + string.length()) <= 1) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                // Hanya izinkan satu digit angka 1-9, atau string kosong (untuk menghapus/mengganti)
                 if (text.matches("[1-9]?") && (fb.getDocument().getLength() - length + text.length()) <= 1) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (text.isEmpty()) { // Izinkan penghapusan (mengganti dengan string kosong)
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
        this.isConflictingCell = conflicting;
        updateAppearance();
    }

    /* Memeriksa apakah sel ini berkonflik. */
    public boolean isCellConflicting() { return isConflictingCell; }

    /*
     * Mengatur apakah sel ini sedang aktif (dipilih).
     * @param active true jika aktif, false jika tidak.
     */
    public void setActive(boolean active) {
        this.isActiveCell = active;
        updateAppearance();
    }

    /*
     * Mengatur nilai yang ditampilkan di sel.
     * @param value Angka 0-9 (0 untuk kosong).
     * @param isHint Apakah nilai ini hasil dari hint (untuk pewarnaan berbeda).
     */
    public void setValueInView(int value, boolean isHint) {
        if (value >= 1 && value <= 9) {
            setText(String.valueOf(value));
            if(isHint && !isFixedCell) { // Hanya warnai hint jika bukan sel fixed
                 setForeground(HINT_FG_COLOR);
            } else {
                // Warna akan diatur oleh updateAppearance()
            }
        } else {
            setText(""); // Kosongkan jika 0 atau tidak valid
        }
        // Panggil updateAppearance untuk memastikan warna latar dan font benar
        // jika isFixed atau isConflicting berubah akibat setValue.
        // Namun, warna teks hint di atas sudah spesifik.
        updateAppearance(); 
        if(isHint && !isFixedCell) setForeground(HINT_FG_COLOR); // Pastikan hint tetap hijau
    }

    /*
     * Mendapatkan nilai integer dari teks di sel.
     * @return Angka 1-9, atau 0 jika sel kosong atau input tidak valid.
     */
    public int getIntValue() {
        try {
            String text = getText();
            if (text == null || text.isEmpty()) {
                return 0; // Sel kosong dianggap 0
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0; // Jika bukan angka, anggap 0
        }
    }

    /*
     * Memperbarui tampilan visual sel (warna latar, warna teks, font)
     * berdasarkan statusnya saat ini (tetap, bisa diisi, konflik, aktif).
     */
    private void updateAppearance() {
        // Atur warna teks dan font default dulu
        if (isFixedCell) {
            setForeground(FIXED_FG_COLOR);
            setFont(FIXED_CELL_FONT);
        } else {
            setForeground(EDITABLE_FG_COLOR);
            setFont(EDITABLE_CELL_FONT);
        }

        // Atur warna latar
        if (isFixedCell) {
            setBackground(FIXED_BG_COLOR);
        } else {
            if (isConflictingCell) {
                setBackground(CONFLICT_BG_COLOR);
            } else if (isActiveCell) {
                setBackground(ACTIVE_BG_COLOR);
            } else {
                setBackground(EDITABLE_BG_COLOR);
            }
        }
    }
}