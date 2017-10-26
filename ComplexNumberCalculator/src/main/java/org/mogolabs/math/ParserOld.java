package org.mogolabs.math;

import java.util.ArrayList;
import java.util.List;

import org.mogolabs.Complex;

public class ParserOld {

	public static final String ALLOWED_CHARS = ".0123456789()+-*/^i\t\b\n\r\f ";
	public static final String SPACIAL_CHARS = "\t\b\n\r\f ";
	public static final String STRING_IS_NOT_VALID_EXPRESSION = "Стрічка не є допустимим математичним виразом";

	public static final String ALLOWED_OPERATIONS = "+-*/^";
	public static final String OPERATION_GROUP_ONE = "+-";
	public static final String OPERATION_GROUP_TWO = "*/";
	public static final String OPERATION_GROUP_THREE = "^";

	public String delSpacesAndSpecialChars(String input)
			throws IllegalArgumentException {
		if (input == null || input.isEmpty())
			return input;

		StringBuilder builder = new StringBuilder();
		int len = input.length();
		char ch;
		for (int index = 0; index < len; index++) {
			ch = input.charAt(index);
			if (ALLOWED_CHARS.indexOf(ch) >= 0) {
				if (SPACIAL_CHARS.indexOf(ch) < 0) {
					builder.append(ch);
					if (index + 1 < len && ch == ')'
							&& input.charAt(index + 1) == '(')
						builder.append('*');
				}
			} else {
				throw new IllegalArgumentException(
						STRING_IS_NOT_VALID_EXPRESSION+" "+input);
			}
		}

		return builder.toString();
	}

	public Complex calculate(String input) {
		// 1) +-, 2) */, 3) ^ 4) () - порядок вичленення операцій

		// 1 - удалити всі пробіли та символи табуляції і переведення стрічки
		input = delSpacesAndSpecialChars(input);
		
		Complex result = new Complex();
		// 2 - будуємо масив по операціях першої групи
		List<String> tokens = extractByGroupOne(input);
		
		// 3 - вичисляємо операції третьої групи

		// 4 - вичисляємо операції другої групи

		// 5 - вичисляэмо операції першої групи
		if(tokens.size()>1)
			result = calculateGroupOne(tokens);
		
		return result;
	}

	private Complex calculateGroupOne(List<String> tokens) {
		Complex result = new Complex();
		if (tokens.isEmpty())
			return result;

		Complex other = new Complex();

		// strip ^
		while (tokens.contains("^")) {
			for (int index = 0; index < tokens.size(); index++) {
				String token = tokens.get(index);
				if (token.charAt(0) == '^') {
					String subLevelToken = tokens.get(index - 1);
					String power = tokens.get(index + 1);

					tokens.remove(index - 1);
					tokens.remove(index - 1);
					tokens.remove(index - 1);

					Complex subLevelResult = calculate(subLevelToken);
					subLevelResult = subLevelResult
							.pow(Integer.parseInt(power));

					tokens.add(index - 1, subLevelResult.toString());
					
					break;
				}
			}
		}

		//strip * && /
		while (tokens.contains("*") || tokens.contains("/")) {
			for (int index = 0; index < tokens.size(); index++) {
				String token = tokens.get(index);
				if (token.charAt(0) == '*' || token.charAt(0) == '/') {
					char op = token.charAt(0);
					String subLevelToken1 = tokens.get(index - 1);
					String subLevelToken2 = tokens.get(index + 1);

					tokens.remove(index - 1);
					tokens.remove(index - 1);
					tokens.remove(index - 1);
					
					Complex subLevelResult1 = calculate(subLevelToken1);
					Complex subLevelResult2 = calculate(subLevelToken2);
					Complex subLevelResult = null;
					switch (op) {
					case '*':
						subLevelResult = subLevelResult1
								.mul(subLevelResult2);
						tokens.add(index - 1, subLevelResult.toString());
						break;
					case '/':
						subLevelResult = subLevelResult1.div(subLevelResult2);
						tokens.add(index - 1, subLevelResult.toString());
						break;
					default:
						break;
					}
					break;
					//TODO
					
				}
			}
		}

		char op = '+';

		for (String token : tokens) {
			if(token.isEmpty())
				continue;
			if (isReal(token)){
				other = new Complex(Double.parseDouble(token),0);
			} else if(isImage(token)) {
				other = new Complex(0, Double.parseDouble( getImage(token)));
			} else if (isOperation(token)) {
				op = token.charAt(0);
				continue;
			} else {
				other = calculate(token);
			}

			switch (op) {
			case '+':
				result = result.add(other);
				break;
			case '-':
				result = result.sub(other);
				break;
			default:
				break;
			}
		}
		return result;
	}

	private String getImage(String token) {
		if (token.length() > 0)
			token = token.substring(0, token.length() - 1);
		String result = token;
		if (token.isEmpty())
			result = "1";
		if (token.length() == 1 && token.equals("-"))
			result = "-1";
		return result;
	}

	private boolean isOperation(String token) {
		return ALLOWED_OPERATIONS.contains(token);
	}

	private boolean isReal(String token) {
		if (token.isEmpty() || token.charAt(0) == '-' && token.length() == 1)
			return false;
		if(token.charAt(0)!='-' && !Character.isDigit(token.charAt(0)))
			return false;
		
		for (int index = 1; index < token.length(); index++) {
			if (!Character.isDigit(token.charAt(index))) {
				return false;
			}
		}
		return true;
	}

	private boolean isImage(String token) {
		if (token.isEmpty())
			return false;
		if (token.indexOf("i") == token.length() - 1 // є i , він єдиний такий
				&& (token.charAt(0) == '-' || token.charAt(0) == 'i' || Character
						.isDigit(token.charAt(0)))
				&& (token.length() < 2 || isReal(token.substring(0,
						token.length() - 1)))) {
			return true;
		}
		return false;
	}

	private List<String> extractByGroupOne(String input) {
		List<String> list = new ArrayList<String>();
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
				if (ch == '+' || ch == '-' || ch == '*' || ch == '/'
						|| ch == '^' || ch == '(') {
					op = ch;
					String token = input.substring(start, index);
					token = stripBrackets(token, list, op);

					list.add(token);
					list.add(String.valueOf(ch));
					start = index + 1;
				}
				;
			}
			index++;
		}
		if (start < end){
			String token = input.substring(start, index);
			token = stripBrackets(token, list, op);
			list.add(token);
		}	
		return list;
	}

	private String stripBrackets(String token, List<String> list, char op) {
		String otherOp;
		if (list.size() == 0)
			otherOp = String.valueOf(op);
		else
			otherOp = list.get(list.size() - 1);
		if (OPERATION_GROUP_TWO.indexOf(op) >= 0
				|| OPERATION_GROUP_THREE.indexOf(op) >= 0
				|| OPERATION_GROUP_TWO.indexOf(otherOp) >= 0
				|| OPERATION_GROUP_THREE.indexOf(otherOp) >= 0) {
			if (token.charAt(0) == '('
					&& token.charAt(token.length() - 1) == ')') {
				token = token.substring(1, token.length() - 1);
			}
		}
		return token;
	}

}
