public class Powerup {
    private int x;
    private int y;
    private int length;
    private int height;

    public Powerup(int a, int b, int c, int d){
        x = a;
        y = b;
        length = c;
        height = d;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getLength(){
        return length;
    }

    public int getHeight(){
        return height;
    }

    public boolean checkCollision(int playerX, int playerY){
        if (playerY < y && playerX > x && playerX < x + height){
            return true;
        }
        return false;
    }
}
