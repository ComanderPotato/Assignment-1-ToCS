import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class LexicalAnalyser {
	
	public static String regexNumPattern = "[1-9]";
	public static String regexOperatorPattern = "[-/*+]";
	
	private enum State {
		EMPTYSTRING,  //q0
		ONEZERO,      //q1
		ONEDECIMAL,   //q2
		NUMBER,       //q3
		SPACE,        //q4
		ONEOPERATION, //q5
		EXPEXC, 	  //q6
		NUMEXC        //q7
	};
	public static List<Token> analyse(String input) throws NumberException, ExpressionException {
		List<Token> tokenList = new ArrayList<Token>();
		State state = State.EMPTYSTRING;
		String wholeNum = "";  //wholeNum string initialized to empty
		
		Pattern regexNum = Pattern.compile(regexNumPattern); 		 //regexNumPattern pattern compiler
		Pattern regexOpe = Pattern.compile(regexOperatorPattern);	 //regexOperatorPatern pattern compiler
		
		for(int i = 0; i < input.length(); i++) {
			String characterRegex = String.valueOf(input.charAt(i));
			Matcher matchNum = regexNum.matcher(characterRegex);		
			Matcher matchOpe = regexOpe.matcher(characterRegex);
			char character = input.charAt(i);
			if(character == '0') {
				wholeNum+= character;
				switch(state) {
				case EMPTYSTRING: state = State.ONEZERO; break; //q0 --> q1
				case ONEZERO: state = State.EXPEXC; break; 		//q1 --> q6
				case ONEDECIMAL: state = State.NUMBER; break;	//q2 --> q3
				case NUMBER: state = State.NUMBER; break;		//q3 --> q3
				case SPACE: state = State.EXPEXC; break;		//q4 --> q6
				case ONEOPERATION: state = State.ONEZERO; break;//q5 --> q1
				case EXPEXC: state = State.EXPEXC; break;		//q6 --> q6
				case NUMEXC: state = State.NUMEXC; break;		//q7 --> q7
				}
			} else if(matchNum.matches()) { //Number IF statement
				wholeNum+= character;
				switch(state) {
					case EMPTYSTRING: state = State.NUMBER; break;
					case ONEZERO: state = State.EXPEXC; break;
					case ONEDECIMAL: state = State.NUMBER; break;
					case NUMBER: state = State.NUMBER; break;
					case SPACE: state = State.EXPEXC; break;
					case ONEOPERATION: state = State.NUMBER; break;
					case EXPEXC: state = State.EXPEXC; break;
					case NUMEXC: state = State.NUMEXC; break;
				}
			} else if(matchOpe.matches()) { 	//Operator IF statement
				if(!wholeNum.isEmpty()) {
					double numberParsed = Double.parseDouble(wholeNum);  
					Token numberTokenized = new Token(numberParsed);
					tokenList.add(numberTokenized);
					wholeNum = "";
					Token tokenOperator = new Token(Token.typeOf(character));
					tokenList.add(tokenOperator);
				}
				switch(state) {
					case EMPTYSTRING: state = State.EXPEXC ; break;
					case ONEZERO: state = State.ONEOPERATION; break;
					case ONEDECIMAL: state = State.EXPEXC; break;
					case NUMBER: state = State.ONEOPERATION; break;
					case SPACE: state = State.ONEOPERATION; break;
					case ONEOPERATION: state = State.EXPEXC; break;
					case EXPEXC: state = State.EXPEXC; break;
					case NUMEXC: state = State.NUMEXC; break;				
				}
			} else if(character == ' ') {
				switch(state) {
				case EMPTYSTRING: state = State.EXPEXC; break;
				case ONEZERO: state = State.SPACE; break;
				case ONEDECIMAL: state = State.EXPEXC; break;
				case NUMBER: state = State.SPACE; break;
				case SPACE: state = State.SPACE; break;
				case ONEOPERATION: state = State.ONEOPERATION; break;
				case EXPEXC: state = State.EXPEXC; break;
				case NUMEXC: state = State.NUMEXC; break;
				}
			} else if(character == '.') {
				wholeNum+= character;
				switch(state) {
					case EMPTYSTRING: state = State.NUMEXC; break;
					case ONEZERO: state = State.ONEDECIMAL; break;
					case ONEDECIMAL: state = State.NUMEXC;
					case NUMBER: state = State.NUMEXC; break;
					case SPACE: state = State.EXPEXC; break;
					case ONEOPERATION: state = State.EXPEXC; break;
					case EXPEXC: state = State.EXPEXC; break;
					case NUMEXC: state = State.NUMEXC; break;
				}
			}
		}	
		if(state == State.NUMEXC || state == State.ONEDECIMAL) throw new NumberException();
		if(state == State.EXPEXC || state == State.ONEOPERATION) throw new ExpressionException();
			if(!wholeNum.isEmpty()) {
				double numberParsed = Double.parseDouble(wholeNum);  
				Token numberTokenized = new Token(numberParsed);
				tokenList.add(numberTokenized);
		}
	return tokenList;
	}
}
