/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


public class GameClient extends SimpleApplication {
    
    @Override
    public void simpleInitApp() {
        // Set up camera
        cam.setLocation(new Vector3f(0, 5, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        // Create a simple ground
        Box ground = new Box(20, 0.1f, 20);
        Geometry groundGeom = new Geometry("Ground", ground);
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setColor("Color", ColorRGBA.Green);
        groundGeom.setMaterial(groundMat);
        rootNode.attachChild(groundGeom);
        
        // Add lighting
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        // Create player character
        createCharacter(new Vector3f(0, 1, 0), ColorRGBA.Blue);
        
        // Create some enemies
        createCharacter(new Vector3f(3, 1, -3), ColorRGBA.Red);
        createCharacter(new Vector3f(-3, 1, -3), ColorRGBA.Red);
        createCharacter(new Vector3f(0, 1, -5), ColorRGBA.Red);
    }
    
    private void createCharacter(Vector3f position, ColorRGBA color) {
        // Create a simple box to represent a character
        Box box = new Box(0.5f, 1f, 0.5f);
        Geometry character = new Geometry("Character", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        character.setMaterial(mat);
        character.setLocalTranslation(position);
        rootNode.attachChild(character);
    }
    
    // Method to update character positions based on game state
    public void updateGameState(String gameStateJson) {
        // Parse JSON and update character positions
        // Implementation details omitted for brevity
    }
}