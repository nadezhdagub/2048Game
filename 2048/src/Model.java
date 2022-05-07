package src;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
    public int score;
    public int maxTile;
    private boolean isSaveNeeded = true;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {
        resetGameTiles();
    }

    public void autoMove() {
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.offer(getMoveEfficiency(this::left));
        queue.offer(getMoveEfficiency(this::right));
        queue.offer(getMoveEfficiency(this::up));
        queue.offer(getMoveEfficiency(this::down));
        queue.peek().getMove().move();
    }

    public boolean hasBoardChanged() {
        if (previousStates.isEmpty()) return false;
        Tile[][] gameTilesPrev = previousStates.peek();
        int dim = gameTiles.length;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (gameTiles[i][j].value != gameTilesPrev[i][j].value) return true;
            }
        }
        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency moveEfficiency;
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        else {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        }
        rollback();
        return moveEfficiency;
    }

    private void saveState(Tile[][] gameTiles) {
        Tile[][] gameTilesSaved = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTilesSaved.length; i++) {
            for (int j = 0; j < gameTilesSaved[i].length; j++) {
                gameTilesSaved[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(gameTilesSaved);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public boolean canMove() {
        int dim = gameTiles.length;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (gameTiles[i][j].isEmpty()) return true;
                else if (i < dim - 1 && gameTiles[i][j].value == gameTiles[i + 1][j].value) return true;
                else if (j < dim - 1 && gameTiles[i][j].value == gameTiles[i][j + 1].value) return true;
            }
        }
        return false;
    }

    private Tile[][] turnToRight(Tile[][] array) {
        Tile[][] resultArray = new Tile[array[0].length][array.length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                resultArray[j][array.length - i - 1] = array[i][j];
            }
        }
        return resultArray;
    }

    private Tile[][] turnToLeft(Tile[][] array) {
        Tile[][] resultArray = new Tile[array[0].length][array.length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                resultArray[array[i].length - j - 1][i] = array[i][j];
            }
        }
        return resultArray;
    }

    public void left() {
        if (isSaveNeeded) saveState(gameTiles);
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if (moveFlag) {
            addTile();
        }
        isSaveNeeded = true;
    }

    public void right() {
        saveState(gameTiles);
        isSaveNeeded = false;
        gameTiles = turnToLeft(gameTiles);
        gameTiles = turnToLeft(gameTiles);
        left();
        gameTiles = turnToLeft(gameTiles);
        gameTiles = turnToLeft(gameTiles);
    }

    public void up() {
        saveState(gameTiles);
        isSaveNeeded = false;
        gameTiles = turnToLeft(gameTiles);
        left();
        gameTiles = turnToRight(gameTiles);
    }

    public void down() {
        saveState(gameTiles);
        isSaveNeeded = false;
        gameTiles = turnToRight(gameTiles);
        left();
        gameTiles = turnToLeft(gameTiles);
    }

    private boolean compressTiles(Tile[] tiles) {
        Tile[] temp = Arrays.copyOf(tiles, tiles.length);
        Arrays.sort(tiles, Comparator.comparing(Tile::isEmpty));
        return !Arrays.equals(tiles, temp);
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean flag = false;
        for (int i = 1; i < tiles.length; i++) {
            Tile tile1 = tiles[i - 1];
            Tile tile2 = tiles[i];
            if (tile2.value != 0 && tile2.value == tile1.value) {
                tile2.value = 0;
                tile1.value *= 2;
                if (tile1.value > maxTile) maxTile = tile1.value;
                score += tile1.value;
                flag = true;
            }
        }
        compressTiles(tiles);
        return flag;
    }

    public void resetGameTiles() {
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private void addTile() {
        List<Tile> tiles = getEmptyTiles();
        if (!tiles.isEmpty()) {
            Tile tile = tiles.get((int) (Math.random() * tiles.size()));
            tile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> result = new ArrayList<>();
        for (Tile[] tiles : gameTiles) {
            for (Tile tile : tiles) {
                if (tile.isEmpty()) result.add(tile);
            }
        }
        return result;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }
}
