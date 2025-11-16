# V3 UI and Interaction Design

## Overview

This document specifies the user interface and interaction design for the V3 Repertoire Editor mode. The design focuses on intuitive editing, visual annotation, and seamless workflow.

## Design Principles

1. **Intuitive**: Natural interactions (drag, click, right-click)
2. **Responsive**: <100ms UI feedback on all actions
3. **Visual**: Clear visual hierarchy and feedback
4. **Efficient**: Keyboard shortcuts for power users
5. **Forgiving**: Undo/redo for all operations

## Layout Design

### Main Window Layout

```
┌────────────────────────────────────────────────────────────────┐
│ Orion Repertoire Editor                         [─] [□] [×]    │
├────────────────────────────────────────────────────────────────┤
│ File  Edit  View  Annotate  Help                               │
├────────────────────────────────────────────────────────────────┤
│ [New] [Open] [Save] │ [!] [!!] [?] [??] │ [🔴] [🟢] [🟡] [🔵]  │
│ [←] [→] [Undo] [Redo]                                          │
├────────────────────────────────────────────────────────────────┤
│ ┌──────────────┬───────────────────────────────────────────┐  │
│ │              │ Repertoire Tree                           │  │
│ │              │ ┌───────────────────────────────────────┐ │  │
│ │              │ │ 1. e4 ! {My main move}                │ │  │
│ │              │ │   1... e5 {Open game}                 │ │  │
│ │              │ │     2. Nf3 {King's Knight}            │ │  │
│ │              │ │       2... Nc6 {Main line}            │ │  │
│ │   Chess      │ │         3. Bb5 !! {Ruy Lopez}         │ │  │
│ │   Board      │ │       2... Nf6 {Petrov Defense}       │ │  │
│ │              │ │   1... c5 ? {Sicilian - study}        │ │  │
│ │   8 ♜♞♝♛♚♝♞♜  │ │     2. Nf3 {My response}              │ │  │
│ │   7 ♟♟♟♟♟♟♟♟  │ │                                       │ │  │
│ │   6 ▢▢▢▢▢▢▢▢  │ │ [Right-click for options]             │ │  │
│ │   5 ▢▢▢▢▢▢▢▢  │ └───────────────────────────────────────┘ │  │
│ │   4 ▢▢▢▢♙▢▢▢  ├───────────────────────────────────────────┤  │
│ │   3 ▢▢▢▢▢▢▢▢  │ Comment                                   │  │
│ │   2 ♙♙♙♙▢♙♙♙  │ ┌───────────────────────────────────────┐ │  │
│ │   1 ♖♘♗♕♔♗♘♖  │ │ This is the Ruy Lopez, my main weapon │ │  │
│ │     abcdefgh │ │ against 1...e5. I need to study the   │ │  │
│ │              │ │ Berlin Defense more carefully.        │ │  │
│ │ To Move: W   │ │                                       │ │  │
│ │              │ └───────────────────────────────────────┘ │  │
│ │              │ [Save Comment] [Clear]                    │  │
│ └──────────────┴───────────────────────────────────────────┘  │
├────────────────────────────────────────────────────────────────┤
│ ● Unsaved changes │ File: My_White_e4.pgn │ Moves: 15        │
└────────────────────────────────────────────────────────────────┘
```

### Component Breakdown

**1. Menu Bar**:
- File: New, Open, Save, Save As, Export, Exit
- Edit: Undo, Redo, Cut, Copy, Paste, Delete
- View: Zoom In, Zoom Out, Reset View
- Annotate: Add Comment, Add NAG, Add Highlight, Add Arrow
- Help: User Guide, Keyboard Shortcuts, About

**2. Toolbar**:
- File operations (New, Open, Save)
- Navigation (Back, Forward, Undo, Redo)
- NAG quick-add (!, !!, ?, ??, !?, ?!)
- Color selection (Red, Green, Yellow, Blue)

**3. Chessboard** (Left Panel):
- Interactive board (60% of left panel)
- Move input (click piece, click destination)
- Visual annotations (highlights, arrows)
- To-move indicator

