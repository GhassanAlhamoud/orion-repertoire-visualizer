# V3 Architecture Design

## Overview

This document defines the technical architecture for V3 (Repertoire Editor mode), including system design, data structures, component interactions, and integration with existing V1/V2 infrastructure.

## Architectural Principles

1. **Mode Separation**: V3 is a distinct mode, not a replacement for V1/V2
2. **Shared Foundation**: Reuse existing services and engine where possible
3. **Data Independence**: V3 uses standard PGN files, not .oriondb
4. **Extensibility**: Design for future features (collaboration, training)
5. **Performance**: Maintain <100ms UI responsiveness

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Application Shell                             │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              Mode Manager                                   │ │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │ │
│  │  │ Analytics    │  │ Historical   │  │ Repertoire   │    │ │
│  │  │ Mode (V1)    │  │ Mode (V2)    │  │ Editor (V3)  │    │ │
│  │  └──────────────┘  └──────────────┘  └──────────────┘    │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────────┐
│                    Shared Services Layer                         │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐ │
│  │ Chess Engine │ Database     │ Analysis     │ Opening      │ │
│  │ Service      │ Service      │ Service      │ Service      │ │
│  └──────────────┴──────────────┴──────────────┴──────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────────┐
│                    V3-Specific Services                          │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐ │
│  │ Repertoire   │ PGN Parser   │ Visual       │ Tree Editor  │ │
│  │ Manager      │ Service      │ Annotation   │ Service      │ │
│  │              │              │ Service      │              │ │
│  └──────────────┴──────────────┴──────────────┴──────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────────┐
│                    Data Layer                                    │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐ │
│  │ .oriondb     │ .orpd        │ .pgn         │ Config       │ │
│  │ (V1 Data)    │ (V2 Data)    │ (V3 Data)    │ Files        │ │
│  └──────────────┴──────────────┴──────────────┴──────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### V3 Component Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Repertoire Editor UI                          │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  RepertoireEditorView (Main Container)                     │ │
│  │  ┌──────────────┬────────────────────────────────────────┐ │ │
│  │  │              │  RepertoireTreeView                     │ │ │
│  │  │  Interactive │  - Tree display                         │ │ │
│  │  │  Chessboard  │  - Context menu                         │ │ │
│  │  │  View        │  - Move navigation                      │ │ │
│  │  │              ├────────────────────────────────────────┤ │ │
│  │  │  - Visual    │  CommentEditorView                      │ │ │
│  │  │    annot.    │  - Text input                           │ │ │
│  │  │  - Move      │  - NAG selector                         │ │ │
│  │  │    input     │  - Save/Clear                           │ │ │
│  │  └──────────────┴────────────────────────────────────────┘ │ │
│  │  ┌──────────────────────────────────────────────────────┐ │ │
│  │  │  Toolbar (File ops, NAGs, Colors, Undo/Redo)         │ │ │
│  │  └──────────────────────────────────────────────────────┘ │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────────┐
│                    Repertoire Editor Controller                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │  RepertoireEditorController                                │ │
│  │  - Coordinate UI components                                │ │
│  │  - Handle user interactions                                │ │
│  │  - Manage application state                                │ │
│  │  - Delegate to services                                    │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────────┐
│                    V3 Services                                   │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐ │
│  │ Repertoire   │ PGN Parser   │ Visual       │ Tree Editor  │ │
│  │ Manager      │ Service      │ Annotation   │ Service      │ │
│  │              │              │ Service      │              │ │
│  │ - File I/O   │ - Parse PGN  │ - Manage     │ - Add/Delete │ │
│  │ - Dirty      │ - Generate   │   highlights │ - Promote    │ │
│  │   tracking   │   PGN        │ - Manage     │ - Validate   │ │
│  │ - Auto-save  │ - Validate   │   arrows     │              │ │
│  └──────────────┴──────────────┴──────────────┴──────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Data Models

### Core Data Structures

#### RepertoireTree

