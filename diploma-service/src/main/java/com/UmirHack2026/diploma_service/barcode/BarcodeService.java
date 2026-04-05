package com.UmirHack2026.diploma_service.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class BarcodeService {

    public byte[] generateCode(String barcode){
        int size = 500;
        try{
            BitMatrix bitMatrix = new MultiFormatWriter().encode(barcode, BarcodeFormat.QR_CODE, size, size);
            BufferedImage image =  MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream byt = new ByteArrayOutputStream();
            ImageIO.write(image , "png" , byt);
            return byt.toByteArray();
        }catch (Exception e){
            throw new RuntimeException("Ошибка генерации штрих-кода");
        }

    }

}