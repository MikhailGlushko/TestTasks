package org.mogolabs.math.complex;

public final class Complex {
	private double real;
	private double image;

	public Complex() {
		this(0, 0);
	}

	public Complex(double real, double image) {
		this.real = real;
		this.image = image;
	}

	public int getRealAsInt() {
		return (int) this.real;
	}
	
	public int getImageAsInt() {
		return (int) this.image;
	}

	// додавання поточного комплексного числа до числа отриманого в методі
	// @other
	// Результатом є нове комплексне число
	protected Complex add(Complex other) {
		double real = this.real + other.real;
		double image = this.image + other.image;
		return new Complex(real, image);
	}

	// віднімання @other комплексного числа, отриманого в методі від поточного
	// // Результатом є нове комплексне число
	protected Complex sub(Complex other) {
		double real = -other.real;
		double image = -other.image;
		return add(new Complex(real, image));
	}

	// множення поточного комплексного числа на число отримане в методі @other
	// Результатом є нове комплексне число
	protected Complex mul(Complex other) {
		double real = this.real * other.real - this.image * other.image;
		double image = this.image * other.real + this.real * other.image;
		return new Complex(real, image);
	}

	// ділення поточного комплексного числа на число отримане в методі @other
	// Результатом є нове комплексне число
	protected Complex div(Complex other) throws ArithmeticException {
		if (isNull())
			throw new ArithmeticException();
		double divisor = Math.pow(other.real, 2) + Math.pow(other.image, 2);
		double real = (this.real * other.real + this.image * other.image)
				/ divisor;
		double image = (this.image * other.real - this.real * other.image)
				/ divisor;
		return new Complex(real, image);
	}

	// обчислення абсолютного значення поточного комплексного числа
	// Результатом обчислення э дыйсне число
	protected double abs() {
		double sqrt = Math
				.sqrt(Math.pow(this.real, 2) + Math.pow(this.real, 2));
		return sqrt;
	}

	// піднесення поточного комплексного числа до степені, отриманої в методі
	// @power
	// Результатом є нове комплексне число
	protected Complex pow(int power) {
		Complex result = this;
		if (power > 1) {
			for (int i = 1; i < power; i++)
				result = result.mul(this);
		}
		return result;
	}

	// Визначення квадратного кореня поточного числа, вираховується тільки один
	// корінь.
	// Результатом є нове комплексне число
	protected Complex sqrt() {
		double real = Math.sqrt((this.real + Math.sqrt(Math.pow(this.real, 2)
				+ Math.pow(this.image, 2))) / 2);
		double image = 4 / (2 * real);
		return new Complex(real, image);
	}

	protected boolean isNull() {
		if (this.real == 0 && this.image == 0)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(image);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(real);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Complex other = (Complex) obj;
		if (Double.doubleToLongBits(image) != Double
				.doubleToLongBits(other.image))
			return false;
		if (Double.doubleToLongBits(real) != Double
				.doubleToLongBits(other.real))
			return false;
		return true;
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
