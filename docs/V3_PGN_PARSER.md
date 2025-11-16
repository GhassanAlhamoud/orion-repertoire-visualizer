# V3 PGN Parser Design

## Overview

This document specifies the design and implementation of the PGN parser for V3, with full support for visual annotations (`[%csl]` and `[%cal]` commands). The parser must handle standard PGN elements plus ChessMaster-style visual extensions.

## PGN Format Specification

### Standard PGN Elements

**Headers**:
```pgn
[Event "My Repertoire"]
[Site "?"]
[Date "2024.11.17"]
[White "?"]
[Black "?"]
[Result "*"]
```

**Moves and Variations**:
```pgn
1. e4 e5 2. Nf3 Nc6 3. Bb5 (3. Bc4 Bc5 4. O-O) 3... a6
```

**Comments**:
```pgn
1. e4 {This is my main move} e5
```

**NAGs** (Numeric Annotation Glyphs):
```pgn
1. e4 $1 {Good move} e5 $2 {Mistake}
```

**Common NAGs**:
- `$1` = `!` (Good move)
- `$2` = `?` (Mistake)
- `$3` = `!!` (Brilliant move)
- `$4` = `??` (Blunder)
- `$5` = `!?` (Interesting move)
- `$6` = `?!` (Dubious move)

### Visual Annotation Extensions

**Square Highlighting** (`[%csl]`):
```pgn
1. e4 {[%csl Rd4,Ge5,Yd5] Red d4, green e5, yellow d5}
```

**Format**: `[%csl <color><square>,<color><square>,...]`
- Colors: `R` (red), `G` (green), `Y` (yellow), `B` (blue)
- Squares: `a1` to `h8` (lowercase)

**Arrow Drawing** (`[%cal]`):
```pgn
1. e4 {[%cal Gd2d4,Re7e5] Green arrow d2-d4, red arrow e7-e5}
```

**Format**: `[%cal <color><from><to>,<color><from><to>,...]`
- Colors: `R` (red), `G` (green), `Y` (yellow), `B` (blue)
- Squares: `a1` to `h8` (lowercase)
- From/To: Concatenated (e.g., `d2d4` = d2 to d4)

**Combined**:
```pgn
1. e4 {[%csl Rd4] [%cal Gd2d4] Red square on d4, green arrow d2-d4}
```

### Complete Example

```pgn
[Event "My White Repertoire"]
[Site "?"]
[Date "2024.11.17"]
[White "Me"]
[Black "?"]
[Result "*"]

1. e4 $1 {[%csl Ge4] [%cal Ge2e4] My main move - controls center} e5 
(1... c5 {[%csl Rc5] [%cal Rc7c5] Sicilian - need to study more} 2. Nf3 
d6 3. d4 cxd4 4. Nxd4 {[%csl Gd4,Ge4] Strong center}) 
2. Nf3 {[%csl Gf3] [%cal Gg1f3,Gf3e5] Attacks e5} Nc6 
3. Bb5 $3 {[%csl Gb5] [%cal Gf1b5,Gb5c6] Ruy Lopez - my main weapon!!} a6 
4. Ba4 {[%csl Ga4] [%cal Gb5a4] Maintaining pressure} *
```

## Parser Architecture

### Tokenization

**Token Types**:
```java
public enum TokenType {
    HEADER,           // [Event "..."]
    MOVE_NUMBER,      // 1.
    MOVE,             // e4
    NAG,              // $1 or !
    COMMENT,          // {text}
    VARIATION_START,  // (
    VARIATION_END,    // )
    RESULT,           // * or 1-0 or 0-1 or 1/2-1/2
    EOF               // End of file
}
```

**Token Class**:
```java
public class Token {
    private TokenType type;
    private String value;
    private int position;  // Character position in source
    
    public Token(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }
    
    // Getters
    public TokenType getType() { return type; }
    public String getValue() { return value; }
    public int getPosition() { return position; }
}
```

### Lexer (Tokenizer)

