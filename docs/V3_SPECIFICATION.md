# V3 Technical Specification

## Document Information

**Version**: 3.0.0  
**Status**: Planning Phase  
**Last Updated**: November 2024  
**Purpose**: Complete technical specification for V3 Repertoire Editor  

## Executive Summary

V3 transforms Orion from an analytics tool into a **repertoire curation platform**. This specification defines all technical requirements, features, and acceptance criteria for the V3 MVP release.

### Product Goal

Provide competitive players with a single, integrated tool to create, edit, and visually annotate their ideal opening repertoire.

### Success Metric

**40% of active users** create at least one repertoire with 10+ moves and 1+ annotation within 30 days of V3 release.

## Feature Specifications

### F1: Repertoire File Management

**Priority**: P1 (MVP)  
**Epic**: 5 (Repertoire Management)  
**Complexity**: Medium  
**Estimated Effort**: 1 week  

#### Requirements

**F1.1: New Repertoire**

**Description**: Create a new, empty repertoire.

**User Story**: As a Student Player, I want to create a new repertoire, so that I can start building my opening preparation.

**Acceptance Criteria**:
1. Menu item "File → New Repertoire" (Ctrl+N)
2. If current repertoire has unsaved changes, show confirmation dialog
3. Create new RepertoireTree with default headers
4. Clear UI (empty tree, empty board, empty comment)
5. Set window title to "Untitled - Orion Repertoire Editor"
6. Mark as clean (no unsaved changes)

**Technical Details**:
- Create new `RepertoireTree` object
- Set default headers: Event="Repertoire", Site="?", Date=today, White="?", Black="?", Result="*"
- Reset board to starting position
- Clear tree view
- Clear comment editor
- Update status bar

**F1.2: Open Repertoire**

**Description**: Open an existing repertoire from a PGN file.

**User Story**: As a Student Player, I want to open my existing repertoire, so that I can continue working on it.

**Acceptance Criteria**:
1. Menu item "File → Open Repertoire" (Ctrl+O)
2. If current repertoire has unsaved changes, show confirmation dialog
3. Show native file picker (filter: *.pgn)
4. Parse selected PGN file
5. Load into tree view
6. Display first position on board
7. Update window title with file name
8. Mark as clean

**Technical Details**:
- Use JavaFX FileChooser
- Call `PGNParserService.parse()`
- Handle parse errors gracefully
- Show progress indicator for large files (>1000 moves)
- Update recent files list

**F1.3: Save Repertoire**

**Description**: Save current repertoire to file.

**User Story**: As a Student Player, I want to save my repertoire, so that I don't lose my work.

**Acceptance Criteria**:
1. Menu item "File → Save" (Ctrl+S)
2. If no file path, prompt for location (Save As)
3. Generate PGN with visual annotations
4. Write to file
5. Mark as clean
6. Update last modified time
7. Show success message (optional)

**Technical Details**:
- Call `PGNGenerator.generate()`
- Write to file atomically (temp file + rename)
- Create backup (.bak) before overwriting
- Handle write errors (permissions, disk space)

**F1.4: Save As**

**Description**: Save current repertoire to a new file.

**User Story**: As a Student Player, I want to save my repertoire with a new name, so that I can create variations.

**Acceptance Criteria**:
1. Menu item "File → Save As" (Ctrl+Shift+S)
2. Show native file picker (filter: *.pgn)
3. Generate PGN with visual annotations
4. Write to new file
5. Update current file path
6. Mark as clean
7. Update window title

**Technical Details**:
- Use JavaFX FileChooser
- Suggest default name based on headers (e.g., "White_e4.pgn")
- Same save logic as F1.3

**F1.5: Unsaved Changes Warning**

**Description**: Warn user about unsaved changes.

**Acceptance Criteria**:
1. Track dirty state (unsaved changes)
2. Show warning dialog before New/Open/Close
3. Options: Save, Don't Save, Cancel
4. Show indicator in status bar (red dot)
5. Show indicator in window title (*)

**Technical Details**:
- Set dirty flag on any edit operation
- Clear dirty flag on save
- Show confirmation dialog with 3 buttons
- Prevent data loss

---

### F2: Repertoire Tree Editing

**Priority**: P1 (MVP)  
**Epic**: 5 (Repertoire Management)  
**Complexity**: High  
**Estimated Effort**: 2 weeks  

#### Requirements

**F2.1: Add Move**

**Description**: Add a move to the repertoire tree.

**User Story**: As a Student Player, I want to make moves on the board, so that I can build my opening variations.

