# V2 Data Preprocessing Pipeline

## Overview

This document describes the data preprocessing pipeline for creating optimized historical player data files that enable instant loading and analysis in V2.

## Goals

1. **Fast Loading**: <500ms to load any historical player
2. **Compact Storage**: ~5-10MB per player (500+ games)
3. **Rich Data**: Pre-calculated statistics, timelines, and metadata
4. **Searchable**: Fast player search and autocomplete
5. **Updateable**: Easy to add new games and players

## Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Data Sources                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ PGN Files│  │ Lichess  │  │ Chess.com│  │ FICS DB  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 1: Collection                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ • Download PGN files                                  │  │
│  │ • Filter by player name                               │  │
│  │ • Deduplicate games                                   │  │
│  │ • Validate game data                                  │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 2: Enrichment                         │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ • Add player metadata (rating, nationality, etc.)    │  │
│  │ • Detect opening names and ECO codes                 │  │
│  │ • Calculate game-level statistics                    │  │
│  │ • Add time control information                       │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 3: Analysis                           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ • Build opening trees (White and Black)              │  │
│  │ • Calculate statistics for each node                 │  │
│  │ • Generate timeline data                             │  │
│  │ • Detect trends and patterns                         │  │
│  │ • Identify preparation depth                         │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 4: Optimization                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ • Prune low-frequency branches                       │  │
│  │ • Compress timeline data                             │  │
│  │ • Optimize tree structure                            │  │
│  │ • Pre-calculate common queries                       │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Step 5: Serialization                      │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ • Serialize to binary format (.orpd)                 │  │
│  │ • Generate index entry                               │  │
│  │ • Create thumbnail/preview                           │  │
│  │ • Calculate checksum                                 │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Output: Player Data Pack                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │  .orpd   │  │  Index   │  │ Metadata │  │ Thumbnail│   │
│  │  Files   │  │  DB      │  │   JSON   │  │  Images  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Step 1: Data Collection

### Player Selection Criteria

**Top 500 Historical Players**:
- World Champions (all)
- Top 100 players by peak rating
- Top 100 players by total games
- Top 100 players by historical significance
- Top 100 players by era (1900-1950, 1950-1980, 1980-2000, 2000+)
- Notable players requested by community

### Data Sources

1. **PGN Mega Database**
   - ChessBase Mega Database
   - TWIC (The Week in Chess)
   - Lichess Elite Database
   - Chess.com Master Games

2. **Online Databases**
   - Lichess API (for modern players)
   - Chess.com API (for modern players)
   - FICS Games Database
   - 365Chess.com

3. **Historical Archives**
   - Edward Winter's archives
   - ChessGames.com
   - PGN Mentor collections

### Collection Process

```java
public class PlayerDataCollector {
    private List<DataSource> sources;
    
    /**
     * Collect all games for a player.
     */
    public List<Game> collectGames(String playerName) {
        List<Game> allGames = new ArrayList<>();
        
        // Search each data source
        for (DataSource source : sources) {
            List<Game> games = source.searchByPlayer(playerName);
            allGames.addAll(games);
        }
        
        // Deduplicate
        allGames = deduplicateGames(allGames);
        
        // Validate
        allGames = validateGames(allGames);
        
        return allGames;
    }
    
    /**
     * Deduplicate games based on players, date, and moves.
     */
    private List<Game> deduplicateGames(List<Game> games) {
        Set<String> seen = new HashSet<>();
        List<Game> unique = new ArrayList<>();
        
        for (Game game : games) {
            String signature = generateSignature(game);
            if (!seen.contains(signature)) {
                seen.add(signature);
                unique.add(game);
            }
        }
        
        return unique;
    }
    
    /**
     * Generate unique signature for game.
     */
    private String generateSignature(Game game) {
        return String.format("%s|%s|%s|%s",
            game.getWhite(),
            game.getBlack(),
            game.getDate(),
            game.getMoves().subList(0, Math.min(20, game.getMoves().size()))
        );
    }
    
    /**
     * Validate game data quality.
     */
    private List<Game> validateGames(List<Game> games) {
        return games.stream()
            .filter(game -> game.getMoves().size() >= 10)  // Minimum moves
            .filter(game -> game.getDate() != null)        // Has date
            .filter(game -> game.getResult() != null)      // Has result
            .collect(Collectors.toList());
    }
}
```

### Quality Criteria

