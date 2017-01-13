# JUnitParams integration with Spring

## Problems with integration SpringJUnit4ClassRunner with JUnitParamsRunner

There was a known problem with running tests which start spring context combined with JUnitParams. 
Main reason was that running spring tests required usage of runner [SpringJUnit4ClassRunner](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/junit4/SpringJUnit4ClassRunner.html) and it eliminates option to use JUnitParamsRunner.
JUnitParams runner must be top junit runner. Combining it with spring was impossible until now.

## Alternative way to start spring context

Since **spring version 4.2** there were introduced two classes that help us start spring without any special runner defined: 
* [SpringClassRule](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/junit4/rules/SpringClassRule.html)
* [SpringMethodRule](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/junit4/rules/SpringMethodRule.html)

According to its documentation to fully replace SpringJUnit4ClassRunner we need to always defined both of these rules because
first supports all annotations at class level and second all annotations at instance and method level.

Example test which uses this mechanism can be modified version of [CalculationEndpointTest](https://github.com/Pragmatists/junitparams-spring-integration-example/blob/master/src/test/java/pl/pragmatists/junitparams/CalculationEndpointTest.java)
but without all annotations related to JUnitParams.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CalculationEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Test
    public void spring_injection() throws Exception {
        assertThat(restTemplate).isNotNull();
    }

}
```

## Running JUnitParamsRunner with spring

Now it is easy to combine JUnitParamsRunner with spring. As demonstrated in CalculationEndpointTest we just need to add proper JUnitParams annotations.
Example is quite straightforward. It starts spring boot application with simple rest controller which takes json
 as parameter and multiplies passed multiplier and value and returns it as json response.
We supply test (with @Parameters annotation) with set of multiplier value and expected multiplied value f.e. 2, 3, 6 which means we expect 6 out 
of multiplication of 2 and 3.
Started spring boot indicates that annotation @SpringBootTest was applied as well as injecting restTemplate with @Autowired.
```java
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
```

## Older version of spring and JUnitParams

What if you are using older version of spring and you can not migrate to newer one. Generally it is possible to implement
SpringClassRule and SpringMethodRule by copying them from spring 4.2 to your project. You will also need to copy 
 class RunPrepareTestInstanceCallbacks. 
 
In SpringClassRule you need to remove line 
  
```java
statement = withProfileValueCheck(statement, testClass);
```

from apply method (and related methods code). 

In SpringMethodRule you need also small modifications. Remove

```java
statement = withPotentialRepeat(statement, frameworkMethod, testInstance);
statement = withPotentialTimeout(statement, frameworkMethod, testInstance);
statement = withProfileValueCheck(statement, frameworkMethod, testInstance);
```
from apply method (and related methods code).

Thanks to these you can now use your own created rules but as you see you are losing support for annotations:

* @Repeat
* @IfProfileValue
* @Timed
 
 on methods and @IfProfileValue for class.

If you have any problems with these modifications let us know.