**4. Repertoire Tree** (Top Right Panel):
- Hierarchical tree view (40% of right panel)
- Main line vs variations (visual distinction)
- NAG symbols inline
- Context menu (right-click)

**5. Comment Editor** (Bottom Right Panel):
- Text area (60% of right panel)
- Save/Clear buttons
- Character count

**6. Status Bar**:
- Dirty indicator (unsaved changes)
- Current file name
- Move count
- Position info

## Interaction Design

### Move Input

**Primary Method: Click-Click**:
1. Click on piece to select
2. Board highlights legal moves
3. Click on destination square
4. Move is executed and added to tree

**Alternative Method: Drag-Drop**:
1. Click and hold on piece
2. Drag to destination square
3. Release to execute move

**Feedback**:
- Selected piece highlighted (yellow border)
- Legal move squares highlighted (light green)
- Invalid move shows error message

### Visual Annotations

**Highlight Square**:
- **Method 1**: Alt+Click on square
- **Method 2**: Right-click → "Highlight Square" → Select color
- **Feedback**: Square highlighted with selected color
- **Remove**: Alt+Click again or right-click → "Remove Highlight"

**Draw Arrow**:
- **Method 1**: Ctrl+Drag from square to square
- **Method 2**: Right-click → "Draw Arrow" → Click from, click to, select color
- **Feedback**: Arrow drawn with selected color
- **Remove**: Ctrl+Click on arrow or right-click → "Remove Arrow"

**Color Selection**:
- Toolbar buttons for quick color selection
- Current color shown in toolbar (highlighted button)
- Default colors:
  - Red: Danger, blunders, opponent threats
  - Green: Good moves, my plans, targets
  - Yellow: Important squares, key positions
  - Blue: Choice moves, alternatives

### Tree Navigation

**Click on Move**:
- Board updates to position after that move
- Move highlighted in tree
- Comment editor shows move's comment

**Right-Click Menu**:
- **Delete Move**: Remove move and all descendants
- **Promote Variation**: Make this variation the main line
- **Add Variation**: Add alternative move from this position
- **Copy Move**: Copy move to clipboard
- **Paste Move**: Paste move as variation

**Keyboard Navigation**:
- **→**: Next move (main line)
- **←**: Previous move
- **↓**: Next variation
- **↑**: Previous variation
- **Home**: Go to start
- **End**: Go to end of main line

### Comment Editing

**Add Comment**:
1. Navigate to move in tree
2. Type in comment editor
3. Click "Save Comment" or press Ctrl+Enter

**Edit Comment**:
1. Navigate to move
2. Edit text in comment editor
3. Save changes

**Clear Comment**:
- Click "Clear" button
- Or select all text and delete

**Auto-save**:
- Comments auto-save on focus loss
- Or after 2 seconds of inactivity

### NAG Annotations

**Quick Add**:
- Click NAG button in toolbar
- NAG added to current move
- NAG shown inline in tree

**Remove NAG**:
- Right-click on move → "Remove NAG" → Select NAG
- Or click NAG button again to toggle

**Multiple NAGs**:
- Can add multiple NAGs to same move
- Shown in order: !, ?, !!, ??, !?, ?!

### File Operations

**New Repertoire**:
1. File → New Repertoire (Ctrl+N)
2. If unsaved changes, prompt to save
3. Create new empty tree
4. Set default headers

**Open Repertoire**:
1. File → Open Repertoire (Ctrl+O)
2. If unsaved changes, prompt to save
3. Show file picker
4. Load selected PGN file
5. Display in tree

**Save Repertoire**:
1. File → Save (Ctrl+S)
2. If no file path, prompt for location
3. Generate PGN with visual annotations
4. Write to file
5. Mark as clean (no unsaved changes)

**Save As**:
1. File → Save As (Ctrl+Shift+S)
2. Prompt for new file location
3. Save to new file
4. Update current file path

### Undo/Redo