**Games Must Have**:
- ✅ Player names (White and Black)
- ✅ Date (at least year)
- ✅ Result (1-0, 0-1, 1/2-1/2)
- ✅ Minimum 10 moves
- ✅ Valid PGN notation

**Games Should Have** (preferred):
- Event name
- Round information
- Player ratings
- ECO code
- Time control

## Step 2: Data Enrichment

### Player Metadata Enrichment

```java
public class MetadataEnricher {
    
    /**
     * Enrich player metadata from multiple sources.
     */
    public PlayerMetadata enrichMetadata(String playerName, List<Game> games) {
        PlayerMetadata metadata = new PlayerMetadata();
        
        // Basic info from games
        metadata.setFullName(normalizePlayerName(playerName));
        metadata.setTotalGames(games.size());
        
        // Calculate date range
        LocalDate firstGame = games.stream()
            .map(Game::getDate)
            .min(LocalDate::compareTo)
            .orElse(null);
        LocalDate lastGame = games.stream()
            .map(Game::getDate)
            .max(LocalDate::compareTo)
            .orElse(null);
        metadata.setEra(formatEra(firstGame, lastGame));
        
        // Calculate peak rating
        int peakRating = games.stream()
            .mapToInt(g -> getRatingForPlayer(g, playerName))
            .max()
            .orElse(0);
        metadata.setPeakRating(peakRating);
        
        // Fetch additional info from external sources
        enrichFromWikipedia(metadata);
        enrichFromFideDatabase(metadata);
        enrichFromChessGames(metadata);
        
        return metadata;
    }
    
    /**
     * Fetch player info from Wikipedia.
     */
    private void enrichFromWikipedia(PlayerMetadata metadata) {
        // API call to Wikipedia
        // Extract: birth date, nationality, titles, photo
    }
    
    /**
     * Fetch player info from FIDE database.
     */
    private void enrichFromFideDatabase(PlayerMetadata metadata) {
        // API call to FIDE
        // Extract: current rating, title, federation
    }
    
    /**
     * Fetch player info from ChessGames.com.
     */
    private void enrichFromChessGames(PlayerMetadata metadata) {
        // Web scraping or API
        // Extract: bio, notable games, achievements
    }
}
```

### Opening Name Detection

```java
public class OpeningDetector {
    private Map<String, OpeningInfo> openingDatabase;
    
    /**
     * Detect opening name from move sequence.
     */
    public OpeningInfo detectOpening(List<String> moves) {
        // Try exact match first (up to 20 moves)
        for (int depth = Math.min(20, moves.size()); depth >= 3; depth--) {
            List<String> prefix = moves.subList(0, depth);
            String key = String.join(" ", prefix);
            
            if (openingDatabase.containsKey(key)) {
                return openingDatabase.get(key);
            }
        }
        
        // Fallback to first move classification
        if (moves.size() > 0) {
            return classifyByFirstMove(moves.get(0));
        }
        
        return null;
    }
    
    /**
     * Load opening database from ECO file.
     */
    public void loadOpeningDatabase(File ecoFile) {
        // Parse ECO codes and opening names
        // Build lookup table
    }
}
```

## Step 3: Analysis

### Opening Tree Construction

```java
public class TreeBuilder {
    
    /**
     * Build opening tree from games.
     */
    public OpeningTreeNode buildTree(List<Game> games, PlayerSide side) {
        OpeningTreeNode root = new OpeningTreeNode(null);
        
        for (Game game : games) {
            // Determine if player had this side
            boolean isPlayerSide = (side == PlayerSide.WHITE && 
                                   game.getWhite().equals(playerName)) ||
                                  (side == PlayerSide.BLACK && 
                                   game.getBlack().equals(playerName));
            
            if (!isPlayerSide) continue;
            
            // Add game to tree
            addGameToTree(root, game, side);
        }
        
        // Calculate statistics for all nodes
        calculateStatistics(root);
        
        // Detect opening names
        detectOpeningNames(root);
        
        return root;
    }
    
    /**
     * Calculate statistics recursively.
     */
    private void calculateStatistics(OpeningTreeNode node) {
        // Calculate win/draw/loss from games
        for (GameReference game : node.getGames()) {
            if (game.isWin()) node.incrementWins();
            else if (game.isDraw()) node.incrementDraws();
            else node.incrementLosses();
        }
        
        // Calculate additional stats
        node.setAverageOpponentRating(calculateAvgRating(node.getGames()));
        node.setDepth(calculateDepth(node));
        node.setFirstPlayed(findFirstGame(node.getGames()));
        node.setLastPlayed(findLastGame(node.getGames()));
        
        // Recurse to children
        for (OpeningTreeNode child : node.getChildren().values()) {
            calculateStatistics(child);
        }
    }
}
```

