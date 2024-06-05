package Tanks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.WillClose;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public PImage basic;
    public PImage desert;
    public PImage forest;
    public PImage fuel;
    public PImage hills;
    public PImage parachute;
    public PImage snow;
    public PImage tree1;
    public PImage tree2;
    public PImage wind1;
    public PImage wind;
    
    public PVector v1;
    public JSONObject configJSON;

    public String configPath;
    public String layout;
    public String background;
    public String foregroundColor;
    public String trees;
    public JSONObject levelObj;
    JSONArray levelsArray;
    public int countTree=0;
    public Levels level;
    List<String> playersLs;
    public JSONObject playerColoursObj;
    public Tanks[] tanks = new Tanks[10];
    public ArrayList<Trees> treeLs = new ArrayList<Trees>();
    public int currentPlayer;
    public int power=0;
    public int maxPower=100;
    public boolean isFired=false;
    int countTank=0;
    PImage bgLoad;
    float[] heightLs;
    public Levels currentLevel;
    public ArrayList<Projectile> projectiles = new ArrayList<>();
    PVector location;
    PVector gravity;
    PVector velocity;


    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        
        //load images
        basic = loadImage("src/main/resources/Tanks/basic.png");
        desert = loadImage("src/main/resources/Tanks/desert.png");
        forest = loadImage("src/main/resources/Tanks/fuel.png");
        fuel = loadImage("src/main/resources/Tanks/fuel.png");
        fuel.resize(23, 23);
        hills = loadImage("src/main/resources/Tanks/hills.png");
        parachute = loadImage("src/main/resources/Tanks/parachute.png");
        snow = loadImage("src/main/resources/Tanks/snow.png");
        tree1 = loadImage("src/main/resources/Tanks/tree1.png");
        tree2 = loadImage("src/main/resources/Tanks/tree2.png");
        wind1 = loadImage("src/main/resources/Tanks/wind-1.png");
        wind = loadImage("src/main/resources/Tanks/wind.png");


        // load JSON
        configJSON = loadJSONObject(configPath);
        levelsArray = configJSON.getJSONArray("levels");
        for (int i = 0; i < levelsArray.size(); i++) {
            levelObj = levelsArray.getJSONObject(i);
            layout = levelObj.getString("layout");
            background = levelObj.getString("background");
            foregroundColor = levelObj.getString("foreground-colour", ""); 
            if(levelObj.hasKey("trees")){
                trees = levelObj.getString("trees", ""); 
            }
            
        }


        playerColoursObj = configJSON.getJSONObject("player_colours");
        Set players = playerColoursObj.keys();
        List<String> ls = new ArrayList<String>(players);
        playersLs = ls;
        
        
        //draw terrain
        Levels level = new Levels(0, levelsArray); //level number can change
        currentLevel = level;
        currentLevel.setup();
        bgLoad =loadImage("src/main/resources/Tanks/"+currentLevel.bg);
        heightLs = currentLevel.setTerrain();
        

        for(String j:currentLevel.lis){
            int e=0;
            for(int d=0;d<28;d++){
                e+=1;
                if (j.length()>d && j!="" && j!="\n"){
                    char str_x = j.charAt(d);
                    // trees
                    if(str_x=='T'){
                        
                        
                        float y = heightLs[d*32]-20;
                        float x = d*32-10;
                        // PImage loadTree = loadImage(level.treePath);
                        Trees tree = new Trees(currentLevel.treePath, x, y);
                        treeLs.add(tree);
                        countTree+=1;

                    }else if(str_x=='A'|| str_x=='B'||str_x=='C'||str_x=='D'||str_x=='E'||str_x=='F'||str_x=='G'||str_x=='H'||str_x=='I'||str_x=='0'||str_x=='1'||str_x=='2'||str_x=='3'||str_x=='4'||str_x=='5'||str_x=='6'||str_x=='7'||str_x=='8'||str_x=='9'){
                        
                        String str_x1 = String.valueOf(str_x);
                        String colour = playerColoursObj.getString(str_x1);
                        Tanks tank = new Tanks(str_x1, d, colour, heightLs, levelsArray);
                        tanks[countTank] = tank;
                        
                        countTank+=1;
                            
                    }
                  
                    
                }
            }
        }

        currentPlayer = 0;
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        Tanks currentTank = tanks[currentPlayer];
        
            
        if(keyCode==UP){
            currentTank.moveTurret(-3 * radians(1));
            // System.out.println("up");
        }
        if (keyCode == DOWN) {
            currentTank.moveTurret(3 * radians(1));
        }
        if (keyCode == LEFT) {
            currentTank.move(-1);
        }
        if (keyCode == RIGHT) {
            currentTank.move(1);
        
        }
        if (key == 'w' || key == 'W') {
            currentTank.adjustPower(1);
            }
            if (key == 's' || key == 'S') {
            currentTank.adjustPower(-1);
            }
            if (key == ' ') {
                if (!isFired) {
                    currentTank.fire();
                    isFired = true;
                    nextPlayer();
                }
        }
            
        
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport


    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        
        //terrain
        background(bgLoad);
        
        fill(parseInt(currentLevel.fg_color[0]), parseInt(currentLevel.fg_color[1]), parseInt(currentLevel.fg_color[2]));
        stroke(parseInt(currentLevel.fg_color[0]), parseInt(currentLevel.fg_color[1]), parseInt(currentLevel.fg_color[2]));
        for(int b=0; b<heightLs.length;b++){
            rect(b, heightLs[b], 1, CELLHEIGHT*(30));

        }

       //trees
        for(Trees i:treeLs){
            if (i!=null){
                PImage loadTree = loadImage(i.treePath);
                loadTree.resize(25, 25);
                image(loadTree, i.x, i.y);
            }
        }
    
        //tanks
        Tanks[] tanksLimited = new Tanks[countTank];
        for(int i=0;i< tanksLimited.length;i++){
            tanksLimited[i] = tanks[i];
        }
        Tanks[] orderTank = new Tanks[tanks.length];
        for(Tanks i:tanksLimited){
            int num = letter(i.name);
            orderTank[num] = i;
        }
        tanks = orderTank;
       
        for(Tanks i:tanks){
            if (i!=null){
                i.display();
            }
        }

        //display scoreboard:
        displayHUD();
        
    }


    int letter(String letter){
        int num=-1;
        if(letter.equals("A")) num=0;
        else if(letter.equals("B")) num=1;
        else if(letter.equals("C")) num=2;
        else if(letter.equals("D")) num=3;
        else if(letter.equals("E")) num=4;
        else if(letter.equals("F")) num=5;
        else if(letter.equals("G")) num=6;
        else if(letter.equals("H")) num=7;
        else if(letter.equals("I")) num=8;
        else num=parseInt(letter);
        return num;
    }
    
    //display HUD:
    public void displayHUD(){
        Tanks currentTank = tanks[currentPlayer];
        textSize(14);
        fill(0);
        text("Player " + currentTank.name +"'s turn", 20, 25);
        image(fuel, 160, 5);
        text(currentTank.fuel, 190, 25);       
        text("Health:", 400, 25);
        text(currentTank.health, 600, 25);   
        text("Power:  "+currentTank.power, 400, 50);
        text("Scores", 725, 70);
        noFill();
        strokeWeight(3);
        beginShape();
        vertex(720, 50);
        vertex(850, 50);
        vertex(850, 150);
        vertex(720, 150);
        endShape(CLOSE);

        beginShape(LINES);
        vertex(720, 75);
        vertex(850, 75);
        endShape();
        int count=0;
        for(Tanks i:tanks){
            if(i!=null){
                fill(parseInt(i.colour[0]), parseInt(i.colour[1]), parseInt(i.colour[2]));
                text("Player "+i.name,725, 90+count);
                count+=17;
            }
            
        }
        

    }
    

    public void nextPlayer(){
        currentPlayer = (currentPlayer + 1) % tanks.length;
        isFired = false;
    }
    
    // for trees
    public class Trees{
        String treePath;
        float x;
        float y;
        public Trees(String treePath, float x, float y){
            this.treePath = treePath;
            this.x = x;
            this.y = y;
        }
    }
    
    // class for tanks
    public class Tanks {

        public String[] lis;
        public float[] heightLs;
        public JSONArray levelsArray;
        public String[] colour;
        public float x;
        public float y;
        float turretAngle;
        public float turretLength;
        public float turretWidth;
        public String name;
        int health;
        int fuel;
        int power;
        // Projectile p;
    
        public Tanks(String name, int d, String tempC, float[] heightLs, JSONArray levelsArray){
            this.colour = tempC.split(",");
            this.heightLs = heightLs;
            this.levelsArray = levelsArray;
            this.y = heightLs[d*32];
            this.x = d*32;
            this.turretAngle=0;
            this.turretLength = 10;
            this.turretWidth = (float) 0.5;
            this.name = name;
            this.fuel = 250;
            this.power = 50;

        }
        void move(int dir) {
            if (fuel > 0) {
                x += dir * (60.0 / frameRate);
                x = constrain(x, 0, WIDTH);
                int intX = parseInt(x);
                y = heightLs[intX];
                fuel -= abs(dir) * (60.0 / frameRate);
                if (fuel < 0) fuel = 0;
              }
        }
        void moveTurret(float angleChange) {
            this.turretAngle += angleChange; 
        }

        void adjustPower(int amount) {
            power = constrain(power + amount, 0, min(maxPower, health));
        }
          
        void display() {
            // Draw tank body (trapezoid)
            fill(parseInt(colour[0]), parseInt(colour[1]), parseInt(colour[2]));
            stroke(parseInt(colour[0]), parseInt(colour[1]), parseInt(colour[2]));
            ellipse(x, y-5, 12, 2);
            ellipse(x, y, 18, 2);
            
            // Draw turret (rectangle)
            pushMatrix();
            translate(x, y-5);
            rotate(this.turretAngle);
            // System.out.println("angle in display: "+ turretAngle);
            fill(100);
            stroke(0);
            ellipse(-turretWidth / 2, -turretLength+4, turretWidth, turretLength);
            popMatrix();
        }
        void fire() {
            // Implement projectile firing
            float vx = power * cos(this.turretAngle);
            float vy = power * sin(this.turretAngle);
            Projectile p = new Projectile(x, y - 20, vx, vy, this);
            // projectiles.add(p);
            p.update();
            p.display();
            
        }
        
        

        void explode(float radius) { //not sure
            fill(255, 0, 0, 128);
            ellipse(x, y, radius * 2, radius * 2);
            health = 0;
          }
    }
    
    public class Projectile {
        
        Tanks owner;
        PVector location;
        PVector gravity;
        PVector velocity;
        
        Projectile(float x, float y, float vx, float vy, Tanks owner) {
          this.location = new PVector(x, y);
          this.velocity = new PVector(vx, vy); //velocity, not sure
          this.gravity = new PVector(0,(float) 0.2);
          this.owner = owner;

        }
        
        void update() {

            location.add(velocity);
            velocity.add(gravity);
            if ((location.x > WIDTH) || (location.x < 0)) {
                velocity.x = velocity.x * -1;
            }
            if (location.y > height) {
                // We're reducing velocity ever so slightly 
                // when it hits the bottom of the window
                velocity.y = (float) (velocity.y * -0.95); 
                location.y = height;
            }
            if (heightLs[(int) location.x]==location.y || heightLs[(int) location.x+1]==location.y||heightLs[(int) location.x-1]==location.y){
                for(int i=0; i<15;i++){
                    i=1; //TODO
                }
            }
        }
        void display(){
            stroke(0);
            fill(0);
            ellipse(location.x,location.y,5,5);
        }
    }
    
    
    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