```java
/**
 * Lexical analyzer for PGN.
 * Converts PGN text into tokens.
 */
public class PGNLexer {
    private String input;
    private int position;
    private List<Token> tokens;
    
    public PGNLexer(String input) {
        this.input = input;
        this.position = 0;
        this.tokens = new ArrayList<>();
    }
    
    /**
     * Tokenize the input.
     */
    public List<Token> tokenize() throws PGNParseException {
        while (position < input.length()) {
            char c = input.charAt(position);
            
            // Skip whitespace
            if (Character.isWhitespace(c)) {
                position++;
                continue;
            }
            
            // Header: [Event "..."]
            if (c == '[') {
                tokenizeHeader();
            }
            // Comment: {text}
            else if (c == '{') {
                tokenizeComment();
            }
            // Variation start: (
            else if (c == '(') {
                tokens.add(new Token(TokenType.VARIATION_START, "(", position));
                position++;
            }
            // Variation end: )
            else if (c == ')') {
                tokens.add(new Token(TokenType.VARIATION_END, ")", position));
                position++;
            }
            // NAG: $1, $2, etc.
            else if (c == '$') {
                tokenizeNAG();
            }
            // Symbol NAG: !, ?, !!, ??, !?, ?!
            else if (c == '!' || c == '?') {
                tokenizeSymbolNAG();
            }
            // Move number: 1. or 1...
            else if (Character.isDigit(c)) {
                tokenizeMoveNumber();
            }
            // Result: * or 1-0 or 0-1 or 1/2-1/2
            else if (c == '*' || c == '1' || c == '0') {
                if (isResult()) {
                    tokenizeResult();
                } else {
                    tokenizeMove();
                }
            }
            // Move: e4, Nf3, etc.
            else if (Character.isLetter(c)) {
                tokenizeMove();
            }
            else {
                throw new PGNParseException("Unexpected character: " + c + " at position " + position);
            }
        }
        
        tokens.add(new Token(TokenType.EOF, "", position));
        return tokens;
    }
    
    /**
     * Tokenize header: [Event "My Repertoire"]
     */
    private void tokenizeHeader() throws PGNParseException {
        int start = position;
        position++;  // Skip '['
        
        // Find closing ']'
        while (position < input.length() && input.charAt(position) != ']') {
            position++;
        }
        
        if (position >= input.length()) {
            throw new PGNParseException("Unclosed header at position " + start);
        }
        
        position++;  // Skip ']'
        String headerText = input.substring(start, position);
        tokens.add(new Token(TokenType.HEADER, headerText, start));
    }
    
    /**
     * Tokenize comment: {This is a comment}
     */
    private void tokenizeComment() throws PGNParseException {
        int start = position;
        position++;  // Skip '{'
        
        // Find closing '}'
        while (position < input.length() && input.charAt(position) != '}') {
            position++;
        }
        
        if (position >= input.length()) {
            throw new PGNParseException("Unclosed comment at position " + start);
        }
        
        position++;  // Skip '}'
        String commentText = input.substring(start + 1, position - 1);
        tokens.add(new Token(TokenType.COMMENT, commentText, start));
    }
    
    /**
     * Tokenize NAG: $1, $2, etc.
     */
    private void tokenizeNAG() {
        int start = position;
        position++;  // Skip '$'
        
        // Read digits
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        
        String nagText = input.substring(start, position);
        tokens.add(new Token(TokenType.NAG, nagText, start));
    }
    
    /**
     * Tokenize symbol NAG: !, ?, !!, ??, !?, ?!
     */
    private void tokenizeSymbolNAG() {
        int start = position;
        char c1 = input.charAt(position);
        position++;
        
        // Check for double symbol
        if (position < input.length()) {
            char c2 = input.charAt(position);
            if ((c1 == '!' && c2 == '!') || (c1 == '?' && c2 == '?') ||
                (c1 == '!' && c2 == '?') || (c1 == '?' && c2 == '!')) {
                position++;
                tokens.add(new Token(TokenType.NAG, "" + c1 + c2, start));
                return;
            }
        }
        
        tokens.add(new Token(TokenType.NAG, "" + c1, start));
    }
    
    /**
     * Tokenize move number: 1. or 1...
     */
    private void tokenizeMoveNumber() {
        int start = position;
        
        // Read digits
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        
        // Read dots
        while (position < input.length() && input.charAt(position) == '.') {
            position++;
        }
        
        String moveNumText = input.substring(start, position);
        tokens.add(new Token(TokenType.MOVE_NUMBER, moveNumText, start));
    }
    
    /**
     * Tokenize move: e4, Nf3, O-O, etc.
     */
    private void tokenizeMove() {
        int start = position;
        
        // Read move characters (letters, digits, +, #, =, -, x)
        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isLetterOrDigit(c) || c == '+' || c == '#' || 
                c == '=' || c == '-' || c == 'x') {
                position++;
            } else {
                break;
            }
        }
        
        String moveText = input.substring(start, position);
        tokens.add(new Token(TokenType.MOVE, moveText, start));
    }
    
    /**
     * Tokenize result: * or 1-0 or 0-1 or 1/2-1/2
     */
    private void tokenizeResult() {
        int start = position;
        
        if (input.charAt(position) == '*') {
            position++;
            tokens.add(new Token(TokenType.RESULT, "*", start));
        } else {
            // Read until whitespace
            while (position < input.length() && !Character.isWhitespace(input.charAt(position))) {
                position++;
            }
            String resultText = input.substring(start, position);
            tokens.add(new Token(TokenType.RESULT, resultText, start));
        }
    }
    
    /**
     * Check if current position is a result.
     */
    private boolean isResult() {
        if (input.charAt(position) == '*') return true;
        
        // Check for 1-0, 0-1, 1/2-1/2
        String remaining = input.substring(position);
        return remaining.startsWith("1-0") || 
               remaining.startsWith("0-1") || 
               remaining.startsWith("1/2-1/2");
    }
}
```

