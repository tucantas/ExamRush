package es.examrush.config;

import es.examrush.model.Objeto;
import es.examrush.repository.ObjetoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.util.List;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seedObjects(ObjetoRepository objetos) {
        return args -> {
            if (objetos.count() == 0) objetos.saveAll(List.of(
                    new Objeto("Apuntes de Java", "Objeto de prueba de la tienda EETAC.", new BigDecimal("20.00"), 25),
                    new Objeto("Café de biblioteca", "Objeto de prueba de la tienda EETAC.", new BigDecimal("10.00"), 40),
                    new Objeto("Cuaderno técnico", "Objeto de prueba de la tienda EETAC.", new BigDecimal("30.00"), 15)
            ));
        };
    }
}
