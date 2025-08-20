import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Function;

/**
 * Non-intrusive autocomplete overlay for JTextField.
 * - Uses a lightweight JWindow and JList so it doesn't steal focus from the field
 * - Supports UP/DOWN/ENTER to navigate/accept
 */
public class AutoCompleteOverlay<T> {
    private final JTextField field;
    private final Function<String, List<T>> provider;
    private final JWindow window;
    private final JList<T> list;
    private final JScrollPane scroll;
    private final DefaultListModel<T> model;
    private final int maxRows;
    private final Function<T, String> toText;

    public AutoCompleteOverlay(JTextField field,
                               Function<String, List<T>> provider,
                               ListCellRenderer<? super T> renderer,
                               int maxRows,
                               Function<T, String> toText) {
        this.field = field;
        this.provider = provider;
        this.maxRows = Math.max(3, Math.min(12, maxRows));
        this.toText = toText != null ? toText : (t -> t != null ? t.toString() : "");

        Window owner = SwingUtilities.getWindowAncestor(field);
        this.window = new JWindow(owner);
        this.window.setFocusableWindowState(false);
        this.window.setAlwaysOnTop(false);

        this.model = new DefaultListModel<>();
        this.list = new JList<>(model);
        this.list.setFocusable(false);
        if (renderer != null) this.list.setCellRenderer(renderer);
        this.scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,120)));
        window.getContentPane().add(scroll);

        // Document updates
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });

        // Position overlay under caret on movement or resize
        field.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) { position(); }
            public void componentResized(ComponentEvent e) { position(); }
        });
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { hide(); }
        });

        // Keyboard navigation without stealing focus
        field.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!window.isVisible()) return;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    int idx = list.getSelectedIndex();
                    list.setSelectedIndex(Math.min(model.size()-1, idx+1));
                    list.ensureIndexIsVisible(list.getSelectedIndex());
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    int idx = list.getSelectedIndex();
                    list.setSelectedIndex(Math.max(0, idx-1));
                    list.ensureIndexIsVisible(list.getSelectedIndex());
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    accept();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hide();
                    e.consume();
                }
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) accept();
            }
        });
    }

    private void refresh() {
        String q = field.getText().trim();
        if (q.isEmpty()) { hide(); return; }
        List<T> data = provider.apply(q);
        model.clear();
        if (data == null || data.isEmpty()) { hide(); return; }
        int limit = Math.min(maxRows, data.size());
        for (int i = 0; i < limit; i++) model.addElement(data.get(i));
        list.setSelectedIndex(0);
        position();
        window.setVisible(true);
        window.pack();
    }

    private void position() {
        try {
            int pos = field.getCaretPosition();
            Rectangle r = field.modelToView(pos);
            Point p = new Point(r.x, r.y + r.height);
            SwingUtilities.convertPointToScreen(p, field);
            window.setLocation(p);
            window.setSize(Math.max(field.getWidth(), 220), list.getFixedCellHeight() * (Math.min(model.getSize(), maxRows) + 1));
        } catch (Exception ignore) {}
    }

    private void accept() {
        T sel = list.getSelectedValue();
        if (sel == null) { hide(); return; }
        field.setText(toText.apply(sel));
        field.requestFocusInWindow();
        field.setCaretPosition(field.getText().length());
        hide();
    }

    // Theming from host app
    public void applyTheme(Color bg, Color fg, Color selectionBg, Color border) {
        list.setBackground(bg);
        list.setForeground(fg);
        list.setSelectionBackground(selectionBg);
        list.setSelectionForeground(fg);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(border));
        window.getContentPane().setBackground(bg);
    }

    private void hide() { window.setVisible(false); }
}


