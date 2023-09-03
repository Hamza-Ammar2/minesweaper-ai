import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import javax.imageio.ImageIO;

public class Game {
    static final int numx = 8;
    static final int numy = 8;
    static final int tileSize = 40;
    static final int SCREEN_WIDTH = numx*tileSize;
    static final int SCREEN_HEIGHT = numy*tileSize;

    boolean gameover = false;
    Square[][] squares;
    int bombs = 10;
    int bombsfound = 0;

    Minesweeper minesweeper;
    List<Square> chained = new ArrayList<>();
    BufferedImage images;
    BufferedImage cover;
    BufferedImage flag;
    int h;
    int w;

    Game() {
        try {
            images = ImageIO.read(new File("assets/tiles.png"));
            h = images.getHeight()/3;
            w = images.getWidth()/4;
            cover = images.getSubimage(0, 0, w, h);
            flag = images.getSubimage(w, 0, w, h);
        } catch (IOException e) {
            System.out.println(e);
        }

        createLevel();
        minesweeper = new Minesweeper(this);
    }

    private void createLevel() {
        squares = new Square[numy][numx];
        bombsfound = 0;

        int count = bombs;
        while(count > 0) {
            int i = (int) (Math.random()*numy);
            int j = (int) (Math.random()*numx);
            if (squares[i][j] != null) continue;

            squares[i][j] = new Square(i, j, true);
            count--;
        }

        for (int i = 0; i < numy; i++) {
            for (int j = 0; j < numx; j++) {
                if (squares[i][j] != null) continue;

                int neighboringmines = 0;
                for (int y = -1; y < 2; y++) {
                    if (i + y < 0 | i + y > numy - 1) continue;
                    for (int x = -1; x < 2; x++) {
                        if (x + j < 0 | x + j > numx - 1) continue;

                        Square square = squares[i + y][j + x];
                        if (square != null)
                            if (square.isBomb)
                                neighboringmines++;
                    }
                }
                Square square = new Square(i, j, false);
                square.neighboringmines = neighboringmines;
                square.chooseImage();
                squares[i][j] = square;
            }
        }
    }


    public void clickSquare(int x, int y, boolean isFlag) {
        int i = y/tileSize;
        int j = x/tileSize;
        chained.clear();
        if (!isFlag){
            //chain(i, j);
            if (squares[i][j].uncovered)
                System.out.println("interesting");
            squares[i][j].uncover();
        }else 
            squares[i][j].flag();
    }

    
    private void chain(int i, int j) {
        squares[i][j].uncover();
        chained.add(squares[i][j]);
        if (squares[i][j].isBomb | squares[i][j].neighboringmines != 0) return;

        for (int y = -1; y < 2; y++) {
            if (i + y < 0 | i + y > numy - 1) continue;
            for (int x = -1; x < 2; x++) {
                if (x + j < 0 | x + j > numx - 1) continue;
                if (x == 0 && y == 0) continue;
                if (chained.contains(squares[i + y][j + x])) continue;

                chained.add(squares[i + y][j + x]);
                chain(i + y, j + x);
            }
        }
    }


    public void update() {
    }

    public void draw(Graphics g) {
        for (Square[] row : squares) 
            for (Square square : row)
                square.draw(g);
    }

    class Square {
        int x;
        int y;

        boolean isBomb;
        int neighboringmines = 0;
        BufferedImage uncover;
        boolean uncovered = false;
        boolean flagged = false;

        Square(int i, int j, boolean isBomb) {
            x = j*tileSize;
            y = i*tileSize;
            this.isBomb = isBomb;
            if (isBomb) {
                uncover = images.getSubimage(2*w, 0, h, w);
            }
        }

        public void uncover() {
            if (uncovered) return;
            uncovered = true;
            if (isBomb)
                gameover = true;
        }


        public void chooseImage() {
            int i = neighboringmines < 5 ? 1 : 2;
            int j = i == 1 ? neighboringmines - 1 : neighboringmines - 5;
            if (j < 0) {
                i = 0;
                j = 3;
            }

            uncover = images.getSubimage(j*w, i*h, h, w);
        }


        public void flag() {
            if (uncovered) return;
            if (flagged) {
                flagged = false;
                if (isBomb)
                    bombsfound--;
                return;
            }

            flagged = true;
            if (isBomb){
                bombsfound++;
                if (bombsfound == bombs)
                    System.out.println("You Win!");
            }
        }


        public void draw(Graphics g) {
            if (uncovered | gameover)
                g.drawImage(uncover, x, y, tileSize, tileSize, null);
            else if (flagged)
                g.drawImage(flag, x, y, tileSize, tileSize, null);   
            else 
                g.drawImage(cover, x, y, tileSize, tileSize, null);
        }
    }
}
