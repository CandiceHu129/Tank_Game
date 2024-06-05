package Tanks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;


public class Levels{
    public int level;
    public JSONArray levelsArray;
    public String terrain_path;
    public String bg;
    public String[] fg_color;
    public PVector v1;
    public int count;
    public float[] heightLs = new float[896];
    public String[] lis;
    public String treePath;

    public Levels(int level, JSONArray levelsArray){
        this.level = level;
        this.levelsArray = levelsArray;
    }
    public void setup(){
        this.terrain_path = levelsArray.getJSONObject(level).getString("layout"); // 0 can be changed
   
        this.bg = levelsArray.getJSONObject(level).getString("background");
    
        this.fg_color = levelsArray.getJSONObject(level).getString("foreground-colour").split(",");
   
        if (levelsArray.getJSONObject(level).hasKey("trees")){
            this.treePath = "src/main/resources/Tanks/"+levelsArray.getJSONObject(level).getString("trees");
        }
    }
    public float[] setTerrain(){
        try{
        File file = new File(this.terrain_path);
        Scanner scan = new Scanner(file);
        StringBuilder content = new StringBuilder();
        while (scan.hasNextLine()) {
            content.append(scan.nextLine());
            content.append("\n"); 
        }
        scan.close();
        String levelStr = content.toString(); // read content from e.g. level1.txt
        String[] allContent = levelStr.split("\n");
        this.lis = allContent;
        } catch (FileNotFoundException e) {
            System.out.println("file not found:" + terrain_path);
            e.printStackTrace();
        }
        
        
        int i = 0; // calculate the x-coordinate
        while (i<28) {
            int e = 0; // calculate the height
      
            for (String j: lis){
                e +=1;
                if (j.length()>i && j!="" && j!="\n"){
                    char str_x = j.charAt(i);
                    if (str_x=='X'){
                        if (terrain_path.equals("level1.txt")){
                            v1 = new PVector((i)*32, (e-3)*App.CELLHEIGHT);
                            
                        }else{
                            v1 = new PVector((i)*32, (e)*App.CELLHEIGHT);
                            
                        }
                        for(int q=0; q<App.CELLSIZE; q++){
                            heightLs[count] = v1.y;
                            count +=1;
                        }
                         
                    }
                }
                
            }
            i+=1;
        }
        for(int b=0; b<heightLs.length;b++){
            int sum = 0;
            int count = 0;
            for (int c=b; c<b+32 && c<heightLs.length; c++){
                sum+=heightLs[c];
                count+=1;
                
            }
            heightLs[b] = sum / count;
        }
        for(int b=0; b<heightLs.length;b++){
            int sum = 0;
            int count = 0;
            for (int c=b; c<b+32 && c<heightLs.length; c++){
                sum+=heightLs[c];
                count+=1;
                
            }
            heightLs[b] = sum / count;
        }
        return heightLs;
    }
    
}