public class Powerup {
    private int x;
    private int y;
    private int width;
    private int height;

    public Powerup(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth(){
        return width;
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
