package org.mogolabs.math.complex;

import org.mogolabs.Complex;

public class Parser {

	public static final String[] OPERATION_GROUPS = new String[] { "+-", "*/", "^" };
	public static final String ALLOWED_OPERATIONS = "+-*/^";
	public static final String ALLOWED_CHARS = ",.0123456789()+-*/^i\t\b\n\r\f ";
	public static final String SPACIAL_CHARS = "\t\b\n\r\f ";

	public static final String STRING_IS_NOT_VALID_EXPRESSION = "Стрічка не є допустимим математичним виразом";
	public static final String STRING_BRACKETS_ERROR = "Стрічка має неузгодженість дужок";
	public static final String STRINGPIS_NOT_A_NUMBER = "Стрічка не може бути конвертована в число";

	public String delSpacesAndSpecialChars(String expression) throws IllegalArgumentException {
		if (expression == null || expression.isEmpty())
			return expression;

		StringBuilder newExpressionWithoutSpecialChars = new StringBuilder();
		boolean badString = false;
		int expressionLength = expression.length();
		char ch;
		int bracketHasNoPair = 0;
		for (int index = 0; index < expressionLength; index++) {
			ch = expression.charAt(index);
			if (ch == '(') {
				bracketHasNoPair++;
			}
			if (ch == ')') {
				bracketHasNoPair--;
			}
			if (bracketHasNoPair < 0) {
				badString = true;
				break;
			}
			if(ALLOWED_OPERATIONS.indexOf(ch)>=0 && index<expressionLength-1 && ch==expression.charAt(index=1)){
				badString = true;
				break;
			}
			if (ALLOWED_CHARS.indexOf(ch) >= 0) {
				if (SPACIAL_CHARS.indexOf(ch) < 0) {
					newExpressionWithoutSpecialChars.append(ch);
					if (index + 1 < expressionLength && (ch == ')' || Character.isDigit(ch) || ch == 'i')
							&& expression.charAt(index + 1) == '(')
						newExpressionWithoutSpecialChars.append('*');
				}
			} else {
				badString = true;
				break;
			}
		}
		if (bracketHasNoPair > 0)
			badString = true;

		if (badString) {
			throw new IllegalArgumentException(STRING_IS_NOT_VALID_EXPRESSION + " " + expression);
		}

		return newExpressionWithoutSpecialChars.toString();
	}

	public String stripBrackets(String token) {
		int start = -1;
		if (token == null || token.isEmpty() || (start = token.indexOf('(')) < 0)
			return token;
		boolean needStep;
		do {
			int end = token.length();
			int level = 0;
			char ch = 0;

			needStep = false;
			for (int index = start; index < end; index++) {
				ch = token.charAt(index);
				if (ch == '(')
					level++;
				if (ch == ')')
					level--;
				if (level == 0 && index > start && index < end - 1)
					break;
				if (ch == ')' && level == 0 && index == end - 1 && token.indexOf('(') == 0
						&& end > 2) {
					token = token.substring(1, end - 1);
					needStep = true; // провіримо вложеніть дужок
				}
			}
		} while (needStep);
		return token;
	}

