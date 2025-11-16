# V2 Architecture Design

## Overview

V2 architecture extends V1 with historical player support, advanced visualizations, and improved data management. The design maintains backward compatibility while adding new capabilities.

## Architectural Principles

1. **Separation of Concerns**: Personal vs Historical data sources
2. **Performance First**: Pre-processing for instant loading
3. **Extensibility**: Plugin architecture for new visualizations
4. **Backward Compatibility**: V1 databases continue to work
5. **Progressive Enhancement**: Features degrade gracefully

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Main UI    │  │  Timeline    │  │  Comparison  │      │
│  │  Controller  │  │  Visualizer  │  │    View      │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Analysis   │  │  Historical  │  │  Comparison  │      │
│  │   Service    │  │   Service    │  │   Service    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Timeline   │  │   Opening    │  │     Gap      │      │
│  │   Service    │  │   Service    │  │   Detector   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                       Data Access Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Personal   │  │  Historical  │  │   Opening    │      │
│  │   Database   │  │   Database   │  │   Database   │      │
│  │   Service    │  │   Service    │  │   Service    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                        Storage Layer                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   OrionDB    │  │  Pre-processed│  │   Opening    │      │
│  │   Files      │  │  Player Data  │  │  ECO Data    │      │
│  │  (.oriondb)  │  │   (.orpd)     │  │   (.json)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

## New Components in V2

### 1. Historical Database Service

**Purpose**: Manage pre-processed historical player data

**Responsibilities**:
- Load pre-processed player files (.orpd format)
- Index player metadata for fast search
- Cache frequently accessed players
- Provide unified interface with personal databases

**API**:
```java
public interface HistoricalDatabaseService {
    // Search for players
    List<PlayerMetadata> searchPlayers(String query);
    
    // Load pre-processed player data
    PlayerRepertoire loadPlayer(String playerId);
    
    // Get player metadata
    PlayerMetadata getPlayerInfo(String playerId);
    
    // List available players
    List<PlayerMetadata> listAllPlayers();
    
    // Check if player data exists
    boolean hasPlayer(String playerId);
}
```

**Data Structure**:
```java
public class PlayerMetadata {
    private String id;              // "kasparov_garry"
    private String fullName;        // "Kasparov, Garry"
    private String photoUrl;        // URL to player photo
    private int peakRating;         // 2851
    private String nationality;     // "RUS"
    private LocalDate birthDate;    // 1963-04-13
    private String era;             // "1980-2005"
    private int totalGames;         // 2500+
    private List<String> titles;    // ["World Champion 1985-2000"]
}

public class PlayerRepertoire {
    private PlayerMetadata metadata;
    private OpeningTreeNode whiteTree;
    private OpeningTreeNode blackTree;
    private Map<String, TimelineData> timelines;
    private Map<String, Statistics> statistics;
}
```

### 2. Timeline Service

**Purpose**: Generate timeline visualizations and trend data

**Responsibilities**:
- Aggregate games by time period
- Calculate trends (improving/declining)
- Generate chart data
- Detect pattern changes

**API**:
```java
public interface TimelineService {
    // Generate opening usage timeline
    TimelineData getOpeningUsageTimeline(
        OpeningTreeNode node, 
        TimeGranularity granularity
    );
    
    // Generate win rate timeline
    TimelineData getWinRateTimeline(
        OpeningTreeNode node,
        TimeGranularity granularity
    );
    
    // Detect trends
    Trend analyzeTrend(TimelineData data);
    
    // Compare timelines
    ComparisonData compareTimelines(
        TimelineData timeline1,
        TimelineData timeline2
    );
}

public enum TimeGranularity {
    MONTH, QUARTER, YEAR, DECADE
}

public class TimelineData {
    private List<TimePoint> dataPoints;
    private Trend trend;
    private Statistics summary;
}

public class TimePoint {
    private LocalDate date;
    private int gameCount;
    private double winRate;
    private double drawRate;
    private double lossRate;
}

public enum Trend {
    IMPROVING,      // Win rate increasing
    STABLE,         // No significant change
    DECLINING,      // Win rate decreasing
    ABANDONED,      // No longer played
    NEWLY_ADOPTED   // Recently started
}
```

### 3. Comparison Service

**Purpose**: Enable side-by-side comparison of repertoires

**Responsibilities**:
- Compare two opening trees
- Highlight differences
- Calculate comparative statistics
- Generate comparison reports

