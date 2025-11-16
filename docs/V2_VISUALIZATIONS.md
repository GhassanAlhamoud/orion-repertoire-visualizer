# V2 Advanced Visualization Components

## Overview

This document details the visualization components for V2, focusing on rich, interactive charts and graphs that reveal patterns and trends in opening repertoires.

## Visualization Philosophy

### Design Principles

1. **Information Density**: Show maximum insight with minimal clutter
2. **Progressive Disclosure**: Details on demand, overview by default
3. **Interactive**: Click, hover, zoom for deeper exploration
4. **Consistent**: Unified color scheme and interaction patterns
5. **Responsive**: Smooth animations and real-time updates

### Color Palette

```java
public class VisualizationColors {
    // Performance colors (consistent with V1)
    public static final Color EXCELLENT = Color.rgb(76, 175, 80);    // Green
    public static final Color GOOD = Color.rgb(139, 195, 74);        // Light Green
    public static final Color AVERAGE = Color.rgb(255, 152, 0);      // Orange
    public static final Color POOR = Color.rgb(244, 67, 54);         // Red
    public static final Color INSUFFICIENT = Color.rgb(158, 158, 158); // Gray
    
    // Trend colors
    public static final Color IMPROVING = Color.rgb(33, 150, 243);   // Blue
    public static final Color DECLINING = Color.rgb(156, 39, 176);   // Purple
    public static final Color STABLE = Color.rgb(96, 125, 139);      // Blue Gray
    
    // Comparison colors
    public static final Color PLAYER_1 = Color.rgb(63, 81, 181);     // Indigo
    public static final Color PLAYER_2 = Color.rgb(233, 30, 99);     // Pink
    
    // Depth heatmap
    public static final Color[] DEPTH_GRADIENT = {
        Color.rgb(255, 235, 238),  // Very light red
        Color.rgb(255, 205, 210),
        Color.rgb(239, 154, 154),
        Color.rgb(229, 115, 115),
        Color.rgb(239, 83, 80),
        Color.rgb(244, 67, 54)     // Deep red
    };
}
```

## Core Visualization Components

### 1. Timeline Chart

**Purpose**: Show opening usage and performance over time

#### Features

- **Multiple Series**: Compare multiple openings simultaneously
- **Dual Y-Axis**: Game count (left) and win rate (right)
- **Interactive Legend**: Click to show/hide series
- **Zoom & Pan**: Focus on specific time periods
- **Tooltips**: Detailed stats on hover
- **Annotations**: Mark significant events (tournaments, rating milestones)

#### Component Design

```java
public class EnhancedTimelineChart extends LineChart<String, Number> {
    private TimeGranularity granularity;
    private List<TimelineSeries> series;
    private ChartMode mode;
    
    public enum ChartMode {
        USAGE,          // Game count over time
        WIN_RATE,       // Win percentage over time
        COMBINED        // Both metrics
    }
    
    // Add a series with trend line
    public void addSeries(String name, TimelineData data, boolean showTrend);
    
    // Highlight time period
    public void highlightPeriod(LocalDate start, LocalDate end);
    
    // Add annotation
    public void addAnnotation(LocalDate date, String label, String description);
    
    // Export chart as image
    public BufferedImage exportAsImage(int width, int height);
}
```

#### Visual Example

```
Opening Usage Timeline (2020-2024)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Games                                    Win Rate
100 │                                           │ 70%
    │         ╱╲                                │
 80 │       ╱    ╲         e4                   │ 60%
    │     ╱        ╲      ────                  │
 60 │   ╱            ╲  ╱                       │ 50%
    │ ╱                ╲╱                       │
 40 │                     ╲                     │ 40%
    │                       ╲    d4             │
 20 │                         ╲  ────           │ 30%
    │                           ╲               │
  0 │━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━│ 20%
    2020   2021   2022   2023   2024

Legend: [■ e4] [■ d4] [□ Nf3]  [Show Trend Lines ☑]
```

### 2. Sankey Diagram (Opening Flow)

**Purpose**: Visualize move sequences and branching patterns

#### Features

- **Flow Visualization**: Width represents game count
- **Color Coding**: Performance-based coloring
- **Interactive**: Click to focus on specific branch
- **Depth Control**: Show first N moves
- **Filtering**: Hide low-frequency moves

#### Component Design

