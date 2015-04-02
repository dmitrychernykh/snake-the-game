package Snake;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel implements ActionListener {

    /**
     * this is an empty part of snake
     * I use it to avoid NullPointEx when getting the ends of snake */
    public final SnakePart NOPART = new SnakePart(false);

    /**
     * this part describes the game board params*/
    private final int B_WIDTH = 30;
    private final int B_HEIGHT = 30;
    private final int DOT_SIZE = 12;

    /**
     * for random */
    private final int RAND_POS = 29;

    /**
     * this controls a speed of the game*/
    private final int DELAY = 140;

    /**
     * Apple object that for some reason are very yammy for snakes*/
    private Apple apple;

    /**
     * our snake Sam, he likes apples*/
    private Snake snake;

    /**
     * this controls the direction of snake moves*/
    private int current_direction; // 0 - > right, 1 - v down, 2 - < left, 3 - ^ up

    /**
     * this controls normal understanding of user direction commands*/
    private boolean directionChangeAvailable = true;

    /**
     * available variants of directions
     * with it help snake directs next move*/
    private int[] directions_xy =
            {
                    1, 0, // > right
                    0, 1, // v down
                    -1, 0,// < left
                    0, -1,// ^ up
            };

    /**
     * checker if game is running or not*/
    private boolean inGame = true;

    /**
     * this timer will ensure the action
     * */
    private Timer timer;
//    private Image ball;
//    private Image apple;
//    private Image head;

    /**
     * Board constructor*/
    public Board() {

        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH*DOT_SIZE, B_HEIGHT*DOT_SIZE));
        initGame();

        addKeyListener(new TAdapter());

    }

    /**
     * initializes game process
     * creates new snake Sam and constructs his new body
     * adds new apple and starts the game timer*/
    private void initGame() {

        snake = new Snake();
        snake.makeSnake();
        apple = new Apple();

        if (timer == null) timer = new Timer(DELAY, this);
        timer.start();

    }

    /**
     * restarts the game
     * do all things for game restart*/
    private void restartTheGame(){

        inGame = true;
        snake = null;
        apple = null;
        current_direction = 0;
        directionChangeAvailable = true;
        initGame();
        repaint();
    }

    /**
     * every time after calculating all the game math
     * we call redraw the game picture*/
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);

    }

    /**
     * makes the magic of Java painting*/
    private void doDrawing(Graphics g) {
        
        if (inGame) {

            /**
             * draw the apple on board*/
            g.setColor(Color.RED);
            g.fillOval(apple.getX() * DOT_SIZE, apple.getY() * DOT_SIZE, DOT_SIZE, DOT_SIZE);

            /**
             * takes snake Sam to draw it*/
            SnakePart Sn = snake.Head;
            int[] snakexy = snake.getSnake_xy();

            while (Sn != NOPART){

                /**
                 * snake is a simple double-linked list
                 * so we doing loop for each part of snake in a chain*/


                int z = Sn.getPartPosition();

                /**
                 * need for elements that have different size*/
                int offset = 0;

                /**
                 * some parts has different color*/
                if (Sn.itIsHead()) g.setColor(Color.BLUE);
                else g.setColor(Color.CYAN);

                if (Sn.isEatenApple()){

                    /**
                     * if Sam eat apple it represents as eaten Apple
                     * inside Sams stomach*/
                    offset = -2;
                    g.fillRoundRect(snakexy[z] * DOT_SIZE + offset/2, snakexy[z + 1] * DOT_SIZE + offset/2, DOT_SIZE - offset, DOT_SIZE - offset, 1-offset, 1-offset);
                    offset = DOT_SIZE / 8;
                    g.setColor(Color.RED);
                    g.fillOval(snakexy[z] * DOT_SIZE + offset, snakexy[z + 1] * DOT_SIZE + offset, DOT_SIZE - (offset << 1), DOT_SIZE - (offset<<1));

                } else {
                    /**
                     * each part of snake is just a rectangular*/
                    g.fillRect(snakexy[z] * DOT_SIZE + offset, snakexy[z + 1] * DOT_SIZE + offset, DOT_SIZE - (offset >> 1), DOT_SIZE - (offset >> 1));
                }

                /**
                 * next part in a snake chain*/
                Sn = Sn.getBack();
            }

            /**
             * Many GUI operations may be performed asynchronously.
             * This means that if you set the state of a component,
             * and then immediately query the state,
             * the returned value may not yet reflect the requested change
             *
             * So this code synchronizes the graphics state*/
            Toolkit.getDefaultToolkit().sync();

        } else {

            /**
             * game is over< lets draw the final page*/
            gameOver(g);

        }        
    }

    /**
     * Draws a final Game Over page*/
    private void gameOver(Graphics g) {

        /**
         * Just how to draw the text in Java
         * and positioning on board*/

        String msg = "Game Over";
        Font mid = new Font("Helvetica", Font.BOLD, 16);
        Font small = new Font("Helvetica", Font.BOLD, 10);
        FontMetrics metr = getFontMetrics(mid);
        int y = B_HEIGHT*DOT_SIZE / 2;

        g.setColor(Color.white);
        g.setFont(mid);
        g.drawString(msg, (B_WIDTH*DOT_SIZE - metr.stringWidth(msg)) / 2, y);

        msg = "your snake rose "+(snake.length()/2)+" points.\nCongratulations!";
        metr = getFontMetrics(small);
        y += DOT_SIZE*2;
        g.setFont(small);
        g.drawString(msg, (B_WIDTH*DOT_SIZE - metr.stringWidth(msg)) / 2, y);

        y+= DOT_SIZE*2;
        msg = "press R for restart the game";
        g.drawString(msg, (B_WIDTH*DOT_SIZE - metr.stringWidth(msg)) / 2, y);

    }

    /**
     * calls checks if snake eat apple and calls move apple to a new place*/
    private void checkApple() {

        if (snakeEatApple())
            apple.locateAppleOnBoard();

    }

    /**
     * gets check that snake get the apple*/
    private boolean snakeEatApple(){

        return snake.checkHeadEatingApple();

    }

    /**
     * improve tha moving of snake*/
    private void move() {

        if (inGame)
            snake.makeSnakeMove();

    }

    /**
     * cheks for collisions that may cause the game over*/
    private void checkCollision() {

        /**
         * the coords of snake parts*/
        int[] snakexy = snake.getSnake_xy();
        int x,y, headPos = snake.getHead().getPartPosition(), eatenAppPos = snake.getHead().getApplePartPosition();

        /** head position on board*/
        x = snakexy[headPos];
        y = snakexy[headPos+1];

        /** if snake just eat apple, we can return, collision can't be*/
        if (snake.getHead().isEatenApple() && x == snakexy[eatenAppPos] && y == snakexy[eatenAppPos+1]) return;

        /** game ends if snake go out of board*/
        if (x < 0 || y < 0 || x > B_WIDTH || y > B_HEIGHT) {
            inGame = false;
            return;
        }

        /** game ends if snake Sam eat some part of himself*/
        for (int i = snake.length() - 2; i >= 0; i = i - 2) {
            if (i == headPos) continue;
            if (snakexy[i] == x && snakexy[i+1] == y){
                inGame = false;
                return;
            }
        }

        /** stops the timer if game over*/
        if(!inGame) {
            timer.stop();
        }
    }

    /**
     * Apple class - it is eat for snake*/
    private class Apple{

        /** apple coordinates*/
        int x, y;

        public Apple() {
            locateAppleOnBoard();
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        /** provides random location of apple
         * every time in new place and not above the snake
         * if apple tried to get point where the snake is, call itself recursively*/
        public void locateAppleOnBoard() {

            int nextX = (int) (Math.random() * RAND_POS);
            int nextY = (int) (Math.random() * RAND_POS);

            if (nextX == x || nextY == y || !checkXYisFree(nextX, nextY))
                locateAppleOnBoard();
            else { x = nextX; y =nextY; }

        }

        /**
         * check that on that coordinates no one of snake Sam parts located*/
        private boolean checkXYisFree(int x, int y){

            int[] snakexy = snake.getSnake_xy();
            for (int i = snake.length(); i >=0 ; i -= 2) {
                if (snakexy[i] == x && snakexy[i+1] == y) return false;
            }
            return true;

        }

    } // END OF Apple

    /**
     * Snake Sam class*/
    private class Snake{

        /**
         * head and tail of snake
         * helps to understand snake :)*/
        private SnakePart Head = NOPART;
        private SnakePart Tail = NOPART;

        /**
         * holds all coordinates of all snake parts
         * in format [x1, y1, x2, y2, ... ]*/
        private int[] snake_xy = new int[B_HEIGHT*B_WIDTH*2];

        /**
         * is needed to know in what index new part of snake
         * put its coordinates. length helps with it*/
        private int length;

        /**
         * construct new snake in the center of the board
         * every new part of snake become a tail element*/
        public void makeSnake() {
            int snakeLen = 3;
            for (int i = 0; i < snakeLen; i++) {
                SnakePart newPart = new SnakePart(length);
                operateNewPartAdding(newPart);
                length += 2;
                newPart.movePart((B_WIDTH >> 1) - 2 - i, B_HEIGHT >> 1);
            }
        }

        public SnakePart getTail() {
            return Tail;
        }

        public void setTail(SnakePart tail) {
            Tail = tail;
        }

        public int[] getSnake_xy() {
            return snake_xy;
        }

        public int length() {
            return length;
        }

        public SnakePart getHead() {
            return Head;
        }

        public void setHead(SnakePart head) {
            this.Head = head;
        }

        /**
         * adds a new snake part in tail*/
        public void addNewPart() {

            snake_xy[Head.getApplePartPosition()] = snake_xy[Head.getPartPosition()];
            snake_xy[Head.getApplePartPosition()+1] = snake_xy[Head.getPartPosition()+1];

            SnakePart newPart = new SnakePart(Head.getApplePartPosition());
            operateNewPartAdding(newPart);

        }

        /**
         * every new part causes permutations in tail part of snake
         * */
        public void operateNewPartAdding(SnakePart newPart){
            if (getHead() == NOPART) {
                setHead(newPart);
            } else {
                getTail().setBack(newPart);
                newPart.setFront(getTail());
            }
            setTail(newPart);
        }

        /**
         * provides snake moving
         * illusion of moving created by permutation tail part in head*/
        public void makeSnakeMove(){

            Head.setFront(Tail);
            Tail.setBack(Head);

            Head = Tail;
            Tail = Tail.getFront();
            Head.setFront(NOPART);
            Tail.setBack(NOPART);

            int prevpos = Head.getBack().getPartPosition();

            /**
             * if moved pat  holds eaten apple
             * adds new part in tail of the snake*/
            if(Head.isEatenApple()){
                addNewPart();
                Head.setEatenApple(false);
            }

            Head.movePart(snake_xy[prevpos]+directions_xy[current_direction<<1], snake_xy[prevpos+1]+directions_xy[(current_direction<<1)+1]);

        }

        /**
         * provides check if snake eat apple
         * by comparing coordinates of the snake head and the apple*/
        public boolean checkHeadEatingApple() {

            int pos = getHead().getPartPosition();

            if(snake_xy[pos] == apple.getX() && snake_xy[pos + 1] == apple.getY()) {
                snake_xy[length++] = apple.getX();
                snake_xy[length++] = apple.getY();
                getHead().setEatenApple(true);
            }

            return getHead().isEatenApple();
        }
    }

    /**
     * class for parts of snake
     * helps navigate snake
     * holds info about eaten apple*/
    private class SnakePart{

        private SnakePart front = NOPART;
        private SnakePart back = NOPART;
        private int partPosition;

        private int ApplePartPosition;
        private boolean isEatenApple;

        SnakePart(int pos){

            this.partPosition = pos;

        }

        SnakePart(boolean isEatenApple){
            this.isEatenApple = isEatenApple;
        }

        public void setBack(SnakePart back) {
            this.back = back;
        }

        public void setFront(SnakePart front) {
            this.front = front;
        }

        public SnakePart getBack() {
            return back;
        }

        public SnakePart getFront() {
            return front;
        }

        public boolean itIsHead(){
            return front == NOPART;
        }

        public int getPartPosition() {
            return (this == NOPART) ? -1 : partPosition;
        }

        public int getApplePartPosition() {
            return ApplePartPosition;
        }

        public void movePart(int x, int y){

            int[] snakexy = snake.getSnake_xy();

            snakexy[partPosition]   = x;
            snakexy[partPosition+1] = y;

            directionChangeAvailable = true;

        }

        public boolean isEatenApple() {
            return isEatenApple;
        }

        public void setEatenApple(boolean isEatenApple) {
            this.isEatenApple = isEatenApple;
            if (isEatenApple) this.ApplePartPosition = snake.length()-2;
            else this.ApplePartPosition = -1;
        }
    }

    /**
     * timer tic event handler
     * every period that we signed in DELAY it moves the game forward*/
    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    /**
     * adapter for user inputs*/
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (directionChangeAvailable && (key == KeyEvent.VK_LEFT) && (current_direction != 0)) {
                directionChangeAvailable = false;
                current_direction = 2;
            }

            if (directionChangeAvailable && (key == KeyEvent.VK_RIGHT) && (current_direction != 2)) {
                directionChangeAvailable = false;
                current_direction = 0;
            }

            if (directionChangeAvailable && (key == KeyEvent.VK_UP) && (current_direction != 1)) {
                directionChangeAvailable = false;
                current_direction = 3;
            }

            if (directionChangeAvailable && (key == KeyEvent.VK_DOWN) && (current_direction != 3)) {
                directionChangeAvailable = false;
                current_direction = 1;
            }

            if (!inGame && (key == KeyEvent.VK_R)){
                restartTheGame();
            }
        }
    }
}