### Parser

```java
/**
 * Recursive descent parser for PGN.
 * Builds RepertoireTree from tokens.
 */
public class PGNParser {
    private List<Token> tokens;
    private int position;
    private ChessEngineService chessEngine;
    
    public PGNParser(ChessEngineService chessEngine) {
        this.chessEngine = chessEngine;
    }
    
    /**
     * Parse PGN text into RepertoireTree.
     */
    public RepertoireTree parse(String pgnText) throws PGNParseException {
        // Tokenize
        PGNLexer lexer = new PGNLexer(pgnText);
        this.tokens = lexer.tokenize();
        this.position = 0;
        
        // Create tree
        RepertoireTree tree = new RepertoireTree();
        
        // Parse headers
        while (peek().getType() == TokenType.HEADER) {
            parseHeader(tree);
        }
        
        // Parse moves
        String startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        tree.getRoot().setFen(startFen);
        parseMoveSequence(tree.getRoot(), startFen, true);
        
        return tree;
    }
    
    /**
     * Parse header: [Event "My Repertoire"]
     */
    private void parseHeader(RepertoireTree tree) throws PGNParseException {
        Token token = consume(TokenType.HEADER);
        String headerText = token.getValue();
        
        // Extract key and value
        Pattern pattern = Pattern.compile("\\[([^\\s]+)\\s+\"([^\"]+)\"\\]");
        Matcher matcher = pattern.matcher(headerText);
        
        if (matcher.matches()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            tree.setHeader(key, value);
        } else {
            throw new PGNParseException("Invalid header format: " + headerText);
        }
    }
    
    /**
     * Parse move sequence (handles variations).
     */
    private void parseMoveSequence(RepertoireNode parent, 
                                   String currentFen, 
                                   boolean isMainLine) throws PGNParseException {
        while (position < tokens.size()) {
            Token token = peek();
            
            // End of variation
            if (token.getType() == TokenType.VARIATION_END || 
                token.getType() == TokenType.EOF) {
                break;
            }
            
            // Skip move numbers
            if (token.getType() == TokenType.MOVE_NUMBER) {
                consume(TokenType.MOVE_NUMBER);
                continue;
            }
            
            // Parse move
            if (token.getType() == TokenType.MOVE) {
                RepertoireNode node = parseMove(parent, currentFen, isMainLine);
                currentFen = node.getFen();
                parent = node;
                isMainLine = true;  // After first move, continue as main line
            }
            // Parse variation
            else if (token.getType() == TokenType.VARIATION_START) {
                consume(TokenType.VARIATION_START);
                parseMoveSequence(parent, currentFen, false);
                consume(TokenType.VARIATION_END);
            }
            // Parse result
            else if (token.getType() == TokenType.RESULT) {
                consume(TokenType.RESULT);
                break;
            }
            else {
                throw new PGNParseException("Unexpected token: " + token.getType());
            }
        }
    }
    
    /**
     * Parse a single move with annotations.
     */
    private RepertoireNode parseMove(RepertoireNode parent, 
                                     String currentFen, 
                                     boolean isMainLine) throws PGNParseException {
        // Get move
        Token moveToken = consume(TokenType.MOVE);
        String moveSAN = moveToken.getValue();
        
        // Validate move
        if (!chessEngine.isLegalMove(currentFen, moveSAN)) {
            throw new PGNParseException("Illegal move: " + moveSAN + " at position " + currentFen);
        }
        
        // Create node
        RepertoireNode node = parent.addChild(moveSAN, isMainLine);
        
        // Calculate new position
        String newFen = chessEngine.makeMove(currentFen, moveSAN);
        node.setFen(newFen);
        
        // Parse NAGs
        while (peek().getType() == TokenType.NAG) {
            Token nagToken = consume(TokenType.NAG);
            int nag = parseNAGValue(nagToken.getValue());
            node.getNags().add(nag);
        }
        
        // Parse comment (with visual annotations)
        if (peek().getType() == TokenType.COMMENT) {
            Token commentToken = consume(TokenType.COMMENT);
            parseComment(node, commentToken.getValue());
        }
        
        return node;
    }
    
    /**
     * Parse comment and extract visual annotations.
     */
    private void parseComment(RepertoireNode node, String commentText) {
        // Extract visual annotations
        String textComment = commentText;
        
        // Parse [%csl] commands
        Pattern cslPattern = Pattern.compile("\\[%csl\\s+([^\\]]+)\\]");
        Matcher cslMatcher = cslPattern.matcher(commentText);
        while (cslMatcher.find()) {
            String cslContent = cslMatcher.group(1);
            parseCSLCommand(node, cslContent);
            
            // Remove from text comment
            textComment = textComment.replace(cslMatcher.group(0), "");
        }
        
        // Parse [%cal] commands
        Pattern calPattern = Pattern.compile("\\[%cal\\s+([^\\]]+)\\]");
        Matcher calMatcher = calPattern.matcher(commentText);
        while (calMatcher.find()) {
            String calContent = calMatcher.group(1);
            parseCALCommand(node, calContent);
            
            // Remove from text comment
            textComment = textComment.replace(calMatcher.group(0), "");
        }
        
        // Set text comment (without visual annotations)
        node.setComment(textComment.trim());
    }
    
    /**
     * Parse [%csl] command.
     */
    private void parseCSLCommand(RepertoireNode node, String cslContent) {
        String[] parts = cslContent.split(",");
        
        for (String part : parts) {
            part = part.trim();
            if (part.length() >= 3) {
                char colorCode = part.charAt(0);
                String square = part.substring(1, 3);
                HighlightColor color = HighlightColor.fromCode(colorCode);
                node.getVisualAnnotations().addHighlight(square, color);
            }
        }
    }
    
    /**
     * Parse [%cal] command.
     */
    private void parseCALCommand(RepertoireNode node, String calContent) {
        String[] parts = calContent.split(",");
        
        for (String part : parts) {
            part = part.trim();
            if (part.length() >= 5) {
                char colorCode = part.charAt(0);
                String from = part.substring(1, 3);
                String to = part.substring(3, 5);
                ArrowColor color = ArrowColor.fromCode(colorCode);
                node.getVisualAnnotations().addArrow(from, to, color);
            }
        }
    }
    
    /**
     * Parse NAG value from token.
     */
    private int parseNAGValue(String nagText) {
        // Symbol NAGs
        if (nagText.equals("!")) return 1;
        if (nagText.equals("?")) return 2;
        if (nagText.equals("!!")) return 3;
        if (nagText.equals("??")) return 4;
        if (nagText.equals("!?")) return 5;
        if (nagText.equals("?!")) return 6;
        
        // Numeric NAGs
        if (nagText.startsWith("$")) {
            return Integer.parseInt(nagText.substring(1));
        }
        
        return 0;  // Unknown
    }
    
    /**
     * Peek at current token without consuming.
     */
    private Token peek() {
        if (position >= tokens.size()) {
            return tokens.get(tokens.size() - 1);  // EOF
        }
        return tokens.get(position);
    }
    
    /**
     * Consume current token and advance.
     */
    private Token consume(TokenType expected) throws PGNParseException {
        Token token = peek();
        if (token.getType() != expected) {
            throw new PGNParseException("Expected " + expected + " but got " + token.getType());
        }
        position++;
        return token;
    }
}
```