**Acceptance Criteria**:
1. Click on piece to select
2. Click on destination to move
3. Validate move is legal
4. Add move to tree (or navigate if exists)
5. Update board to new position
6. Highlight move in tree
7. Mark as dirty

**Technical Details**:
- Use `ChessEngineService.isLegalMove()`
- Use `TreeEditorService.addMove()`
- Calculate new FEN with `ChessEngineService.makeMove()`
- If move already exists, navigate to it (don't duplicate)
- Support both click-click and drag-drop

**F2.2: Delete Move**

**Description**: Delete a move and all its descendants.

**User Story**: As a Student Player, I want to delete moves, so that I can remove incorrect lines.

**Acceptance Criteria**:
1. Right-click on move in tree
2. Select "Delete Move" from context menu
3. Show confirmation dialog
4. Delete move and all descendants
5. Navigate to parent move
6. Update board
7. Mark as dirty

**Technical Details**:
- Use `TreeEditorService.deleteMove()`
- Cannot delete root node
- Confirmation dialog: "Delete this move and all variations?"
- Support undo

**F2.3: Promote Variation**

**Description**: Promote a variation to the main line.

**User Story**: As a Student Player, I want to promote variations, so that I can reorganize my repertoire.

**Acceptance Criteria**:
1. Right-click on variation move in tree
2. Select "Promote to Main Line" from context menu
3. Swap with current main line
4. Update tree display (bold for main line)
5. Mark as dirty

**Technical Details**:
- Use `TreeEditorService.promoteVariation()`
- Reorder children so promoted variation is first
- Update isMainLine flags
- Support undo

**F2.4: Navigate Tree**

**Description**: Navigate through the repertoire tree.

**Acceptance Criteria**:
1. Click on move in tree to navigate
2. Board updates to position after that move
3. Move highlighted in tree
4. Comment editor shows move's comment
5. Keyboard shortcuts work (→, ←, ↑, ↓)

**Technical Details**:
- Track current node in tree
- Update board FEN on navigation
- Scroll tree view to show current move
- Support keyboard navigation

---

### F3: Text and NAG Annotations

**Priority**: P1 (MVP)  
**Epic**: 5 (Repertoire Management)  
**Complexity**: Low  
**Estimated Effort**: 3 days  

#### Requirements

**F3.1: Add Comment**

**Description**: Add text comment to a move.

**User Story**: As a Student Player, I want to add notes to moves, so that I can remember my analysis.

**Acceptance Criteria**:
1. Text area for comment editing
2. Type comment for current move
3. Auto-save on focus loss or Ctrl+Enter
4. Comment shown in tree (truncated)
5. Mark as dirty

**Technical Details**:
- Use `TreeEditorService.addComment()`
- Auto-save after 2 seconds of inactivity
- Support multi-line comments
- Escape special characters in PGN

**F3.2: Add NAG**

**Description**: Add numeric annotation glyph (NAG) to a move.

**User Story**: As a Student Player, I want to add chess symbols (!, ??) to moves, so that I can mark blunders and good moves.

**Acceptance Criteria**:
1. Toolbar buttons for common NAGs (!, !!, ?, ??, !?, ?!)
2. Click button to add NAG to current move
3. NAG shown inline in tree
4. Support multiple NAGs per move
5. Mark as dirty

**Technical Details**:
- Use `TreeEditorService.addNAG()`
- NAG codes: $1=!, $2=?, $3=!!, $4=??, $5=!?, $6=?!
- Show NAG symbols in tree using Unicode or images
- Support keyboard shortcuts (Ctrl+1 to Ctrl+6)

**F3.3: Remove NAG**

**Description**: Remove NAG from a move.

**Acceptance Criteria**:
1. Right-click on move → "Remove NAG" → Select NAG
2. Or click NAG button again to toggle
3. NAG removed from move
4. Tree updated
5. Mark as dirty

**Technical Details**:
- Use `TreeEditorService.removeNAG()`
- Support undo

---

### F4: Visual Annotations - Highlights

**Priority**: P1 (MVP)  
**Epic**: 6 (Visual Annotations)  
**Complexity**: Medium  
**Estimated Effort**: 1 week  

#### Requirements

**F4.1: Highlight Square**

**Description**: Highlight a square on the board.

**User Story**: As a Student Player, I want to highlight squares, so that I can mark key squares visually.

**Acceptance Criteria**:
1. Alt+Click on square to highlight
2. Or right-click → "Highlight Square" → Select color
3. Square highlighted with selected color
4. Saved with current move
5. Shown in PGN as `[%csl Rd4]`
6. Mark as dirty

**Technical Details**:
- Use `VisualAnnotationService.addHighlight()`
- Support colors: Red, Green, Yellow, Blue
- Render highlight on board (semi-transparent overlay)
- Parse/generate `[%csl]` command in PGN

**F4.2: Remove Highlight**

**Description**: Remove highlight from a square.

**Acceptance Criteria**:
1. Alt+Click on highlighted square
2. Or right-click → "Remove Highlight"
3. Highlight removed
4. Mark as dirty

**Technical Details**:
- Use `VisualAnnotationService.removeHighlight()`
- Support undo

**F4.3: Multiple Highlights**

**Description**: Support multiple highlights per position.

**Acceptance Criteria**:
1. Can highlight multiple squares
2. Each square can have different color
3. All highlights saved in PGN
4. All highlights rendered on board

**Technical Details**:
- Store list of highlights per move
- Generate `[%csl Rd4,Ge5,Yd5]` format

---

### F5: Visual Annotations - Arrows

**Priority**: P1 (MVP)  
**Epic**: 6 (Visual Annotations)  
**Complexity**: Medium  
**Estimated Effort**: 1 week  

#### Requirements

**F5.1: Draw Arrow**

**Description**: Draw an arrow on the board.

**User Story**: As a Student Player, I want to draw arrows, so that I can visualize plans and threats.

**Acceptance Criteria**:
1. Ctrl+Drag from square to square
2. Or right-click → "Draw Arrow" → Click from, click to, select color
3. Arrow drawn with selected color
4. Saved with current move
5. Shown in PGN as `[%cal Gd2d4]`
6. Mark as dirty

**Technical Details**:
- Use `VisualAnnotationService.addArrow()`
- Support colors: Red, Green, Yellow, Blue
- Render arrow on board (curved or straight)
- Parse/generate `[%cal]` command in PGN

**F5.2: Remove Arrow**

**Description**: Remove arrow from the board.

**Acceptance Criteria**:
1. Ctrl+Click on arrow
2. Or right-click → "Remove Arrow"
3. Arrow removed
4. Mark as dirty

**Technical Details**:
- Use `VisualAnnotationService.removeArrow()`
- Support undo

**F5.3: Multiple Arrows**

**Description**: Support multiple arrows per position.

**Acceptance Criteria**:
1. Can draw multiple arrows
2. Each arrow can have different color
3. All arrows saved in PGN
4. All arrows rendered on board

**Technical Details**:
- Store list of arrows per move
- Generate `[%cal Gd2d4,Re7e5]` format

---

### F6: Undo/Redo

**Priority**: P2 (Post-MVP)  
**Complexity**: Medium  
**Estimated Effort**: 3 days  

#### Requirements

**F6.1: Undo**

**Description**: Undo last operation.

**Acceptance Criteria**:
1. Menu item "Edit → Undo" (Ctrl+Z)
2. Toolbar button
3. Undo last operation
4. Update UI
5. Can undo multiple times

**Technical Details**:
- Implement command pattern
- Store last 100 operations
- Support operations: add move, delete move, promote, add comment, add NAG, add visual

**F6.2: Redo**

**Description**: Redo last undone operation.

**Acceptance Criteria**:
1. Menu item "Edit → Redo" (Ctrl+Y)
2. Toolbar button
3. Redo last undone operation
4. Update UI
5. Can redo multiple times

**Technical Details**:
- Maintain redo stack
- Clear redo stack on new operation

---

### F7: Auto-Save

**Priority**: P2 (Post-MVP)  
**Complexity**: Low  
**Estimated Effort**: 2 days  

#### Requirements

**F7.1: Auto-Save**

**Description**: Automatically save repertoire periodically.

**Acceptance Criteria**:
1. Auto-save every 5 minutes
2. Save to temporary file
3. Keep last 3 auto-saves
4. Restore on crash

**Technical Details**:
- Background thread for auto-save
- Save to `.autosave/` directory
- File name: `<original>_autosave_<timestamp>.pgn`
- Clean up old auto-saves

---

### F8: Export

**Priority**: P3 (Future)  
**Complexity**: Low  
**Estimated Effort**: 2 days  

#### Requirements

**F8.1: Export to PGN (without visuals)**

**Description**: Export repertoire as standard PGN without visual annotations.

**Acceptance Criteria**:
1. Menu item "File → Export → PGN (Standard)"
2. Generate PGN without `[%csl]` and `[%cal]`
3. Compatible with all PGN readers

**F8.2: Export to PDF**

**Description**: Export repertoire as PDF document.

**Acceptance Criteria**:
1. Menu item "File → Export → PDF"
2. Generate PDF with tree diagram
3. Include comments and annotations

---

## Non-Functional Requirements

### Performance

**P1: UI Responsiveness**
- All UI operations complete in <100ms
- Board rendering at 60 FPS
- Tree navigation in <50ms

**P2: File Operations**
- Parse 1000-move repertoire in <1s
- Generate 1000-move repertoire in <500ms
- Save file in <200ms

**P3: Memory Usage**
- Maximum 200MB for 1000-move repertoire
- No memory leaks
- Efficient garbage collection

### Reliability

**R1: Data Integrity**
- No data loss on crash
- Atomic file writes
- Backup before overwrite

**R2: Error Handling**
- Graceful error messages
- Recovery from parse errors
- Validation of all inputs

**R3: Stability**
- <1% crash rate
- No UI freezes
- Responsive during long operations

### Usability

**U1: Learnability**
- New users productive in <10 minutes
- Intuitive interactions
- Clear visual feedback

**U2: Efficiency**
- Keyboard shortcuts for all operations
- Quick access to common features
- Minimal clicks for common tasks

**U3: Accessibility**
- Screen reader support
- Keyboard-only navigation
- Color blind friendly

### Compatibility

**C1: File Format**
- Standard PGN format
- Compatible with ChessBase, Lichess, Chess.com
- Human-readable

**C2: Platform**
- Windows 10+
- macOS 10.14+
- Linux (Ubuntu 20.04+)

**C3: Java Version**
- Java 11+ required
- JavaFX 17+ required

## Testing Requirements

### Unit Tests

**Coverage**: 80%+ code coverage

**Tests**:
- PGN parser (lexer, parser, generator)
- Tree editor service
- Visual annotation service
- Repertoire manager

### Integration Tests

**Tests**:
- File operations (new, open, save)
- Tree editing (add, delete, promote)
- Annotations (comment, NAG, visual)
- Undo/redo

### UI Tests

**Tests**:
- All interactions
- Keyboard shortcuts
- Error dialogs
- Visual rendering

### Performance Tests

**Tests**:
- Large file parsing (1000+ moves)
- Tree navigation (1000+ moves)
- Memory usage (1000+ moves)
- UI responsiveness

### Compatibility Tests

**Tests**:
- PGN compatibility (ChessBase, Lichess, Chess.com)
- Platform compatibility (Windows, macOS, Linux)
- Java version compatibility (11, 17, 21)

## Security Requirements

### Data Security

**S1: Local Storage**
- All data stored locally
- No cloud upload (unless user opts in)
- File permissions respected

**S2: Input Validation**
- Validate all PGN input
- Sanitize user input
- Prevent code injection

### Privacy

**P1: No Tracking**
- No analytics (unless user opts in)
- No telemetry
- No personal data collection

## Documentation Requirements

### User Documentation

**D1: User Guide**
- Getting started
- Feature overview
- Keyboard shortcuts
- Troubleshooting

**D2: Video Tutorials**
- Creating a repertoire
- Adding annotations
- Navigating the tree

### Developer Documentation

**D3: API Documentation**
- JavaDoc for all public APIs
- Architecture overview
- Contributing guide

## Success Criteria

### MVP Release Criteria

V3 MVP is ready when:
- ✅ All P1 features implemented
- ✅ All critical bugs fixed
- ✅ Performance targets met
- ✅ Documentation complete
- ✅ Beta testing successful
- ✅ <1% crash rate

### Success Metrics (Post-Launch)

**Adoption** (First 3 Months):
- 40% of active users create at least one repertoire
- 25% of users create 3+ repertoires
- 60% of repertoires have visual annotations

**Engagement**:
- Average repertoire size: 50+ moves
- Average annotations per repertoire: 20+
- 70% of users use both text and visual annotations

**Quality**:
- <2% bug reports per user
- >4.5 star rating
- <5% PGN compatibility issues

**Retention**:
- 60% of users who create a repertoire return within 7 days
- 40% of users update their repertoire at least once per month

## Conclusion

This specification defines all technical requirements for V3 MVP. Implementation should follow this specification to ensure:

1. **Complete Feature Set**: All P1 features included
2. **High Quality**: Performance, reliability, usability targets met
3. **Compatibility**: Standard PGN format, cross-platform
4. **Success**: Adoption and engagement metrics achieved

---

**Next Steps**:
1. Review and approve specification
2. Begin implementation (Phase 1: Foundation)
3. Weekly progress reviews
4. Beta testing at 80% completion
5. Public release

---

**Document Version**: 1.0  
**Status**: Draft - Awaiting Approval  
**Last Updated**: November 2024
