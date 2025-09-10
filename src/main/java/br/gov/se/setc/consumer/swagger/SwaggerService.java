package br.gov.se.setc.consumer.swagger;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.entity.ContratosFiscais;
import br.gov.se.setc.consumer.repository.EndpontSefazRepository;
import jakarta.websocket.Endpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class SwaggerService {
    private final EndpontSefazRepository endpontSefazRepository;
    @Autowired
    public SwaggerService( EndpontSefazRepository endpontSefazRepository) {
        this.endpontSefazRepository = endpontSefazRepository;
    }
}