package org.mogolabs.math.complex;

import java.util.ArrayList;
import java.util.List;

import org.mogolabs.math.complex.exception.ComplexException;
import org.mogolabs.math.complex.exception.IncorrectBracketException;
import org.mogolabs.math.complex.exception.IncorrectElementException;
import org.mogolabs.math.complex.exception.IncorrectOperationException;
import org.mogolabs.math.complex.exception.IncorrectTypetException;

/**
 * Calculator engine
 */
public final class Calculator {

	public static final String[] OPERATION_GROUPS = new String[] { "+-", "*/", "^" };
	public static final String ALLOWED_OPERATIONS = "+-*/^";
	public static final String ALLOWED_CHARS = ",.0123456789()+-*/^i\t\b\n\r\f ";
	public static final String SPACIAL_CHARS = "\t\b\n\r\f ";

	/**
	 * Remove all spaces, tabs
	 * Check string for correct expression
	 * 
	 * @param expression
	 * @return new instance of String
	 * @throws ComplexException
	 */
	protected String delSpacesAndNormalize(String expression) throws ComplexException {
		if (expression == null || expression.isEmpty())
			return expression;

		StringBuilder newExpressionBuilder = new StringBuilder();
	
		int start = 0;
		int end = expression.length();
		int countBrackets = 0;
		char ch;

		for (int index = start; index < end; index++) {
			ch = expression.charAt(index);
			if (ch == '(')
				countBrackets++;
			if (ch == ')')
				countBrackets--;
			if (countBrackets < 0)
				throw new IncorrectBracketException("Brackets mismatch");

			if (index == 0 && ALLOWED_OPERATIONS.indexOf(ch) >= 0 && ch != '-') 
				throw new IncorrectOperationException("Operation mismatch");
				
			if (ALLOWED_OPERATIONS.indexOf(ch) >= 0 && index < end - 1
					&& ALLOWED_OPERATIONS.indexOf(expression.charAt(index + 1)) >= 0) 
				throw new IncorrectOperationException("Operation mismatch");

			if (ALLOWED_CHARS.indexOf(ch) >= 0) {
				// if (SPACIAL_CHARS.indexOf(ch) < 0) {
				newExpressionBuilder.append(ch);
				if ((ch == ')' && index + 1 < end
						&& ALLOWED_OPERATIONS.indexOf(expression.charAt(index + 1)) < 0
						&& expression.charAt(index + 1) != ')')
						|| (Character.isDigit(ch) && index + 1 < end
								&& ALLOWED_OPERATIONS.indexOf(expression.charAt(index + 1)) < 0
								&& expression.charAt(index + 1) != ')' && expression.charAt(index + 1) != '^')
								&& expression.charAt(index + 1) != 'i')
					newExpressionBuilder.append('*');
				// }
			} else {
				throw new IncorrectBracketException("Character mismatch");
			}
		}
		if (countBrackets > 0) 
			throw new IncorrectBracketException("Brackets mismatch");

		return newExpressionBuilder.toString();
	}

	/**
	 * Remove enclosed brackets
	 * @param token
	 * @return new instance of String
	 */
	protected String stripBrackets(String token) {
		int start = -1;
		if (token == null || token.isEmpty() || (start = token.indexOf('(')) < 0)
			return token;

		boolean needAnotherStep;
		do {
			int end = token.length();
			int countBrackets = 0;
			char ch = 0;
			needAnotherStep = false;
			for (int index = start; index < end; index++) {
				ch = token.charAt(index);
				if (ch == '(')
					countBrackets++;
				if (ch == ')')
					countBrackets--;
				if (countBrackets == 0 && index > start && index < end - 1)
					break;
				if (ch == ')' && countBrackets == 0 && index == end - 1 && token.indexOf('(') == 0 && end > 2) {
					token = token.substring(1, end - 1);
					needAnotherStep = true; //
				}
			}
		} while (needAnotherStep);
		return token;
	}

	/**
	 * Evaluate expression
	 * @param input,
	 * @return new instance Complex()
	 */
	public Complex calculate(String input) throws ComplexException {
		if (input == null || input.isEmpty())
			return null;

		String[] tokens = tokanize(delSpacesAndNormalize(input));

		if (tokens == null || tokens.length == 0)
			return null;

		int index = 0;
		String firstToken = index < tokens.length ? getToken(tokens, index++) : "";
		Complex firstOperand = new Complex();

		if (index == tokens.length) { 
			try {
				firstOperand = new Complex(Double.parseDouble(firstToken), 0);
			} catch (NumberFormatException ex) {
				if (firstToken.charAt(firstToken.length() - 1) == 'i') {
					if (firstToken.length() > 1)
						firstToken = firstToken.substring(0, firstToken.length() - 1);
					else
						firstToken = "1";
					if (firstToken.equals("-"))
						firstToken = "-1";
				}
				try {
					firstOperand = new Complex(0, Double.parseDouble(firstToken));
				} catch (NumberFormatException e) {
					throw new IncorrectTypetException("String is not a number: " + " " + firstToken);
				}
			}
		} else {
			firstOperand = calculate(firstToken);
			while (index < tokens.length) { // якщо є операції - вирахуємо їх
				char op = index < tokens.length ? getToken(tokens, index++).charAt(0) : 0;
				String secondToken = index < tokens.length ? getToken(tokens, index++) : "";
				Complex secontOperand = calculate(secondToken);
				firstOperand = firstOperand.operate(secontOperand, op);
			}
		}
		return firstOperand;
	}

	private String getToken(String[] tokens, int i) {
		if (tokens == null || i > tokens.length - 1)
			return null;

		return tokens[i];
	}

	/**
	 * Split the expression to several expressions by operation
	 * @param input
	 * @throws ComplexExpression
	 * @return Arrays of Strings
	 */
	protected String[] tokanize(String input) {
		List<String> list = new ArrayList<>();

		String token = "";

		String[] tokens = new String[3];
		String firstToken = "";
		String secondToken = "";
		char operation = 0;
		for (int step = 0; step < OPERATION_GROUPS.length; step++) {
			boolean needStep = false;
			for (int i = 0; i < OPERATION_GROUPS[step].length(); i++) {
				if (input.indexOf(OPERATION_GROUPS[step].charAt(i)) >= 0)
					needStep = true;
			}
			if (firstToken == "" && needStep) {
				operation = 0;
				char ch = 0;
				int bracketHasNoPair = 0;
				int start = 0;
				int end = input.length();
				int index = 0; 

				while (index < end) {
					ch = input.charAt(index);
					if (ch == '(')
						bracketHasNoPair++;
					if (ch == ')')
						bracketHasNoPair--;
					if (bracketHasNoPair == 0) {
						boolean foundOperation = false;
						for (int i = 0; i < OPERATION_GROUPS[step].length(); i++) {
							if (ch == OPERATION_GROUPS[step].charAt(i)) {
								foundOperation = true;
							}
						}
						if (foundOperation) {
							if (index == 0 && ch == '-') {
								token = "0";
							} else {
								token = stripBrackets(input.substring(start, index));
							}
							list.add(token);
							list.add(String.valueOf(ch));
							start = index + 1;
						}
					}
					index++;
				}
				if (start != 0 && start < end) {
					token = stripBrackets(input.substring(start));
					list.add(token);
					break;
				}
			}
		}

		if (list.isEmpty()) {
			token = input;
			token = stripBrackets(token);
			list.add(token);
		}

		return list.toArray(new String[list.size()]);
	}
}
