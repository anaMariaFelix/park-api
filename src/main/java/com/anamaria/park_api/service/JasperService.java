package com.anamaria.park_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JasperService {

    private final ResourceLoader resourceLoader;
    private final DataSource dataSource; //faz a coneção com banco,faz a configuração de conexao para usar o sql que contem no meu relatorio, fazendo com que a consulta seja processada

    private Map<String, Object> params = new HashMap<>();

    private static final String JASPER_DIRETORIO = "classpath:reports/"; //CONTEM O VALOR DE ONDE ESTAO OS DIRETORIOS


    public void addParams(String key, Object value){
        this.params.put("IMAGEM_DIRETORIO",JASPER_DIRETORIO); //"IMAGEM_DIRETORIO" REFERENTE A CONFIGURAÇÃO QUE FOI FEITA NO RELATORIO COM O CAMINHO DA PASTA IMAGES
        this.params.put("REPORT_LOCALE",new Locale("pt", "BR"));
        this.params.put(key, value);//se algum parametro for adicionado ao relatorio eles seram adicionados a parti desse metodo
    }

    //metodo que vai trabalhar realmente com um relatorio
    public byte[] gerarPdf() {//toda vez que esse metodo for chamado o spring vai la no diretorio reports e procura pelo arquivo estacionamentos_old.jasper
        byte[] bytes = null;
        try {
            Resource resource = resourceLoader.getResource(JASPER_DIRETORIO.concat("estacionamentos.jasper"));
            InputStream stream = resource.getInputStream();//contem o stream do relatorio que foi localizado
            JasperPrint print = JasperFillManager.fillReport(stream, params, dataSource.getConnection());//JasperFillManager.fillReport recebe todas as informações necssarias para que o arquivo receba os parametros que ele esta esperando
                                                                                                        // e tbm uma conexao com o banco de dados para que a consulta seja feita, processando a consulta e retornando um objeto jasperrePrint

            bytes = JasperExportManager.exportReportToPdf(print);//vai tranforma o relatorio em um array de bytes para um pdf
        } catch (IOException | JRException | SQLException e) {
            log.error("Jasper Reports ::: ", e.getCause());
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
