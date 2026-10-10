import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements GameConstants {
    private int paddleX = GameConstants.WIDTH / 2 - PADDLE_WIDTH / 2;
    private int ballX = GameConstants.WIDTH / 2, ballY = 120;
    private int dx = START_SPEED, dy = START_SPEED;
    private int score = 0, lives = 3;
    private boolean playing = false, gameOver = false;

    public GamePanel() {
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setBackground(BACKGROUND);
        setFocusable(true);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                movePaddle(e.getX());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                movePaddle(e.getX());
            }
        });

        bindKey("SPACE", KeyEvent.VK_SPACE, () -> {
            if (!gameOver) playing = true;
        });
        bindKey("NEW_GAME", KeyEvent.VK_N, this::newGame);
        bindKey("EXIT", KeyEvent.VK_ESCAPE, () -> System.exit(0));

        new Thread(new GameEngine(this), "GameLoop").start();
    }

    private void bindKey(String name, int keyCode, Runnable action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyCode, 0), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                repaint();
            }
        });
    }

    private synchronized void movePaddle(int mouseX) {
        paddleX = Math.max(0, Math.min(
                GameConstants.WIDTH - PADDLE_WIDTH, mouseX - PADDLE_WIDTH / 2));
    }

    public synchronized void tick() {
        if (!playing || gameOver) return;

        ballX += dx;
        ballY += dy;

        if (ballX <= 0 || ballX >= GameConstants.WIDTH - BALL_SIZE) dx = -dx;
        if (ballY <= 0) dy = Math.abs(dy);

        if (dy > 0
                && ballY + BALL_SIZE >= PADDLE_Y
                && ballY <= PADDLE_Y + PADDLE_HEIGHT
                && ballX + BALL_SIZE >= paddleX
                && ballX <= paddleX + PADDLE_WIDTH) {
            score++;
            int speed = Math.min(MAX_SPEED, START_SPEED + score / 5);
            dx = dx > 0 ? speed : -speed;
            dy = -speed;
        }

        if (ballY > GameConstants.HEIGHT) {
            lives--;
            if (lives <= 0) {
                gameOver = true;
                playing = false;
            } else {
                ballX = GameConstants.WIDTH / 2;
                ballY = 120;
                dx = START_SPEED;
                dy = START_SPEED;
            }
        }

        repaint();
    }

    private synchronized void newGame() {
        score = 0;
        lives = 3;
        ballX = GameConstants.WIDTH / 2;
        ballY = 120;
        dx = START_SPEED;
        dy = START_SPEED;
        gameOver = false;
        playing = false;
        repaint();
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(45, 35, 75));
        for (int x = 0; x < GameConstants.WIDTH; x += 40) {
            for (int y = 0; y < GameConstants.HEIGHT; y += 40) {
                g2.fillOval(x, y, 2, 2);
            }
        }

        g2.setFont(new Font("Monospaced", Font.BOLD, 24));
        g2.setColor(NEON);
        g2.drawString("NEON CATCH", 30, 40);

        g2.setColor(PINK);
        g2.drawString("SCORE: " + score, 540, 40);
        g2.drawString("LIVES: " + lives, 540, 75);

        g2.setColor(NEON);
        g2.fillRoundRect(paddleX, PADDLE_Y,
                PADDLE_WIDTH, PADDLE_HEIGHT, 12, 12);

        g2.setColor(PINK);
        g2.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        if (!playing) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 28));
            g2.setColor(Color.WHITE);
            String message = gameOver ? "GAME OVER" : "NEON CATCH";
            g2.drawString(message, GameConstants.WIDTH / 2 - 100, GameConstants.HEIGHT / 2 - 20);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
            g2.drawString("SPACE - START   N - NEW GAME   ESC - EXIT",
                    145, GameConstants.HEIGHT / 2 + 25);
        }
    }
}
