package org.mogolabs;

import org.mogolabs.math.complex.Complex;
import org.mogolabs.math.complex.Parser;

public class Test {

	public static void main(String[] args) {
		String input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2i+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		Parser parser = new Parser();

		if (args != null)
			for (String expression : args) {
				Complex result = parser.calculate(expression);
				System.out.println(expression+ " = "+result);
			}
		else{
			Complex result = parser.calculate(input);
			System.out.println(input+ " = "+result);
		}
	}

}
