package gklijs.tech.sticker.axon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JugStickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JugStickerApplication.class, args);
    }
}
