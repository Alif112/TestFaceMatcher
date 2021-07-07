/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.revesoft.alif;
import java.util.Properties;
import org.apache.log4j.*;
/**
 *
 * @author alif
 */
public class TestAlif {
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        
	PictureDTO picture=new PictureDTO();
	picture.nidPicturePath=ImageSource.nidPicturePath;
	picture.selfiePicturePath=ImageSource.selfiePicturePath;
        
	FaceMatcher faceMatcher=FaceMatcher.getInstance();
        faceMatcher.addToQueue(picture);
        
        PictureDTO picture2=new PictureDTO();
	picture2.nidPicturePath=ImageSource.nidPicturePath;
	picture2.selfiePicturePath=ImageSource.selfiePicturePath2;
        faceMatcher.addToQueue(picture2);
        
        faceMatcher.checkNidImageFaceMatchesWithSelfiImage(ImageSource.nidPicturePath, ImageSource.selfiePicturePath);
        faceMatcher.checkNidImageFaceMatchesWithSelfiImage(ImageSource.nidPicturePath, ImageSource.selfiePicturePath2);
        faceMatcher.checkNidImageFaceMatchesWithSelfiImage(ImageSource.nidPicturePath, ImageSource.selfiePicturePath);
        faceMatcher.checkNidImageFaceMatchesWithSelfiImage(ImageSource.nidPicturePath, ImageSource.selfiePicturePath2);
        
        
        
    }
}
