import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Pixel{
    double val;
    int posx;
    int posy;
    final int scale =75;
    final int size = 1*scale;
    
    Pixel(int x, int y,double v){
      posx = x;
      posy = y;
      val = v;
    }
    
    void show(){
      Graphics2D g2d = (Graphics2D) App.frame.getGraphics();
      JPanel PixelPanel = new JPanel();
      int i=(int)val;
      g2d.setColor(i==1?Color.white:Color.black);
      g2d.fillRect(posx*scale, posy*scale, size, size);
        // System.out.print(val);
        
    //   fill(255*val);
      //System.out.println(val*2-1);
    //   rect(posx*scale,posy*scale,size,size);
    }
    void show(int movRight){
      Graphics2D g2d = (Graphics2D) App.frame.getGraphics();
      JPanel PixelPanel = new JPanel();
      int i=(int)val;
      g2d.setColor(i==1?Color.white:Color.black);
      g2d.fillRect(posx*scale+movRight*scale, posy*scale, size, size);
      // g2d.drawRect(posx*scale+movRight*scale, posy*scale, size, size);

      // PixelPanel.setBackground(i==1?Color.white:Color.black);
      // PixelPanel.setBounds(posx*scale+movRight*scale, posy*scale, size, size);
      // App.frame.add(PixelPanel);
        // System.out.print(val);
        
    //   fill(255*val);
      //System.out.println(val*2-1);
    //   rect(posx*scale,posy*scale,size,size);
    }
  }