**Undo** (Ctrl+Z):
- Undo last operation
- Supported operations:
  - Add move
  - Delete move
  - Promote variation
  - Add comment
  - Add NAG
  - Add visual annotation

**Redo** (Ctrl+Y):
- Redo last undone operation
- Same operations as undo

**Undo Stack**:
- Keep last 100 operations
- Clear on file load
- Preserve across save

## Visual Design

### Color Scheme

**Primary Colors**:
- Background: #FFFFFF (white)
- Text: #333333 (dark gray)
- Border: #CCCCCC (light gray)
- Accent: #4A90E2 (blue)

**Board Colors**:
- Light squares: #F0D9B5 (beige)
- Dark squares: #B58863 (brown)
- Highlight: #FFFF00 (yellow, 50% opacity)
- Legal moves: #90EE90 (light green, 30% opacity)

**Annotation Colors**:
- Red: #FF0000
- Green: #00FF00
- Yellow: #FFFF00
- Blue: #0000FF

**Tree Colors**:
- Main line: **Bold** black
- Variations: Regular gray
- Current move: Blue background
- NAG symbols: Colored (!, !!, ?, ??)

### Typography

**Font Family**:
- UI: "Segoe UI", "Helvetica Neue", Arial, sans-serif
- Chess pieces: "Chess Merida" or Unicode chess symbols
- Monospace: "Consolas", "Monaco", monospace

**Font Sizes**:
- Headers: 18px
- Body: 14px
- Tree: 13px
- Status bar: 12px
- Chess pieces: 48px

### Spacing

**Padding**:
- Window: 10px
- Panels: 15px
- Buttons: 8px 16px
- Text areas: 10px

**Margins**:
- Between panels: 10px
- Between buttons: 5px
- Between tree items: 3px

### Icons

**Toolbar Icons**:
- New: 📄 (document)
- Open: 📁 (folder)
- Save: 💾 (floppy disk)
- Undo: ↶ (curved arrow left)
- Redo: ↷ (curved arrow right)
- Back: ← (left arrow)
- Forward: → (right arrow)

**NAG Icons**:
- !: Green checkmark
- !!: Green star
- ?: Orange question mark
- ??: Red X
- !?: Blue exclamation-question
- ?!: Orange question-exclamation

**Color Icons**:
- Red: 🔴 (red circle)
- Green: 🟢 (green circle)
- Yellow: 🟡 (yellow circle)
- Blue: 🔵 (blue circle)

## Responsive Design

### Window Sizes

**Minimum Size**: 1024x768
**Recommended Size**: 1280x800
**Maximum Size**: Unlimited

### Panel Resizing

**Splitter**:
- Vertical splitter between board and tree
- Horizontal splitter between tree and comment
- Drag to resize
- Double-click to reset to default

**Default Proportions**:
- Board: 50% width
- Tree: 50% width, 40% height
- Comment: 50% width, 60% height

### Zoom

**Board Zoom**:
- View → Zoom In (Ctrl++)
- View → Zoom Out (Ctrl+-)
- View → Reset Zoom (Ctrl+0)

**Tree Zoom**:
- View → Increase Font Size
- View → Decrease Font Size
- View → Reset Font Size

## Keyboard Shortcuts

### File Operations

| Shortcut | Action |
|----------|--------|
| Ctrl+N | New Repertoire |
| Ctrl+O | Open Repertoire |
| Ctrl+S | Save |
| Ctrl+Shift+S | Save As |
| Ctrl+W | Close |
| Ctrl+Q | Quit |

### Editing

| Shortcut | Action |
|----------|--------|
| Ctrl+Z | Undo |
| Ctrl+Y | Redo |
| Ctrl+X | Cut |
| Ctrl+C | Copy |
| Ctrl+V | Paste |
| Delete | Delete Move |

### Navigation

| Shortcut | Action |
|----------|--------|
| → | Next Move |
| ← | Previous Move |
| ↓ | Next Variation |
| ↑ | Previous Variation |
| Home | Go to Start |
| End | Go to End |

### Annotations

