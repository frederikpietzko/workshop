package iits.workshop.htmx.service;

import iits.workshop.htmx.model.CarModel;
import iits.workshop.htmx.model.CarModelRepository;
import iits.workshop.htmx.model.Make;
import iits.workshop.htmx.model.MakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MakeRepository makeRepository;
    private final CarModelRepository modelRepository;

    @Override
    public void run(ApplicationArguments args) {
        // BMW
        Make bmw = makeRepository.save(new Make("BMW"));
        modelRepository.save(new CarModel("3 Series", bmw));
        modelRepository.save(new CarModel("5 Series", bmw));
        modelRepository.save(new CarModel("7 Series", bmw));
        modelRepository.save(new CarModel("X3", bmw));
        modelRepository.save(new CarModel("X5", bmw));

        // Mercedes-Benz
        Make mercedes = makeRepository.save(new Make("Mercedes-Benz"));
        modelRepository.save(new CarModel("A-Class", mercedes));
        modelRepository.save(new CarModel("C-Class", mercedes));
        modelRepository.save(new CarModel("E-Class", mercedes));
        modelRepository.save(new CarModel("S-Class", mercedes));
        modelRepository.save(new CarModel("GLE", mercedes));

        // Audi
        Make audi = makeRepository.save(new Make("Audi"));
        modelRepository.save(new CarModel("A3", audi));
        modelRepository.save(new CarModel("A4", audi));
        modelRepository.save(new CarModel("A6", audi));
        modelRepository.save(new CarModel("Q5", audi));
        modelRepository.save(new CarModel("Q7", audi));

        // Volkswagen
        Make vw = makeRepository.save(new Make("Volkswagen"));
        modelRepository.save(new CarModel("Golf", vw));
        modelRepository.save(new CarModel("Passat", vw));
        modelRepository.save(new CarModel("Tiguan", vw));
        modelRepository.save(new CarModel("Touareg", vw));

        // Porsche
        Make porsche = makeRepository.save(new Make("Porsche"));
        modelRepository.save(new CarModel("911", porsche));
        modelRepository.save(new CarModel("Cayenne", porsche));
        modelRepository.save(new CarModel("Panamera", porsche));
        modelRepository.save(new CarModel("Macan", porsche));
    }
}
