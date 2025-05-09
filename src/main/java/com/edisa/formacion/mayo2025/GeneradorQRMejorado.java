package com.edisa.formacion.mayo2025;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GeneradorQRMejorado {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Uso: java GeneradorCodigo \"<texto>\" \"<ruta_salida.jpg>\" \"<formato>\"");
            System.err.println("Ejemplo: java GeneradorCodigo \"1234567890128\" \"C:\\codigos\\codigo.jpg\" \"EAN_13\"");
            System.exit(1);
        }

        String texto = args[0];
        String rutaSalida = args[1];
        String formatoEntrada = args[2].toUpperCase();

        try {
            BarcodeFormat formato = obtenerFormato(formatoEntrada);
            generarCodigo(texto, rutaSalida, formato, 300, 300);
            System.out.println("Código generado correctamente en: " + rutaSalida);

        } catch (IllegalArgumentException e) {
            System.err.println("Formato no válido: " + formatoEntrada);
            System.err.println("Formatos soportados: QR_CODE, EAN_13, CODE_128, etc.");

        } catch (WriterException | IOException e) {
            System.err.println("Error generando el código: " + e.getMessage());
        }
    }

    private static BarcodeFormat obtenerFormato(String formato) {
        return BarcodeFormat.valueOf(formato);
    }

    private static void generarCodigo(String texto, String rutaArchivo, BarcodeFormat formato, int ancho, int alto)
            throws WriterException, IOException {

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(texto, formato, ancho, alto);

        File archivoSalida = new File(rutaArchivo);
        File carpeta = archivoSalida.getParentFile();
        if (!carpeta.exists()) {
            carpeta.mkdirs(); // Crea la carpeta si no existe
        }

        Path path = archivoSalida.toPath();
        MatrixToImageWriter.writeToPath(matrix, "JPG", path);
    }

    public static BufferedImage generarImagenCodigo(String texto, BarcodeFormat formato, int ancho, int alto)
            throws WriterException {

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(texto, formato, ancho, alto);

        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}