```java
public class OpeningSankeyDiagram extends Pane {
    private OpeningTreeNode root;
    private int maxDepth;
    private int minGames;
    
    // Render the diagram
    public void render(OpeningTreeNode tree, int maxDepth);
    
    // Highlight a path
    public void highlightPath(List<String> moves);
    
    // Set minimum game threshold
    public void setMinimumGames(int threshold);
    
    // Export as SVG
    public String exportAsSVG();
}
```

#### Visual Example

```
Opening Flow Diagram (Width = Game Count)

Start ══════════════════════════════════════════════════╗
      ║                                                   ║
      ║  e4 (120 games, 60% win) ═══════════════════╗   ║
      ║                                              ║   ║
      ║                  e5 (80) ═══════════════╗   ║   ║
      ║                                         ║   ║   ║
      ║                            Nf3 (60) ════║   ║   ║
      ║                            Bc4 (20) ════║   ║   ║
      ║                                         ║   ║   ║
      ║                  c5 (40) ═══════════════╝   ║   ║
      ║                                              ║   ║
      ║  d4 (80 games, 55% win) ═══════════════════╝   ║
      ║                                                  ║
      ║  Nf3 (20 games, 45% win) ════════════════════╝  ║
      ║                                                  ║
      ╚══════════════════════════════════════════════════╝

Color: Green (>55%), Orange (45-55%), Red (<45%)
```

### 3. Preparation Depth Heatmap

**Purpose**: Visualize preparation depth across the opening tree

#### Features

- **2D Heatmap**: Rows = first moves, Columns = depth levels
- **Color Intensity**: Darker = more preparation
- **Interactive Cells**: Click to see games at that depth
- **Gap Highlighting**: Outline cells with insufficient preparation
- **Comparison Mode**: Side-by-side heatmaps

#### Component Design

```java
public class DepthHeatmap extends GridPane {
    private OpeningTreeNode tree;
    private int maxDepth;
    
    // Render heatmap
    public void render(OpeningTreeNode tree, int maxDepth);
    
    // Highlight gaps
    public void highlightGaps(List<PreparationGap> gaps);
    
    // Set color scheme
    public void setColorScheme(ColorScheme scheme);
    
    // Export as image
    public BufferedImage exportAsImage();
}
```

#### Visual Example

```
Preparation Depth Heatmap

         Move Depth
First    1-5  6-10 11-15 16-20 21+
Move     
e4       ████ ████ ███░  ██░░  ░░░░  ← 15 moves deep
  e5     ████ ████ ███░  █░░░  ░░░░  ← 14 moves deep
  c5     ████ ███░ ██░░  ░░░░  ░░░░  ← 12 moves deep
d4       ████ ████ ██░░  ░░░░  ░░░░  ← 12 moves deep
  d5     ████ ███░ █░░░  ░░░░  ░░░░  ← 11 moves deep
  Nf6    ████ ██░░ ░░░░  ░░░░  ░░░░  ← 8 moves deep  ⚠ GAP
Nf3      ████ █░░░ ░░░░  ░░░░  ░░░░  ← 6 moves deep  ⚠ GAP

Legend: ████ 100%  ███░ 75%  ██░░ 50%  █░░░ 25%  ░░░░ 0%
        ⚠ = Preparation gap detected
```

### 4. Radial Tree Diagram

**Purpose**: Alternative tree visualization for compact display

#### Features

- **Circular Layout**: Root in center, branches radiate outward
- **Compact**: Fits more information in less space
- **Color Rings**: Performance-based coloring
- **Expandable Sectors**: Click to expand/collapse
- **Search Highlight**: Find and highlight specific moves

#### Component Design

```java
public class RadialTreeDiagram extends Pane {
    private OpeningTreeNode root;
    private double radius;
    private int maxDepth;
    
    // Render radial tree
    public void render(OpeningTreeNode tree, int maxDepth);
    
    // Expand/collapse sector
    public void toggleSector(String move);
    
    // Highlight path
    public void highlightPath(List<String> moves);
    
    // Rotate to focus
    public void rotateTo(String move);
}
```

#### Visual Example

```
Radial Opening Tree (View from above)

                  Nf3
                   │
              ╱────┼────╲
            ╱      │      ╲
          d5      d6       Nc6
         ╱        │         ╲
        ╱         │          ╲
    [Root]────────e4──────────e5
        ╲         │          ╱
         ╲        │         ╱
          c5      Bc4      Nf3
            ╲     │      ╱
              ╲───┼───╱
                  │
                 d4

Color: Green ring = good, Red ring = poor
Size: Thicker lines = more games
```

