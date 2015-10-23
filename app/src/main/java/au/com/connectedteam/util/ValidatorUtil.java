package au.com.connectedteam.util;

public class ValidatorUtil {

	public static boolean range(String value, int minLen) {

		if (value == null) {
			return false;
		}
		

		return value.length() >= minLen;

	}
	
	public static boolean range(String value, int minLen, int maxLen) {

		if (value == null) {
			return false;
		}
		

		return (value.length() < minLen || value.length() > maxLen) ? false
				: true;

	}

	public static boolean digitsOnly(String value, int minLen, int maxLen) {

		if (value == null) {
			return false;
		}


		if (value.length() < minLen || value.length() > maxLen) return false;
		try{
			return Integer.parseInt(value)>=0;
		}
		catch (NumberFormatException e){
			return false;
		}

	}
	
	public static boolean email(String email) {


		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		

		return email!=null?email.matches(regex):false;

	}
	
	
	private static int calculateLuhn(String partialCardNumber){
		int runSum=0;
		boolean isDouble = true;
		for(int i=partialCardNumber.length()-1;i>=0;i--){
			int digit=Character.digit(partialCardNumber.charAt(i), 10);
			int sum;
			if(!isDouble){
				sum=digit;
			}
			else{
				int other = digit*2;
				if(other>9)other-=9;
				sum=other;
			}
			runSum+=sum;
			isDouble=!isDouble;
		}
		int checkDigit = (runSum*9)%10;
		
		return checkDigit;
	}
	
	public static boolean isLuhnValid(String cardNumber){
		int checkDigit = Character.digit(cardNumber.charAt(cardNumber.length()-1), 10);
		return checkDigit== calculateLuhn(cardNumber.substring(0, cardNumber.length()-1));
	}
}
