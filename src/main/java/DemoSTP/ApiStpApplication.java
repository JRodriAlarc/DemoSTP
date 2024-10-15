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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

@SpringBootApplication
public class ApiStpApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApiStpApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new PaymentService().enviaPago();
    }

    @Service
    public class PaymentService {
        private String url = "https://demo.stpmex.com:7024/speiws/rest/ordenPago/registra";
        private int connectionTimeout = 5000;
        private int receptionTimeout = 15000;
        private String trustStore = ""; // Define el path del trustStore
        private String trustStorePassword = "changeit";

        public void enviaPago() throws Exception {
            CryptoHandler cryptoHandler = new CryptoHandler();
            OrdenPagoWS ordenPagoWS = new OrdenPagoWS();
            // Configuración de la orden de pago
            // ... (Código para configurar ordenPagoWS)

            JSONObject jsonObject = new JSONObject();
            // Llenar el JSONObject con datos de ordenPagoWS
            // ... (Código para llenar jsonObject)

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

                if (url.startsWith("https")) {
                    KeyStore trustStore1 = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore1.load(trustStream, trustStorePassword.toCharArray());
                    SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore1);
                    Scheme sch = new Scheme("https", 443, socketFactory);
                    httpclient.getConnectionManager().getSchemeRegistry().register(sch);
                }

                HttpPut httpPut = new HttpPut(url);
                HttpEntity entity = new ByteArrayEntity(peticion.getBytes(StandardCharsets.UTF_8));
                httpPut.setHeader("Content-Type", "application/json");
                httpPut.setEntity(entity);
                HttpResponse response = httpclient.execute(httpPut);
                respuesta = response.getStatusLine().getStatusCode();
                
                // Procesar la respuesta
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
            } catch (Exception e) {
                throw new Exception("Exception: " + e.getMessage(), e.getCause());
            }
            return body + " HttpCode: " + respuesta;
        }
    }
}
