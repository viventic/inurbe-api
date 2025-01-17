package co.com.minvivienda.inurbe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * 
 * @author James R
 *
 */
public class PdfProcessor implements Processor {
	
    @Override
    public void process(Exchange exchange) throws Exception {
    	String matricula = (String) exchange.getIn().getHeader("matricula");
    	System.out.println("matricula=" + matricula);
    	
        //Nombre del certificado
        String archivoCertificado = matricula + ".pdf";
        byte[] pdfContent = new byte[0];
        try {
            //Lee el contenido del PDF
            pdfContent = PdfProcessor.readFileFromClasspath(archivoCertificado);   
            
            //Establece el contenido como cuerpo de la respuesta
            exchange.getMessage().setBody(pdfContent);
            exchange.getMessage().setHeader("Content-Length", pdfContent.length);
            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/pdf");
            exchange.getMessage().setHeader("Content-Disposition", "attachment; filename=INURBE-certificado-obligacion-financiera.pdf");
            
        }catch(IOException ex) {
        	ex.printStackTrace();
        	String errorMessage = String.format(
                "El certificado de la matricula inmbiliaria '%s' no fue encontrado en el servidor. ",
                matricula
            );
            
            exchange.getMessage().setBody(errorMessage);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
        }
    }
    
    
    /**
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    private static byte[] readFileFromClasspath(String fileName) throws IOException {
        Resource resource = new ClassPathResource(fileName);
        if (!resource.exists()) {
            throw new IOException("No se encontro el certifiado especificado: " + fileName);
        }
        
        
        //System.out.println("exists = " + resource.exists());
        //System.out.println("contentLength = " + resource.contentLength());
        //System.out.println(resource.getURI());
        try (InputStream inputStream = resource.getInputStream()) {
            
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[inputStream.available()];
            int bytesRead;
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            
            return buffer.toByteArray();
        }
    }
}