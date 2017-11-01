package org.mogolabs.math.complex;

import org.mogolabs.math.complex.exception.IncorrectElementException;

/**
 * Complex number (a + bI) 
 */
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

	public double getReal() {
		return this.real;
	}

	public double getImage() {
		return this.image;
	}
	
	/**
	 * All other methods are declared as protected for the possibility of going out in test cases
	 */

    /**
     * Perform the operation (operation) to the two complex numbers (this and other)
     * @param operation
     * @param other 
     * @throws IncorrectElementException 
     * @result new instance of Complex
     */
	protected Complex operate(Complex other, char operation) throws IncorrectElementException {
		Complex result = new Complex();
		switch (operation) {
		case '+':
			result = this.add(other);
			break;
		case '-':
			result = this.sub(other);
			break;
		case '*':
			result = this.mul(other);
			break;
		case '/':
			result = this.div(other);
			break;
		case '^':
			if (other.getImage() != 0 || other.getReal() % 1 != 0)
				throw new IncorrectElementException("Не допустимий формат степеня: " + other);
			result = this.pow((int)other.getReal());
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return result;
	}

    /**
     * Additions two complex numbers (this + other)
     * @param other 
     * @result new instance of Complex
     */
	protected Complex add(Complex other) {
		double real = this.real + other.real;
		double image = this.image + other.image;
		return new Complex(real, image);
	}

	/**
     * Subtraction two complex numbers (this - other)
     * @param other 
     * @result new instance of Complex
     */	
	protected Complex sub(Complex other) {
		double real = this.real - other.real;;
		double image = this.image - other.image;
		return new Complex(real, image);
	}

	/**
     * Multiplication two complex numbers (this * other)
     * @param other 
     * @result new instance of Complex
     */	
	protected Complex mul(Complex other) {
		double real = this.real * other.real - this.image * other.image;
		double image = this.real * other.image + this.image * other.real;
		return new Complex(real, image);
	}

	/**
     * Division two complex numbers (this / other)
     * @param other 
     * @result new instance of Complex
     */	
	protected Complex div(Complex other) throws ArithmeticException {
		if (isNull())
			throw new ArithmeticException();
		double divisor = Math.pow(other.real, 2) + Math.pow(other.image, 2);
		double real = (this.real * other.real + this.image * other.image) / divisor;
		double image = (this.image * other.real - this.real * other.image) / divisor;
		return new Complex(real, image);
	}

	/**
     * Calculate the absolute value of complex number (this)
     * @result double value
     */	
	protected double abs() {
		double sqrt = Math.sqrt(Math.pow(this.real, 2) + Math.pow(this.real, 2));
		return sqrt;
	}

	/**
     * Calculate the power (power) of complex number (this)
     * @param power 
     * @result new instance of Complex
     */	
	protected Complex pow(int power) {
		Complex result = this;
		if (power > 1) {
			for (int i = 1; i < power; i++)
				result = result.mul(this);
		}
		return result;
	}

	/**
     * Calculate the square of the complex number (this)
     * @result new instance of Complex
     */	
	protected Complex sqrt() {
		double real = Math.sqrt((this.real + Math.sqrt(Math.pow(this.real, 2) + Math.pow(this.image, 2))) / 2);
		double image = 4 / (2 * real);
		return new Complex(real, image);
	}

	/**
     * Check the complex numbers (this) for 0 values
     * @result boolean
     */	
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
		if (image != other.image)
			return false;
		if (real != other.real)
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
			if (this.image < 0) {
				//result += "-";
			}

			if (this.image % 1 == 0) {
				if (Math.abs(this.image) != 1) {
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