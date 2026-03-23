# Trie Auto-Suggestion GUI

Simple Java Swing application that provides real-time top-3 auto-suggestions from a trie-based dictionary.

## Features

- Loads words and usage frequency from `dictionary.txt` at startup
- Insert words into a Trie data structure with frequency tracking
- Provides the top 3 suggestions for a given prefix, sorted by frequency and lexicographically
- Adds unknown prefixes to dictionary with initial frequency 1 (learns on the fly)
- Select suggestion to increase its frequency (adaptive ranking)
- Save dictionary back to `dictionary.txt` with updated frequencies

## Project structure

- `TrieAutoSuggestionGUI.java` - main application with UI and Trie logic
- `dictionary.txt` - persisted word-frequency knowledge base (auto-created/updated)

## Prerequisites

- Java JDK 8 or later

## Build and Run

From command line in project folder:

```bash
javac TrieAutoSuggestionGUI.java
java TrieAutoSuggestionGUI
```

## Usage

1. Type a prefix into the input field.
2. Click `Search Suggestions` to display up to 3 suggested words.
3. Select a suggestion and click `Select Word` to increase its frequency.
4. Click `Save & Exit` to persist dictionary updates and close app.

## Notes

- Only alphabetic characters are accepted for trie paths (case-insensitive).
- Missing words are added automatically when search returns no suggestions.
- Word-frequency data is stored in plain text (`word frequency`) per line.