```java
/**
 * Root of a repertoire tree.
 * Represents the entire PGN file.
 */
public class RepertoireTree {
    private Map<String, String> headers;  // PGN headers
    private RepertoireNode root;          // Root node (starting position)
    private String filePath;              // File location
    private boolean dirty;                // Has unsaved changes
    private LocalDateTime lastModified;   // Last save time
    
    public RepertoireTree() {
        this.headers = new HashMap<>();
        this.root = new RepertoireNode(null, null, true);
        this.dirty = false;
        
        // Set default headers
        headers.put("Event", "Repertoire");
        headers.put("Site", "?");
        headers.put("Date", LocalDate.now().toString().replace("-", "."));
        headers.put("White", "?");
        headers.put("Black", "?");
        headers.put("Result", "*");
    }
    
    public void setHeader(String key, String value) {
        headers.put(key, value);
        markDirty();
    }
    
    public void markDirty() {
        this.dirty = true;
    }
    
    public void markClean() {
        this.dirty = false;
        this.lastModified = LocalDateTime.now();
    }
}
```

#### RepertoireNode

```java
/**
 * A single node in the repertoire tree.
 * Represents a move and its associated data.
 */
public class RepertoireNode {
    private String move;                          // SAN notation (e.g., "e4")
    private String comment;                       // Text comment
    private List<Integer> nags;                   // NAG codes ($1, $2, etc.)
    private VisualAnnotations visualAnnotations;  // Highlights and arrows
    private boolean isMainLine;                   // True if main line, false if variation
    
    private RepertoireNode parent;                // Parent node
    private List<RepertoireNode> children;        // Child nodes (variations)
    
    private String fen;                           // Position after this move
    private int moveNumber;                       // Full move number
    private boolean isWhiteMove;                  // True if White's move
    
    public RepertoireNode(String move, RepertoireNode parent, boolean isMainLine) {
        this.move = move;
        this.parent = parent;
        this.isMainLine = isMainLine;
        this.children = new ArrayList<>();
        this.nags = new ArrayList<>();
        this.visualAnnotations = new VisualAnnotations();
    }
    
    /**
     * Add a child node (variation).
     */
    public RepertoireNode addChild(String move, boolean isMainLine) {
        RepertoireNode child = new RepertoireNode(move, this, isMainLine);
        children.add(child);
        return child;
    }
    
    /**
     * Remove a child node and all its descendants.
     */
    public void removeChild(RepertoireNode child) {
        children.remove(child);
    }
    
    /**
     * Promote a variation to main line.
     */
    public void promoteToMainLine() {
        if (parent == null || isMainLine) return;
        
        // Find current main line sibling
        RepertoireNode mainLine = parent.children.stream()
            .filter(RepertoireNode::isMainLine)
            .findFirst()
            .orElse(null);
        
        if (mainLine != null) {
            mainLine.isMainLine = false;
        }
        
        this.isMainLine = true;
        
        // Reorder children so main line is first
        parent.children.remove(this);
        parent.children.add(0, this);
    }
    
    /**
     * Get the main line from this node.
     */
    public List<RepertoireNode> getMainLine() {
        List<RepertoireNode> line = new ArrayList<>();
        RepertoireNode current = this;
        
        while (!current.children.isEmpty()) {
            RepertoireNode mainChild = current.children.stream()
                .filter(RepertoireNode::isMainLine)
                .findFirst()
                .orElse(current.children.get(0));
            
            line.add(mainChild);
            current = mainChild;
        }
        
        return line;
    }
    
    /**
     * Get all variations from this node (excluding main line).
     */
    public List<RepertoireNode> getVariations() {
        return children.stream()
            .filter(n -> !n.isMainLine)
            .collect(Collectors.toList());
    }
}
```

#### VisualAnnotations

