package DemoSTP;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;

public class Main {
    private static String url = "https://demo.stpmex.com:7024/speiws/rest/ordenPago/registra";
    private int connectionTimeout = 5000;
    private int receptionTimeout = 15000;
    //trustStore = ${JAVA_HOME}/lib/security/cacerts
    private String trustStore = "";
    private String trusStorePassword = "changeit";
    public static void main(String[] args) throws Exception {
        new Main().enviaPago();
    }

    private void enviaPago() throws Exception {
        CryptoHandler cryptoHandler = new CryptoHandler();
        OrdenPagoWS ordenPagoWS = new OrdenPagoWS();
        ordenPagoWS.setEmpresa("BRANDME");
        ordenPagoWS.setMonto(new BigDecimal("0.01"));
        ordenPagoWS.setInstitucionContraparte(90646);
        ordenPagoWS.setClaveRastreo("Ras00002");
        ordenPagoWS.setReferenciaNumerica(1234567);
        ordenPagoWS.setNombreBeneficiario("Eduardo");
        ordenPagoWS.setConceptoPago("Pago de prueba");
        ordenPagoWS.setCuentaBeneficiario("646180257400000009");
        ordenPagoWS.setTipoCuentaBeneficiario(40);
        ordenPagoWS.setTipoPago(1);
        ordenPagoWS.setCuentaOrdenante("646180110400000007");
        ordenPagoWS.setTipoCuentaOrdenante(40);
        ordenPagoWS.setRfcCurpBeneficiario("ND");
        ordenPagoWS.setInstitucionOperante(90646);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("empresa", ordenPagoWS.getEmpresa());
        jsonObject.put("monto", ordenPagoWS.getMonto());
        jsonObject.put("institucionContraparte", ordenPagoWS.getInstitucionContraparte());
        jsonObject.put("claveRastreo", ordenPagoWS.getClaveRastreo());
        jsonObject.put("referenciaNumerica", ordenPagoWS.getReferenciaNumerica());
        jsonObject.put("nombreBeneficiario", ordenPagoWS.getNombreBeneficiario());
        jsonObject.put("conceptoPago", ordenPagoWS.getConceptoPago());
        jsonObject.put("cuentaBeneficiario", ordenPagoWS.getCuentaBeneficiario());
        jsonObject.put("tipoCuentaBeneficiario", ordenPagoWS.getTipoCuentaBeneficiario());
        jsonObject.put("tipoPago", ordenPagoWS.getTipoPago());
        jsonObject.put("cuentaOrdenante", ordenPagoWS.getCuentaOrdenante());
        jsonObject.put("tipoCuentaOrdenante", ordenPagoWS.getTipoCuentaOrdenante());
        jsonObject.put("rfcCurpBeneficiario", ordenPagoWS.getRfcCurpBeneficiario());
        jsonObject.put("institucionOperante", ordenPagoWS.getInstitucionOperante());
        jsonObject.put("firma", cryptoHandler.firmar(ordenPagoWS).replace("\n", ""));
        System.out.println(jsonObject);
        HttpClient(jsonObject.toString(), url);
    }
    public String HttpClient(String peticion, String url) throws Exception {
        String body;
        int respuesta;
        try (FileInputStream trustStream = new FileInputStream(this.trustStore)) {
            HttpParams httpParams = new BasicHttpParams();
            org.apache.http.params.HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
            org.apache.http.params.HttpConnectionParams.setSoTimeout(httpParams, receptionTimeout);
            DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
            System.out.println("url: " + url);
            if (url.startsWith("https")) {
                System.out.println("Se carga trustStore: " + trustStore);
                KeyStore trustStore1;
                trustStore1 = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore1.load(trustStream, trusStorePassword.toCharArray());
                SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore1);
                Scheme sch = new Scheme("https", 443, socketFactory);
                httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            }
            HttpPut httpPut = new HttpPut(url);
            System.out.println("peticion: " + peticion);
            HttpEntity entity = new ByteArrayEntity(peticion.getBytes(StandardCharsets.UTF_8));
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setEntity(entity);
            HttpResponse response = httpclient.execute(httpPut);
            respuesta = response.getStatusLine().getStatusCode();
            System.out.println("response Code: " + respuesta);
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("Error con HTTPStatusCode:" + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder stringBuilder = new StringBuilder();
            while ((body = br.readLine()) != null) {
                stringBuilder.append(body);
            }
            body = stringBuilder.toString();
            br.close();
            if (response.getEntity().getContent() != null) {
                response.getEntity().getContent().close();
            }
            System.out.println("Respuesta: " + body);
        } catch (CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new Exception("Exception" + e.getMessage(), e.getCause());
        }
        return body + " HttpCode: " + respuesta;
    }
}