### Timeline Generation

```java
public class TimelineGenerator {
    
    /**
     * Generate timeline data for a tree node.
     */
    public TimelineData generateTimeline(OpeningTreeNode node, 
                                        TimeGranularity granularity) {
        List<TimePoint> points = new ArrayList<>();
        
        // Group games by time period
        Map<String, List<GameReference>> grouped = 
            groupByPeriod(node.getGames(), granularity);
        
        // Calculate statistics for each period
        for (Map.Entry<String, List<GameReference>> entry : grouped.entrySet()) {
            TimePoint point = new TimePoint();
            point.setDate(parseDate(entry.getKey()));
            point.setGameCount(entry.getValue().size());
            
            // Calculate win rate
            long wins = entry.getValue().stream()
                .filter(GameReference::isWin)
                .count();
            point.setWinRate((double) wins / entry.getValue().size());
            
            points.add(point);
        }
        
        // Sort by date
        points.sort(Comparator.comparing(TimePoint::getDate));
        
        // Detect trend
        Trend trend = detectTrend(points);
        
        return new TimelineData(points, trend);
    }
    
    /**
     * Detect trend from timeline data.
     */
    private Trend detectTrend(List<TimePoint> points) {
        if (points.size() < 3) return Trend.STABLE;
        
        // Linear regression on win rates
        double[] winRates = points.stream()
            .mapToDouble(TimePoint::getWinRate)
            .toArray();
        
        double slope = calculateSlope(winRates);
        
        if (slope > 0.05) return Trend.IMPROVING;
        if (slope < -0.05) return Trend.DECLINING;
        return Trend.STABLE;
    }
}
```

### Preparation Depth Analysis

```java
public class DepthAnalyzer {
    
    /**
     * Analyze preparation depth for entire tree.
     */
    public DepthAnalysis analyzeDepth(OpeningTreeNode root) {
        DepthAnalysis analysis = new DepthAnalysis();
        
        // Traverse tree and collect depth info
        analyzeNode(root, 0, analysis);
        
        // Calculate statistics
        analysis.setAverageDepth(calculateAverageDepth(analysis));
        analysis.setMaxDepth(findMaxDepth(analysis));
        
        // Identify shallow lines
        analysis.setShallowLines(findShallowLines(root, 8));
        
        return analysis;
    }
    
    /**
     * Recursively analyze node depth.
     */
    private void analyzeNode(OpeningTreeNode node, int depth, 
                            DepthAnalysis analysis) {
        analysis.incrementDepthCount(depth, node.getGames().size());
        
        if (node.getChildren().isEmpty()) {
            // Leaf node - record terminal depth
            analysis.addTerminalDepth(depth, node.getGames().size());
        } else {
            // Recurse to children
            for (OpeningTreeNode child : node.getChildren().values()) {
                analyzeNode(child, depth + 1, analysis);
            }
        }
    }
}
```

## Step 4: Optimization

### Tree Pruning

```java
public class TreeOptimizer {
    
    /**
     * Prune low-frequency branches to reduce file size.
     */
    public void pruneTree(OpeningTreeNode root, int minGames) {
        pruneNode(root, minGames);
    }
    
    /**
     * Recursively prune nodes with insufficient games.
     */
    private void pruneNode(OpeningTreeNode node, int minGames) {
        // Remove children with too few games
        node.getChildren().entrySet().removeIf(entry -> 
            entry.getValue().getGames().size() < minGames
        );
        
        // Recurse to remaining children
        for (OpeningTreeNode child : node.getChildren().values()) {
            pruneNode(child, minGames);
        }
    }
    
    /**
     * Compress timeline data by removing redundant points.
     */
    public void compressTimeline(TimelineData timeline) {
        List<TimePoint> compressed = new ArrayList<>();
        
        TimePoint prev = null;
        for (TimePoint point : timeline.getDataPoints()) {
            // Keep point if significantly different from previous
            if (prev == null || 
                Math.abs(point.getWinRate() - prev.getWinRate()) > 0.05 ||
                point.getGameCount() > prev.getGameCount() * 1.5) {
                compressed.add(point);
                prev = point;
            }
        }
        
        timeline.setDataPoints(compressed);
    }
}
```

