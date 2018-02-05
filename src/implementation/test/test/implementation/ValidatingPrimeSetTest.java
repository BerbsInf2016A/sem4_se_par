package implementation;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatingPrimeSetTest {

    @Test
    public void addEntry_TooMuch(){
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        assertTrue(set.addEntry(1));
        assertFalse(set.addEntry(17));
    }

    @Test
    public void countOfMissingDigits(){
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.addEntry(1);
        set.addEntry(3);

        assertEquals(2, set.countOfMissingDigit(2));
        assertEquals(2, set.countOfMissingDigit(3));
    }

    @Test
    public void equals_equals(){
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.addEntry(1);
        set.addEntry(3);

        ValidatingPrimeSet secondSet = new ValidatingPrimeSet();
        secondSet.addEntry(1);
        secondSet.addEntry(3);

        assertTrue(set.equals(secondSet));

    }

    @Test
    public void hashcode_equals(){
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.addEntry(1);
        set.addEntry(3);

        ValidatingPrimeSet secondSet = new ValidatingPrimeSet();
        secondSet.addEntry(1);
        secondSet.addEntry(3);

        assertTrue(set.hashCode() == secondSet.hashCode());

    }
    @Test
    public void hashcode_notEquals(){
        ValidatingPrimeSet set = new ValidatingPrimeSet();
        set.addEntry(1);
        set.addEntry(3);

        ValidatingPrimeSet secondSet = new ValidatingPrimeSet();
        secondSet.addEntry(1);
        secondSet.addEntry(5);

        assertTrue(set.hashCode() != secondSet.hashCode());

    }


}