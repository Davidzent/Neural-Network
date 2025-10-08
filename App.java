import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

class App {
  private static EventListener eventListener = new EventListener();
  public static JFrame frame = new JFrame();
  private static int timeout = 200;
  private static boolean showNothing = false;

  public static void main(String[] args) throws InterruptedException {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(null);
    frame.setSize(1440, 750);
    frame.setVisible(true);

    Population pop = new Population(1000);
    int squareType = (int) Math.floor(Math.random() * 8);
    Square square = types(squareType, 0, 0);
    // square.show();
    Graphics2D g2d = (Graphics2D) frame.getGraphics();
    int guess = 0;
    int currentbestScore = 0;
    int currentbestCorrect = 0;

    int best = currentbestScore;
    int gen = 1;
    int cnt = 0;
    int temp = timeout;

    do {
      while (!pop.done()) {
        if (EventListener.isAddPressed()) {
          cnt++;
          if(cnt>=10)timeout+=10;
          else timeout++;
          System.out.println("pressing +:"+timeout);

        }
        else if (EventListener.isMinusPressed()) {
          cnt++;
          if(cnt>=10)timeout-=10;
          else timeout--;
          if(timeout<0)timeout=0;
          System.out.println("pressing -:"+timeout);

        }else cnt=0;
        if (EventListener.isNPressed()) {
          showNothing = true;
          if(timeout!=0)temp = timeout;
          timeout=0;
          System.out.println("pressing N:"+showNothing);
        }else if(EventListener.isMPressed()){
          showNothing = false;
          timeout=temp;
          System.out.println("pressing M:"+showNothing);
        }
        g2d.clearRect(0, 0, 1440, 750);
        squareType = (int) Math.floor(Math.random() * 8);
        square = types(squareType, 0, 0);
        pop.updateAlive(square, squareType, showNothing);
        // drawBrain(pop);
        if(!showNothing)square.show();
        guess++;
        currentbestScore = pop.currentBestScore;
        currentbestCorrect = pop.currentBestCorrect;
        if(!showNothing){
          g2d.drawString("Guess " + guess, 10, 200);
          g2d.drawString("Current Best Score " + currentbestScore, 10, 220);
          g2d.drawString("Current Best Correct " + currentbestCorrect, 10, 240);
          g2d.drawString("Grade Correct %" + (int) ((currentbestCorrect / (float) guess) * 100), 10, 260);
          g2d.drawString("Grade Score %" + (int) ((currentbestScore / (float) guess) * 100), 10, 280);
          g2d.drawString("Passing Score Grade %" + (int) (pop.passingGrade * 100), 10, 300);
          g2d.drawString("Best Score " + best, 10, 320);
          g2d.drawString("Generation " + gen, 10, 340);
          g2d.drawString("Players Alive " + pop.cntAlive, 10, 360);
        }
        

        TimeUnit.MILLISECONDS.sleep(timeout);

      }
      gen++;
      if (best < currentbestScore)
        best = currentbestScore;
      currentbestScore = 0;
      currentbestCorrect = 0;
      guess = 0;
      pop.naturalSelection();
      System.out.println(0);
    } while (true);

  }

  public static void drawBrain(Player player) { // show the brain of whatever genome is currently showing
    int startX = 600;
    int startY = 10;
    int w = 600;
    int h = 400;
    player.brain.show(startX, startY, w, h);
    // for (int i = 0; i< pop.pop.size(); i++) {
    // if (!pop.pop.get(i).dead) {
    // pop.pop.get(i).brain.show(startX, startY, w, h);
    // break;
    // }
    // }
  }

  public static Square types(int type, int x, int y) {
    Square s = new Square(2, 2);

    switch (type) {
      case 0:// solid white
        s.pixels[0][0] = new Pixel(x, y, 0);
        s.pixels[0][1] = new Pixel(x, y + 1, 0);
        s.pixels[1][0] = new Pixel(x + 1, y, 0);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 0);
        break;

      case 1:// solid Black

        s.pixels[0][0] = new Pixel(x, y, 1);
        s.pixels[0][1] = new Pixel(x, y + 1, 1);
        s.pixels[1][0] = new Pixel(x + 1, y, 1);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 1);
        break;

      case 2:// vertical first
        s.pixels[0][0] = new Pixel(x, y, 1);
        s.pixels[0][1] = new Pixel(x, y + 1, 0);
        s.pixels[1][0] = new Pixel(x + 1, y, 1);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 0);
        break;

      case 3:// vertical second
        s.pixels[0][0] = new Pixel(x, y, 0);
        s.pixels[0][1] = new Pixel(x, y + 1, 1);
        s.pixels[1][0] = new Pixel(x + 1, y, 0);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 1);
        break;

      case 4:// diagonal top to bottom right
        s.pixels[0][0] = new Pixel(x, y, 1);
        s.pixels[0][1] = new Pixel(x, y + 1, 0);
        s.pixels[1][0] = new Pixel(x + 1, y, 0);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 1);
        break;

      case 5: // bottom to top right
        s.pixels[0][0] = new Pixel(x, y, 0);
        s.pixels[0][1] = new Pixel(x, y + 1, 1);
        s.pixels[1][0] = new Pixel(x + 1, y, 1);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 0);
        break;

      case 6:// horizontal top
        s.pixels[0][0] = new Pixel(x, y, 1);
        s.pixels[0][1] = new Pixel(x, y + 1, 1);
        s.pixels[1][0] = new Pixel(x + 1, y, 0);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 0);
        break;

      case 7:// horizontal bottom
        s.pixels[0][0] = new Pixel(x, y, 0);
        s.pixels[0][1] = new Pixel(x, y + 1, 0);
        s.pixels[1][0] = new Pixel(x + 1, y, 1);
        s.pixels[1][1] = new Pixel(x + 1, y + 1, 1);
        break;
    }
    return s;
  }
}