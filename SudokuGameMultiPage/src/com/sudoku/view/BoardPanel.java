/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sudoku.view;
import com.sudoku.model.SudokuBoard;
import com.sudoku.model.SudokuCell; // Import SudokuCell
import com.sudoku.util.SudokuConstant; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
/**
 *
 * @author Solkhann
 */

/*
 * Panel yang menampilkan grid Sudoku 9x9.
 * Bertanggung jawab untuk menyusun CellView dan memberikan border yang sesuai.
 */
public class BoardPanel extends JPanel {
    private static final int SIZE = SudokuBoard.SIZE;
    private CellView[][] cellViews;
    private CellView activeCellView = null; // Menyimpan referensi sel yang sedang aktif

    /*
     * Konstruktor untuk BoardPanel.
     */
    public BoardPanel() {
        super(new GridLayout(SIZE, SIZE, 0, 0));
        // Latar belakang panel di bawah sel-sel (jika ada gap, atau jika sel transparan)
        this.setBackground(SudokuConstant.BACKGROUND_COLOR); 
        initCellViews();
    }

    /*
     * Menginisialisasi semua CellView dan menambahkannya ke panel dengan border yang sesuai.
     */
    private void initCellViews() {
        cellViews = new CellView[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cellViews[row][col] = new CellView(row, col); // CellView akan mengambil style dari konstantanya sendiri
                
                int top = (row % 3 == 0) ? SudokuConstant.THICK_BORDER_THICKNESS : SudokuConstant.REGULAR_BORDER_THICKNESS;
                int left = (col % 3 == 0) ? SudokuConstant.THICK_BORDER_THICKNESS : SudokuConstant.REGULAR_BORDER_THICKNESS;
                int bottom = ((row + 1) % 3 == 0 || row == SIZE - 1) ? SudokuConstant.THICK_BORDER_THICKNESS : SudokuConstant.REGULAR_BORDER_THICKNESS;
                int right = ((col + 1) % 3 == 0 || col == SIZE - 1) ? SudokuConstant.THICK_BORDER_THICKNESS : SudokuConstant.REGULAR_BORDER_THICKNESS;
                
                if (row == 0) top = SudokuConstant.THICK_BORDER_THICKNESS;
                if (col == 0) left = SudokuConstant.THICK_BORDER_THICKNESS;
                if (row == SIZE - 1) bottom = SudokuConstant.THICK_BORDER_THICKNESS;
                if (col == SIZE - 1) right = SudokuConstant.THICK_BORDER_THICKNESS;

                cellViews[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, SudokuConstant.PRIMARY_ACCENT_COLOR)); // << GUNAKAN WARNA DARI KONSTANTA

                final CellView currentCell = cellViews[row][col];
                currentCell.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (activeCellView != null) {
                            activeCellView.setActive(false); // Nonaktifkan sel aktif sebelumnya
                        }
                        activeCellView = currentCell;
                        activeCellView.setActive(true); // Aktifkan sel yang baru difokus
                    }
                    // focusLost tidak perlu dimodifikasi khusus di sini, 
                    // karena setActive(false) akan dipanggil oleh focusGained berikutnya
                });
                add(cellViews[row][col]);
            }
        }
    }

    /*
     * Mendapatkan array 2D dari semua CellView di papan.
     * @return CellView[][]
     */
    public CellView[][] getCellViews() {
        return cellViews;
    }

    /*
     * Memperbarui tampilan seluruh papan berdasarkan data dari SudokuBoard.
     * @param board Model SudokuBoard yang berisi data terbaru.
     */
    public void updateBoardView(SudokuBoard board) {
        if (board == null) return;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                SudokuCell modelCell = board.getCell(row, col);
                cellViews[row][col].setFixed(modelCell.isFixed());
                // Saat update, jangan tandai sebagai hint kecuali memang sumbernya dari hint
                cellViews[row][col].setValueInView(modelCell.getValue(), false); 
                cellViews[row][col].setConflicting(modelCell.isConflicting());
            }
        }
         // Pastikan sel aktif (jika ada) tetap terlihat aktif setelah update
        if (activeCellView != null) { 
            activeCellView.setActive(true);
        }
        repaint(); // Memastikan panel digambar ulang
    }

    /*
     * Secara spesifik memperbarui satu sel di view, misalnya setelah hint.
     * @param row Baris sel.
     * @param col Kolom sel.
     * @param value Nilai baru.
     * @param isHint Apakah ini dari hint.
     */
    public void updateSingleCellView(int row, int col, int value, boolean isHint) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            cellViews[row][col].setValueInView(value, isHint);
        }
    }

    /*
     * Menghilangkan sorotan (highlight) dari sel yang sedang aktif.
     */
    public void clearActiveCellHighlighting() {
         if (activeCellView != null) {
            activeCellView.setActive(false);
            // Jangan set activeCellView ke null agar bisa di-highlight ulang jika perlu
        }
    }

    /*
     * Mendapatkan CellView yang sedang aktif.
     * @return CellView yang aktif, atau null jika tidak ada.
     */
    public CellView getActiveCellView() {
        return activeCellView;
    }
}