### Pre-calculation

```java
public class PreCalculator {
    
    /**
     * Pre-calculate common queries for fast access.
     */
    public void preCalculate(PlayerRepertoire repertoire) {
        // Most played openings
        repertoire.setMostPlayedWhite(
            findMostPlayed(repertoire.getWhiteTree())
        );
        repertoire.setMostPlayedBlack(
            findMostPlayed(repertoire.getBlackTree())
        );
        
        // Overall statistics
        repertoire.setOverallStats(
            calculateOverallStats(repertoire)
        );
        
        // Performance by year
        repertoire.setPerformanceByYear(
            calculateYearlyPerformance(repertoire)
        );
        
        // Top opponents
        repertoire.setTopOpponents(
            findTopOpponents(repertoire, 10)
        );
    }
}
```

## Step 5: Serialization

### Binary Format Specification

```
ORPD File Format (Orion Repertoire Player Data)
Version 2.0

┌────────────────────────────────────────────────────────┐
│ Header (256 bytes)                                     │
├────────────────────────────────────────────────────────┤
│ Magic:           "ORPD" (4 bytes)                      │
│ Version:         2 (4 bytes, little-endian)            │
│ Player ID:       SHA-256 hash (32 bytes)               │
│ Metadata Offset: 8 bytes (little-endian)               │
│ White Tree Offset: 8 bytes                             │
│ Black Tree Offset: 8 bytes                             │
│ Timeline Offset: 8 bytes                               │
│ Index Offset:    8 bytes                               │
│ Checksum:        SHA-256 (32 bytes)                    │
│ Reserved:        152 bytes (zeros)                     │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│ Metadata Section (JSON, GZIP compressed)               │
├────────────────────────────────────────────────────────┤
│ {                                                       │
│   "player": {                                          │
│     "id": "kasparov_garry",                           │
│     "fullName": "Kasparov, Garry",                    │
│     "peakRating": 2851,                               │
│     "nationality": "RUS",                             │
│     "birthDate": "1963-04-13",                        │
│     "era": "1980-2005",                               │
│     "totalGames": 2500,                               │
│     "titles": ["World Champion 1985-2000"],          │
│     "photoUrl": "https://..."                         │
│   },                                                   │
│   "statistics": { ... },                              │
│   "generated": "2024-11-17T00:00:00Z",               │
│   "version": 2                                        │
│ }                                                      │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│ White Tree Section (Binary, GZIP compressed)           │
├────────────────────────────────────────────────────────┤
│ Serialized OpeningTreeNode structure                   │
│ - Move strings (UTF-8)                                 │
│ - Statistics (integers)                                │
│ - Game references (compressed)                         │
│ - Child nodes (recursive)                              │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│ Black Tree Section (Binary, GZIP compressed)           │
├────────────────────────────────────────────────────────┤
│ Same format as White Tree                              │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│ Timeline Section (Binary, GZIP compressed)             │
├────────────────────────────────────────────────────────┤
│ Pre-calculated timeline data                           │
│ - Time points (date + statistics)                      │
│ - Trend indicators                                     │
│ - Compressed for fast loading                          │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│ Index Section (Binary)                                 │
├────────────────────────────────────────────────────────┤
│ Fast lookup index for tree navigation                  │
│ - Move hash → offset mapping                           │
│ - Enables O(1) node lookup                             │
└────────────────────────────────────────────────────────┘
```

### Serialization Implementation

