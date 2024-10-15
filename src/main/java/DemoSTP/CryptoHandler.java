package DemoSTP;

import java.util.Base64;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;

public class CryptoHandler {
    private static String fileName = "nycapital.jks";
    private static String password = "12345678";
    private static String alias = "nycapital";

    // Método para firmar la cadena
    private String sign(String data) throws Exception {
        // Cargar el KeyStore
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyStoreStream = new FileInputStream(fileName)) {
            keyStore.load(keyStoreStream, password.toCharArray());
        }

        // Obtener la clave privada
        Key key = keyStore.getKey(alias, password.toCharArray());
        if (!(key instanceof RSAPrivateKey)) {
            throw new Exception("No es una clave privada RSA");
        }
        RSAPrivateKey privateKey = (RSAPrivateKey) key;

        // Firmar la cadena
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signedData = signature.sign();

        // Retornar la firma en Base64
        return Base64.getEncoder().encodeToString(signedData);
    }

    public String firmar(OrdenPagoWS oPW) throws Exception {
        String firma;
        try {
            firma = sign(cadenaOriginal(oPW));
        } catch (Exception e) {
            throw new Exception("Exception: " + e.getMessage(), e.getCause());
        }
        return firma;
    }

    public String cadenaOriginal(OrdenPagoWS oPW) {
        StringBuilder sB = new StringBuilder();
        sB.append("||");
        sB.append(oPW.getInstitucionContraparte()).append("|");
        sB.append(oPW.getEmpresa()).append("|");
        sB.append(oPW.getFechaOperacion() == null ? "" : oPW.getFechaOperacion()).append("|");
        sB.append(oPW.getFolioOrigen() == null ? "" : oPW.getFolioOrigen()).append("|");
        sB.append(oPW.getClaveRastreo() == null ? "" : oPW.getClaveRastreo()).append("|");
        sB.append(oPW.getInstitucionOperante() == null ? "" : oPW.getInstitucionOperante()).append("|");
        sB.append(oPW.getMonto() == null ? "" : oPW.getMonto()).append("|");
        sB.append(oPW.getTipoPago() == null ? "" : oPW.getTipoPago()).append("|");
        sB.append(oPW.getTipoCuentaOrdenante() == null ? "" : oPW.getTipoCuentaOrdenante()).append("|");
        sB.append(oPW.getNombreOrdenante() == null ? "" : oPW.getNombreOrdenante()).append("|");
        sB.append(oPW.getCuentaOrdenante() == null ? "" : oPW.getCuentaOrdenante()).append("|");
        sB.append(oPW.getRfcCurpOrdenante() == null ? "" : oPW.getRfcCurpOrdenante()).append("|");
        sB.append(oPW.getTipoCuentaBeneficiario() == null ? "" : oPW.getTipoCuentaBeneficiario()).append("|");
        sB.append(oPW.getNombreBeneficiario() == null ? "" : oPW.getNombreBeneficiario()).append("|");
        sB.append(oPW.getCuentaBeneficiario() == null ? "" : oPW.getCuentaBeneficiario()).append("|");
        sB.append(oPW.getRfcCurpBeneficiario() == null ? "" : oPW.getRfcCurpBeneficiario()).append("|");
        sB.append(oPW.getEmailBeneficiario() == null ? "" : oPW.getEmailBeneficiario()).append("|");
        sB.append(oPW.getTipoCuentaBeneficiario2() == null ? "" : oPW.getTipoCuentaBeneficiario2()).append("|");
        sB.append(oPW.getNombreBeneficiario2() == null ? "" : oPW.getNombreBeneficiario2()).append("|");
        sB.append(oPW.getCuentaBeneficiario2() == null ? "" : oPW.getCuentaBeneficiario2()).append("|");
        sB.append(oPW.getRfcCurpBeneficiario2() == null ? "" : oPW.getRfcCurpBeneficiario2()).append("|");
        sB.append(oPW.getConceptoPago() == null ? "" : oPW.getConceptoPago()).append("|");
        sB.append(oPW.getConceptoPago2() == null ? "" : oPW.getConceptoPago2()).append("|");
        sB.append(oPW.getClaveCatUsuario1() == null ? "" : oPW.getClaveCatUsuario1()).append("|");
        sB.append(oPW.getClaveCatUsuario2() == null ? "" : oPW.getClaveCatUsuario2()).append("|");
        sB.append(oPW.getClavePago() == null ? "" : oPW.getClavePago()).append("|");
        sB.append(oPW.getReferenciaCobranza() == null ? "" : oPW.getReferenciaCobranza()).append("|");
        sB.append(oPW.getReferenciaNumerica() == null ? "" : oPW.getReferenciaNumerica()).append("|");
        sB.append(oPW.getTipoOperacion() == null ? "" : oPW.getTipoOperacion()).append("|");
        sB.append(oPW.getTopologia() == null ? "" : oPW.getTopologia()).append("|");
        sB.append(oPW.getUsuario() == null ? "" : oPW.getUsuario()).append("|");
        sB.append(oPW.getMedioEntrega() == null ? "" : oPW.getMedioEntrega()).append("|");
        sB.append(oPW.getPrioridad() == null ? "" : oPW.getPrioridad()).append("|");
        sB.append(oPW.getIva() == null ? "" : oPW.getIva()).append("||");
        String cadena = sB.toString();
        System.out.println("Cadena original: " + cadena);
        return cadena;
    }
}
