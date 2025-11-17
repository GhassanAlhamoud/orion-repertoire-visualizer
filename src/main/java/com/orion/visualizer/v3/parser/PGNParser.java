package com.orion.visualizer.v3.parser;

import com.orion.visualizer.v3.model.RepertoireNode;
import com.orion.visualizer.v3.model.RepertoireTree;
import com.orion.visualizer.v3.model.VisualAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simplified PGN parser for repertoire files with visual annotations.
 */
public class PGNParser {
    private static final Pattern HEADER_PATTERN = Pattern.compile("\\[([A-Za-z]+)\\s+\"([^\"]*)]\"\\]");
    private static final Pattern NAG_PATTERN = Pattern.compile("\\$([0-9]+)");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("\\{([^}]*)\\}");
    private static final Pattern CSL_PATTERN = Pattern.compile("\\[%csl\\s+([^\\]]+)\\]");
    private static final Pattern CAL_PATTERN = Pattern.compile("\\[%cal\\s+([^\\]]+)\\]");
    
    /**
     * Parse PGN string into RepertoireTree.
     */
    public RepertoireTree parse(String pgn) throws IOException {
        RepertoireTree tree = new RepertoireTree();
        
        BufferedReader reader = new BufferedReader(new StringReader(pgn));
        
        // Parse headers
        Map<String, String> headers = parseHeaders(reader);
        tree.setHeaders(headers);
        
        // Parse moves
        StringBuilder movesText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty() && !line.startsWith("[")) {
                movesText.append(line).append(" ");
            }
        }
        
        if (movesText.length() > 0) {
            parseMoves(tree, movesText.toString());
        }
        
        tree.setModified(false);
        return tree;
    }
    
    /**
     * Parse PGN headers.
     */
    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (!line.startsWith("[")) break;
            
            Matcher matcher = HEADER_PATTERN.matcher(line);
            if (matcher.find()) {
                headers.put(matcher.group(1), matcher.group(2));
            }
        }
        
        return headers;
    }
    
    /**
     * Parse move text (simplified version).
     */
    private void parseMoves(RepertoireTree tree, String movesText) {
        // Remove result marker
        movesText = movesText.replaceAll("\\s+[01/\\*-]+\\s*$", "");
        
        // This is a simplified parser - in production, use a proper lexer/parser
        // For now, we'll just demonstrate the structure
        
        // TODO: Implement full move parsing with variations
        // This would require a proper recursive descent parser
    }
    
    /**
     * Generate PGN string from RepertoireTree.
     */
    public String generate(RepertoireTree tree) {
        StringBuilder pgn = new StringBuilder();
        
        // Generate headers
        Map<String, String> headers = tree.getHeaders();
        String[] headerOrder = {"Event", "Site", "Date", "Round", "White", "Black", "Result"};
        
        for (String key : headerOrder) {
            if (headers.containsKey(key)) {
                pgn.append(String.format("[%s \"%s\"]%n", key, headers.get(key)));
            }
        }
        
        // Add other headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            boolean isStandard = false;
            for (String key : headerOrder) {
                if (key.equals(entry.getKey())) {
                    isStandard = true;
                    break;
                }
            }
            if (!isStandard) {
                pgn.append(String.format("[%s \"%s\"]%n", entry.getKey(), entry.getValue()));
            }
        }
        
        pgn.append("\n");
        
        // Generate moves
        generateMoves(tree.getRoot(), pgn, 1, true);
        
        // Add result
        String result = headers.getOrDefault("Result", "*");
        pgn.append(" ").append(result).append("\n");
        
        return pgn.toString();
    }
    
    /**
     * Generate moves recursively.
     */
    private void generateMoves(RepertoireNode node, StringBuilder pgn, 
                              int moveNumber, boolean isWhite) {
        for (int i = 0; i < node.getChildren().size(); i++) {
            RepertoireNode child = node.getChildren().get(i);
            boolean isMainLine = (i == 0);
            
            if (!isMainLine) {
                pgn.append(" (");
            }
            
            // Add move number
            if (isWhite) {
                pgn.append(moveNumber).append(". ");
            } else if (!isMainLine || i == 0) {
                pgn.append(moveNumber).append("... ");
            }
            
            // Add move
            pgn.append(child.getMove());
            
            // Add NAGs
            for (int nag : child.getNags()) {
                pgn.append(" $").append(nag);
            }
            
            // Add comment with visual annotations
            if (child.hasComment() || child.hasVisualAnnotations()) {
                pgn.append(" {");
                
                // Add visual annotations
                if (child.hasVisualAnnotations()) {
                    VisualAnnotations va = child.getVisualAnnotations();
                    if (va.hasHighlights()) {
                        pgn.append(va.generateCslCommand()).append(" ");
                    }
                    if (va.hasArrows()) {
                        pgn.append(va.generateCalCommand()).append(" ");
                    }
                }
                
                // Add text comment
                if (child.hasComment()) {
                    pgn.append(child.getComment());
                }
                
                pgn.append("}");
            }
            
            // Recurse for children
            if (!child.isLeaf()) {
                pgn.append(" ");
                generateMoves(child, pgn, 
                    isWhite ? moveNumber : moveNumber + 1, 
                    !isWhite);
            }
            
            if (!isMainLine) {
                pgn.append(")");
            }
        }
    }
    
    /**
     * Parse visual annotation from comment.
     */
    public static VisualAnnotations parseVisualAnnotations(String comment) {
        VisualAnnotations annotations = new VisualAnnotations();
        
        // Parse [%csl] commands
        Matcher cslMatcher = CSL_PATTERN.matcher(comment);
        while (cslMatcher.find()) {
            annotations.parseCslCommand("[%csl " + cslMatcher.group(1) + "]");
        }
        
        // Parse [%cal] commands
        Matcher calMatcher = CAL_PATTERN.matcher(comment);
        while (calMatcher.find()) {
            annotations.parseCalCommand("[%cal " + calMatcher.group(1) + "]");
        }
        
        return annotations;
    }
    
    /**
     * Remove visual annotations from comment text.
     */
    public static String stripVisualAnnotations(String comment) {
        String result = comment;
        result = CSL_PATTERN.matcher(result).replaceAll("");
        result = CAL_PATTERN.matcher(result).replaceAll("");
        return result.trim();
    }
}
