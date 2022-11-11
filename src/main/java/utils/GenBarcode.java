/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author SML-DEV-PC9
 */
public class GenBarcode {
    
    public byte[] _qrcodeByte(String content) {
        byte[] __value = new byte[1024];

        try {
            QRCodeWriter __writer = new QRCodeWriter();

            ByteArrayOutputStream __output = new ByteArrayOutputStream();
            //Hashtable hintMap = new Hashtable();
            //hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            int __size = 100;
            BitMatrix _bitMatrix = __writer.encode(content, BarcodeFormat.QR_CODE, __size, __size);

            //byte[][] __array = _bitMatrix.getArray();
            int __width = _bitMatrix.getWidth();
            //int __height = _bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(__width, __width, BufferedImage.TYPE_INT_RGB);

            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, __width, __width);
            // Paint and save the image using the ByteMatrix
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < __width; i++) {
                for (int j = 0; j < __width; j++) {
                    if (_bitMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            ImageIO.write(image, "png", __output);
            __value = __output.toByteArray();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return __value;
    }
    
    public byte[] _barcodeByte(String content,int _width,int _height) {
        byte[] __value = new byte[1024];

        try {
            MultiFormatWriter writer =new  MultiFormatWriter();
            ByteArrayOutputStream __output = new ByteArrayOutputStream();
            //Hashtable hintMap = new Hashtable();
            //hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            
           
            BitMatrix _bitMatrix =  _bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, _width,_height );
            
            //byte[][] __array = _bitMatrix.getArray();
            int __width = _bitMatrix.getWidth();
            int __height = _bitMatrix.getHeight();
            //int __height = _bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(__width, __height, BufferedImage.TYPE_INT_RGB);

            image.createGraphics();
            int width   = _bitMatrix.getWidth ();
            int height  = _bitMatrix.getHeight ();
            
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, __width, __height);
            // Paint and save the image using the ByteMatrix
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < __width; i++) {
                for (int j = 0; j < __height; j++) {
                    if (_bitMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            
            
            ImageIO.write(image, "png", __output);
            __value = __output.toByteArray();
     
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return __value;
    }
    
     public byte[] _barcodeByte2(String content,int _width,int _height) {
        byte[] __value = new byte[1024];

        try {
            MultiFormatWriter writer =new  MultiFormatWriter();
            ByteArrayOutputStream __output = new ByteArrayOutputStream();
            //Hashtable hintMap = new Hashtable();
            //hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            
           
            BitMatrix _bitMatrix =  _bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, _width,_height );
            
            //byte[][] __array = _bitMatrix.getArray();
            int __width = _bitMatrix.getWidth();
            int __height = _bitMatrix.getHeight();
            //int __height = _bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(__width, __height, BufferedImage.TYPE_INT_RGB);

            image.createGraphics();
            int width   = _bitMatrix.getWidth ();
            int height  = _bitMatrix.getHeight ();
            
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, __width, __height);
            // Paint and save the image using the ByteMatrix
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < __width; i++) {
                for (int j = 0; j < __height; j++) {
                    if (_bitMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            
            
            ImageIO.write(image, "png", __output);
            __value = __output.toByteArray();
     
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return __value;
    }
    
}
