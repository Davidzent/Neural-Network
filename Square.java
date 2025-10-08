public class Square{
    Pixel[][] pixels;
    int length;
    int width;
    
    Square(){
    
    }
    
    
    public Square(int length, int width) {
      this.length = length;
      this.width = width;
      pixels = new Pixel[length][width];
    }


    void show(){
      for(int i=0;i<length;i++){
        for(int j=0;j<width;j++){
             pixels[i][j].show();
        }
      }
    }

    void show(int movRight){
      for(int i=0;i<length;i++){
        for(int j=0;j<width;j++){
             pixels[i][j].show(movRight);
        }
      }
    }
    
    boolean isEqual(Square s){
      for(int i=0;i<length;i++){
        for(int j=0;j<width;j++){
             if(s.pixels[i][j]!=pixels[i][j])return false;
        }
      }
      return true;
    }
    
 }