```java
/**
 * Visual annotations for a position.
 * Includes highlighted squares and arrows.
 */
public class VisualAnnotations {
    private List<SquareHighlight> highlights;
    private List<Arrow> arrows;
    
    public VisualAnnotations() {
        this.highlights = new ArrayList<>();
        this.arrows = new ArrayList<>();
    }
    
    /**
     * Add a square highlight.
     */
    public void addHighlight(String square, HighlightColor color) {
        // Remove existing highlight on same square
        highlights.removeIf(h -> h.getSquare().equals(square));
        highlights.add(new SquareHighlight(square, color));
    }
    
    /**
     * Add an arrow.
     */
    public void addArrow(String from, String to, ArrowColor color) {
        arrows.add(new Arrow(from, to, color));
    }
    
    /**
     * Generate PGN [%csl] command.
     */
    public String toCSLCommand() {
        if (highlights.isEmpty()) return "";
        
        StringBuilder sb = new StringBuilder("[%csl ");
        for (SquareHighlight h : highlights) {
            sb.append(h.getColor().getCode())
              .append(h.getSquare())
              .append(",");
        }
        // Remove trailing comma
        sb.setLength(sb.length() - 1);
        sb.append("]");
        
        return sb.toString();
    }
    
    /**
     * Generate PGN [%cal] command.
     */
    public String toCALCommand() {
        if (arrows.isEmpty()) return "";
        
        StringBuilder sb = new StringBuilder("[%cal ");
        for (Arrow a : arrows) {
            sb.append(a.getColor().getCode())
              .append(a.getFrom())
              .append(a.getTo())
              .append(",");
        }
        // Remove trailing comma
        sb.setLength(sb.length() - 1);
        sb.append("]");
        
        return sb.toString();
    }
    
    /**
     * Parse [%csl] command.
     */
    public static List<SquareHighlight> parseCSL(String cslCommand) {
        List<SquareHighlight> highlights = new ArrayList<>();
        
        // Extract content between [%csl and ]
        Pattern pattern = Pattern.compile("\\[%csl\\s+([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(cslCommand);
        
        if (matcher.find()) {
            String content = matcher.group(1);
            String[] parts = content.split(",");
            
            for (String part : parts) {
                part = part.trim();
                if (part.length() >= 3) {
                    char colorCode = part.charAt(0);
                    String square = part.substring(1, 3);
                    HighlightColor color = HighlightColor.fromCode(colorCode);
                    highlights.add(new SquareHighlight(square, color));
                }
            }
        }
        
        return highlights;
    }
    
    /**
     * Parse [%cal] command.
     */
    public static List<Arrow> parseCAL(String calCommand) {
        List<Arrow> arrows = new ArrayList<>();
        
        // Extract content between [%cal and ]
        Pattern pattern = Pattern.compile("\\[%cal\\s+([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(calCommand);
        
        if (matcher.find()) {
            String content = matcher.group(1);
            String[] parts = content.split(",");
            
            for (String part : parts) {
                part = part.trim();
                if (part.length() >= 5) {
                    char colorCode = part.charAt(0);
                    String from = part.substring(1, 3);
                    String to = part.substring(3, 5);
                    ArrowColor color = ArrowColor.fromCode(colorCode);
                    arrows.add(new Arrow(from, to, color));
                }
            }
        }
        
        return arrows;
    }
}
```

#### Supporting Classes

```java
/**
 * A highlighted square.
 */
public class SquareHighlight {
    private String square;  // e.g., "e4"
    private HighlightColor color;
    
    public SquareHighlight(String square, HighlightColor color) {
        this.square = square;
        this.color = color;
    }
    
    // Getters
    public String getSquare() { return square; }
    public HighlightColor getColor() { return color; }
}

/**
 * An arrow between two squares.
 */
public class Arrow {
    private String from;  // e.g., "e2"
    private String to;    // e.g., "e4"
    private ArrowColor color;
    
    public Arrow(String from, String to, ArrowColor color) {
        this.from = from;
        this.to = to;
        this.color = color;
    }
    
    // Getters
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public ArrowColor getColor() { return color; }
}

/**
 * Highlight colors.
 */
public enum HighlightColor {
    RED('R'),
    GREEN('G'),
    YELLOW('Y'),
    BLUE('B');
    
    private char code;
    
    HighlightColor(char code) {
        this.code = code;
    }
    
    public char getCode() { return code; }
    
    public static HighlightColor fromCode(char code) {
        for (HighlightColor color : values()) {
            if (color.code == code) return color;
        }
        return GREEN;  // Default
    }
}

/**
 * Arrow colors.
 */
public enum ArrowColor {
    RED('R'),
    GREEN('G'),
    YELLOW('Y'),
    BLUE('B');
    
    private char code;
    
    ArrowColor(char code) {
        this.code = code;
    }
    
    public char getCode() { return code; }
    
    public static ArrowColor fromCode(char code) {
        for (ArrowColor color : values()) {
            if (color.code == code) return color;
        }
        return GREEN;  // Default
    }
}
```

## Service Layer

### RepertoireManager

