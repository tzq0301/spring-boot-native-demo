package org.example;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

@SpringBootApplication
@ImportRuntimeHints(HintsRegistrar.class)
public class SpringBootNativeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootNativeDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner reflect() {
        return args -> {
            Customer customer = (Customer) Class.forName("org.example.Customer")
                    .getDeclaredConstructors()[0]
                    .newInstance("May");
            System.out.println(customer);
        };
    }

    @Bean
    public CommandLineRunner contributors() {
        return args -> {
            var contributors = new ClassPathResource("CONTRIBUTORS");
            var content = FileCopyUtils.copyToString(new InputStreamReader(contributors.getInputStream()));
            System.out.println("CONTRIBUTORS: " + content);
        };
    }

}

record Customer(String name) {
}

class HintsRegistrar implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerResource(new ClassPathResource("CONTRIBUTORS"));
        hints.reflection().registerType(Customer.class, MemberCategory.values());
    }
}