**API**:
```java
public interface ComparisonService {
    // Compare two repertoires
    ComparisonResult compare(
        OpeningTreeNode tree1,
        OpeningTreeNode tree2,
        ComparisonMode mode
    );
    
    // Find unique moves
    List<OpeningTreeNode> findUniqueMoves(
        OpeningTreeNode tree1,
        OpeningTreeNode tree2
    );
    
    // Calculate differences
    Map<String, Difference> calculateDifferences(
        OpeningTreeNode tree1,
        OpeningTreeNode tree2
    );
}

public enum ComparisonMode {
    TIME_PERIOD,    // Same player, different periods
    PLAYER,         // Different players
    SIDE            // Same player, White vs Black
}

public class ComparisonResult {
    private OpeningTreeNode tree1;
    private OpeningTreeNode tree2;
    private List<Difference> differences;
    private Statistics comparative;
}

public class Difference {
    private String move;
    private DifferenceType type;
    private double delta;           // Change in win rate
    private int gameCountDelta;     // Change in usage
}

public enum DifferenceType {
    NEW_MOVE,           // Only in tree2
    ABANDONED_MOVE,     // Only in tree1
    INCREASED_USAGE,    // More frequent in tree2
    DECREASED_USAGE,    // Less frequent in tree2
    IMPROVED_RESULTS,   // Better win rate in tree2
    WORSE_RESULTS       // Worse win rate in tree2
}
```

### 4. Opening Service

**Purpose**: Provide opening names and ECO codes

**Responsibilities**:
- Detect opening names from positions
- Provide ECO codes
- Link to theory resources
- Maintain opening database

**API**:
```java
public interface OpeningService {
    // Get opening name for position
    OpeningInfo getOpeningInfo(String fen);
    
    // Get opening name from move sequence
    OpeningInfo getOpeningInfo(List<String> moves);
    
    // Search openings by name
    List<OpeningInfo> searchOpenings(String query);
    
    // Get opening variations
    List<OpeningVariation> getVariations(String ecoCode);
}

public class OpeningInfo {
    private String name;            // "Sicilian Defense"
    private String variation;       // "Najdorf Variation"
    private String ecoCode;         // "B90"
    private List<String> moves;     // ["e4", "c5", "Nf3", "d6", ...]
    private String description;     // Theory description
    private List<String> resources; // Links to study materials
}
```

### 5. Gap Detector Service

**Purpose**: Identify preparation gaps and weaknesses

**Responsibilities**:
- Analyze preparation depth
- Detect under-prepared lines
- Identify losing variations
- Recommend study positions

**API**:
```java
public interface GapDetectorService {
    // Analyze preparation depth
    DepthAnalysis analyzeDepth(OpeningTreeNode tree);
    
    // Find preparation gaps
    List<PreparationGap> findGaps(OpeningTreeNode tree);
    
    // Find weak lines
    List<WeakLine> findWeakLines(
        OpeningTreeNode tree,
        double threshold
    );
    
    // Generate study recommendations
    List<StudyRecommendation> generateRecommendations(
        OpeningTreeNode tree
    );
}

public class DepthAnalysis {
    private Map<Integer, Integer> depthDistribution;
    private int averageDepth;
    private int maxDepth;
    private List<ShallowLine> shallowLines;
}

public class PreparationGap {
    private List<String> moves;
    private int currentDepth;
    private int recommendedDepth;
    private GapSeverity severity;
    private String reason;
}

public enum GapSeverity {
    CRITICAL,   // <5 moves deep, frequently reached
    HIGH,       // <8 moves deep, occasionally reached
    MEDIUM,     // <12 moves deep, rarely reached
    LOW         // >12 moves deep
}

public class WeakLine {
    private List<String> moves;
    private double winRate;
    private int gameCount;
    private List<GameReference> losses;
    private String recommendation;
}

public class StudyRecommendation {
    private List<String> position;
    private String reason;
    private Priority priority;
    private List<String> resources;
}
```

## Data Models (Extended)

### Enhanced OpeningTreeNode

```java
public class OpeningTreeNode {
    // V1 fields (unchanged)
    private String move;
    private int wins;
    private int draws;
    private int losses;
    private List<GameReference> games;
    private Map<String, OpeningTreeNode> children;
    
    // V2 additions
    private OpeningInfo openingInfo;        // Opening name/ECO
    private TimelineData timeline;          // Usage over time
    private Trend trend;                    // Performance trend
    private int depth;                      // Depth in tree
    private LocalDate firstPlayed;          // When adopted
    private LocalDate lastPlayed;           // When last used
    private double averageOpponentRating;   // Opponent strength
    private Map<String, Object> metadata;   // Extensible metadata
}
```

### Pre-processed Player Data Format (.orpd)

