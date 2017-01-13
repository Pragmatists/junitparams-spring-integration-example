package pl.pragmatists.junitparams;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculationEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Test
    @Parameters({
            "2, 3, 6",
            "3, 3, 9",
            "0, 3, 0",
            "3, 0, 0"
    })
    public void multiply_values(int multiplier, int value, int expectedMultipliedValue) throws Exception {
        MultiplyOperationResponseJson multiplyOperationResponseJson = restTemplate.postForObject(
                "/multiply/", new MultiplyOperationRequestJson(multiplier, value), MultiplyOperationResponseJson.class
        );

        assertThat(multiplyOperationResponseJson.multipliedValue).isEqualTo(expectedMultipliedValue);
    }
}
