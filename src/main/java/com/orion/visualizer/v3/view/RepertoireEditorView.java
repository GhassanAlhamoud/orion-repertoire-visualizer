package com.orion.visualizer.v3.view;

import com.orion.visualizer.v3.model.RepertoireNode;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Main view for V3 Repertoire Editor mode.
 */
public class RepertoireEditorView extends BorderPane {
    private final AnnotatedChessboardView chessboardView;
    private final RepertoireTreeView treeView;
    private final CommentEditorView commentEditor;
    private final ToolBar toolbar;
    
    public RepertoireEditorView() {
        // Initialize components
        this.chessboardView = new AnnotatedChessboardView();
        this.treeView = new RepertoireTreeView();
        this.commentEditor = new CommentEditorView();
        this.toolbar = createToolbar();
        
        // Layout
        setupLayout();
    }
    
    /**
     * Set up the layout.
     */
    private void setupLayout() {
        // Left panel: Chessboard
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(500);
        leftPanel.getChildren().add(chessboardView);
        
        // Right panel: Tree and comment editor
        SplitPane rightPanel = new SplitPane();
        rightPanel.setOrientation(Orientation.VERTICAL);
        rightPanel.getItems().addAll(treeView, commentEditor);
        rightPanel.setDividerPositions(0.6);
        
        // Main split
        SplitPane mainSplit = new SplitPane();
        mainSplit.getItems().addAll(leftPanel, rightPanel);
        mainSplit.setDividerPositions(0.5);
        
        setTop(toolbar);
        setCenter(mainSplit);
    }
    
    /**
     * Create toolbar with buttons and controls.
     */
    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();
        
        // File operations
        Button newBtn = new Button("New");
        Button openBtn = new Button("Open");
        Button saveBtn = new Button("Save");
        
        // NAG buttons
        Button goodBtn = new Button("!");
        Button brilliantBtn = new Button("!!");
        Button mistakeBtn = new Button("?");
        Button blunderBtn = new Button("??");
        
        // Color buttons for annotations
        Button redBtn = new Button("🔴");
        Button greenBtn = new Button("🟢");
        Button yellowBtn = new Button("🟡");
        Button blueBtn = new Button("🔵");
        
        toolbar.getItems().addAll(
            newBtn, openBtn, saveBtn,
            new Separator(),
            goodBtn, brilliantBtn, mistakeBtn, blunderBtn,
            new Separator(),
            redBtn, greenBtn, yellowBtn, blueBtn
        );
        
        return toolbar;
    }
    
    public AnnotatedChessboardView getChessboardView() {
        return chessboardView;
    }
    
    public RepertoireTreeView getTreeView() {
        return treeView;
    }
    
    public CommentEditorView getCommentEditor() {
        return commentEditor;
    }
    
    public ToolBar getToolbar() {
        return toolbar;
    }
}
