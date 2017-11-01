package org.mogolabs;

import org.mogolabs.math.complex.Complex;
import org.mogolabs.math.complex.exception.ComplexException;
import org.mogolabs.math.complex.Calculator;

public class Calc {

	public static void main(String[] args) {

		String input = "(3+4i)/(7-5i)+5i+1-((2+3i)^2+(4+i)(2-i))/(2+3i)-4i+7+(1-2i)^3+(1+3i)(2i-2i+3)/7i";
		Calculator calc = new Calculator();

		if (args != null && args.length != 0) {
			System.out.println("***************************************\nОтримано " + args.length
					+ " параметрів\n***************************************");
			int count = 0;
			int completed = 0;
			for (String expression : args) {
				try {
					count++;
					if (expression != null && !expression.isEmpty()) {
						System.out.print("вираз " + count + " : " + expression + " = ");
						Complex result = calc.calculate(expression);
						System.out.println(result);
						completed++;
					}
				} catch (ComplexException ex) {
					System.out.println("(ПОМИЛКА РОЗРАХУНКУ! " + ex.getMessage() + ")");
				}
			}
			System.out.println("***************************************\nРозрахунок виконано для " + completed + " з "
					+ count + "\n***************************************");
		} else {
			System.out.println("Use: java Calc \"expression\" [\"expression\" ...] ENTER");
			System.out.println("For example:");
			System.out.println("java Calc \""+input+"\"");
			System.out.println("***************************************\nОтримано " + 1
					+ " параметрів\n***************************************");
				try {
						System.out.print("вираз " + 1 + " : " + input + " = ");
						Complex result = calc.calculate(input);
						System.out.println(result);
				} catch (ComplexException ex) {
					System.out.println("(ПОМИЛКА РОЗРАХУНКУ! " + ex.getMessage() + ")");
				}
			System.out.println("***************************************\nРозрахунок виконано для " + 1 + " з "
					+ 1 + "\n***************************************");
			
			
		}
	}

}