```java
/**
 * Manages repertoire files (CRUD operations).
 */
public class RepertoireManager {
    private RepertoireTree currentRepertoire;
    private PGNParserService pgnParser;
    
    /**
     * Create a new empty repertoire.
     */
    public RepertoireTree createNew() {
        currentRepertoire = new RepertoireTree();
        return currentRepertoire;
    }
    
    /**
     * Open an existing repertoire from file.
     */
    public RepertoireTree open(File file) throws IOException {
        String pgnContent = Files.readString(file.toPath());
        currentRepertoire = pgnParser.parse(pgnContent);
        currentRepertoire.setFilePath(file.getAbsolutePath());
        currentRepertoire.markClean();
        return currentRepertoire;
    }
    
    /**
     * Save current repertoire to file.
     */
    public void save() throws IOException {
        if (currentRepertoire == null) {
            throw new IllegalStateException("No repertoire to save");
        }
        
        if (currentRepertoire.getFilePath() == null) {
            throw new IllegalStateException("No file path set, use saveAs()");
        }
        
        String pgnContent = pgnParser.generate(currentRepertoire);
        Files.writeString(
            Paths.get(currentRepertoire.getFilePath()),
            pgnContent
        );
        
        currentRepertoire.markClean();
    }
    
    /**
     * Save current repertoire to a new file.
     */
    public void saveAs(File file) throws IOException {
        if (currentRepertoire == null) {
            throw new IllegalStateException("No repertoire to save");
        }
        
        currentRepertoire.setFilePath(file.getAbsolutePath());
        save();
    }
    
    /**
     * Check if current repertoire has unsaved changes.
     */
    public boolean isDirty() {
        return currentRepertoire != null && currentRepertoire.isDirty();
    }
    
    /**
     * Get current repertoire.
     */
    public RepertoireTree getCurrentRepertoire() {
        return currentRepertoire;
    }
}
```

### PGNParserService

```java
/**
 * Parse and generate PGN files with visual annotations.
 */
public class PGNParserService {
    
    /**
     * Parse PGN string into RepertoireTree.
     */
    public RepertoireTree parse(String pgnContent) throws PGNParseException {
        RepertoireTree tree = new RepertoireTree();
        
        // Parse headers
        Map<String, String> headers = parseHeaders(pgnContent);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            tree.setHeader(entry.getKey(), entry.getValue());
        }
        
        // Parse moves
        String movesSection = extractMovesSection(pgnContent);
        parseMovesRecursive(tree.getRoot(), movesSection, new ParseContext());
        
        tree.markClean();
        return tree;
    }
    
    /**
     * Generate PGN string from RepertoireTree.
     */
    public String generate(RepertoireTree tree) {
        StringBuilder pgn = new StringBuilder();
        
        // Generate headers
        for (Map.Entry<String, String> entry : tree.getHeaders().entrySet()) {
            pgn.append("[")
               .append(entry.getKey())
               .append(" \"")
               .append(entry.getValue())
               .append("\"]\n");
        }
        
        pgn.append("\n");
        
        // Generate moves
        generateMovesRecursive(tree.getRoot(), pgn, new GenerateContext());
        
        return pgn.toString();
    }
    
    /**
     * Parse headers from PGN.
     */
    private Map<String, String> parseHeaders(String pgnContent) {
        Map<String, String> headers = new HashMap<>();
        Pattern pattern = Pattern.compile("\\[([^\\s]+)\\s+\"([^\"]+)\"\\]");
        Matcher matcher = pattern.matcher(pgnContent);
        
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            headers.put(key, value);
        }
        
        return headers;
    }
    
    /**
     * Extract moves section (after headers).
     */
    private String extractMovesSection(String pgnContent) {
        // Find the end of headers (last ']')
        int lastBracket = pgnContent.lastIndexOf(']');
        if (lastBracket == -1) return pgnContent;
        
        return pgnContent.substring(lastBracket + 1).trim();
    }
    
    /**
     * Parse moves recursively (handles variations).
     */
    private void parseMovesRecursive(RepertoireNode parent, 
                                     String movesText, 
                                     ParseContext context) {
        // Implementation details:
        // 1. Tokenize moves, comments, NAGs, variations
        // 2. Build tree structure
        // 3. Parse visual annotations from comments
        // 4. Handle nested variations
        
        // This is a complex parser - see PGN Parser section for details
    }
    
    /**
     * Generate moves recursively (handles variations).
     */
    private void generateMovesRecursive(RepertoireNode node,
                                       StringBuilder pgn,
                                       GenerateContext context) {
        // Implementation details:
        // 1. Generate move number
        // 2. Generate move in SAN
        // 3. Generate NAGs
        // 4. Generate comment with visual annotations
        // 5. Generate variations in parentheses
        // 6. Recurse to children
        
        // This is a complex generator - see PGN Parser section for details
    }
}
```