### 5. Comparison View (Split Screen)

**Purpose**: Side-by-side comparison of two repertoires

#### Features

- **Synchronized Scrolling**: Both trees scroll together
- **Difference Highlighting**: New/abandoned/changed moves
- **Delta Display**: Show change in statistics
- **Merge View**: Combined tree with annotations
- **Export Comparison**: Generate comparison report

#### Component Design

```java
public class ComparisonView extends SplitPane {
    private OpeningTreeView leftTree;
    private OpeningTreeView rightTree;
    private ComparisonResult comparison;
    
    // Load comparison
    public void loadComparison(ComparisonResult result);
    
    // Synchronize navigation
    public void enableSynchronization(boolean enabled);
    
    // Highlight differences
    public void highlightDifferences(DifferenceType... types);
    
    // Switch to merge view
    public void showMergeView();
    
    // Export comparison
    public void exportComparison(File output, ExportFormat format);
}
```

#### Visual Example

```
Comparison View: 2023 vs 2024

2023 Repertoire              2024 Repertoire
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
├── e4 (60%, N=100)      │   ├── e4 (65%, N=120) ↗ +5%
│   ├── e5 (65%, N=60)   │   │   ├── e5 (68%, N=70) ↗ +3%
│   │   ├── Nf3 (70%)    │   │   │   ├── Nf3 (72%) ↗ +2%
│   │   └── Bc4 (55%)    │   │   │   └── Bc4 (60%) ↗ +5%
│   └── c5 (55%, N=40)   │   │   └── c5 (60%, N=50) ↗ +5%
├── d4 (30%, N=50)       │   ├── d4 (25%, N=40) ↘ -5%
│   └── d5 (58%)         │   │   └── d5 (55%) ↘ -3%
└── Nf3 (10%, N=20)      │   └── c4 (10%, N=20) ✨ NEW

Legend: ↗ Improved  ↘ Declined  ✨ New  ⚠ Abandoned
```

### 6. Performance Trend Sparklines

**Purpose**: Inline mini-charts showing trends at a glance

#### Features

- **Inline Display**: Show in tree view next to moves
- **Quick Insight**: Trend direction without opening chart
- **Hover Details**: Expand to full chart on hover
- **Color Coded**: Green (improving), Red (declining)

#### Component Design

```java
public class TrendSparkline extends Canvas {
    private TimelineData data;
    private int width;
    private int height;
    
    // Render sparkline
    public void render(TimelineData data);
    
    // Set dimensions
    public void setDimensions(int width, int height);
    
    // Show tooltip on hover
    public void showTooltip(MouseEvent event);
}
```

#### Visual Example

```
Opening Tree with Sparklines

├── e4 (60%, N=120) [▁▂▃▅▇] ↗ Improving
│   ├── e5 (65%, N=80) [▃▄▅▅▆] → Stable
│   └── c5 (55%, N=40) [▇▅▃▂▁] ↘ Declining
├── d4 (55%, N=80) [▄▄▄▄▄] → Stable
└── Nf3 (45%, N=20) [▆▄▃▂▁] ↘ Declining

Sparkline: ▁▂▃▄▅▆▇ = Win rate over last 5 periods
```

### 7. Opening Popularity Chart

**Purpose**: Show how popular an opening is compared to others

#### Features

- **Bubble Chart**: Size = game count, Color = win rate
- **Quadrant Analysis**: High usage/High win rate quadrants
- **Filtering**: By time period, rating range
- **Comparison**: Your repertoire vs database average

#### Component Design

```java
public class PopularityBubbleChart extends ScatterChart<Number, Number> {
    private List<OpeningData> openings;
    
    // Add opening data
    public void addOpening(String name, int games, double winRate);
    
    // Highlight quadrants
    public void showQuadrants(boolean show);
    
    // Filter by criteria
    public void filter(FilterCriteria criteria);
    
    // Compare to database
    public void compareToDatabase(DatabaseStatistics stats);
}
```

#### Visual Example

```
Opening Popularity (Bubble Chart)

Win Rate
   70% │         ● e4, e5, Nf3
       │       (High usage, High win)
       │
   60% │  ● d4, d5     ● e4, c5
       │ (Med usage,  (High usage,
       │  Good win)    Med win)
   50% │─────────────────────────
       │  ● Nf3, d5
       │ (Low usage,
       │  Low win)
   40% │
       │
       └──────────────────────────
         0    50   100   150  Games

Bubble size = Total games
Color: Green (>55%), Orange (45-55%), Red (<45%)
```

