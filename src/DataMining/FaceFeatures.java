/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataMining;

/**
 *
 * @author arman
 */
public class FaceFeatures {

    public static enum Gender {
        male, female, unknown
    }
    public static enum HairModel {
        straight, curly, bald, unknown
    }
    public static enum BeardModel {
        goat, fill, none, unknown
    }
    public static enum Glasses {
        yes, no, unknown
    }

    public Gender gender;
    public Glasses glasses;
    public HairModel hairModel;
    public BeardModel beardModel;
    public String hairColor, eyeColor, skinColor;   // "" means Unknown ?
    public int hairLong, mustacheLong, beardLong, noseSize; // -1 means unknown ?
    public int age;     // -1 means unknown ?

}
