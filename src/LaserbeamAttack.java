import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LaserbeamAttack extends JPanel implements ActionListener, KeyListener {

    // LĂȚIME MĂRITĂ LA 1400 PIXELI
    private final int WIDTH = 1400, HEIGHT = 600;
    private Timer timer;

    enum Scene { CAMP, PARTER, ETAL_1, ETAL_2, ANIMATIE_SCARI, GAMEOVER }
    private Scene currentScene = Scene.CAMP;
    private Scene nextScene = Scene.CAMP;

    // Mike - Atribute
    private int mikeX = 50, mikeY = 490;
    private int velX = 0, velY = 0;
    private final int MIKE_W = 40, MIKE_H = 60;
    private float offsetDecor = 0;
    private int animStep = 0;
    private boolean hasBat = false;
    private boolean isHidden = false;

    // Boss & Dialog
    private boolean dialogActive = false;
    private int dialogStep = 0;
    private boolean uncleAggressive = false;
    private int uncleX = 100, uncleY = 380; // Poziția unchiului Boss
    private int uncleW = 80, uncleH = 120; // Dublu față de Mike

    // Obiecte
    private Rectangle doorLab = new Rectangle(0, 430, 60, 120);
    private Rectangle batRect = new Rectangle(600, 520, 40, 10);
    private Rectangle stairsRight = new Rectangle(1320, 450, 70, 100);

    public LaserbeamAttack() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentScene == Scene.CAMP) drawCamp(g2);
        else if (currentScene == Scene.ANIMATIE_SCARI) drawStairsAnimation(g2);
        else if (currentScene == Scene.GAMEOVER) drawGameOver(g2);
        else drawInterior(g2);

        if (currentScene != Scene.ANIMATIE_SCARI && currentScene != Scene.GAMEOVER && !isHidden) drawMike(g2);
        if (dialogActive) drawDialog(g2);
        drawHUD(g2);
    }

    private void drawCamp(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(135, 206, 235), 0, 400, Color.WHITE));
        g.fillRect(0, 0, WIDTH, 400);
        g.setColor(new Color(34, 139, 34)); g.fillRect(0, 400, WIDTH, 200);

        int bX = 1000 - (int)(offsetDecor * 0.8);
        doorLab.x = bX + 145;

        // Acoperiș
        g.setColor(new Color(80, 20, 20));
        g.fillPolygon(new int[]{bX - 20, bX + 175, bX + 370}, new int[]{100, 40, 100}, 3);
        g.setColor(Color.BLACK); g.setStroke(new BasicStroke(3));
        g.drawPolygon(new int[]{bX - 20, bX + 175, bX + 370}, new int[]{100, 40, 100}, 3);

        // Clădire
        g.setColor(new Color(230, 230, 225)); g.fillRect(bX, 100, 350, 450);
        g.setColor(Color.BLACK); g.drawRect(bX, 100, 350, 450);

        for(int i=0; i<2; i++) {
            for(int j=0; j<3; j++) drawWindow(g, bX + 40 + (j*100), 160 + (i*150), 60, 80);
        }

        // Ușă
        g.setColor(new Color(100, 60, 30)); g.fillRect(doorLab.x, doorLab.y, doorLab.width, doorLab.height);
        g.setColor(new Color(60, 30, 10));
        for(int lx = 10; lx < 60; lx += 15) g.drawLine(doorLab.x + lx, doorLab.y, doorLab.x + lx, doorLab.y + 120);

        g.setColor(Color.WHITE); g.fillRect(doorLab.x + 5, doorLab.y + 20, 50, 15);
        g.setColor(Color.RED); g.setFont(new Font("Arial", Font.BOLD, 8));
        g.drawString("Don't enter", doorLab.x + 8, doorLab.y + 30);
        g.setColor(new Color(212, 175, 55)); g.fillOval(doorLab.x + 45, doorLab.y + 65, 8, 8);
        g.setColor(Color.BLACK); g.drawRect(doorLab.x, doorLab.y, doorLab.width, doorLab.height);
    }

    private void drawInterior(Graphics2D g) {
        g.setColor(new Color(240, 240, 240)); g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.BLACK); g.drawRect(0, 0, WIDTH-1, HEIGHT-1);
        g.setColor(new Color(170, 170, 170)); g.fillRect(0, 550, WIDTH, 50);

        if (currentScene == Scene.PARTER) {
            g.setColor(new Color(110, 110, 110));
            int[] xPts = {WIDTH - 300, WIDTH - 270, WIDTH - 220, WIDTH - 180, WIDTH - 120, WIDTH - 120, WIDTH - 300};
            int[] yPts = {350, 320, 340, 320, 350, 550, 550};
            g.fillPolygon(xPts, yPts, 7);
            g.setColor(Color.BLACK); g.drawPolygon(xPts, yPts, 7);
        } else if (currentScene == Scene.ETAL_1) {
            // 6 Birouri și calculatoare - spațiu mărime mărire
            for(int i=0; i<6; i++) {
                int bx = 100 + (i * 200);
                g.setColor(new Color(101, 67, 33)); g.fillRect(bx, 480, 100, 70);
                g.setColor(Color.CYAN); g.fillRect(bx+25, 445, 50, 30);
            }
            if (!hasBat) {
                g.setColor(new Color(193, 154, 107));
                g.fillRoundRect(batRect.x, batRect.y, batRect.width, batRect.height, 5, 5);
            }
        } else if (currentScene == Scene.ETAL_2) {
            drawUncleBoss(g);
        }

        // Scări mereu în dreapta, poziționate la 1320
        g.setColor(Color.DARK_GRAY); g.fillRect(stairsRight.x, stairsRight.y, stairsRight.width, stairsRight.height);
        g.setColor(Color.BLACK); g.drawRect(stairsRight.x, stairsRight.y, stairsRight.width, stairsRight.height);
    }

    private void drawUncleBoss(Graphics2D g) {
        if (!uncleAggressive) {
            // Birou Unchi - PERSPECTIVĂ LATERALĂ ÎN STÂNGA
            g.setColor(new Color(80, 50, 20));
            g.fillRect(50, 400, 150, 150); // Corpul biroului
            g.setColor(Color.BLACK); g.drawRect(50, 400, 150, 150);

            // Monitor văzut din profil
            g.setColor(Color.GRAY); g.fillRect(180, 420, 10, 80);
            g.setColor(Color.CYAN); g.fillRect(178, 425, 5, 70); // Ecran profil
        }

        int ux = uncleX, uy = uncleAggressive ? uncleY : 410;
        int s = uncleAggressive ? 2 : 1;

        // Unchiul orientat spre monitor (stânga)
        g.setColor(new Color(30, 80, 180)); g.fillRect(ux, uy + (40*s), 20*s, 20*s); // Pantaloni
        g.setColor(Color.BLACK); g.fillRect(ux, uy + (15*s), 20*s, 30*s); // Tricou
        g.setColor(Color.WHITE); g.drawRect(ux - (5*s), uy + (10*s), 30*s, 45*s); // Halat
        g.setColor(new Color(255, 220, 180)); g.fillRect(ux + (2*s), uy, 16*s, 16*s); // Cap
        g.setColor(Color.GRAY); g.fillRect(ux, uy, 4*s, 10*s); // Păr

        if (uncleAggressive) {
            g.setColor(Color.LIGHT_GRAY); g.fillRect(ux - 20, uy + 40, 15, 80); // Sabia
        } else {
            // Ochelari orientați spre stânga
            g.setColor(Color.BLACK); g.drawRect(ux + 3, uy + 5, 4, 3);
            g.drawLine(ux + 3, uy + 6, ux + 1, uy + 6); // Ramă profil
        }
    }

    private void drawDialog(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200)); g.fillRect(300, 50, 800, 120);
        g.setColor(Color.WHITE); g.setStroke(new BasicStroke(3)); g.drawRect(300, 50, 800, 120);
        g.setFont(new Font("Arial", Font.BOLD, 17));
        String speaker = (dialogStep == 0 || dialogStep == 2) ? "UNCHIUL: " : "MIKE: ";
        String[] lines = {
                "Ce cauți aici? Nu trebuia să afle nimeni de ce fac eu aici!!!",
                "Mă plimbam și văzusem clădirea asta abandonată... nu am știut că lucrezi aici.",
                "NU! Nu trebuia să intri! Scria și pe ușă! ACUM CE SĂ FAC CU TINE? Va trebui să MORIII!"
        };
        g.drawString(speaker + lines[dialogStep], 330, 110);
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.drawString("[ENTER pentru a continua]", 900, 150);
    }

    private void drawMike(Graphics2D g) {
        g.setColor(Color.BLACK); g.drawRect(mikeX + 10, mikeY, 20, 60);
        g.setColor(new Color(30, 80, 180)); g.fillRect(mikeX + 11, mikeY + 36, 18, 18);
        g.setColor(Color.WHITE); g.fillRect(mikeX + 11, mikeY + 16, 18, 18);
        g.setColor(new Color(255, 220, 180)); g.fillRect(mikeX + 13, mikeY + 2, 14, 14);
        if (hasBat) {
            g.setColor(new Color(193, 154, 107)); g.fillRect(mikeX + 30, mikeY + 20, 5, 30);
        }
    }

    private void drawWindow(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(150, 200, 255)); g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK); g.drawRect(x, y, w, h);
        g.drawLine(x + w/2, y, x + w/2, y + h);
        g.drawLine(x, y + h/2, x + w, y + h/2);
    }

    private void drawStairsAnimation(Graphics2D g) {
        g.setColor(Color.DARK_GRAY); g.fillRect(0, 0, WIDTH, HEIGHT);
        for(int i=0; i<12; i++) {
            g.setColor(Color.GRAY); g.fillRect(500, HEIGHT - (i*55), 400, 25);
            g.setColor(Color.BLACK); g.drawRect(500, HEIGHT - (i*55), 400, 25);
        }
        g.setColor(new Color(100, 65, 45)); g.fillRect(WIDTH/2 - 10, HEIGHT - animStep, 20, 25);
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(Color.BLACK); g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.RED); g.setFont(new Font("Impact", Font.PLAIN, 60));
        g.drawString("GAME OVER", WIDTH/2 - 150, HEIGHT/2);
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(Color.BLACK);
        String msg = isHidden ? "ASCUNS (G pentru a ieși)" : (hasBat ? "Armă: Bâtă" : "Fugă!");
        g.drawString("Statut: " + msg + " | Etaj: " + currentScene, 20, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentScene == Scene.ANIMATIE_SCARI) {
            animStep += 10;
            if (animStep > HEIGHT + 50) {
                currentScene = nextScene;
                mikeX = 1250; // Apare mereu în dreapta
                animStep = 0;
                // DECLANȘARE CUT-SCENE INSTANT la Etajul 2
                if (currentScene == Scene.ETAL_2 && dialogStep == 0) {
                    dialogActive = true;
                    velX = 0;
                }
            }
        } else if (!dialogActive && currentScene != Scene.GAMEOVER) {
            if (!isHidden) {
                mikeX += velX; offsetDecor += velX;
                velY += 1; mikeY += velY;
                if (mikeY > 490) { mikeY = 490; velY = 0; }
                if (mikeX < 0) mikeX = 0; if (mikeX > WIDTH-40) mikeX = WIDTH-40;
            }

            if (uncleAggressive) {
                // Unchiul te caută doar dacă nu ești ascuns sau dacă e la alt etaj
                if (currentScene == Scene.ETAL_2 || (currentScene == Scene.ETAL_1 && !isHidden)) {
                    if (uncleX < mikeX) uncleX += 4; else uncleX -= 4;
                    if (!isHidden && new Rectangle(mikeX, mikeY, 40, 60).intersects(uncleX, uncleY, uncleW, uncleH)) {
                        currentScene = Scene.GAMEOVER;
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (dialogActive && k == KeyEvent.VK_ENTER) {
            dialogStep++;
            if (dialogStep > 2) { dialogActive = false; uncleAggressive = true; uncleY = 430; }
            return;
        }

        if (k == KeyEvent.VK_LEFT && !isHidden) velX = -7;
        if (k == KeyEvent.VK_RIGHT && !isHidden) velX = 7;
        if (k == KeyEvent.VK_SPACE && mikeY >= 490 && !isHidden) velY = -18;

        // Mecanica de ascuns sub birou la Etajul 1 - actualizată pentru harta extinsă
        if (k == KeyEvent.VK_G && currentScene == Scene.ETAL_1) {
            for(int i=0; i<6; i++) {
                int bx = 100 + (i * 200);
                if (mikeX > bx - 20 && mikeX < bx + 80) {
                    isHidden = !isHidden;
                    velX = 0;
                    break;
                }
            }
        }

        if (k == KeyEvent.VK_F && !isHidden) {
            Rectangle m = new Rectangle(mikeX, mikeY, MIKE_W, MIKE_H);
            if (currentScene == Scene.CAMP && m.intersects(doorLab)) {
                currentScene = Scene.PARTER; mikeX = 50;
            } else if (currentScene == Scene.ETAL_1 && m.intersects(batRect)) {
                hasBat = true;
            } else if (m.intersects(stairsRight)) {
                if (currentScene == Scene.PARTER) nextScene = Scene.ETAL_1;
                else if (currentScene == Scene.ETAL_1) nextScene = Scene.ETAL_2;
                currentScene = Scene.ANIMATIE_SCARI;
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) velX = 0;
    }
}