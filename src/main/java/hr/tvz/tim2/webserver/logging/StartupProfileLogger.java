package hr.tvz.tim2.webserver.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class StartupProfileLogger implements ApplicationRunner {
    private final Environment env;

    public StartupProfileLogger(Environment env) {
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] active = env.getActiveProfiles();
        String[] defaults = env.getDefaultProfiles();

        log.info("Active Spring profiles: {}", active.length == 0 ? "(none)" : Arrays.toString(active));
        log.info("Default Spring profiles: {}", Arrays.toString(defaults));
    }
}