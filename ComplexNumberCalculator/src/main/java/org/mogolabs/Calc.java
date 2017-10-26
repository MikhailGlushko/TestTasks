package org.mogolabs;

import org.mogolabs.math.complex.Complex;
import org.mogolabs.math.complex.Calculator;

public class Calc {

	public static void main(String[] args) {
		String input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2i+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		Calculator calc = new Calculator();

		if (args != null)
			for (String expression : args) {
				Complex result = calc.calculate(expression);
				System.out.println(expression+ " = "+result);
			}
		else{
			Complex result = calc.calculate(input);
			System.out.println(input+ " = "+result);
		}
	}

}
