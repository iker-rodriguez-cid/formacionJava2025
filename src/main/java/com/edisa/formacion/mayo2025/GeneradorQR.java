package com.edisa.formacion.mayo2025;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GeneradorQR {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java GeneradorQR \"<texto>\" \"<ruta_salida.jpg>\"");
            System.exit(1);
        }

        String texto = args[0];
        String rutaSalida = args[1];

        try {
            generarQR(texto, rutaSalida, 300, 300);
            System.out.println("Código QR generado correctamente en: " + rutaSalida);
        } catch (WriterException | IOException e) {
            System.err.println("Error generando el código QR: " + e.getMessage());
        }
    }

    public static void generarQR(String texto, String rutaArchivo, int ancho, int alto)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto);

        Path path = new File(rutaArchivo).toPath();
        MatrixToImageWriter.writeToPath(bitMatrix, "JPG", path);
    }
}


