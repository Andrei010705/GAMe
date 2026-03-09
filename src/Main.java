import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Am setat fereastra la 1400 de pixeli lățime
        JFrame frame = new JFrame("Laserbeam Attack - Harta Extinsă");
        LaserbeamAttack game = new LaserbeamAttack();

        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}