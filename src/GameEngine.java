public class GameEngine implements Runnable, GameConstants {
    private final GamePanel panel;

    public GameEngine(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            panel.tick();
            try {
                Thread.sleep(FRAME_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
