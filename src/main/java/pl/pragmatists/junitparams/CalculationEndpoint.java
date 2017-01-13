package pl.pragmatists.junitparams;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CalculationEndpoint {

    @RequestMapping(path = "/multiply/", method = POST)
    public MultiplyOperationResponseJson multiplyValue(@RequestBody MultiplyOperationRequestJson multiplyOperationRequestJson) {
        Integer multipliedValue = multiplyOperationRequestJson.multiplier * multiplyOperationRequestJson.value;
        return new MultiplyOperationResponseJson(multipliedValue);
    }

}
