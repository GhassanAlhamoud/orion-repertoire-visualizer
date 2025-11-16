# Orion Repertoire Visualizer

A modern JavaFX desktop application for visualizing chess opening repertoire evolution over time. Built on top of the [OrionDB Chess Engine](https://github.com/GhassanAlhamoud/oriondb-chess-engine), this tool helps competitive chess players and coaches analyze how opening choices and performance change across different time periods.

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Java](https://img.shields.io/badge/java-17+-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-green)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

## Features

### Core Functionality (V1)

- **PGN Import**: Import your personal chess games from PGN files
- **Opening Tree Visualization**: Interactive tree showing all opening variations played
- **Performance Statistics**: Win/Loss/Draw percentages for each opening line
- **Time-Based Filtering**: Filter games by date range (months or years)
- **Side Filtering**: Analyze White and Black repertoires separately
- **Opponent Filtering**: Focus on games against specific opponents
- **Interactive Chessboard**: Visual board display synchronized with tree navigation
- **Game List**: View all games reaching a specific position

### Advanced Features (V2 Ready)

The architecture supports future enhancements:
- Historical player database analysis (e.g., analyze Karpov's career)
- Pre-processed player data for instant loading
- Comparison mode (compare two time periods or players)
- Opening name detection and ECO code mapping

## Screenshots

### Main Interface
The application features a three-panel layout:
- **Left Panel**: Filters and chess board display
- **Center Panel**: Opening tree with color-coded statistics
- **Right Panel**: List of games for the selected position

### Color Coding
- **Green**: Good performance (>55% win rate)
- **Orange**: Average performance (45-55% win rate)
- **Red**: Poor performance (<45% win rate)
- **Gray**: Insufficient data (<3 games)

## Installation

### Prerequisites

- **Java 17 or higher** - [Download JDK](https://adoptium.net/)
- **Apache Maven** - [Download Maven](https://maven.apache.org/download.cgi)
- **OrionDB Chess Engine** - Built automatically as a dependency

### Building from Source

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/orion-repertoire-visualizer.git
   cd orion-repertoire-visualizer
   ```

2. **Clone and build OrionDB Chess Engine:**
   ```bash
   cd ..
   git clone https://github.com/GhassanAlhamoud/oriondb-chess-engine.git
   cd oriondb-chess-engine
   mvn clean install
   cd ../orion-repertoire-visualizer
   ```

3. **Build the visualizer:**
   ```bash
   mvn clean package
   ```

4. **Run the application:**
   ```bash
   mvn javafx:run
   ```

   Or run the JAR directly:
   ```bash
   java -jar target/orion-repertoire-visualizer-1.0.0.jar
   ```

## Usage Guide

### Getting Started

1. **Import Your Games**
   - Click `File → Import PGN...`
   - Select your PGN file (can contain thousands of games)
   - Choose where to save the OrionDB database
   - Wait for import to complete

2. **Apply Filters**
   - Enter your player name in the "Player Name" field
   - Select side (White/Black/Both)
   - Set date range using the date pickers
   - Optionally filter by opponent
   - Click "Apply Filters"

3. **Explore Your Repertoire**
   - Click on moves in the opening tree to navigate
   - The chessboard updates to show the position
   - View statistics for each move (game count, win %)
   - See all games reaching that position in the game list

### Example: Analyzing Karpov's Repertoire

Assuming you have a database with Karpov's games:

1. Enter "Karpov" in the player name field
2. Select "White" to analyze his White repertoire
3. Set date range to 1975-1985 to see his early career
4. Click "Apply Filters"
5. Explore the tree to see his most played openings
6. Change date range to 1986-1995 to compare evolution

### Keyboard Shortcuts

- `Ctrl+O` - Open database
- `Ctrl+I` - Import PGN
- `Ctrl+R` - Reset view
- `Ctrl+Q` - Quit application

## Architecture

### Technology Stack

- **UI Framework**: JavaFX 21
- **Database Engine**: OrionDB (custom chess database)
- **Build Tool**: Maven
- **Language**: Java 17

### Project Structure

```
orion-repertoire-visualizer/
├── src/main/java/com/orion/visualizer/
│   ├── OrionVisualizerApp.java          # Main application entry point
│   ├── model/                            # Data models
│   │   ├── OpeningTreeNode.java         # Opening tree structure
│   │   ├── GameReference.java           # Lightweight game reference
│   │   ├── FilterCriteria.java          # Filter configuration
│   │   └── PlayerSide.java              # Side enumeration
│   ├── service/                          # Business logic
│   │   ├── DatabaseService.java         # OrionDB integration
│   │   ├── AnalysisService.java         # Opening tree analysis
│   │   └── ChessEngineService.java      # Board state management
│   ├── controller/                       # Application controllers
│   │   └── MainController.java          # Main application controller
│   ├── view/                             # UI components
│   │   ├── ChessboardView.java          # Chess board display
│   │   ├── OpeningTreeView.java         # Tree visualization
│   │   ├── GameListView.java            # Game list table
│   │   └── TimelineChart.java           # Timeline visualization
│   └── util/                             # Utilities
│       ├── ChessNotation.java           # Chess notation helpers
│       └── DateUtils.java               # Date parsing utilities
└── src/main/resources/
    └── css/
        └── styles.css                    # Modern UI styling
```

### Key Design Patterns

- **Singleton**: DatabaseService for single database instance
- **Observer**: UI components react to model changes
- **MVC**: Separation of concerns between model, view, and controller
- **Async Processing**: Background threads for database operations

## Performance

- **Import Speed**: ~5,000-10,000 games per second (depending on hardware)
- **Tree Building**: Typically <2 seconds for 1,000 games
- **Memory Usage**: ~100-200 MB for typical databases (10,000 games)
- **UI Responsiveness**: All operations run on background threads

## Troubleshooting

### Common Issues

**Issue**: Application won't start
- **Solution**: Ensure Java 17+ is installed: `java -version`

**Issue**: "No database loaded" error
- **Solution**: Import a PGN file or load an existing .oriondb file

**Issue**: Import fails with parsing errors
- **Solution**: Ensure PGN file is valid. The parser is error-tolerant but severely malformed files may fail

**Issue**: Tree shows no results after filtering
- **Solution**: Check that player name matches exactly (case-insensitive). Try "Both" for side filter.

### Debug Mode

Run with debug logging:
```bash
mvn javafx:run -Djavafx.verbose=true
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Setup

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods focused and concise

## Roadmap

### Version 1.1
- [ ] Export filtered games to PGN
- [ ] Save/load filter presets
- [ ] Opening name detection (ECO codes)
- [ ] Improved timeline visualizations

### Version 2.0
- [ ] Historical player database support
- [ ] Pre-processed player data files
- [ ] Comparison mode (two time periods)
- [ ] Advanced statistics (trends, gaps)

### Version 2.1
- [ ] Cloud database integration
- [ ] Multi-device synchronization
- [ ] Collaborative analysis features

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **OrionDB Chess Engine** by Ghassan Alhamoud - The foundation of this project
- **JavaFX Community** - For the excellent UI framework
- **Chess Programming Wiki** - For chess programming resources

## Contact

- **Project Repository**: [GitHub](https://github.com/yourusername/orion-repertoire-visualizer)
- **Issue Tracker**: [GitHub Issues](https://github.com/yourusername/orion-repertoire-visualizer/issues)

## Support

If you find this project useful, please consider:
- ⭐ Starring the repository
- 🐛 Reporting bugs
- 💡 Suggesting new features
- 🤝 Contributing code

---

**Happy Analyzing! ♟️**