### 8. Move Statistics Panel

**Purpose**: Detailed statistics for selected move

#### Features

- **Comprehensive Stats**: All metrics in one place
- **Historical Context**: How stats changed over time
- **Comparison**: vs Database average, vs Top players
- **Recommendations**: Based on statistics

#### Component Design

```java
public class MoveStatisticsPanel extends VBox {
    private OpeningTreeNode node;
    private Statistics stats;
    
    // Display statistics
    public void displayStats(OpeningTreeNode node);
    
    // Compare to database
    public void compareToDatabase(DatabaseStatistics dbStats);
    
    // Show recommendations
    public void showRecommendations(List<Recommendation> recs);
    
    // Export stats
    public void exportStats(File output);
}
```

#### Visual Layout

```
┌─────────────────────────────────────────────────┐
│ Move Statistics: e4 → e5 → Nf3                 │
├─────────────────────────────────────────────────┤
│ Performance                                     │
│   Win Rate:    65.0% ↗ (+5% vs last year)     │
│   Draw Rate:   20.0% → (stable)                │
│   Loss Rate:   15.0% ↘ (-5% vs last year)     │
│                                                 │
│ Usage                                           │
│   Total Games: 60                               │
│   First Played: 2020-03-15                     │
│   Last Played:  2024-11-10                     │
│   Frequency:    50% of e4 e5 games             │
│                                                 │
│ Opponents                                       │
│   Avg Rating:   1950                           │
│   Strongest:    2200 (Win)                     │
│   Weakest:      1700 (Win)                     │
│                                                 │
│ Preparation                                     │
│   Depth:        13 moves                       │
│   Coverage:     Good ✓                         │
│   Gaps:         None detected                  │
│                                                 │
│ Comparison                                      │
│   vs Database:  +10% (Better than average)    │
│   vs Top 100:   -5% (Below elite level)       │
│                                                 │
│ Recommendations                                 │
│   ✓ Keep playing - performing well            │
│   ⚠ Study move 14 - only 3 games deep         │
│                                                 │
│ [View Games] [Export Stats] [Study Position]   │
└─────────────────────────────────────────────────┘
```

### 9. Dashboard View

**Purpose**: Overview of entire repertoire at a glance

#### Features

- **KPI Cards**: Key metrics (total games, win rate, etc.)
- **Mini Charts**: Sparklines and small visualizations
- **Quick Insights**: Trends, gaps, recommendations
- **Customizable**: User can choose which widgets to show

#### Component Design

```java
public class DashboardView extends GridPane {
    private List<DashboardWidget> widgets;
    
    // Add widget
    public void addWidget(DashboardWidget widget, int col, int row);
    
    // Remove widget
    public void removeWidget(DashboardWidget widget);
    
    // Save layout
    public void saveLayout(File config);
    
    // Load layout
    public void loadLayout(File config);
}

public interface DashboardWidget {
    void refresh();
    Node getNode();
    String getTitle();
}
```

#### Visual Layout