```java
public class ORPDSerializer {
    
    /**
     * Serialize player repertoire to .orpd file.
     */
    public void serialize(PlayerRepertoire repertoire, File output) 
            throws IOException {
        try (FileOutputStream fos = new FileOutputStream(output);
             DataOutputStream dos = new DataOutputStream(fos)) {
            
            // Write header
            writeHeader(dos, repertoire);
            
            // Write metadata
            long metadataOffset = dos.size();
            writeMetadata(dos, repertoire.getMetadata());
            
            // Write white tree
            long whiteTreeOffset = dos.size();
            writeTree(dos, repertoire.getWhiteTree());
            
            // Write black tree
            long blackTreeOffset = dos.size();
            writeTree(dos, repertoire.getBlackTree());
            
            // Write timeline data
            long timelineOffset = dos.size();
            writeTimelines(dos, repertoire.getTimelines());
            
            // Write index
            long indexOffset = dos.size();
            writeIndex(dos, repertoire);
            
            // Update header with offsets
            updateHeader(output, metadataOffset, whiteTreeOffset, 
                        blackTreeOffset, timelineOffset, indexOffset);
            
            // Calculate and write checksum
            writeChecksum(output);
        }
    }
    
    /**
     * Write tree structure recursively.
     */
    private void writeTree(DataOutputStream dos, OpeningTreeNode node) 
            throws IOException {
        // Write move
        writeString(dos, node.getMove());
        
        // Write statistics
        dos.writeInt(node.getWins());
        dos.writeInt(node.getDraws());
        dos.writeInt(node.getLosses());
        dos.writeInt(node.getDepth());
        dos.writeDouble(node.getAverageOpponentRating());
        
        // Write dates
        writeDate(dos, node.getFirstPlayed());
        writeDate(dos, node.getLastPlayed());
        
        // Write opening info
        writeOpeningInfo(dos, node.getOpeningInfo());
        
        // Write game references (compressed)
        writeGameReferences(dos, node.getGames());
        
        // Write children count
        dos.writeInt(node.getChildren().size());
        
        // Write children recursively
        for (OpeningTreeNode child : node.getChildren().values()) {
            writeTree(dos, child);
        }
    }
}
```

### Deserialization Implementation

```java
public class ORPDDeserializer {
    
    /**
     * Deserialize .orpd file to player repertoire.
     */
    public PlayerRepertoire deserialize(File input) throws IOException {
        try (FileInputStream fis = new FileInputStream(input);
             DataInputStream dis = new DataInputStream(fis)) {
            
            // Read and validate header
            Header header = readHeader(dis);
            validateHeader(header);
            
            // Read metadata
            dis.skip(header.metadataOffset - dis.available());
            PlayerMetadata metadata = readMetadata(dis);
            
            // Read white tree
            dis.skip(header.whiteTreeOffset - dis.available());
            OpeningTreeNode whiteTree = readTree(dis);
            
            // Read black tree
            dis.skip(header.blackTreeOffset - dis.available());
            OpeningTreeNode blackTree = readTree(dis);
            
            // Read timeline data
            dis.skip(header.timelineOffset - dis.available());
            Map<String, TimelineData> timelines = readTimelines(dis);
            
            // Construct repertoire
            PlayerRepertoire repertoire = new PlayerRepertoire();
            repertoire.setMetadata(metadata);
            repertoire.setWhiteTree(whiteTree);
            repertoire.setBlackTree(blackTree);
            repertoire.setTimelines(timelines);
            
            return repertoire;
        }
    }
    
    /**
     * Read tree structure recursively.
     */
    private OpeningTreeNode readTree(DataInputStream dis) 
            throws IOException {
        // Read move
        String move = readString(dis);
        OpeningTreeNode node = new OpeningTreeNode(move);
        
        // Read statistics
        node.setWins(dis.readInt());
        node.setDraws(dis.readInt());
        node.setLosses(dis.readInt());
        node.setDepth(dis.readInt());
        node.setAverageOpponentRating(dis.readDouble());
        
        // Read dates
        node.setFirstPlayed(readDate(dis));
        node.setLastPlayed(readDate(dis));
        
        // Read opening info
        node.setOpeningInfo(readOpeningInfo(dis));
        
        // Read game references
        node.setGames(readGameReferences(dis));
        
        // Read children
        int childCount = dis.readInt();
        for (int i = 0; i < childCount; i++) {
            OpeningTreeNode child = readTree(dis);
            node.addChild(child);
        }
        
        return node;
    }
}
```

## Player Index Database

### SQLite Schema

