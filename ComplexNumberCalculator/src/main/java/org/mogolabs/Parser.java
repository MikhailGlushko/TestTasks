package org.mogolabs;

public class Parser {
	public static void main(String[] args) {

		String input = args[0];
		Complex result = calculate(input);
		System.out.println(result);

	}

	public static Complex calculate(String input) throws IllegalArgumentException {

		System.out.println(input);

		Complex res = null;

		if (isNumber(input)) {
			if (!isImage(input)) {
				Double d;
				try {
					d = Double.parseDouble(input);
					res = new Complex(d, 0);
				} catch (NumberFormatException e) {
					// nothing
				}
			} else {
				Double d;
				try {
					d = Double.parseDouble(input.substring(0, input.length() - 1));
					res = new Complex(0, d);
				} catch (NumberFormatException e) {
					// nothing
				}
			}
			return res;
		}

		char op = 0;
		int level = 0;
		int start = 0;
		int end = input.length();
		int index = 0;
		while (index < end) {
			char ch = input.charAt(index);
			if (ch == '(')
				level++;
			if (ch == ')')
				level--;
			if (level == 0) {
				if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch=='(') {
					op = ch;
					String st1 = input.substring(start, index);
					String st2 = input.substring(index + 1);
					Complex res1 = Parser.calculate(st1);
					Complex res2 = Parser.calculate(st2);
					res = Complex.operation(res1, res2, op);
					break;
				} else {
					if(index==end-1) {
						input = input.substring(1,end-1);
						System.out.println(input);
						index = -1;
					}
				}
			}
			index++;
		}

		return res;
	}

	private static boolean isNumber(String input) {
		boolean result = true;
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if (!Character.isDigit(ch) && ch != 'i') {
				result = false;
			}
		}
		return result;
	}

	private static boolean isImage(String input) {
		boolean result = true;
		if (input.charAt(input.length()-1) == 'i')
			result = true;
		else
			result = false;
		return result;
	}
}
