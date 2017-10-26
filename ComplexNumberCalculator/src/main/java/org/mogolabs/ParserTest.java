package org.mogolabs;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.mogolabs.math.complex.Parser;

public class ParserTest {

	Parser parser = new Parser();
	String input;
	String result;

	@Test(expected = IllegalArgumentException.class)
	public void testDelSpacesAndSpecialCharsWithException() {
		input = "qwerty";
		result = parser.delSpacesAndSpecialChars(input);
	}

	@Test
	public void testDelSpacesAndSpecialChars() {
		String input;
		String result;

		input = null;
		result = parser.delSpacesAndSpecialChars(input);
		assertEquals(result, null);

		input = "";
		result = parser.delSpacesAndSpecialChars(input);
		assertEquals(result, input);

		input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		result = parser.delSpacesAndSpecialChars(input);
		assertEquals(
				result,
				"(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)*(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)*(2i-2i+3)/7i");
	}

	@Test
	public void testTokanizer() {
		String[] tokens = new String[3];
		input = "";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "", " ", "" });

		input = "1";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "1", " ", "" });

		input = "1+2";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "1", "+", "2" });

		input = "(1+2)";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "1", "+", "2" });

		input = "(1+2)+3";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "1+2", "+", "3" });

		input = "(1+2)*3";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "1+2", "*", "3" });

		input = "3(1+2)";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "3", "*", "1+2" });

		input = "(1+2)+3+(2+3)";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[] { "1+2", "+", "3+(2+3)" });
		
		input = "(1+2)(3+4)";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[]{"1+2","*","3+4"});
	
		input = "(1+2)^3+(3+4)";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[]{"(1+2)^3","+","3+4"});
	
		input = "(1+2)^3";
		input = parser.delSpacesAndSpecialChars(input);
		input = parser.stripBrackets(input);
		tokens = parser.tokanize(input);
		assertEquals(tokens, new String[]{"1+2","^","3"});
	}

	@Test
	public void testCalculateNull() {
		input = "";
		Complex result = parser.calculate(input);
		assertEquals(result, null);
	}

	@Test
	public void testCalculateRealNumber() {
		input = "5";
		Complex result = parser.calculate(input);
		assertEquals(result, new Complex(5, 0));
		
		input = "(3+4i)/(7-5i)";
		result = parser.calculate(input);
		assertEquals(result, new Complex(0.013513513513513514,0.581081081081081));
		
		//input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		input = "(2+3i)^*2";
		//input = "(1-2i)^3";
		//input = "(2i-2i+3)/7i";
		//input = "(1+3i)(2i-2i+3)/7i";
		//input = "((2+3i)^2+(4+i)(2-i))/(2+3i)";
		//input = "(3+4i)/(7-5i)";
		result = parser.calculate(input);
		System.out.println(result);
	}
	

	public static void main(String[] args) {

		Parser parser = new Parser();
		String input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		// TODO
		System.out.println(input);

		// 1 - вичислита значення виоазу за його текстовим представленням
		Complex result = parser.calculate(input);
		System.out.println(result);

	}
}