### Generator

```java
/**
 * Generate PGN text from RepertoireTree.
 */
public class PGNGenerator {
    
    /**
     * Generate PGN text from tree.
     */
    public String generate(RepertoireTree tree) {
        StringBuilder pgn = new StringBuilder();
        
        // Generate headers
        for (Map.Entry<String, String> entry : tree.getHeaders().entrySet()) {
            pgn.append("[")
               .append(entry.getKey())
               .append(" \"")
               .append(escapeString(entry.getValue()))
               .append("\"]\n");
        }
        
        pgn.append("\n");
        
        // Generate moves
        GenerateContext context = new GenerateContext();
        generateMoveSequence(tree.getRoot(), pgn, context);
        
        // Add result
        String result = tree.getHeaders().getOrDefault("Result", "*");
        pgn.append(" ").append(result);
        
        return pgn.toString();
    }
    
    /**
     * Generate move sequence recursively.
     */
    private void generateMoveSequence(RepertoireNode node, 
                                     StringBuilder pgn, 
                                     GenerateContext context) {
        for (RepertoireNode child : node.getChildren()) {
            // Generate move number
            if (child.isWhiteMove()) {
                pgn.append(child.getMoveNumber()).append(". ");
            } else if (context.needsBlackMoveNumber()) {
                pgn.append(child.getMoveNumber()).append("... ");
            }
            
            // Generate move
            pgn.append(child.getMove());
            
            // Generate NAGs
            for (int nag : child.getNags()) {
                pgn.append(" ").append(formatNAG(nag));
            }
            
            // Generate comment with visual annotations
            String comment = generateComment(child);
            if (!comment.isEmpty()) {
                pgn.append(" {").append(comment).append("}");
            }
            
            pgn.append(" ");
            
            // Generate variations (non-main-line children)
            List<RepertoireNode> variations = child.getVariations();
            for (RepertoireNode variation : variations) {
                pgn.append("(");
                GenerateContext varContext = new GenerateContext();
                varContext.setNeedsBlackMoveNumber(!variation.isWhiteMove());
                generateMoveSequence(child, pgn, varContext);
                pgn.append(") ");
            }
            
            // Continue with main line
            if (child.isMainLine()) {
                context.setNeedsBlackMoveNumber(false);
                generateMoveSequence(child, pgn, context);
            }
        }
    }
    
    /**
     * Generate comment with visual annotations.
     */
    private String generateComment(RepertoireNode node) {
        StringBuilder comment = new StringBuilder();
        
        // Add visual annotations
        VisualAnnotations annotations = node.getVisualAnnotations();
        
        String csl = annotations.toCSLCommand();
        if (!csl.isEmpty()) {
            comment.append(csl).append(" ");
        }
        
        String cal = annotations.toCALCommand();
        if (!cal.isEmpty()) {
            comment.append(cal).append(" ");
        }
        
        // Add text comment
        if (node.getComment() != null && !node.getComment().isEmpty()) {
            comment.append(node.getComment());
        }
        
        return comment.toString().trim();
    }
    
    /**
     * Format NAG as symbol or number.
     */
    private String formatNAG(int nag) {
        switch (nag) {
            case 1: return "!";
            case 2: return "?";
            case 3: return "!!";
            case 4: return "??";
            case 5: return "!?";
            case 6: return "?!";
            default: return "$" + nag;
        }
    }
    
    /**
     * Escape special characters in string.
     */
    private String escapeString(String s) {
        return s.replace("\"", "\\\"")
                .replace("\\", "\\\\");
    }
}

/**
 * Context for PGN generation.
 */
class GenerateContext {
    private boolean needsBlackMoveNumber = false;
    
    public boolean needsBlackMoveNumber() {
        return needsBlackMoveNumber;
    }
    
    public void setNeedsBlackMoveNumber(boolean needs) {
        this.needsBlackMoveNumber = needs;
    }
}
```