### TreeEditorService

```java
/**
 * Edit operations on repertoire tree.
 */
public class TreeEditorService {
    private ChessEngineService chessEngine;
    
    /**
     * Add a move to the tree.
     */
    public RepertoireNode addMove(RepertoireNode parent, String move) 
            throws IllegalMoveException {
        // Validate move is legal
        if (!chessEngine.isLegalMove(parent.getFen(), move)) {
            throw new IllegalMoveException("Illegal move: " + move);
        }
        
        // Check if move already exists
        for (RepertoireNode child : parent.getChildren()) {
            if (child.getMove().equals(move)) {
                return child;  // Move already exists, return it
            }
        }
        
        // Add new move
        boolean isMainLine = parent.getChildren().isEmpty();
        RepertoireNode newNode = parent.addChild(move, isMainLine);
        
        // Calculate position after move
        String newFen = chessEngine.makeMove(parent.getFen(), move);
        newNode.setFen(newFen);
        
        // Calculate move number
        int moveNumber = parent.getMoveNumber();
        if (!parent.isWhiteMove()) {
            moveNumber++;
        }
        newNode.setMoveNumber(moveNumber);
        newNode.setWhiteMove(!parent.isWhiteMove());
        
        return newNode;
    }
    
    /**
     * Delete a move and all its descendants.
     */
    public void deleteMove(RepertoireNode node) {
        if (node.getParent() == null) {
            throw new IllegalArgumentException("Cannot delete root node");
        }
        
        node.getParent().removeChild(node);
    }
    
    /**
     * Promote a variation to main line.
     */
    public void promoteVariation(RepertoireNode node) {
        node.promoteToMainLine();
    }
    
    /**
     * Add a comment to a move.
     */
    public void addComment(RepertoireNode node, String comment) {
        node.setComment(comment);
    }
    
    /**
     * Add a NAG to a move.
     */
    public void addNAG(RepertoireNode node, int nag) {
        if (!node.getNags().contains(nag)) {
            node.getNags().add(nag);
        }
    }
    
    /**
     * Remove a NAG from a move.
     */
    public void removeNAG(RepertoireNode node, int nag) {
        node.getNags().remove(Integer.valueOf(nag));
    }
}
```

### VisualAnnotationService

```java
/**
 * Manage visual annotations (highlights and arrows).
 */
public class VisualAnnotationService {
    
    /**
     * Add a square highlight.
     */
    public void addHighlight(RepertoireNode node, String square, HighlightColor color) {
        node.getVisualAnnotations().addHighlight(square, color);
    }
    
    /**
     * Remove a square highlight.
     */
    public void removeHighlight(RepertoireNode node, String square) {
        node.getVisualAnnotations().getHighlights()
            .removeIf(h -> h.getSquare().equals(square));
    }
    
    /**
     * Add an arrow.
     */
    public void addArrow(RepertoireNode node, String from, String to, ArrowColor color) {
        node.getVisualAnnotations().addArrow(from, to, color);
    }
    
    /**
     * Remove an arrow.
     */
    public void removeArrow(RepertoireNode node, String from, String to) {
        node.getVisualAnnotations().getArrows()
            .removeIf(a -> a.getFrom().equals(from) && a.getTo().equals(to));
    }
    
    /**
     * Clear all visual annotations.
     */
    public void clearAnnotations(RepertoireNode node) {
        node.getVisualAnnotations().getHighlights().clear();
        node.getVisualAnnotations().getArrows().clear();
    }
}
```

## Mode Integration

### Mode Manager

```java
/**
 * Manages switching between application modes.
 */
public class ModeManager {
    private ApplicationMode currentMode;
    private Map<ApplicationMode, ModeController> controllers;
    
    public enum ApplicationMode {
        ANALYTICS_V1,
        HISTORICAL_V2,
        REPERTOIRE_EDITOR_V3
    }
    
    /**
     * Switch to a different mode.
     */
    public void switchMode(ApplicationMode newMode) {
        // Save current mode state
        if (currentMode != null) {
            ModeController currentController = controllers.get(currentMode);
            currentController.saveState();
            
            // Check for unsaved changes
            if (currentController.hasUnsavedChanges()) {
                boolean confirmed = showUnsavedChangesDialog();
                if (!confirmed) {
                    return;  // User cancelled
                }
            }
            
            currentController.deactivate();
        }
        
        // Activate new mode
        currentMode = newMode;
        ModeController newController = controllers.get(newMode);
        newController.activate();
        newController.restoreState();
        
        // Update UI
        updateModeUI(newMode);
    }
    
    /**
     * Get current mode.
     */
    public ApplicationMode getCurrentMode() {
        return currentMode;
    }
}
```

