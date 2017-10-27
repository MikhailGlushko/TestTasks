package org.mogolabs.math.complex;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class CalculatorTests {

	Calculator calculator = new Calculator();
	String input;
	String result;

	@Test(expected = IllegalArgumentException.class)
	public void testDelSpacesAndSpecialCharsWithException() {
		input = "1++3";
		result = calculator.delSpacesAndNormalize(input);
	}

	@Test
	public void testDelSpacesAndSpecialChars() {
		String input;
		String result;

		input = null;
		result = calculator.delSpacesAndNormalize(input);
		assertEquals(result, null);

		input = "";
		result = calculator.delSpacesAndNormalize(input);
		assertEquals(result, input);

		input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		result = calculator.delSpacesAndNormalize(input);
		assertEquals(
				result,
				"(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)*(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)*(2i-2i+3)/7i");
	}

	@Test
	public void testTokanizer() {
		String[] tokens = new String[3];
		input = "";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { ""});

		input = "1";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { "1" });

		input = "1+2";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { "1", "+", "2" });

		input = "(1+2)";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { "1", "+", "2" });

		input = "(1+2)+3";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { "1+2", "+", "3" });

		input = "(1+2)*3";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { "1+2", "*", "3" });

		input = "3(1+2)";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { "3", "*", "1+2" });

		input = "(1+2)+3+(2+3)";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[] { "1+2", "+", "3","+","2+3" });
		
		input = "(1+2)(3+4)";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[]{"1+2","*","3+4"});
	
		input = "(1+2)^3+(3+4)";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[]{"(1+2)^3","+","3+4"});
	
		input = "(1+2)^3";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[]{"1+2","^","3"});
		
		input = "1+2-3";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[]{"1","+","2","-","3"});
		
		input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		input = calculator.delSpacesAndNormalize(input);
		input = calculator.stripBrackets(input);
		tokens = calculator.tokanize(input);
		assertArrayEquals(tokens, new String[]{"(3+4i)/(7-5i)","+","5i","+","1","-","((2+3i)^2+(4+i)*(2-i))/(2+3i)","-","4i","+","7","+","(1-2i)^3","+","(1+3i)*(2i-2i+3)/7i"});
	}

	@Test
	public void testCalculateNull() {
		input = "";
		Complex result = calculator.calculate(input);
		assertEquals(result, null);
	}

	@Test
	public void testCalculateRealNumber() {
		input = "5";
		Complex result = calculator.calculate(input);
		assertEquals(result, new Complex(5, 0));
		
		input = "-5";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(-5, 0));
		
		input = "5i";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(0, 5));
		
		input = "-5i";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(0, -5));
		
		input = "-i+1";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(1, -1));

		input = "i-2-i";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(-2, 0));
		
//		input = "-(1+i)";
//		result = calculator.calculate(input);
//		assertEquals(result, new Complex(-1, 1));
		
		input = "i^3";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(0, -1));
				
		input = "(3+4i)/(7-5i)";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(0.013513513513513514,0.581081081081081));
		
		input = "(1+i)^(4/2)";
		result = calculator.calculate(input);
		assertEquals(result, new Complex(0, 2));	
	
		//input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		
	}
}
