/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.util;

import java.awt.Color;
import java.awt.Font;
/**
 *
 * @author Solkhann
 */

/*
 * Kelas utilitas untuk menyimpan konstanta tampilan aplikasi Sudoku.
 * Termasuk palet warna dan definisi font.
 */
public class SudokuConstant {

    // Palet Warna Dark Theme (tetap sama seperti yang sudah Anda definisikan)
    public static final Color BACKGROUND_COLOR = new Color(34, 40, 49);
    public static final Color SECONDARY_ACCENT_COLOR = new Color(57, 62, 70);
    public static final Color PRIMARY_ACCENT_COLOR = new Color(0, 173, 181);
    public static final Color TEXT_COLOR = new Color(238, 238, 238);
    public static final Color FIXED_CELL_BACKGROUND_COLOR = new Color(50, 58, 67);
    public static final Color FIXED_CELL_FOREGROUND_COLOR = new Color(150, 158, 168);
    public static final Color EDITABLE_CELL_BG_COLOR = new Color(44, 50, 59);
    public static final Color ACTIVE_CELL_BG_COLOR = new Color(0, 173, 181);
    public static final Color CONFLICT_TEXT_COLOR = Color.RED;
    public static final Color CONFLICT_CELL_BG_COLOR = new Color(181, 0, 0);
    public static final Color EDITABLE_CELL_FOREGROUND_COLOR = TEXT_COLOR;
    public static final Color HINT_CELL_FOREGROUND_COLOR = new Color(19, 245, 5);

    // Java akan mencoba menggunakan font "Monospaced". Jika tidak ditemukan di sistem,
    // maka akan menggunakan font fallback default (misalnya, SansSerif).
    public static final Font PRIMARY_FONT = new Font("Monospaced", Font.PLAIN, 16);
    public static final Font BOLD_PRIMARY_FONT = new Font("Monospaced", Font.BOLD, 16);
    public static final Font TITLE_FONT = new Font("Monospaced", Font.BOLD, 28);
    public static final Font CELL_FONT = new Font("Monospaced", Font.BOLD, 22);
    public static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 14);
    public static final Font STATUS_FONT = new Font("Monospaced", Font.PLAIN, 14);
    public static final Font COMBOBOX_FONT = new Font("Monospaced", Font.PLAIN, 14);

    // Ketebalan Garis Board (tetap sama)
    public static final int REGULAR_BORDER_THICKNESS = 1;
    public static final int THICK_BORDER_THICKNESS = 3;
}
