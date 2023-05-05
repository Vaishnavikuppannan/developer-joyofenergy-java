package uk.tw.energy.generator;

import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ElectricityReadingsGeneratorTest {

    ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();

    @Test
    public void given_number_should_generate_electricity_reading_values(){
        Instant now = Instant.now();
        List<ElectricityReading> values =  electricityReadingsGenerator.generate(3);
        assertNotNull(values.get(0).equals(now));
        assertNotNull(values.get(1).equals(now.minusSeconds(1 * 10)));
        assertNotNull(values.get(2).equals(now.minusSeconds(1 * 10)));
    }

}