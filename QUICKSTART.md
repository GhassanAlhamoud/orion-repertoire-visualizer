# Quick Start Guide

Get up and running with Orion Repertoire Visualizer in 5 minutes!

## Prerequisites

- Java 11 or higher installed
- Maven installed
- Git installed

## Installation

### 1. Clone and Build OrionDB Engine

```bash
git clone https://github.com/GhassanAlhamoud/oriondb-chess-engine.git
cd oriondb-chess-engine
mvn clean install -DskipTests
cd ..
```

### 2. Clone and Build Orion Repertoire Visualizer

```bash
git clone https://github.com/GhassanAlhamoud/orion-repertoire-visualizer.git
cd orion-repertoire-visualizer
mvn clean package
```

### 3. Run the Application

```bash
mvn javafx:run
```

## First Steps

### Import Your Games

1. Click **File → Import PGN...**
2. Select your PGN file (e.g., `my_games.pgn`)
3. Choose where to save the database (e.g., `my_games.oriondb`)
4. Wait for import to complete

### Analyze Your Repertoire

1. Enter your name in the **Player Name** field
2. Select **White** or **Black** from the **Side** dropdown
3. Set your desired **Date Range**
4. Click **Apply Filters**

### Explore the Opening Tree

1. Click on moves in the tree to navigate
2. Watch the board update to show the position
3. View statistics (W/L/D %) for each move
4. See all games reaching that position in the game list

## Example: Analyze Karpov's Repertoire

If you have a database with Karpov's games:

```
1. Player Name: "Karpov"
2. Side: White
3. Date Range: 1975-01-01 to 1985-12-31
4. Click "Apply Filters"
```

Now you can see:
- Karpov's most played openings as White in the 1970s-80s
- His success rate with each opening
- How his repertoire evolved over time

## Tips

- **Expand Tree**: Right-click → Expand All (or use View menu)
- **Reset View**: View → Reset View to return to starting position
- **Filter by Opponent**: Enter opponent name to focus on specific matchups
- **Date Ranges**: Use different date ranges to see repertoire evolution

## Troubleshooting

### Application won't start

```bash
# Check Java version
java -version

# Should show Java 11 or higher
```

### Import fails

- Ensure PGN file is valid
- Check you have write permissions for output directory
- Try a smaller PGN file first

### No results after filtering

- Check player name spelling (case-insensitive)
- Try "Both" for side filter
- Expand date range

## Next Steps

- Read the full [README.md](README.md) for detailed features
- Check [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for advanced setup
- Review [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) for architecture details

## Need Help?

- Open an issue: https://github.com/GhassanAlhamoud/orion-repertoire-visualizer/issues
- Check OrionDB docs: https://github.com/GhassanAlhamoud/oriondb-chess-engine

## Example Workflow

```
Import PGN → Apply Filters → Explore Tree → Analyze Statistics → Adjust Filters → Repeat
```

Happy analyzing! ♟️
