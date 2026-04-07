package br.gov.se.setc.consumer.swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.gov.se.setc.consumer.repository.EndpontSefazRepository;
@Service
public class SwaggerService {
    private final EndpontSefazRepository endpontSefazRepository;
    @Autowired
    public SwaggerService( EndpontSefazRepository endpontSefazRepository) {
        this.endpontSefazRepository = endpontSefazRepository;
    }
}