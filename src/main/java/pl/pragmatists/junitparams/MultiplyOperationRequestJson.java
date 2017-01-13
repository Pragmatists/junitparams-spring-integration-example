package pl.pragmatists.junitparams;

public class MultiplyOperationRequestJson {
    public int multiplier;
    public int value;

    public MultiplyOperationRequestJson() { }

    public MultiplyOperationRequestJson(int multiplier, int value) {
        this.multiplier = multiplier;
        this.value = value;
    }
}
