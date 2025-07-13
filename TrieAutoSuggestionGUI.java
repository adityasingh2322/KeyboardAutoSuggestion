import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// ==================== Trie Node ====================
class TrieNode {
    TrieNode[] children;
    boolean isEnd;
    String word;
    int freq;

    TrieNode() {
        children = new TrieNode[26];
        isEnd = false;
        word = "";
        freq = 0;
    }
}

// ==================== Trie Class ====================
class Trie {
    private TrieNode root;
    private Map<String, Integer> dictionary;

    public Trie() {
        root = new TrieNode();
        dictionary = new HashMap<>();
    }

    public void insert(String word, int freq) {
        TrieNode curr = root;
        for (char ch : word.toCharArray()) {
            if (!Character.isLetter(ch)) return;
            int idx = Character.toLowerCase(ch) - 'a';
            if (curr.children[idx] == null)
                curr.children[idx] = new TrieNode();
            curr = curr.children[idx];
        }
        curr.isEnd = true;
        curr.word = word;
        curr.freq += freq;
        dictionary.put(word, curr.freq);
    }

    public List<String> suggestTop3(String prefix) {
        TrieNode curr = root;
        for (char ch : prefix.toCharArray()) {
            if (!Character.isLetter(ch)) return new ArrayList<>();
            int idx = Character.toLowerCase(ch) - 'a';
            if (curr.children[idx] == null) return new ArrayList<>();
            curr = curr.children[idx];
        }

        List<Map.Entry<String, Integer>> found = new ArrayList<>();
        collect(curr, found);

        found.sort((a, b) -> {
            if (!a.getValue().equals(b.getValue()))
                return b.getValue() - a.getValue();
            return a.getKey().compareTo(b.getKey());
        });

        List<String> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, found.size()); i++)
            top3.add(found.get(i).getKey());

        return top3;
    }

    private void collect(TrieNode node, List<Map.Entry<String, Integer>> res) {
        if (node.isEnd) res.add(Map.entry(node.word, node.freq));
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null)
                collect(node.children[i], res);
        }
    }

    public void increaseFrequency(String word) {
        insert(word, 1);
    }

    public void saveToFile(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
                out.println(entry.getKey() + " " + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("❌ Cannot write to file " + filename);
        }
    }

    public void loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return;

        try (Scanner in = new Scanner(file)) {
            while (in.hasNext()) {
                String word = in.next();
                int freq = in.nextInt();
                insert(word, freq);
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }
    }
}

// ==================== GUI Class ====================
public class TrieAutoSuggestionGUI extends JFrame {
    private Trie trie;
    private JTextField inputField;
    private DefaultListModel<String> listModel;
    private JList<String> suggestionList;
    private JLabel statusLabel;

    public TrieAutoSuggestionGUI() {
        trie = new Trie();
        trie.loadFromFile("dictionary.txt");
        setupGUI();
    }

    private void setupGUI() {
        setTitle("🔍 Trie Auto-Suggestion");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // Input field
        inputField = new JTextField();
        panel.add(inputField, BorderLayout.NORTH);

        // Suggestion list in scroll pane
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        panel.add(new JScrollPane(suggestionList), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton searchBtn = new JButton("Search Suggestions");
        JButton selectBtn = new JButton("Select Word");
        JButton saveBtn = new JButton("Save & Exit");
        buttonPanel.add(searchBtn);
        buttonPanel.add(selectBtn);
        buttonPanel.add(saveBtn);

        // Status label
        statusLabel = new JLabel("Type a prefix and click 'Search Suggestions'");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        // Combine button panel and status label
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(buttonPanel, BorderLayout.NORTH);
        bottomContainer.add(statusLabel, BorderLayout.SOUTH);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        // ✅ Add the full panel to the frame
        add(panel);

        // Button actions
        searchBtn.addActionListener(e -> searchSuggestions());
        selectBtn.addActionListener(e -> selectWord());
        saveBtn.addActionListener(e -> {
            trie.saveToFile("dictionary.txt");
            JOptionPane.showMessageDialog(this, "✅ Dictionary saved.");
            System.exit(0);
        });
    }

    private void searchSuggestions() {
        String prefix = inputField.getText().trim();
        if (prefix.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please enter a prefix.");
            return;
        }

        List<String> suggestions = trie.suggestTop3(prefix);
        listModel.clear();
        if (suggestions.isEmpty()) {
            listModel.addElement("❌ No suggestions found.");
            statusLabel.setText("No suggestions. Word added to dictionary.");
            trie.insert(prefix, 1);
        } else {
            for (String s : suggestions)
                listModel.addElement(s);
            statusLabel.setText("Top 3 suggestions shown.");
        }
    }

    private void selectWord() {
        String selected = suggestionList.getSelectedValue();
        if (selected == null || selected.startsWith("❌")) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a valid suggestion.");
            return;
        }

        trie.increaseFrequency(selected);
        statusLabel.setText("✅ Frequency increased for '" + selected + "'");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TrieAutoSuggestionGUI().setVisible(true);
        });
    }
}
