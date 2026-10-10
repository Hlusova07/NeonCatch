import java.awt.Color;

public interface GameConstants {
    int WIDTH = 800, HEIGHT = 600;
    int PADDLE_WIDTH = 120, PADDLE_HEIGHT = 16, BALL_SIZE = 18;
    int PADDLE_Y = 510, START_SPEED = 4, MAX_SPEED = 10;
    int FRAME_DELAY = 16;

    Color BACKGROUND = new Color(15, 12, 35);
    Color NEON = new Color(0, 255, 220);
    Color PINK = new Color(255, 55, 170);
}
