package implementation;


import org.junit.Assert;
import org.junit.Test;

public class ValidatorTest {
    @Test
    public void isCandidate() throws Exception {
    }

    @Test
    public void isValid() throws Exception {
        String value = "(5,7,29,47,59,61,67,79,83,89,269,463,467,487,569,599,859,883,887";
        boolean retValue = Validator.isValid(value);
        Assert.assertTrue(retValue);
    }

}