```sql
-- Player index for fast search
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
    file_checksum TEXT,
    last_updated TEXT,
    photo_url TEXT,
    titles TEXT,  -- JSON array
    UNIQUE(full_name, birth_date)
);

-- Indexes for fast lookup
CREATE INDEX idx_player_name ON players(full_name);
CREATE INDEX idx_player_rating ON players(peak_rating DESC);
CREATE INDEX idx_player_era ON players(era);
CREATE INDEX idx_player_nationality ON players(nationality);

-- Full-text search
CREATE VIRTUAL TABLE player_search USING fts5(
    full_name,
    nationality,
    titles,
    content=players,
    content_rowid=rowid
);

-- Triggers to keep FTS in sync
CREATE TRIGGER players_ai AFTER INSERT ON players BEGIN
    INSERT INTO player_search(rowid, full_name, nationality, titles)
    VALUES (new.rowid, new.full_name, new.nationality, new.titles);
END;

CREATE TRIGGER players_ad AFTER DELETE ON players BEGIN
    DELETE FROM player_search WHERE rowid = old.rowid;
END;

CREATE TRIGGER players_au AFTER UPDATE ON players BEGIN
    UPDATE player_search 
    SET full_name = new.full_name,
        nationality = new.nationality,
        titles = new.titles
    WHERE rowid = new.rowid;
END;
```

### Index Management

```java
public class PlayerIndexManager {
    private Connection db;
    
    /**
     * Add player to index.
     */
    public void addPlayer(PlayerMetadata metadata, File orpdFile) 
            throws SQLException {
        String sql = "INSERT INTO players " +
                    "(id, full_name, peak_rating, nationality, " +
                    "birth_date, era, total_games, file_path, " +
                    "file_size, file_checksum, last_updated, " +
                    "photo_url, titles) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, metadata.getId());
            stmt.setString(2, metadata.getFullName());
            stmt.setInt(3, metadata.getPeakRating());
            stmt.setString(4, metadata.getNationality());
            stmt.setString(5, metadata.getBirthDate().toString());
            stmt.setString(6, metadata.getEra());
            stmt.setInt(7, metadata.getTotalGames());
            stmt.setString(8, orpdFile.getAbsolutePath());
            stmt.setLong(9, orpdFile.length());
            stmt.setString(10, calculateChecksum(orpdFile));
            stmt.setString(11, LocalDateTime.now().toString());
            stmt.setString(12, metadata.getPhotoUrl());
            stmt.setString(13, toJson(metadata.getTitles()));
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Search players by name.
     */
    public List<PlayerMetadata> searchPlayers(String query) 
            throws SQLException {
        String sql = "SELECT * FROM players " +
                    "WHERE full_name LIKE ? " +
                    "ORDER BY peak_rating DESC " +
                    "LIMIT 20";
        
        List<PlayerMetadata> results = new ArrayList<>();
        
        try (PreparedStatement stmt = db.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapResultToMetadata(rs));
                }
            }
        }
        
        return results;
    }
}
```

## Batch Processing

### Parallel Processing

```java
public class BatchProcessor {
    private ExecutorService executor;
    private int numThreads;
    
    /**
     * Process multiple players in parallel.
     */
    public void processPlayers(List<String> playerNames, 
                              File outputDir) {
        executor = Executors.newFixedThreadPool(numThreads);
        
        List<Future<ProcessingResult>> futures = new ArrayList<>();
        
        for (String playerName : playerNames) {
            Future<ProcessingResult> future = executor.submit(() -> {
                return processPlayer(playerName, outputDir);
            });
            futures.add(future);
        }
        
        // Wait for all to complete
        for (Future<ProcessingResult> future : futures) {
            try {
                ProcessingResult result = future.get();
                System.out.println("Processed: " + result.getPlayerName());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        
        executor.shutdown();
    }
    
    /**
     * Process a single player.
     */
    private ProcessingResult processPlayer(String playerName, 
                                          File outputDir) {
        try {
            // Step 1: Collect games
            List<Game> games = collectGames(playerName);
            
            // Step 2: Enrich metadata
            PlayerMetadata metadata = enrichMetadata(playerName, games);
            
            // Step 3: Build trees
            OpeningTreeNode whiteTree = buildTree(games, PlayerSide.WHITE);
            OpeningTreeNode blackTree = buildTree(games, PlayerSide.BLACK);
            
            // Step 4: Generate timelines
            Map<String, TimelineData> timelines = generateTimelines(
                whiteTree, blackTree
            );
            
            // Step 5: Optimize
            optimizeTree(whiteTree);
            optimizeTree(blackTree);
            
            // Step 6: Serialize
            PlayerRepertoire repertoire = new PlayerRepertoire(
                metadata, whiteTree, blackTree, timelines
            );
            
            File outputFile = new File(outputDir, 
                metadata.getId() + ".orpd");
            serialize(repertoire, outputFile);
            
            // Step 7: Update index
            updateIndex(metadata, outputFile);
            
            return new ProcessingResult(playerName, true, outputFile);
            
        } catch (Exception e) {
            return new ProcessingResult(playerName, false, null, e);
        }
    }
}
```

