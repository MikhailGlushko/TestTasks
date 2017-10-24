package org.mogolabs;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		int choise = 0;

		System.out.println("Start");
		do {
			choise = 0;
			choise = menu(scanner, choise);

			switch (choise) {
			case 1:			
				Complex complex1 = inputComplex(
						"Введіть перше число в форматі [x + yi]", scanner);
				Complex complex2 = inputComplex(
						"Введіть друге число в форматі [x + yi]", scanner);
				Complex result = complex1.add(complex2);
				System.out
						.println("Результат операції додавання комплексних чисел: "
								+ result);
				System.out.println("Для продовження нажміть ENTER");
				scanner.nextLine();
				break;
				
			default:
				System.out.println("Решта відключено");
				break;
			}

		} while (choise != 9);
		System.out.println("End");
	}

	private static Complex inputComplex(String string, Scanner scanner) {
		System.out.println(string);
		Complex complex = null;

		try {
			String input = scanner.nextLine();
			System.out.print("Отриманий вираз \"" + input + "\"");
			complex = Complex.parseFromString(input);
		} catch (InputMismatchException ex) {
		}
		return complex;
	}

	private static int menu(Scanner scanner, int choise) {
		System.out.println("Операції с мплексними числами:");
		System.out.println("------------------------------");
		System.out.println("1) Додавання");
		System.out.println("2) Віднімання");
		System.out.println("3) Множення");
		System.out.println("4) Ділення");
		System.out.println("5) Обчислення абсолютного значення");
		System.out.println("6) Піднесення до степеня");
		System.out.println("7) Визначення кореня квадратного");
		System.out.println("8) Завершення роботи");
		System.out.println("-------------------------------");
		System.out.println("Зробіть вибір");
		System.out.println();

		try {
			choise = scanner.nextInt();
			scanner.nextLine();
		} catch (InputMismatchException ex) {
			scanner.nextLine();
			System.out.println("Не коректний вибір");
			System.out.println();
			choise = 0;
		}
		return choise;
	}
}
