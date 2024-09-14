import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {


        // definindo o tamanho do tabuleiro
        int boardWidth = 600;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("Snake");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight); // tamanho da janela
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o aplicativo ao fechar a janela

        // Adiciona o painel do jogo à janela
        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);  // Inicializa o jogo
        frame.add(snakeGame);
        frame.pack(); // Ajusta o tamanho da janela
        snakeGame.requestFocus(); // Foca no painel para capturar entradas de teclado
    }
}