```
┌─────────────────────────────────────────────────────────────┐
│ Repertoire Dashboard                                        │
├─────────────────────────────────────────────────────────────┤
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│ │ Games    │ │ Win Rate │ │ Openings │ │ Avg Depth│       │
│ │   250    │ │   58%    │ │    12    │ │   11.5   │       │
│ │  ↗ +50   │ │  ↗ +3%   │ │  → Same  │ │  ↗ +2    │       │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
│                                                             │
│ ┌─────────────────────────┐ ┌─────────────────────────┐   │
│ │ Most Played Openings    │ │ Performance Trends      │   │
│ │ 1. e4 (45%) [▁▃▅▆▇] ↗   │ │                         │   │
│ │ 2. d4 (35%) [▄▄▄▄▄] →   │ │   60% ╱╲                │   │
│ │ 3. Nf3 (20%) [▇▅▃▂▁] ↘  │ │      ╱  ╲___            │   │
│ └─────────────────────────┘ │   50%                   │   │
│                             │                         │   │
│ ┌─────────────────────────┐ └─────────────────────────┘   │
│ │ Preparation Gaps        │                               │
│ │ ⚠ e4 c5 Nf3 d6 d4       │ ┌─────────────────────────┐   │
│ │   Only 5 moves deep     │ │ Recommendations         │   │
│ │ ⚠ d4 Nf6 c4 e6          │ │ • Study Sicilian deeper │   │
│ │   Only 6 moves deep     │ │ • Review losing lines   │   │
│ └─────────────────────────┘ │ • Add new opening       │   │
│                             └─────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Interactive Features

### Hover Interactions

- **Tree Nodes**: Show tooltip with detailed stats
- **Chart Points**: Show exact values and date
- **Heatmap Cells**: Show game count and depth
- **Sparklines**: Expand to full chart

### Click Interactions

- **Tree Nodes**: Navigate to position, update board
- **Chart Points**: Filter to that time period
- **Heatmap Cells**: Show games at that depth
- **Legend Items**: Toggle series visibility

### Keyboard Shortcuts

```
Arrow Keys:     Navigate tree
Space:          Expand/collapse node
Enter:          View games for position
Ctrl+F:         Find move
Ctrl+C:         Copy position FEN
Ctrl+E:         Export current view
Ctrl+Z:         Undo navigation
Ctrl+Y:         Redo navigation
```

### Context Menus

```
Right-click on tree node:
├── View Games
├── Copy Move
├── Copy FEN
├── Add to Study List
├── Compare with...
├── Show Timeline
├── Find Similar Positions
└── Export Branch
```

## Animation & Transitions

### Smooth Transitions

- **Tree Expansion**: Fade in with slide animation (200ms)
- **Chart Updates**: Smooth line interpolation (300ms)
- **View Switching**: Cross-fade transition (400ms)
- **Highlight Effects**: Pulse animation (500ms)

### Loading States

- **Skeleton Screens**: Show structure while loading
- **Progress Indicators**: For long operations
- **Incremental Rendering**: Show partial results immediately

## Responsive Design

### Adaptive Layouts

- **Small Window**: Single column, collapsible panels
- **Medium Window**: Two columns, side-by-side
- **Large Window**: Three columns, dashboard view
- **Ultra-Wide**: Four columns, comparison mode

### Zoom Levels

- **100%**: Default view
- **125%**: Larger text and controls
- **150%**: Accessibility mode
- **75%**: Compact mode for power users

## Accessibility

### Screen Reader Support

- **ARIA Labels**: All interactive elements labeled
- **Keyboard Navigation**: Full keyboard access
- **Focus Indicators**: Clear focus outlines
- **Announcements**: Status updates announced

### Color Blind Modes

- **Deuteranopia**: Red-green adjusted palette
- **Protanopia**: Red-green adjusted palette
- **Tritanopia**: Blue-yellow adjusted palette
- **High Contrast**: Black and white with patterns

## Export Options

### Image Export

- **PNG**: Raster format, configurable DPI
- **SVG**: Vector format, scalable
- **PDF**: Multi-page reports

### Data Export

- **CSV**: Statistics tables
- **JSON**: Raw data for further analysis
- **PGN**: Filtered games

### Report Export

- **HTML**: Interactive web report
- **PDF**: Printable report with charts
- **Markdown**: Text-based report

## Performance Optimization

### Rendering Optimization

- **Virtual Scrolling**: Only render visible nodes
- **Canvas Rendering**: Use Canvas for complex charts
- **Lazy Loading**: Load chart data on demand
- **Caching**: Cache rendered charts

### Update Optimization

- **Debouncing**: Delay updates during rapid changes
- **Throttling**: Limit update frequency
- **Incremental Updates**: Update only changed parts
- **Background Rendering**: Render in worker thread

## Testing Strategy

### Visual Regression Testing

- **Screenshot Comparison**: Detect unintended changes
- **Cross-Browser**: Test on different browsers
- **Cross-Platform**: Test on Windows, Mac, Linux

### Interaction Testing

- **Click Paths**: Test common user workflows
- **Keyboard Navigation**: Test all shortcuts
- **Touch Gestures**: Test on touch devices

### Performance Testing

- **Render Time**: Measure chart rendering speed
- **Memory Usage**: Monitor memory consumption
- **Frame Rate**: Ensure smooth animations (60fps)

## Conclusion

V2 visualization components provide:

- **Rich Insights**: Multiple chart types for different perspectives
- **Interactive Exploration**: Click, hover, zoom for details
- **Comparative Analysis**: Side-by-side and overlay comparisons
- **Professional Quality**: Publication-ready exports
- **Accessible**: Works for all users including those with disabilities

These visualizations transform raw data into actionable insights, making it easy to understand opening repertoire evolution and identify areas for improvement.