### Mode Controller Interface

```java
/**
 * Interface for mode controllers.
 */
public interface ModeController {
    /**
     * Activate this mode (show UI, load data).
     */
    void activate();
    
    /**
     * Deactivate this mode (hide UI, release resources).
     */
    void deactivate();
    
    /**
     * Save current state.
     */
    void saveState();
    
    /**
     * Restore previous state.
     */
    void restoreState();
    
    /**
     * Check if there are unsaved changes.
     */
    boolean hasUnsavedChanges();
    
    /**
     * Get the root UI node for this mode.
     */
    Node getUIRoot();
}
```

## Integration with Existing Services

### Shared Services

**ChessEngineService** (Existing):
- ✅ Move generation
- ✅ Move validation
- ✅ Board state management
- ✅ FEN conversion

**OpeningService** (Existing):
- ✅ Opening name detection
- ✅ ECO code lookup
- ⚠️ May need extension for repertoire context

**DatabaseService** (Existing):
- ❌ Not used in V3 (different data model)
- ⚠️ May be used for importing from V1/V2

### New Services

**RepertoireManager** (New):
- File I/O for .pgn files
- Dirty state tracking
- Auto-save capability

**PGNParserService** (New):
- Parse PGN with visual annotations
- Generate PGN with visual annotations
- Validate PGN format

**TreeEditorService** (New):
- Add/delete moves
- Promote variations
- Add comments and NAGs

**VisualAnnotationService** (New):
- Manage highlights
- Manage arrows
- Generate/parse visual commands

## Performance Considerations

### Rendering Performance

**Challenge**: Rendering visual annotations on every move can be slow.

**Solution**:
- Use JavaFX Canvas for efficient rendering
- Cache rendered annotations
- Only re-render on changes
- Use GPU acceleration where available

**Target**: <16ms per frame (60 FPS)

### Tree Navigation Performance

**Challenge**: Large repertoires (1000+ moves) can be slow to navigate.

**Solution**:
- Lazy loading of tree nodes
- Virtual scrolling in tree view
- Index for fast node lookup
- Cache current path

**Target**: <100ms to navigate to any node

### File I/O Performance

**Challenge**: Large PGN files can be slow to parse/generate.

**Solution**:
- Streaming parser (don't load entire file into memory)
- Incremental generation (write as you go)
- Background thread for I/O
- Progress indicator for large files

**Target**: <1s for 1000-move repertoire

## Security and Data Integrity

### Data Validation

**On Parse**:
- Validate PGN format
- Validate move legality
- Validate square names
- Validate color codes

**On Save**:
- Validate tree structure
- Validate FEN positions
- Escape special characters
- Check file permissions

### Data Backup

**Auto-save**:
- Save to temporary file every 5 minutes
- Keep last 3 auto-saves
- Restore on crash

**Backup on Save**:
- Create .bak file before overwriting
- Keep last backup
- Option to restore from backup

### Error Handling

**Parse Errors**:
- Show detailed error message
- Highlight problematic line
- Offer to fix common issues
- Option to open in text editor

**Save Errors**:
- Retry with exponential backoff
- Save to alternative location
- Preserve unsaved changes in memory
- Alert user immediately

## Conclusion

V3 architecture is designed to:

1. **Coexist** with V1/V2 without conflicts
2. **Reuse** existing services where appropriate
3. **Extend** functionality with new services
4. **Maintain** performance and responsiveness
5. **Ensure** data integrity and compatibility

The architecture supports future enhancements such as:
- Collaboration (shared repertoires)
- Training mode (practice variations)
- Spaced repetition (memory training)
- Cloud sync (backup and sync)

---

**Next Steps**:
1. Design PGN parser in detail
2. Design UI components
3. Implement prototype
4. Test with real PGN files

---

**Document Version**: 1.0  
**Status**: Draft  
**Last Updated**: November 2024
