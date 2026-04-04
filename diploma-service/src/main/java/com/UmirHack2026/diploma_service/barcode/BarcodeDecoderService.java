package com.UmirHack2026.diploma_service.barcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Service
public class BarcodeDecoderService {

    public String decoderBarcode(MultipartFile file){
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap((new HybridBinarizer(source)));

            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        }catch (Exception e){
            throw new RuntimeException("Ошибка декодирования штрих-кода" , e);
        }
    }

    public String decoderBarcode(byte[] file){
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        }catch (Exception e){
            throw new RuntimeException("Ошибка декодирования штрих-кода" , e);}
    }
}