```
File Format: Orion Repertoire Player Data (ORPD)
Extension: .orpd
Compression: GZIP
Structure: Binary with JSON metadata

Header (256 bytes):
- Magic number: "ORPD" (4 bytes)
- Version: 2 (4 bytes)
- Player ID hash (32 bytes)
- Metadata offset (8 bytes)
- White tree offset (8 bytes)
- Black tree offset (8 bytes)
- Timeline data offset (8 bytes)
- Reserved (184 bytes)

Metadata Section (JSON):
{
  "player": {
    "id": "kasparov_garry",
    "fullName": "Kasparov, Garry",
    "peakRating": 2851,
    "nationality": "RUS",
    "birthDate": "1963-04-13",
    "era": "1980-2005",
    "totalGames": 2500,
    "titles": ["World Champion 1985-2000"]
  },
  "statistics": {
    "whiteGames": 1300,
    "blackGames": 1200,
    "whiteWinRate": 0.58,
    "blackWinRate": 0.52,
    "mostPlayedWhite": "e4",
    "mostPlayedBlack": "Sicilian"
  },
  "generated": "2024-11-17T00:00:00Z",
  "sourceGames": 2500
}

Tree Section (Binary):
- Serialized OpeningTreeNode structures
- Optimized for fast deserialization
- Includes all statistics pre-calculated

Timeline Section (Binary):
- Pre-calculated timeline data
- Monthly granularity
- Win rates, usage counts
```

### Opening Database Format

```json
{
  "openings": [
    {
      "eco": "B90",
      "name": "Sicilian Defense",
      "variation": "Najdorf Variation",
      "moves": ["e4", "c5", "Nf3", "d6", "d4", "cxd4", "Nxd4", "Nf6", "Nc3", "a6"],
      "fen": "rnbqkb1r/1p2pppp/p2p1n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq -",
      "popularity": 0.85,
      "description": "One of the most popular and aggressive responses to 1.e4",
      "resources": [
        "https://www.chessgames.com/perl/chessopening?eco=B90"
      ]
    }
  ]
}
```

## Component Interactions

### Scenario 1: Load Historical Player

```
User Action: Search "Kasparov"
    ↓
UI Controller → HistoricalDatabaseService.searchPlayers("Kasparov")
    ↓
HistoricalDatabaseService → Search index, return matches
    ↓
UI displays results with photos and metadata
    ↓
User selects "Kasparov, Garry"
    ↓
UI Controller → HistoricalDatabaseService.loadPlayer("kasparov_garry")
    ↓
HistoricalDatabaseService → Load .orpd file, deserialize
    ↓
UI Controller → TimelineService.generateTimelines(repertoire)
    ↓
UI displays tree + timeline charts
```

### Scenario 2: Compare Two Time Periods

```
User Action: Click "Compare" button
    ↓
UI shows comparison dialog
    ↓
User selects: 2020-2022 vs 2023-2024
    ↓
UI Controller → AnalysisService.buildTree(filter1)
UI Controller → AnalysisService.buildTree(filter2)
    ↓
UI Controller → ComparisonService.compare(tree1, tree2)
    ↓
ComparisonService → Calculate differences
    ↓
UI displays side-by-side view with highlights
```

### Scenario 3: Find Preparation Gaps

```
User Action: Click "Find Gaps" button
    ↓
UI Controller → GapDetectorService.analyzeDepth(tree)
    ↓
GapDetectorService → Traverse tree, measure depths
    ↓
GapDetectorService → Identify shallow lines
    ↓
GapDetectorService → Generate recommendations
    ↓
UI displays heatmap + gap list + recommendations
```

## Performance Optimizations

### 1. Pre-processing Strategy

**Historical Players**:
- Pre-calculate all trees and statistics
- Store in optimized binary format
- Include pre-rendered timeline data
- Target: <500ms load time

**Personal Databases**:
- Cache recent analyses
- Incremental tree updates
- Background pre-calculation
- Target: <1s rebuild time

### 2. Caching Strategy

```java
public class CacheManager {
    // LRU cache for player data
    private LRUCache<String, PlayerRepertoire> playerCache;
    
    // Cache for timeline data
    private LRUCache<String, TimelineData> timelineCache;
    
    // Cache for opening info
    private LRUCache<String, OpeningInfo> openingCache;
    
    // Cache configuration
    private static final int PLAYER_CACHE_SIZE = 10;
    private static final int TIMELINE_CACHE_SIZE = 50;
    private static final int OPENING_CACHE_SIZE = 1000;
}
```

### 3. Lazy Loading

- Load tree nodes on-demand when expanded
- Defer timeline calculation until viewed
- Load game details only when selected
- Progressive rendering for large trees

### 4. Background Processing