## Testing Strategy

### Unit Tests

**Lexer Tests**:
- Test tokenization of headers
- Test tokenization of moves
- Test tokenization of comments
- Test tokenization of NAGs
- Test tokenization of variations
- Test tokenization of visual annotations

**Parser Tests**:
- Test parsing of simple games
- Test parsing of variations
- Test parsing of comments
- Test parsing of NAGs
- Test parsing of visual annotations
- Test error handling

**Generator Tests**:
- Test generation of headers
- Test generation of moves
- Test generation of variations
- Test generation of comments
- Test generation of NAGs
- Test generation of visual annotations

### Integration Tests

**Round-trip Tests**:
- Parse PGN → Generate PGN → Compare
- Ensure no data loss
- Ensure format consistency

**Compatibility Tests**:
- Test with real PGN files from ChessBase
- Test with PGN files from Lichess
- Test with PGN files from Chess.com
- Ensure compatibility

### Performance Tests

**Large File Tests**:
- Test parsing of 1000-move repertoire
- Test generation of 1000-move repertoire
- Measure time and memory usage

**Target Performance**:
- Parse 1000 moves in <1 second
- Generate 1000 moves in <500ms
- Memory usage <50MB for 1000 moves

## Error Handling

### Parse Errors

**Common Errors**:
- Unclosed header
- Unclosed comment
- Illegal move
- Invalid square name
- Invalid color code
- Unexpected token

**Error Messages**:
```
PGNParseException: Illegal move 'Nf7' at position 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
  at line 5, column 12
  
  1. e4 e5 2. Nf3 Nc6 3. Nf7
                         ^^^
```

### Recovery Strategies

**Lenient Parsing**:
- Skip invalid visual annotations (but keep text comment)
- Ignore invalid NAGs
- Continue parsing after non-critical errors

**Strict Parsing** (default):
- Stop on first error
- Report exact location
- Provide helpful error message

## Conclusion

The PGN parser is a critical component of V3. It must:

1. **Parse** standard PGN and visual annotations
2. **Generate** valid PGN with visual annotations
3. **Validate** moves and positions
4. **Handle** errors gracefully
5. **Perform** efficiently on large files

The parser design uses:
- **Lexer** for tokenization
- **Recursive descent parser** for tree building
- **Generator** for PGN output
- **Comprehensive testing** for reliability

---

**Next Steps**:
1. Implement lexer
2. Implement parser
3. Implement generator
4. Write unit tests
5. Test with real PGN files

---

**Document Version**: 1.0  
**Status**: Draft  
**Last Updated**: November 2024
