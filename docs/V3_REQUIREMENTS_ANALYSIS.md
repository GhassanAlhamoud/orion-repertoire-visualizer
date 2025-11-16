# V3 Requirements Analysis

## Document Information

**Version**: 3.0.0  
**Status**: Planning Phase  
**Last Updated**: November 2024  
**Purpose**: Analyze V3 requirements and define product transformation  

## Executive Summary

Version 3.0 represents a **fundamental product transformation** from an analytics tool (V1/V2) to a **repertoire curation and editing tool**. This is not an incremental update but a new product mode that enables users to create, edit, and visually annotate their ideal opening repertoires.

### Product Evolution

```
V1: Personal Analytics
    "What did I play?"
    ↓
V2: Historical Analytics + Comparison
    "What did masters play? How do I compare?"
    ↓
V3: Repertoire Curation + Visual Editing
    "What should I play? How do I prepare?"
```

## Product Vision

### V3 Goal

> **Provide competitive players with a single, integrated tool to create, edit, and visually annotate their ideal opening repertoire.**

### Value Proposition

**Stop using separate tools** for game analysis and repertoire building. Analyze your game history (V1/V2) and build your "perfect" repertoire (V3) in the same application, using the same powerful visualization engine.

### Success Metric (KPI)

**Repertoire Creation**: Number of users who create and save a new Repertoire PGN file with at least 10 moves and 1 annotation (a note, NAG, or visual arrow).

**Target**: 40% of active users create at least one repertoire within 30 days.

## Target Audience

### User Persona: "The Student" Competitive Player

**Profile**:
- Rating: 1600-2200
- Goal: Improve opening preparation
- Current Workflow: Analyze games in one tool, build repertoire in another
- Pain Point: Context switching, duplicate work, disconnected tools

**User Journey**:

1. **Analysis Phase** (V1/V2):
   - Import games
   - Identify weaknesses
   - Study historical players
   - Compare to masters

2. **Curation Phase** (V3):
   - Create new repertoire
   - Add main lines from analysis
   - Add variations for opponent responses
   - Annotate with notes and symbols
   - Mark blunders to avoid
   - Draw arrows for plans

3. **Study Phase** (V3):
   - Review repertoire
   - Practice variations
   - Update based on new games
   - Share with coach/teammates

### User Quote

> "I've analyzed my games in V1 and see my problems. Now I need to build my new repertoire. I want to save my main lines, add notes for my 'Why,' mark blunders to avoid, and draw arrows for my plans, just like in ChessMaster."

## Product Positioning

### V3 vs Competitors

**vs ChessBase**:
- ✅ Integrated analytics + curation
- ✅ Modern visual annotations
- ✅ More affordable
- ❌ Smaller database (initially)

**vs Chessable**:
- ✅ Personal repertoire creation
- ✅ Visual annotations
- ✅ Offline capability
- ❌ No spaced repetition (yet)

**vs ChessMaster**:
- ✅ Modern UI
- ✅ Integrated analytics
- ✅ Active development
- ❌ Smaller feature set (initially)

**vs Lichess Study**:
- ✅ Offline capability
- ✅ Integrated with personal games
- ✅ More powerful visualizations
- ❌ No online collaboration (yet)

### Unique Selling Points

1. **Seamless Workflow**: Analyze → Curate → Study in one tool
2. **Visual Annotations**: ChessMaster-style arrows and highlights
3. **Analytics Integration**: Build repertoire from your actual games
4. **Standard Format**: Uses standard PGN with visual extensions
5. **Offline First**: No internet required, full privacy

## Requirements Analysis

### Epic 5: Repertoire Management & Core Annotations (P1)

**Goal**: Foundational ability to create, edit, and save a text-based repertoire PGN.

#### User Story 5.1: Repertoire File Management

**As a** Student Player  
**I want to** create a "New Repertoire," open an existing one, and save my changes  
**So that** I can manage my repertoire PGN files (e.g., "My_White_e4.pgn", "My_Black_Sicilian.pgn")

**Acceptance Criteria**:
1. UI has "File → New Repertoire", "File → Open Repertoire", "File → Save/Save As"
2. "New" creates a new, empty PGN tree
3. "Open" uses native file picker to select .pgn file and loads it
4. "Save" overwrites existing file
5. "Save As" prompts for new .pgn file name

