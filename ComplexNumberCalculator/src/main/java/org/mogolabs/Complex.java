package org.mogolabs;

import java.util.Arrays;

/**
 * @author Misha
 *
 */
public class Complex {
	private double real;
	private double image;

	Complex() {
		this(0, 0);
	}

	Complex(double real, double image) {
		this.real = real;
		this.image = image;
	}

	public static Complex operation(Complex one, Complex two, char... op ) {
		switch (op[0]) {
		case '+':
			return Complex.add(one, two);
		case '-':
			return Complex.sub(one, two);
		case '*':
			return Complex.mul(one, two);
		case '/':
			return Complex.div(one, two);
		case '^':
			return Complex.pow(one, op[1]);
		default:
			return new Complex();
		}
	}

	// додавання поточного комплексного числа до числа отриманого в методі @other
	// Результатом є нове комплексне число
	public Complex add(Complex other) {
		double real = this.real + other.real;
		double image = this.image + other.image;
		return new Complex(real, image);
	}

	public static Complex add(Complex one, Complex two) {
		double real = one.real + two.real;
		double image = one.image + two.image;
		return new Complex(real, image);
	}

	// віднімання @other комплексного числа, отриманого в методі від поточного
	// // Результатом є нове комплексне число
	public Complex sub(Complex other) {
		double real = -other.real;
		double image = -other.image;
		return add(new Complex(real, image));
	}

	public static Complex sub(Complex one, Complex two) {
		double real = one.real - two.real;
		double image = one.image - two.image;
		return new Complex(real, image);
	}

	// множення поточного комплексного числа на число отримане в методі @other
	// Результатом є нове комплексне число
	public Complex mul(Complex other) {
		double real = this.real * other.real - this.image * other.image;
		double image = this.image * other.real + this.real * other.image;
		return new Complex(real, image);
	}

	public static Complex mul(Complex one, Complex two) {
		double real = one.real * two.real - one.image * two.image;
		double image = one.image * two.real + one.real * two.image;
		return new Complex(real, image);
	}

	// ділення поточного комплексного числа на число отримане в методі @other
	// Результатом є нове комплексне число
	public Complex div(Complex other) throws ArithmeticException {
		if (isNull())
			throw new ArithmeticException();
		double divisor = Math.pow(other.real, 2) + Math.pow(other.image, 2);
		double real = (this.real * other.real + this.image * other.image) / divisor;
		double image = (this.image * other.real - this.real * other.image) / divisor;
		return new Complex(real, image);
	}

	public static Complex div(Complex one, Complex two) throws ArithmeticException {
		if (isNull(two))
			throw new ArithmeticException();
		double divisor = Math.pow(two.real, 2) + Math.pow(two.image, 2);
		double real = (one.real * two.real + one.image * two.image) / divisor;
		double image = (one.image * two.real - one.real * two.image) / divisor;
		return new Complex(real, image);
	}

	// обчислення абсолютного значення поточного комплексного числа
	// Результатом обчислення э дыйсне число
	public double abs() {
		double sqrt = Math.sqrt(Math.pow(this.real, 2) + Math.pow(this.real, 2));
		return sqrt;
	}

	public static double abs(Complex one) {
		double sqrt = Math.sqrt(Math.pow(one.real, 2) + Math.pow(one.real, 2));
		return sqrt;
	}

	// піднесення поточного комплексного числа до степені, отриманої в методі @power
	// Результатом є нове комплексне число
	public Complex pow(int power) {
		Complex result = this;
		if (power > 1) {
			result = result.mul(this);
		}
		return result;
	}

	public static Complex pow(Complex one, int power) {
		Complex result = one;
		if (power > 1) {
			result = result.mul(one);
		}
		return result;
	}

	// Визначення квадратного кореня поточного числа, вираховується тільки один
	// корінь.
	// Результатом є нове комплексне число
	public Complex sqrt() {
		double real = Math.sqrt((this.real + Math.sqrt(Math.pow(this.real, 2) + Math.pow(this.image, 2))) / 2);
		double image = 4 / (2 * real);
		return new Complex(real, image);
	}

	public static Complex sqrt(Complex one) {
		double real = Math.sqrt((one.real + Math.sqrt(Math.pow(one.real, 2) + Math.pow(one.image, 2))) / 2);
		double image = 4 / (2 * real);
		return new Complex(real, image);
	}

	// Аналіз текстової стрічки, отриманої параметром в методі і побудова на основі
	// цих даних комплексного числа
	// Результатом є нове комплексне число.
	// Може генерувати виключення, коли стрічка не відповідає прийнятому формату
	// комплексного числа.
	public static Complex parseFromString(String input) throws IllegalArgumentException, NumberFormatException {
		if (input == null | input.isEmpty())
			throw new IllegalArgumentException("Відсутні дані для аналізу");

		input = input.replaceAll("\\ +|\\,", "").replaceAll("-", " -").replaceAll(",", ".").replaceAll("\\*", "");
		if (input.charAt(0) == ' ' || input.charAt(0) == '+')
			input = input.substring(1);
		String[] partsOfComplex = input.split("[ |+]");
		if (partsOfComplex.length > 2)
			throw new IllegalArgumentException(
					"Не корректна кількість параметрів для числа (число сладається більше як з двох частин)");

		double calculateReal = 0;
		double calculateImage = 0;
		boolean hasReal = false;
		boolean hasImage = false;

		for (int i = 0; i < partsOfComplex.length; i++) {
			boolean f = partsOfComplex[i].contains("i");
			if (!f) {
				if (hasReal == false) {
					try {
						calculateReal = Double.valueOf(partsOfComplex[i]);
					} catch (NumberFormatException ex) {
						throw new NumberFormatException("Введено недопустимі символи");
					}
					hasReal = true;
				} else
					throw new IllegalArgumentException(
							"Не корректні типи параметрів для числа (дійсна частина знайдена більше одного разу)");
			} else {
				if (partsOfComplex[i].charAt(partsOfComplex[i].length() - 1) == 'i')
					if (hasImage == false) {
						if (partsOfComplex[i].length() > 1 & partsOfComplex[i].charAt(0) != '-')
							try {
								calculateImage = Double
										.valueOf(partsOfComplex[i].substring(0, partsOfComplex[i].length() - 1));
							} catch (NumberFormatException ex) {
								throw new NumberFormatException("Введено недопустимі символи");
							}
						else if (partsOfComplex[i].charAt(0) != '-') {
							calculateImage = 1;
						} else {
							calculateImage = -1;
						}
						hasImage = true;
					} else
						throw new IllegalArgumentException(
								"Не корректні типи параметрів для числа (уявна частина знайдена більше одного разу)");
				else
					throw new IllegalArgumentException(
							"Не корректні типи параметрів для числа (знак i стоїть перед числом)");
			}
		}
		System.out.println(" є корректним комплексним числом");
		return new Complex(calculateReal, calculateImage);
	}

	public boolean isNull() {
		if (this.real == 0 && this.image == 0)
			return true;
		return false;
	}

	public static boolean isNull(Complex one) {
		if (one.real == 0 && one.image == 0)
			return true;
		return false;
	}

	@Override
	public String toString() {
		String result = "";
		if (this.real != 0)
			if (this.real % 1 == 0)
				result += (int) (this.real);
			else
				result += this.real;

		if (this.image != 0) {
			if (this.image > 0) {
				if (!result.isEmpty())
					result += "+";
			}

			if (this.image % 1 == 0) {
				if (this.image != 1) {
					result += (int) (this.image);
				}
			} else {
				result += this.image;
			}
			result += "i";
		}
		if (result == "")
			result = "0";

		return result;
	}
}