	public Complex calculate(String input) {
		String[] tokens = new String[3];
		if (input == null || input.isEmpty())
			return null;

		input = delSpacesAndSpecialChars(input);

		tokens = tokanize(input);

		String firstToken = getToken(tokens, 0);
		String op = getToken(tokens, 1);
		String lastToken = getToken(tokens, 2);

		Complex result = new Complex();

		if (lastToken == null || lastToken.isEmpty()) {
			// один токен означаэ, що у нас чбо реальна частина або уявна
			// частина.
			// так як на попередному етапу метод tokanize повинен був розбити
			// вираз на два простіших
			// цього могло не статися лише коли ми маємо число, або вираз не
			// корректний
			try {
				Double d = Double.parseDouble(firstToken);
				result = new Complex(d, 0);
			} catch (NumberFormatException ex) {
				// попробуємо може це уявна частина, у якої на останній позиції
				// стоїть символ "i"
				String image = "";
				if (firstToken.charAt(firstToken.length() - 1) == 'i') {
					if (firstToken.length() > 1) {
						image = firstToken.substring(0, firstToken.length() - 1);
					} else {
						image = "1";
					}
					if (image.equals("-")) {
						image = "-1";
					}
				}
				try {
					Double d = Double.parseDouble(image);
					result = new Complex(0, d);
				} catch (NumberFormatException e) {
					throw new NumberFormatException(STRINGPIS_NOT_A_NUMBER + " " + firstToken);
				}
			}
		} else {
			Complex one = calculate(firstToken);
			Complex two = calculate(lastToken);
			switch (op.charAt(0)) {
			case '+':
				result = one.add(two);
				break;
			case '-':
				result = one.sub(two);
				break;
			case '*':
				result = one.mul(two);
				break;
			case '/':
				result = one.div(two);
				break;
			case '^':
				result = one.pow(two.getRealAsInt());
				break;
			default:
				break;
			}
		}
		return result;
	}

	private String getToken(String[] tokens, int i) {
		if (tokens == null || i > tokens.length - 1)
			return null;
		return tokens[i];
	}

	// Математичний вираз представляэмо у вигляді:
	// ВИРАЗ = ВИРАЗ ОПЕРАЦІЯ ВИРАЗ
	// де ВИРАЗ це ВИРАЗ або ЧИСЛО
	// спочатку шукаємо операції "+" та "-" (перша група)
	// якщо таких немає шукаємо операції "*" та "/" (друга група)
	// якщо таких немає шукаємо операці "^" (третя група)
	public String[] tokanize(String input) {
		String[] tokens = new String[3];
		String firstToken = "";
		String lastToken = "";
		char op = 0;
		for (int step = 0; step < OPERATION_GROUPS.length; step++) {
			boolean needStep = false;
			for (int i = 0; i < OPERATION_GROUPS[step].length(); i++) {
				if (input.indexOf(OPERATION_GROUPS[step].charAt(i)) >= 0)
					needStep = true;
			}
			if (firstToken == "" && needStep) {
				op = 0;
				char ch = 0;
				int level = 0;
				int start = 0;
				int end = input.length();
				int index = 0;

				while (index < end) {
					ch = input.charAt(index);
					if (ch == '(')
						level++;
					if (ch == ')')
						level--;
					if (level == 0) {
						boolean f = false;
						for (int i = 0; i < OPERATION_GROUPS[step].length(); i++) {
							if (ch == OPERATION_GROUPS[step].charAt(i))
								f = true;
						}
						if (f) {
							// операція знайдена, занотовуємо інформацію
							// та завершаємо роботу метода
							op = ch;
							firstToken = input.substring(start, index);
							firstToken = stripBrackets(firstToken);
							lastToken = input.substring(index + 1, end);
							lastToken = stripBrackets(lastToken);
							// інформація про операцію
							tokens[0] = firstToken;
							tokens[1] = String.valueOf(op);
							tokens[2] = lastToken;
							break;
						}
					}
					index++;
				}
			}
		}

		// четвертий етап
		// повинно залишитися комплексне число або його частини ([[-]a]
		// [[+|-][b]i])
		// де a - будь-яке дыйсне число, b - будь-яке дыйсне число, окрым 1
		// реальна чи уявна
		// або ми маємо вираз, який не є комплексним числом
		// або вказано функції чи елементи, на які алгориитм
		// не налаштовано
		if (firstToken.isEmpty()) {
			firstToken = input;
			firstToken = stripBrackets(firstToken);
			op = ' ';
			lastToken = "";
		}
		tokens[0] = firstToken;
		tokens[1] = String.valueOf(op);
		tokens[2] = lastToken;

		return tokens;
	}
}