**Technical Requirements**:
- File dialog integration (JavaFX FileChooser)
- PGN tree data structure (new)
- File I/O operations
- Dirty state tracking (unsaved changes)
- Auto-save capability (optional)

**Complexity**: Medium  
**Estimated Effort**: 1 week  

#### User Story 5.2: Repertoire Tree Editing

**As a** Student Player  
**I want to** make moves on the board to add them to my tree  
**So that** I can build my desired opening variations

**Acceptance Criteria**:
1. Making a legal move on the board adds it to the PGN tree
2. Right-clicking a move allows "Delete" (and all sub-variations)
3. Right-clicking a move allows "Promote Variation" to main line
4. UI clearly distinguishes main line vs variations

**Technical Requirements**:
- Interactive board with move input
- PGN tree manipulation (add, delete, promote)
- Tree view with visual distinction (main line bold/colored)
- Context menu for tree operations
- Undo/redo support

**Complexity**: High  
**Estimated Effort**: 2 weeks  

#### User Story 5.3: Text & Symbol (NAG) Annotations

**As a** Student Player  
**I want to** add text notes and standard chess symbols (like !, ??) to any move  
**So that** I can remember my analysis

**Acceptance Criteria**:
1. Text box available to add/edit comment for current move
2. Comment saved to PGN as `{This is the key line.}`
3. UI (dropdown or right-click) allows adding common NAGs:
   - `!` (Good) = $1
   - `!!` (Brilliant) = $3
   - `?` (Mistake) = $2
   - `??` (Blunder) = $4
   - `!?` (Interesting) = $5
   - `?!` (Dubious) = $6
4. NAGs saved as standard codes in PGN

**Technical Requirements**:
- Text input component for comments
- NAG selector UI (dropdown or toolbar)
- PGN comment generation
- Display NAGs in tree view

**Complexity**: Low  
**Estimated Effort**: 3 days  

### Epic 6: Visual (ChessMaster) Annotations (P1)

**Goal**: Modern features for visual, interactive annotation.

#### User Story 6.1: Highlight Squares

**As a** Student Player  
**I want to** highlight specific squares on the board (e.g., red, green, yellow)  
**So that** I can visually mark key squares (like a weak pawn or an outpost)

**Acceptance Criteria**:
1. User can (Alt+Click or Right-Click-Drag) on square to highlight it
2. Support multiple colors: Red (danger), Green (target), Yellow (important), Blue (choice)
3. Visual information saved with specific move
4. Saved PGN contains `[%csl Rd4,Ge5]` command in move's comment

**Technical Requirements**:
- Square highlighting rendering
- Mouse/keyboard input handling
- Color picker or preset colors
- PGN `[%csl]` command generation/parsing
- Multiple highlights per position

**Complexity**: Medium  
**Estimated Effort**: 1 week  

#### User Story 6.2: Draw Arrows

**As a** Student Player  
**I want to** draw colored arrows on the board  
**So that** I can visualize plans, threats, and piece maneuvers