| Shortcut | Action |
|----------|--------|
| Ctrl+1 | Add ! (Good) |
| Ctrl+2 | Add ? (Mistake) |
| Ctrl+3 | Add !! (Brilliant) |
| Ctrl+4 | Add ?? (Blunder) |
| Ctrl+5 | Add !? (Interesting) |
| Ctrl+6 | Add ?! (Dubious) |
| Alt+Click | Toggle Highlight |
| Ctrl+Drag | Draw Arrow |
| Ctrl+Enter | Save Comment |

### View

| Shortcut | Action |
|----------|--------|
| Ctrl++ | Zoom In |
| Ctrl+- | Zoom Out |
| Ctrl+0 | Reset Zoom |
| F11 | Fullscreen |

## Accessibility

### Screen Reader Support

**Labels**:
- All buttons have aria-labels
- All input fields have labels
- All images have alt text

**Navigation**:
- Tab order follows logical flow
- Focus visible on all interactive elements
- Keyboard accessible (no mouse required)

### Color Blindness

**Patterns**:
- Highlights use patterns in addition to colors
- Red: Diagonal stripes
- Green: Dots
- Yellow: Horizontal stripes
- Blue: Vertical stripes

**Contrast**:
- All text meets WCAG AA standards (4.5:1 contrast ratio)
- Important elements meet AAA standards (7:1)

### Font Scaling

**Zoom**:
- Support system font scaling
- Support application font scaling
- Minimum font size: 12px
- Maximum font size: 24px

## Error Handling

### User Errors

**Illegal Move**:
- Show error message: "Illegal move: Nf7"
- Highlight piece in red
- Play error sound (optional)
- Revert to previous position

**Invalid File**:
- Show error dialog: "Invalid PGN file"
- Show parse error details
- Offer to open in text editor
- Offer to fix common issues

**Save Error**:
- Show error dialog: "Failed to save file"
- Show error details (permissions, disk space, etc.)
- Offer to save to alternative location
- Preserve unsaved changes

### System Errors

**Out of Memory**:
- Show warning: "Low memory"
- Offer to close other applications
- Offer to save and restart

**Crash Recovery**:
- Auto-save every 5 minutes
- On restart, offer to recover unsaved changes
- Show recovery dialog with file list

## Performance Optimization

### Rendering

**Virtual Scrolling**:
- Only render visible tree items
- Lazy load off-screen items
- Recycle tree item views

**Canvas Rendering**:
- Use JavaFX Canvas for board
- Batch draw operations
- Cache piece images

**Animation**:
- Smooth move animation (200ms)
- Fade in/out for highlights
- Slide in/out for arrows

### Memory

**Lazy Loading**:
- Load tree nodes on demand
- Unload off-screen nodes
- Keep only current path in memory

**Image Caching**:
- Cache piece images
- Cache board textures
- Clear cache on low memory

## Testing

### Manual Testing

**Usability Testing**:
- Test with real users
- Observe workflow
- Collect feedback
- Iterate on design

**Accessibility Testing**:
- Test with screen reader
- Test with keyboard only
- Test with color blindness simulator
- Test with different font sizes

### Automated Testing

**UI Tests**:
- Test all interactions
- Test keyboard shortcuts
- Test error handling
- Test undo/redo

**Visual Tests**:
- Screenshot comparison
- Pixel-perfect rendering
- Cross-platform consistency

## Conclusion

The V3 UI is designed to be:

1. **Intuitive**: Natural interactions, clear feedback
2. **Efficient**: Keyboard shortcuts, quick access
3. **Visual**: Clear hierarchy, visual annotations
4. **Accessible**: Screen reader, keyboard, color blind
5. **Performant**: Smooth animations, responsive

The design supports the core V3 workflow:
1. Create/open repertoire
2. Add moves and variations
3. Annotate with text and visuals
4. Navigate and review
5. Save and share

---

**Next Steps**:
1. Create mockups
2. Build prototype
3. User testing
4. Iterate on feedback
5. Final implementation

---

**Document Version**: 1.0  
**Status**: Draft  
**Last Updated**: November 2024
