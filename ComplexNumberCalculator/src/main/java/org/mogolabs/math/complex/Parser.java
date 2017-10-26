package org.mogolabs.math.complex;

public final class Parser {

	public static final String[] OPERATION_GROUPS = new String[] { "+-", "*/", "^" };
	public static final String ALLOWED_OPERATIONS = "+-*/^";
	public static final String ALLOWED_CHARS = ",.0123456789()+-*/^i\t\b\n\r\f ";
	public static final String SPACIAL_CHARS = "\t\b\n\r\f ";

	public static final String STRINGPIS_NOT_A_NUMBER = "Стрічка не може бути конвертована в число";

    /**
     * Удаляє всі пробіли, символи табуляції, перевод каретки
     * Аналізує стрічку на допустимі символи, допустимі оператори, їх комбінування
     * 
     * @param  expression вираз, який необхідно вирахувати
     * @throws IllegalArgumentException коли є недопустимі символи або не правильно бобудований вираз     
     * @return Модифіковану стрічку  
     */
	protected String delSpacesAndSpecialChars(String expression) throws IllegalArgumentException {
		if (expression == null || expression.isEmpty())
			return expression;

		StringBuilder newExpression = new StringBuilder();
		boolean badString = false;
		int expressionLength = expression.length();
		char ch;
		int bracketHasNoPair = 0;
		String message = "";
		for (int index = 0; index < expressionLength; index++) {
			ch = expression.charAt(index);
			if (ch == '(')
				bracketHasNoPair++;
			if (ch == ')')
				bracketHasNoPair--;
			if (bracketHasNoPair < 0) {
				badString = true;
				message = "Неузгодженність дужок";
				break;
			}
			if(index==0 && ALLOWED_OPERATIONS.indexOf(ch) >= 0 && ch!='-'){
				badString = true;
				message = "Неочікувана поява оператора \""+ch+"\" в позиції " + index;
				break;
			}
			if (ALLOWED_OPERATIONS.indexOf(ch) >= 0 && index < expressionLength - 1
					&& ALLOWED_OPERATIONS.indexOf(expression.charAt(index + 1)) >= 0) {
				badString = true;
				message = "Не допустимість сумісного використання операторів \""+ch+expression.charAt(index + 1)+"\" в позиції " + index;
				break;
			}
			if (ALLOWED_CHARS.indexOf(ch) >= 0) {
				if (SPACIAL_CHARS.indexOf(ch) < 0) {
					newExpression.append(ch);
					if (index + 1 < expressionLength
							&& (((ch == ')' || Character.isDigit(ch) || ch == 'i') && expression
									.charAt(index + 1) == '(') || (ch == ')' && expression
									.charAt(index + 1) == 'i')))
						newExpression.append('*');
				}
			} else {
				badString = true;
				message = "Не допустимий символ в позиції " + index;
				break;
			}
		}
		if (bracketHasNoPair > 0) {
			badString = true;
			message = "Неузгодженність дужок";
		}

		if (badString)
			throw new IllegalArgumentException(message + " " + expression);

		return newExpression.toString();
	}

    /**
     * Удаляє лишні дужки
     * 
     * @param  token вираз, який необхідно вирахувати
     * @return Модифіковану стрічку  
     */
	protected String stripBrackets(String token) {
		int start = -1;
		if (token == null || token.isEmpty() || (start = token.indexOf('(')) < 0)
			return token;

		boolean needAnotherStep;
		do {
			int end = token.length();
			int bracketHasNoPair = 0;
			char ch = 0;

			needAnotherStep = false;
			for (int index = start; index < end; index++) {
				ch = token.charAt(index);
				if (ch == '(')
					bracketHasNoPair++;
				if (ch == ')')
					bracketHasNoPair--;
				if (bracketHasNoPair == 0 && index > start && index < end - 1)
					break;
				if (ch == ')' && bracketHasNoPair == 0 && index == end - 1
						&& token.indexOf('(') == 0 && end > 2) {
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
     * @param  input, 
     * @return Обєкт типу Complex()  
     */
	public Complex calculate(String input) {
		String[] tokens = new String[3];
		if (input == null || input.isEmpty())
			return null;

		tokens = tokanize(delSpacesAndSpecialChars(input));

		String firstToken = getToken(tokens, 0);
		String op = getToken(tokens, 1);
		String secondToken = getToken(tokens, 2);

		Complex result = new Complex();

		if (secondToken == null || secondToken.isEmpty()) {
			// один токен означає, що у нас або реальна частина або уявна
			// частина.
			// так як на попередному етапу метод tokanize повинен був розбити
			// вираз на два простіших
			// цього могло не статися лише коли ми маємо число, або вираз не
			// корректний
			try {
				Double realPart = Double.parseDouble(firstToken);
				result = new Complex(realPart, 0);
			} catch (NumberFormatException ex) {
				// попробуємо може це уявна частина, у якої на останній позиції
				// стоїть символ "i"
				if (firstToken.charAt(firstToken.length() - 1) == 'i') {
					if (firstToken.length() > 1)
						firstToken = firstToken.substring(0, firstToken.length() - 1);
					else
						firstToken = "1";
					
					if (firstToken.equals("-"))
						firstToken = "-1";
				}
				try {
					Double imagePart = Double.parseDouble(firstToken);
					result = new Complex(0, imagePart);
				} catch (NumberFormatException e) {
					throw new NumberFormatException(STRINGPIS_NOT_A_NUMBER + " " + firstToken);
				}
			}
		} else {
			Complex firstOperand = calculate(firstToken);
			Complex secontOperand = calculate(secondToken);
			switch (op.charAt(0)) {
			case '+':
				result = firstOperand.add(secontOperand);
				break;
			case '-':
				result = firstOperand.sub(secontOperand);
				break;
			case '*':
				result = firstOperand.mul(secontOperand);
				break;
			case '/':
				result = firstOperand.div(secontOperand);
				break;
			case '^':
				result = firstOperand.pow(secontOperand.getRealAsInt());
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

   /**
    * Розбиває вираз на два вирази
    * 
    * @param  input вираз, який необхідно вирахувати
    * @return масив стрічок  
    */
	// Математичний вираз представляэмо у вигляді:
	// ВИРАЗ = ВИРАЗ ОПЕРАЦІЯ ВИРАЗ
	// де ВИРАЗ це ВИРАЗ або ЧИСЛО
	// спочатку шукаємо операції "+" та "-" (перша група)
	// якщо таких немає шукаємо операції "*" та "/" (друга група)
	// якщо таких немає шукаємо операці "^" (третя група)
	protected String[] tokanize(String input) {
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
								if(index!=0) // знак "-" першій позиції не є операцією
									foundOperation = true;
							}
						}
						if (foundOperation) {
							// операція знайдена, занотовуємо інформацію
							// та завершаємо роботу метода
							operation = ch;
							firstToken = stripBrackets(input.substring(start, index));
							secondToken = stripBrackets(input.substring(index + 1, end));
							// інформація про операцію
							tokens[0] = firstToken;
							tokens[1] = String.valueOf(operation);
							tokens[2] = secondToken;
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
			operation = ' ';
			secondToken = "";
		}
		tokens[0] = firstToken;
		tokens[1] = String.valueOf(operation);
		tokens[2] = secondToken;

		return tokens;
	}
}
