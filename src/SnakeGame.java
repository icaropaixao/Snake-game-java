import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {


    // Representa uma peça do jogo (cabeça ou corpo da cobra, comida)
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    // Estado da cobra
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Estado da comida
    Tile food;
    Random random;

    // Lógica do jogo
    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;

    // Construtor do jogo
    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.gray);
        addKeyListener(this);
        setFocusable(true);

        // Inicializa as variáveis do jogo
        resetGame();
    }

    // Reinicia o jogo para um novo início
    public void resetGame() {
        snakeHead = new Tile(5, 5);  // Reinicializa a cabeça da cobra
        snakeBody = new ArrayList<>();  // Reinicializa o corpo da cobra

        food = new Tile(10, 10);  // Reposiciona a comida
        random = new Random();
        placeFood();  // Posiciona a comida aleatoriamente

        velocityX = 1;  // A cobra começa movendo-se para a direita
        velocityY = 0;

        gameOver = false;  // Reseta a condição de fim de jogo

        if (gameLoop == null) {  // Cria o loop do jogo se não existir
            gameLoop = new Timer(100, this);
        }
        gameLoop.start();  // Inicia o loop do jogo
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Desenha todos os elementos do jogo na tela
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Define o nível de transparência (0.0f = totalmente transparente, 1.0f = totalmente opaco)
        float alpha = 0.2f; // Ajuste este valor para definir a transparência
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComposite);

        // Desenha a grade
        g2d.setColor(Color.black); // Define a cor da grade
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g2d.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g2d.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Restaura a opacidade padrão (se necessário)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Desenha a comida
        g2d.setColor(Color.red);
        g2d.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Desenha a cabeça da cobra
        g2d.setColor(Color.green);
        g2d.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Desenha o corpo da cobra
        for (Tile snakePart : snakeBody) {
            g2d.setColor(Color.green); // Ou qualquer cor que você desejar
            g2d.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Desenha a pontuação
        g2d.setFont(new Font("Arial", Font.BOLD, 23));
        if (gameOver) {
            g2d.setColor(Color.white);
            g2d.drawString("Score: " + String.valueOf(snakeBody.size())  , tileSize - 16, tileSize + 0);
            g2d.drawString("SE FUDEU! ", tileSize +200 , tileSize + 200);
            g2d.drawString(" PRESSIONE ESPAÇO PARA REINICIAR!", tileSize + 50, tileSize + 250) ;
        } else {
            g2d.setColor(Color.white);
            g2d.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
    }

    // Posiciona a comida em uma posição aleatória
    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    // Atualiza a posição da cobra e verifica as condições de fim de jogo
    public void move() {
        if (collision(snakeHead, food)) {  // Se a cobra come a comida
            snakeBody.add(new Tile(food.x, food.y));  // Adiciona uma nova peça ao corpo da cobra
            placeFood();  // Reposiciona a comida
        }

        // Move o corpo da cobra
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {  // Atualiza a posição da primeira parte do corpo
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {  // Move as outras partes do corpo para a posição da parte anterior
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Move a cabeça da cobra
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Verifica condições de fim de jogo
        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {  // Colisão com o próprio corpo
                gameOver = true;
            }
        }

        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth ||
                snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight) {  // Colisão com as bordas
            gameOver = true;
        }
    }

    // Verifica se dois tiles colidem
    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();  // Atualiza a posição da cobra
            repaint();  // Redesenha o painel
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {  // Reinicia o jogo ao pressionar "Espaço"
            resetGame();
        }

        if (!gameOver) {  // Atualiza a direção da cobra apenas se o jogo não estiver terminado
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (velocityY != 1) {
                        velocityX = 0;
                        velocityY = -1;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (velocityY != -1) {
                        velocityX = 0;
                        velocityY = 1;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (velocityX != 1) {
                        velocityX = -1;
                        velocityY = 0;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (velocityX != -1) {
                        velocityX = 1;
                        velocityY = 0;
                    }
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