### Progress Tracking

```java
public class ProgressTracker {
    private int total;
    private AtomicInteger completed;
    private AtomicInteger failed;
    
    /**
     * Update progress.
     */
    public void update(ProcessingResult result) {
        if (result.isSuccess()) {
            completed.incrementAndGet();
        } else {
            failed.incrementAndGet();
        }
        
        printProgress();
    }
    
    /**
     * Print progress to console.
     */
    private void printProgress() {
        int done = completed.get() + failed.get();
        double percent = (done * 100.0) / total;
        
        System.out.printf("Progress: %d/%d (%.1f%%) - " +
                         "Success: %d, Failed: %d\n",
                         done, total, percent, 
                         completed.get(), failed.get());
    }
}
```

## Quality Assurance

### Validation

```java
public class DataValidator {
    
    /**
     * Validate .orpd file integrity.
     */
    public ValidationResult validate(File orpdFile) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Check file size
            if (orpdFile.length() < 1024) {
                result.addError("File too small");
                return result;
            }
            
            // Verify checksum
            if (!verifyChecksum(orpdFile)) {
                result.addError("Checksum mismatch");
                return result;
            }
            
            // Try to deserialize
            PlayerRepertoire repertoire = deserialize(orpdFile);
            
            // Validate metadata
            validateMetadata(repertoire.getMetadata(), result);
            
            // Validate trees
            validateTree(repertoire.getWhiteTree(), result);
            validateTree(repertoire.getBlackTree(), result);
            
            // Validate timelines
            validateTimelines(repertoire.getTimelines(), result);
            
        } catch (Exception e) {
            result.addError("Deserialization failed: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Validate tree structure.
     */
    private void validateTree(OpeningTreeNode tree, 
                             ValidationResult result) {
        if (tree == null) {
            result.addError("Tree is null");
            return;
        }
        
        // Check statistics consistency
        int totalGames = tree.getWins() + tree.getDraws() + tree.getLosses();
        if (totalGames != tree.getGames().size()) {
            result.addWarning("Statistics mismatch in tree");
        }
        
        // Validate recursively
        for (OpeningTreeNode child : tree.getChildren().values()) {
            validateTree(child, result);
        }
    }
}
```

## Distribution

### Package Structure

```
player-data-pack-v2/
├── index.db                    # SQLite index
├── players/
│   ├── kasparov_garry.orpd
│   ├── karpov_anatoly.orpd
│   ├── fischer_bobby.orpd
│   └── ...
├── thumbnails/
│   ├── kasparov_garry.jpg
│   ├── karpov_anatoly.jpg
│   └── ...
├── metadata.json              # Pack metadata
└── README.txt                 # Installation instructions
```

### Update Mechanism

```java
public class UpdateManager {
    
    /**
     * Check for new player data.
     */
    public List<PlayerUpdate> checkForUpdates() {
        // Query update server
        // Compare versions
        // Return list of available updates
    }
    
    /**
     * Download and install player data.
     */
    public void installPlayer(String playerId) {
        // Download .orpd file
        // Verify checksum
        // Update index
        // Notify UI
    }
}
```

## Performance Targets

### Processing Speed

- **Collection**: 1000 games/second
- **Analysis**: 500 games/second
- **Serialization**: 100 MB/second
- **Total Time**: ~5 minutes per player (2000 games)

### File Sizes

- **Small Player** (500 games): 2-3 MB
- **Medium Player** (1500 games): 5-8 MB
- **Large Player** (3000+ games): 10-15 MB
- **Complete Pack** (500 players): ~2-3 GB

### Loading Performance

- **Deserialization**: <500ms per player
- **Index Search**: <50ms
- **Tree Navigation**: <10ms per node

## Conclusion

The preprocessing pipeline enables:

- **Fast Loading**: Pre-calculated data loads in <500ms
- **Rich Analysis**: Complete statistics and timelines included
- **Scalability**: Process 500+ players efficiently
- **Quality**: Validation ensures data integrity
- **Updateability**: Easy to add new players and games

This infrastructure makes historical player analysis practical and performant in V2.