```java
public class BackgroundProcessor {
    private ExecutorService executor;
    
    // Pre-calculate timelines in background
    public Future<TimelineData> calculateTimelineAsync(
        OpeningTreeNode node
    );
    
    // Pre-calculate comparisons
    public Future<ComparisonResult> compareAsync(
        OpeningTreeNode tree1,
        OpeningTreeNode tree2
    );
    
    // Pre-analyze gaps
    public Future<List<PreparationGap>> analyzeGapsAsync(
        OpeningTreeNode tree
    );
}
```

## Database Schema Extensions

### Player Index (SQLite)

```sql
CREATE TABLE players (
    id TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    peak_rating INTEGER,
    nationality TEXT,
    birth_date TEXT,
    era TEXT,
    total_games INTEGER,
    file_path TEXT NOT NULL,
    file_size INTEGER,
    last_updated TEXT,
    photo_url TEXT
);

CREATE INDEX idx_player_name ON players(full_name);
CREATE INDEX idx_player_rating ON players(peak_rating DESC);
CREATE INDEX idx_player_era ON players(era);

CREATE VIRTUAL TABLE player_search USING fts5(
    full_name,
    nationality,
    content=players
);
```

### Opening Database (SQLite)

```sql
CREATE TABLE openings (
    eco TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    variation TEXT,
    moves TEXT NOT NULL,
    fen TEXT NOT NULL,
    popularity REAL,
    description TEXT
);

CREATE INDEX idx_opening_name ON openings(name);
CREATE INDEX idx_opening_popularity ON openings(popularity DESC);

CREATE VIRTUAL TABLE opening_search USING fts5(
    name,
    variation,
    description,
    content=openings
);
```

## Migration Path from V1 to V2

### Backward Compatibility

1. **V1 Databases**: Continue to work without changes
2. **V1 UI**: Still accessible via "Classic Mode"
3. **V1 Features**: All preserved in V2

### Migration Steps

```java
public class MigrationService {
    // Detect database version
    public DatabaseVersion detectVersion(File dbFile);
    
    // Migrate V1 to V2 format (optional)
    public void migrateToV2(File v1Database, File v2Database);
    
    // Add V2 metadata to existing database
    public void enhanceDatabase(File database);
}
```

### Data Enhancement

- Add opening names to existing trees
- Calculate timeline data from existing games
- Generate depth analysis
- Add trend indicators

## Technology Stack Updates

### New Dependencies

```xml
<!-- Charting library -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.4</version>
</dependency>

<!-- SQLite for player index -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.44.1.0</version>
</dependency>

<!-- JSON processing -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- Image loading -->
<dependency>
    <groupId>org.imgscalr</groupId>
    <artifactId>imgscalr-lib</artifactId>
    <version>4.2</version>
</dependency>

<!-- Fuzzy search -->
<dependency>
    <groupId>me.xdrop</groupId>
    <artifactId>fuzzywuzzy</artifactId>
    <version>1.4.0</version>
</dependency>
```

## Security Considerations

### Historical Player Data

- **Integrity**: SHA-256 checksums for .orpd files
- **Verification**: Signature verification for official data
- **Privacy**: No personal data in public datasets

### User Data

- **Encryption**: Optional encryption for personal databases
- **Privacy**: No data sent to external servers
- **Backup**: Automatic backup before migrations

## Testing Strategy

### Unit Tests

- All new services (Historical, Timeline, Comparison, etc.)
- Data format serialization/deserialization
- Cache management
- Search algorithms

### Integration Tests

- Historical player loading
- Timeline generation
- Comparison workflows
- Gap detection

### Performance Tests

- Load time benchmarks (<500ms target)
- Tree rebuild benchmarks (<1s target)
- Memory usage monitoring
- UI responsiveness tests

### UI Tests

- Chart rendering
- Comparison view
- Search autocomplete
- Keyboard navigation

## Deployment Considerations

### Distribution

- **Base Application**: ~50MB
- **Historical Player Pack**: ~500MB (100 players)
- **Full Player Pack**: ~2GB (500+ players)
- **Opening Database**: ~10MB

### Installation Options

1. **Minimal**: App only (personal use)
2. **Standard**: App + 100 top players
3. **Complete**: App + 500+ players + opening DB

### Update Mechanism

```java
public interface UpdateService {
    // Check for updates
    UpdateInfo checkForUpdates();
    
    // Download player data updates
    void downloadPlayerData(String playerId);
    
    // Update opening database
    void updateOpeningDatabase();
    
    // Update application
    void updateApplication(Version newVersion);
}
```

## Conclusion

V2 architecture builds on V1's solid foundation while adding:

- **Historical player support** via pre-processed data
- **Advanced visualizations** with timeline and comparison tools
- **Enhanced analysis** with gap detection and trends
- **Better UX** with search, autocomplete, and saved presets

The architecture maintains backward compatibility, ensures high performance, and provides a clear path for future enhancements.
