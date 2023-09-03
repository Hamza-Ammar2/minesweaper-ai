import java.util.ArrayList;
import java.util.*;

public class Minesweeper {
    List<Sentence> sentences = new ArrayList<>();
    List<int[]> madeMoves = new ArrayList<>();
    List<int[]> safecells = new ArrayList<>();
    List<int[]> mines = new ArrayList<>();

    Game game;
    Minesweeper(Game game) {
        this.game = game;
    }


    private void evaluate() {
        for (int[] cell : safecells) {
            for (Sentence sentence : sentences) {
                if (contains(sentence.cells, cell)) 
                    sentence.remove(cell);
            }
        }

        List<Integer> r = new ArrayList<>();
        for (Sentence sentence : sentences) {
            if (sentence.count == 0 && sentence.cells.size() != 0) {
                r.add(sentences.indexOf(sentence));
                for (int[] cell : sentence.cells){
                    if (contains(safecells, cell)) continue;

                    safecells.add(cell);
                    for (Sentence s : sentences) {
                        if (s == sentence) continue;
                        if (s.contains(cell)) {
                            s.remove(cell);
                        }
                    }
                }
            }
            
            if ((sentence.count == sentence.cells.size()) && sentence.cells.size() != 0) {
                r.add(sentences.indexOf(sentence));
                for (int[] cell : sentence.cells) {
                    if (contains(mines, cell)) continue;

                    mines.add(cell);
                    flag(cell[0], cell[1]);
                    for (Sentence s : sentences) {
                        if (s == sentence) continue;
                        if (s.contains(cell)) {
                            s.remove(cell);
                            s.count--;
                        }
                    }
                }
            }
        }
    
        for (int i : r)
            if (i < sentences.size())    sentences.remove(i);
    }



    private Sentence subseting(Sentence sen1,Sentence sen2) {
        Sentence big = sen1.cells.size() > sen2.cells.size() ? sen1 : sen2;
        Sentence small = sen1.cells.size() < sen2.cells.size() ? sen1 : sen2;

        boolean isSubset = true;
        for (int[] cell : small.cells) {
            if (!contains(big.cells, cell)) {
                isSubset = false;
                break;
            }
        }
        if (!isSubset) return null;
        List<int[]> cells = new ArrayList<>();
        for (int[] cell : big.cells) {
            if (!contains(small.cells, cell))
                cells.add(cell);
        }
        int count = big.count - small.count > 0 ? big.count - small.count : 0;
        return new Sentence(cells, count);
    }


    private void addSubset(Sentence sen) {
        List<Sentence> subs = new ArrayList<>();
        for (Sentence sentence : sentences){
            Sentence s = subseting(sentence, sen);
            if (s != null);
                subs.add(s);
        }
        for (Sentence s : subs)
            if (s != null) {
                if (sentences.indexOf(s) == -1)
                    sentences.add(s);
            }
    }


    private void flag(int i, int j) {
        game.clickSquare(j*Game.tileSize, i*Game.tileSize, true);
    }


    public void makeMove() {
        if (game.gameover) return;
        evaluate();
        Sentence sub = null;
        boolean moveMade = false;
        for (int[] cell : safecells) {
            if (contains(madeMoves, cell)) continue;
            moveMade = true;

            game.clickSquare(cell[1]*Game.tileSize, cell[0]*Game.tileSize, false);
            Sentence sen = createSentence(cell);
            madeMoves.add(cell);
            sentences.add(sen);
            sub = sen;
        }

        if (sub != null)
            addSubset(sub);

        if (moveMade) return;
        randomMove();
    }

    public boolean contains(List<int[]> set, int[] cell) {
        for (int[] s : set) {
            if (s[0] == cell[0] && s[1] == cell[1]) return true;
        }
        return false;
    }


    private void randomMove() {
        int[] move = {(int) (Math.random()*Game.numy), (int) (Math.random()*Game.numx)};
        System.out.println(mines.size());

        while(contains(mines, move) | contains(madeMoves, move)) {
            move = new int[]{(int) (Math.random()*Game.numy), (int) (Math.random()*Game.numx)};
        }

        game.clickSquare(move[1]*Game.tileSize, move[0]*Game.tileSize, false);
        if (!game.gameover) {
            Sentence sen = createSentence(move);
            sentences.add(sen);
            madeMoves.add(move);
            safecells.add(move);
            addSubset(sen);
        }
    }


    private Sentence createSentence(int[] cell) {
        int count = game.squares[cell[0]][cell[1]].neighboringmines;
        List<int[]> neighbors = new ArrayList<>();
        for (int y = -1; y < 2; y++) {
            if (cell[0] + y < 0 | cell[0] + y > Game.numy - 1) continue;
            for (int x = -1; x < 2; x++) {
                if (cell[1] + x < 0 | cell[1] + x > Game.numx - 1) continue;
                if (x == 0 && y == 0) continue;

                neighbors.add(new int[]{cell[0] + y, cell[1] + x});
            }
        }

        return new Sentence(neighbors, count);
    }


    class Sentence {
        List<int[]> cells = new ArrayList<>();
        int count;
        Sentence(List<int[]> cells, int count) {
            for (int[] cell : cells)
                this.cells.add(cell);
            this.count = count;
        }

        public void add(int[] cell) {
            if (contains(cell)) return;
            cells.add(cell);
        }

        public void remove(int[] cell) {
            cells.remove(getIndex(cell));
        }

        private int getIndex(int[] cell) {
            int i = -1;
            int count = 0;
            for (int[] s : cells) {
                if (s[0] == cell[0] && s[1] == cell[1]) {
                    i = count;
                    break;
                }
                count++;
            }
            return i;
        }

        public boolean contains(int[] cell) {return Minesweeper.this.contains(cells, cell);}
    }
}