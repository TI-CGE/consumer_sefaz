package br.gov.se.setc.consumer.contracts;
import java.util.Map;
import br.gov.se.setc.util.ValidacaoUtil;
public interface  Parametrizador {
     /**
     * @param ugCd   unidade gestora code
     * @param ano    year to query
     * @return map of query parameters for this DTO and year
     */
    public  Map<String,Object> getCamposParametrosTodosOsAnos(String ugCd, Short ano);
    /**
     * @param ugCd   unidade gestora code
     * @param utilsService   utility objects for consults and validation
     * @return map of query parameters for this DTO and year
     */
     public  Map<String, Object> getCamposParametrosAtual(String ugCd, ValidacaoUtil utilsService);
}