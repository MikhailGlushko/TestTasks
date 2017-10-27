package org.mogolabs.math.complex;

import java.util.ArrayList;
import java.util.List;

/**
 * Клас для обчислення виразу, що містить косплексні числа
 * Допустимі операції над косплексними числами: +-*.^
 * Допустиме об'єднання чисет в групи за допомогою дужок () 
 * 
 */
public final class Calculator {

	public static final String[] OPERATION_GROUPS = new String[] { "+-", "*/", "^" };
	public static final String ALLOWED_OPERATIONS = "+-*/^";
	public static final String ALLOWED_CHARS = ",.0123456789()+-*/^i\t\b\n\r\f ";
	public static final String SPACIAL_CHARS = "\t\b\n\r\f ";

	public static final String STRINGPIS_NOT_A_NUMBER = "Стрічка не може бути конвертована в число";

	/**
	 * Удаляє всі пробіли, символи табуляції, перевод каретки Аналізує стрічку на
	 * допустимі символи, допустимі оператори, їх комбінування
	 * 
	 * @param expression
	 *            вираз, який необхідно вирахувати
	 * @throws IllegalArgumentException
	 *             коли є недопустимі символи або не правильно бобудований вираз
	 * @return Модифіковану стрічку
	 */
	protected String delSpacesAndNormalize(String expression) throws IllegalArgumentException {
		if (expression == null || expression.isEmpty())
			return expression;

		StringBuilder newExpressionBuilder = new StringBuilder();
		String message = "";
		int start = 0;
		int end = expression.length();
		int countBrackets = 0;
		char ch;
		boolean badString = false;
				
		for (int index = start; index < end; index++) {
			ch = expression.charAt(index);
			if (ch == '(')
				countBrackets++;
			if (ch == ')')
				countBrackets--;
			if (countBrackets < 0) {
				badString = true;
				message = "Неузгодженність дужок";
				break;
			}
			//
			if (index == 0 && ALLOWED_OPERATIONS.indexOf(ch) >= 0 && ch != '-') {
				badString = true;
				message = "Неочікувана поява оператора \"" + ch + "\" в позиції " + index;
				break;
			}
			if (ALLOWED_OPERATIONS.indexOf(ch) >= 0 && index < end - 1
					&& ALLOWED_OPERATIONS.indexOf(expression.charAt(index + 1)) >= 0) {
				badString = true;
				message = "Не допустимість сумісного використання операторів \"" + ch + expression.charAt(index + 1)
						+ "\" в позиції " + index;
				break;
			}
			if (ALLOWED_CHARS.indexOf(ch) >= 0) {	   // допустимий символ
				//if (SPACIAL_CHARS.indexOf(ch) < 0) { // відсутні спецсимволи
					newExpressionBuilder.append(ch);
					if ((ch == ')' && index + 1 < end  // далі йде магія по добавлянню в явному виглядц операції "*"
							&& ALLOWED_OPERATIONS.indexOf(expression.charAt(index + 1)) < 0
							&& expression.charAt(index + 1) != ')')
							|| (Character.isDigit(ch) && index + 1 < end
									&& ALLOWED_OPERATIONS.indexOf(expression.charAt(index + 1)) < 0
									&& expression.charAt(index + 1) != ')' && expression.charAt(index + 1) != '^')
									&& expression.charAt(index + 1) != 'i')
						newExpressionBuilder.append('*');
				//}
			} else {
				badString = true;
				message = "Не допустимий символ в позиції " + index;
				break;
			}
		}
		if (countBrackets > 0) {
			badString = true;
			message = "Неузгодженність дужок";
		}

		if (badString)
			throw new IllegalArgumentException(message + " " + expression);

		return newExpressionBuilder.toString();
	}

	/**
	 * Удаляє лишні дужки
	 * 
	 * @param token
	 *            вираз, який необхідно вирахувати
	 * @return Модифіковану стрічку
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
					needAnotherStep = true; // провіримо вложеніть дужок
				}
			}
		} while (needAnotherStep);
		return token;
	}

	/**
	 * Вираховує математичний вираз з введеної стрічки
	 * 
	 * @param input,
	 * @return Обєкт типу Complex()
	 */
	public Complex calculate(String input) throws IllegalArgumentException, NumberFormatException {
		if (input == null || input.isEmpty())
			return null;

		String[] tokens = tokanize(delSpacesAndNormalize(input));

		if (tokens == null || tokens.length == 0)
			return null;

		int index = 0;
		String firstToken = index < tokens.length ? getToken(tokens, index++) : "";
		Complex firstOperand = new Complex();

		if (index == tokens.length) {	// думаємо що це число, за результатами роботи алгоритму 
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
					throw new NumberFormatException(STRINGPIS_NOT_A_NUMBER + " " + firstToken);
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
	 * Розбиває вираз на два вирази
	 * 
	 * @param input
	 *            вираз, який необхідно вирахувати
	 * @throws IllegalArgumentException
	 * @return масив стрічок
	 */
	// Математичний вираз представляэмо у вигляді:
	// ВИРАЗ = ВИРАЗ ОПЕРАЦІЯ ВИРАЗ
	// де ВИРАЗ це ВИРАЗ або ЧИСЛО
	// спочатку шукаємо операції "+" та "-" (перша група)
	// якщо таких немає шукаємо операції "*" та "/" (друга група)
	// якщо таких немає шукаємо операці "^" (третя група)
	protected String[] tokanize(String input) throws IllegalArgumentException {
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
				int index = 0; // операція може зустрітися починаючи з позиції 1, якщо в позиції 0 стоїть
								// операція
								// то це може бути лише знаком - до числа

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
							// операція знайдена, занотовуємо інформацію
							// та завершаємо роботу метода
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

		// четвертий етап
		// повинно залишитися комплексне число або його частини ([[-]a]
		// [[+|-][b]i])
		// де a - будь-яке дыйсне число, b - будь-яке дыйсне число, окрым 1
		// реальна чи уявна
		// або ми маємо вираз, який не є комплексним числом
		// або вказано функції чи елементи, на які алгориитм
		// не налаштовано
		if (list.isEmpty()) {
			token = input;
			token = stripBrackets(token);
			list.add(token);
		}

		return list.toArray(new String[list.size()]);
	}
}
