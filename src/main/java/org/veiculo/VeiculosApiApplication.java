
package org.veiculo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VeiculosApiApplication {
    public static void main(String[] args) {

        SpringApplication.run(VeiculosApiApplication.class, args);
    }
}