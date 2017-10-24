package com.mogolabs;

import java.util.Arrays;

class Polindrome {

	private int[] primes;
	private String polindromes;

	// формирует масив простых чисел в заданом интервале
	// используя решето Эрастофена
	private int[] getPrimes(int min, int max) {
		
		// to numbers from 1 to max
		int[] eratostheneArray = new int[max+1];
		
		// numbers from min to max
		int[] primitivesArray = new int[max-min+1];
		
		for(int k=2; k*k<=max; k++) {
			if(eratostheneArray[k]==0) {
				for(int l=k*k; l<=max; l+=k) {
					eratostheneArray[l] = 1;
				}
			}
		}
		
		int pos =0;
		for (int i=min; i<=max; i++) {
			if(eratostheneArray[i]==0) {
				primitivesArray[pos++] = i;
			}	
		}
		return Arrays.copyOf(primitivesArray,(pos > 0) ? --pos : 0);
	}

	// проверяет, является ли число Полиндромом
	private boolean isPolindrome(long num) {
		String longAsString = Long.toString(num);
		int length = longAsString.length();
		for (int index = 0; index < length / 2; index++) {
			if (longAsString.charAt(index) != longAsString.charAt(length - 1 - index))
				return false;
		}
		return true;
	}

	// Формирует Карту значание Полиндром <-> множители
	// Найбольший Полиндром и его множители будут в конце списка
	public String getPolindrome() {
		String str = "";
		long r=0,x=0,y=0;
		if (primes == null)
			return "Not Found";
		int length = primes.length - 1;
		for (int i = length; i > 0; i--) {
			for (int j = i; j > 0; j--) {
				long result = (long) primes[i] * (long) primes[j];
				if (isPolindrome(result)) {
					if(result>r) {
						r=result;
						x=primes[i];
						y=primes[j];
					}
				}
			}
		}
		if(x!=0) {
			str = "Max Polindrome is: "+r + " = " + x + " * "+y; 
		}
		return str;
	}

	public Polindrome(int min, int max) {
		// формируем масив простых чисел
		primes = getPrimes(min,max);
	}

}