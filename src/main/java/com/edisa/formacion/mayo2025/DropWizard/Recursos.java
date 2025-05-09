package com.edisa.formacion.mayo2025.DropWizard;

import com.edisa.formacion.mayo2025.GeneradorQRMejorado;
import com.google.zxing.*;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import com.google.zxing.multi.MultipleBarcodeReader;
import com.google.zxing.WriterException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.imageio.ImageIO;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Path("/api/codigoQR")
public class Recursos {

    // Endpoint para generar código QR
    @GET
    @Produces("image/png")
    public Response getQR(@QueryParam("texto") String texto) {
        if (texto == null || texto.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar el parámetro 'texto'")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        try {
            BufferedImage image = GeneradorQRMejorado.generarImagenCodigo(texto, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return Response.ok(imageBytes)
                    .type("image/png")
                    .build();

        } catch (WriterException | IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generando el QR: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    // Nuevo endpoint para leer códigos de barras desde una imagen
    @POST
    @Path("/leer")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response leerCodigosDeBarras(@FormDataParam("imagen") InputStream imagenInputStream) {
        try {
            BufferedImage imagen = ImageIO.read(imagenInputStream);

            LuminanceSource source = new BufferedImageLuminanceSource(imagen);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.values()));

            List<Map<String, String>> codigosDetectados = new ArrayList<>();
            MultipleBarcodeReader multiReader = new GenericMultipleBarcodeReader(new MultiFormatReader());

            Result[] results = multiReader.decodeMultiple(bitmap, hints);
            for (Result result : results) {
                Map<String, String> codigo = new HashMap<>();
                codigo.put("tipo", result.getBarcodeFormat().toString());
                codigo.put("valor", result.getText());
                codigosDetectados.add(codigo);
            }

            return Response.ok(codigosDetectados).build();

        } catch (NotFoundException e) {
            return Response.ok(Collections.emptyList()).build(); // No se encontró ningún código
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al procesar la imagen: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