**Acceptance Criteria**:
1. User can (Ctrl+Drag) to draw arrow from one square to another
2. Support at least two colors: Green (my plan), Red (opponent's threat)
3. Visual information saved with specific move
4. Saved PGN contains `[%cal Gd2d4,Re7e5]` command in move's comment

**Technical Requirements**:
- Arrow rendering (curved or straight)
- Drag gesture detection
- Color selection
- PGN `[%cal]` command generation/parsing
- Multiple arrows per position

**Complexity**: Medium  
**Estimated Effort**: 1 week  

#### User Story 6.3: Draw Dots (Choice Markers)

**As a** Student Player  
**I want to** add "dots" to my choice-moves  
**So that** I can see my preferred options at a glance

**Product Clarification**: This is functionally identical to User Story 6.1 (Highlight Squares). A "dot" is just a visual marker. Implement by allowing user to put a Blue Dot/Square on the origin square of their preferred move.

**Technical Requirements**:
- Reuse square highlighting system
- Special "dot" rendering style (optional)
- Same PGN `[%csl]` command

**Complexity**: Low (reuses 6.1)  
**Estimated Effort**: 2 days  

## Technical Architecture Overview

### Mode Architecture

V3 introduces a **new mode** alongside V1/V2:

```
┌─────────────────────────────────────────────────────────────┐
│                    Orion Chess Application                   │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   V1 Mode    │  │   V2 Mode    │  │   V3 Mode    │     │
│  │              │  │              │  │              │     │
│  │  Personal    │  │ Historical   │  │ Repertoire   │     │
│  │  Analytics   │  │  Analytics   │  │   Editor     │     │
│  │              │  │              │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│         │                 │                 │              │
│         └─────────────────┴─────────────────┘              │
│                           │                                │
│                  ┌────────▼────────┐                       │
│                  │  Shared Engine  │                       │
│                  │  & Services     │                       │
│                  └─────────────────┘                       │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow Comparison

**V1/V2 (Read-Only Analytics)**:
```
PGN Files → Import → Database → Analysis → Visualization
```

**V3 (Read-Write Curation)**:
```
Empty Tree → User Edits → Annotations → Save → PGN File
     ↑                                            │
     └────────────── Open ──────────────────────┘
```

### Storage Model

**V1/V2**: `.oriondb` (binary, optimized for reading)  
**V3**: `.pgn` (text, standard format, human-readable)

**Rationale**: Repertoire files need to be:
- **Portable**: Share with coaches, teammates
- **Standard**: Compatible with other tools
- **Editable**: Can be manually edited if needed
- **Version Control**: Can be tracked in Git

### Core Engine Usage

**oriondb-chess-engine** is still used for:
- Move generation (legal moves)
- Move validation (is move legal?)
- Board state management (FEN, position)
- Notation conversion (SAN, UCI)

**NOT used for**:
- PGN parsing (need custom parser for visual annotations)
- Tree structure (need custom tree for editing)
- File I/O (need custom for repertoire files)

## PGN Format Requirements

### Standard PGN Elements (Already Supported)

```pgn
[Event "My Repertoire"]
[Site "?"]
[Date "2024.11.17"]
[White "?"]
[Black "?"]
[Result "*"]

1. e4 {This is my main move.} e5 (1... c5 {Sicilian variation}) 
2. Nf3 $1 {Good move!} Nc6 3. Bb5 *
```

**Elements**:
- Headers: `[Event "..."]`
- Moves: `1. e4`
- Variations: `(1... c5)`
- Comments: `{text}`
- NAGs: `$1`, `$2`, etc.
- Result: `*` (ongoing), `1-0`, `0-1`, `1/2-1/2`

### Visual Annotation Extensions (NEW)

**Square Highlighting** (`[%csl]`):
```pgn
1. e4 {[%csl Rd4,Ge5,Yd5] This highlights d4 red, e5 green, d5 yellow.}
```

**Arrow Drawing** (`[%cal]`):
```pgn
1. e4 {[%cal Gd2d4,Re7e5] Green arrow d2-d4, red arrow e7-e5.}
```

**Combined**:
```pgn
1. e4 {[%csl Rd4] [%cal Gd2d4] Red square on d4, green arrow d2-d4.}
```

**Format Specification**:
- `[%csl]`: Colored Square List
- `[%cal]`: Colored Arrow List
- Colors: `R` (red), `G` (green), `Y` (yellow), `B` (blue)
- Squares: `a1` to `h8`
- Arrows: `a1b2` (from a1 to b2)

### PGN Parser Requirements

**Must Parse**:
- ✅ Headers
- ✅ Moves (SAN notation)
- ✅ Variations (nested)
- ✅ Comments (text)
- ✅ NAGs (numeric codes)
- ✅ `[%csl]` commands (NEW)
- ✅ `[%cal]` commands (NEW)

**Must Generate**:
- ✅ Valid PGN format
- ✅ Proper move numbering
- ✅ Nested variations
- ✅ Escaped special characters
- ✅ Visual annotation commands

**Parser Complexity**: High  
**Estimated Effort**: 2-3 weeks  

## User Interface Requirements

### Mode Selector

**Location**: Top of window or menu  
**Options**:
- ⚫ Analytics (V1/V2)
- ⚪ Repertoire Editor (V3)

**Behavior**:
- Switching modes saves current state
- Confirm if unsaved changes in V3
- Different toolbar/menu for each mode

### Repertoire Editor Layout

```
┌──────────────────────────────────────────────────────────────┐
│ Orion Repertoire Editor                       [─] [□] [×]    │
├──────────────────────────────────────────────────────────────┤
│ File  Edit  View  Annotate  Help                             │
├──────────────────────────────────────────────────────────────┤
│ [New] [Open] [Save] │ [!] [!!] [?] [??] │ [🔴] [🟢] [🟡] [🔵] │
├──────────────────────────────────────────────────────────────┤
│ ┌────────────┬─────────────────────────────────────────────┐ │
│ │            │ Repertoire Tree                             │ │
│ │            ├─────────────────────────────────────────────┤ │
│ │            │ 1. e4 {My main move} !                      │ │
│ │            │   1... e5 {Open game}                       │ │
│ │            │     2. Nf3 {King's Knight}                  │ │
│ │            │       2... Nc6 {Main line}                  │ │
│ │  Chess     │         3. Bb5 {Ruy Lopez} !!               │ │
│ │  Board     │       2... Nf6 {Petrov Defense}             │ │
│ │            │   1... c5 {Sicilian - study more} ?         │ │
│ │  8 ♜♞♝♛♚♝♞♜ │     2. Nf3 {My response}                    │ │
│ │  7 ♟♟♟♟♟♟♟♟ │                                             │ │
│ │  6 ▢▢▢▢▢▢▢▢ │ [Add Variation] [Delete] [Promote]         │ │
│ │  5 ▢▢▢▢▢▢▢▢ ├─────────────────────────────────────────────┤ │
│ │  4 ▢▢▢▢♙▢▢▢ │ Comment for current move:                  │ │
│ │  3 ▢▢▢▢▢▢▢▢ │ ┌─────────────────────────────────────────┐ │ │
│ │  2 ♙♙♙♙▢♙♙♙ │ │ This is the Ruy Lopez, my main weapon.  │ │ │
│ │  1 ♖♘♗♕♔♗♘♖ │ │ I need to study the Berlin Defense.     │ │ │
│ │    abcdefgh │ │                                         │ │ │
│ │            │ └─────────────────────────────────────────┘ │ │
│ │ To Move: W │                                             │ │
│ │            │ [Add Comment] [Clear]                       │ │
│ └────────────┴─────────────────────────────────────────────┘ │
├──────────────────────────────────────────────────────────────┤
│ Status: Unsaved changes │ File: My_White_e4.pgn              │
└──────────────────────────────────────────────────────────────┘
```

### Key UI Components

**1. Chessboard (Left)**:
- Interactive (click to make moves)
- Visual annotations (highlights, arrows)
- Right-click menu for annotations
- Keyboard shortcuts (Alt+Click, Ctrl+Drag)

**2. Repertoire Tree (Top Right)**:
- Hierarchical tree view
- Main line vs variations (visual distinction)
- NAG symbols displayed inline
- Context menu (delete, promote, add variation)
- Current move highlighted

**3. Comment Editor (Bottom Right)**:
- Text area for move comments
- Add/Edit/Clear buttons
- Auto-save on focus loss

**4. Toolbar**:
- File operations (New, Open, Save)
- NAG quick-add buttons
- Color selection for visual annotations
- Undo/Redo

**5. Status Bar**:
- File name
- Saved/Unsaved indicator
- Move count
- Current position info

## Feature Prioritization

### P1 (MVP - Must Have)

**Epic 5: Repertoire Management**:
- ✅ File management (New, Open, Save)
- ✅ Tree editing (Add, Delete moves)
- ✅ Text comments
- ✅ NAG annotations

**Epic 6: Visual Annotations**:
- ✅ Square highlighting
- ✅ Arrow drawing

**Estimated MVP Effort**: 8-10 weeks

### P2 (Post-MVP - Should Have)

- Variation promotion
- Undo/Redo
- Auto-save
- Export to other formats
- Import from V1/V2 analysis

**Estimated P2 Effort**: 4-6 weeks

### P3 (Future - Nice to Have)

- Multiple repertoire comparison
- Repertoire testing (practice mode)
- Spaced repetition
- Sharing/collaboration
- Cloud sync

**Estimated P3 Effort**: 8-12 weeks

## Success Criteria

### MVP Release Criteria

V3 MVP is ready when:
- ✅ Can create new repertoire
- ✅ Can open existing PGN
- ✅ Can add moves and variations
- ✅ Can add text comments and NAGs
- ✅ Can add visual annotations (highlights, arrows)
- ✅ Can save to standard PGN format
- ✅ PGN is compatible with other tools (Lichess, ChessBase)
- ✅ No data loss or corruption
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

## Risk Assessment

### High-Priority Risks

**R1: PGN Parser Complexity**
- **Impact**: Critical (core feature)
- **Probability**: High
- **Mitigation**: Use existing PGN library (e.g., chess.js, python-chess) as reference
- **Contingency**: Simplify visual annotation format if needed

**R2: Visual Annotation Performance**
- **Impact**: High (user experience)
- **Probability**: Medium
- **Mitigation**: Optimize rendering, use canvas/WebGL
- **Contingency**: Limit number of annotations per position

**R3: Data Loss/Corruption**
- **Impact**: Critical (user trust)
- **Probability**: Medium
- **Mitigation**: Extensive testing, auto-backup, validation
- **Contingency**: Recovery tools, support for manual PGN editing

### Medium-Priority Risks

**R4: Mode Switching Complexity**
- **Impact**: Medium (development time)
- **Probability**: Medium
- **Mitigation**: Clean separation of concerns, shared services
- **Contingency**: Separate applications if needed

**R5: PGN Compatibility**
- **Impact**: Medium (interoperability)
- **Probability**: Low
- **Mitigation**: Follow PGN standard strictly, test with other tools
- **Contingency**: Export to simplified PGN format

**R6: User Adoption**
- **Impact**: High (business)
- **Probability**: Low
- **Mitigation**: Beta testing, tutorials, seamless V1/V2 integration
- **Contingency**: Marketing campaign, free trial period

## Competitive Analysis

### Feature Comparison Matrix

| Feature | Orion V3 | ChessBase | Chessable | Lichess Study | ChessMaster |
|---------|----------|-----------|-----------|---------------|-------------|
| Repertoire Creation | ✅ | ✅ | ✅ | ✅ | ✅ |
| Visual Annotations | ✅ | ✅ | ❌ | ✅ | ✅ |
| Integrated Analytics | ✅ | ✅ | ❌ | ❌ | ❌ |
| Standard PGN | ✅ | ✅ | ❌ | Partial | ✅ |
| Offline | ✅ | ✅ | ❌ | ❌ | ✅ |
| Free | ✅ | ❌ | Partial | ✅ | ❌ |
| Modern UI | ✅ | ❌ | ✅ | ✅ | ❌ |
| Cross-Platform | ✅ | Windows | Web | Web | Windows |

### Competitive Advantages

1. **Integrated Workflow**: Only tool that combines analytics (V1/V2) with curation (V3)
2. **Modern Visual Annotations**: ChessMaster-style in a modern application
3. **Standard Format**: Uses standard PGN, not proprietary format
4. **Offline First**: No internet required, full privacy
5. **Free**: Core features free, no subscription required

### Competitive Disadvantages

1. **Smaller Database**: ChessBase has larger historical database
2. **No Spaced Repetition**: Chessable has better training features
3. **No Collaboration**: Lichess has better sharing features
4. **New Product**: Less mature than established tools

## Conclusion

V3 represents a **strategic product pivot** from analytics to curation. This is a high-risk, high-reward initiative that:

**Opportunities**:
- Differentiate from competitors
- Create sticky user engagement (repertoire creation)
- Enable new revenue streams (premium repertoires, coaching)
- Build community (repertoire sharing)

**Challenges**:
- Complex PGN parser implementation
- Visual annotation performance
- Mode switching complexity
- User adoption (new workflow)

**Recommendation**: **Proceed with MVP** focusing on core features (Epic 5 + Epic 6). Launch as beta to gather feedback before full release.

**Timeline**: 8-10 weeks for MVP, 3-4 months for full V3.0 release.

---

**Next Steps**:
1. Review and approve V3 requirements
2. Design detailed architecture
3. Create technical specification
4. Develop PGN parser prototype
5. Begin MVP implementation

---

**Document Version**: 1.0  
**Status**: Draft - Awaiting Approval  
**Last Updated**: November 2024
