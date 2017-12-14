package ca.ece.ubc.cpen221.mp5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Class walks through the parse tree created by the ANTLR parser,
 * and returns the tree in an easily manipulated format.
 * 
 * The class can return a Stack formatted in postfix notation, and a Map
 * of all the requests in the query each mapped to a unique integer.
 */
public class GrammarListenerGetNodes extends GrammarBaseListener {
	private Stack<String> stack = new Stack<String> ();

	private Map<Integer, String> atoms = new HashMap<Integer, String> ();
	private int counter = 0;
	
	/**
	 * For every condition (or atom) in the request, it is mapped to a
	 * unique arbitrary integer for easy access later.
	 */
	public void exitAtom(GrammarParser.AtomContext ctx) { 
		String text = ctx.getChild(0).getText();
		String formattedText = "";
		
		if (ctx.getChild(0) instanceof GrammarParser.InContext)
			formattedText = text.replace("in(", "I").replace(")", "");

		else if (ctx.getChild(0) instanceof GrammarParser.CategoryContext)
			formattedText = text.replace("category(", "C").replace(")", "");

		else if (ctx.getChild(0) instanceof GrammarParser.NameContext)
			formattedText = text.replace("name(", "N").replace(")", "");

		else if (ctx.getChild(0) instanceof GrammarParser.PriceContext)
			formattedText = text.replace("price", "P");

		else if (ctx.getChild(0) instanceof GrammarParser.RatingContext)
			formattedText = text.replace("rating", "R");	
		
		if (!formattedText.isEmpty()) {
			atoms.put(counter, formattedText);
			stack.push(Integer.toString(counter));
			counter++;
		}
	}
	
	/**
	 * At the start of every expression, the operation of the
	 * expression is pushed onto the stack.
	 */
	public void enterExpr(GrammarParser.ExprContext ctx) {
		String operation = "";
		List<TerminalNode> token = ctx.AND();
		
		for (TerminalNode part: token) {
			operation = part.getText();
			stack.push(operation);
		}
		
		token = ctx.OR();
		for (TerminalNode part: token) {
			operation = part.getText();
			stack.push(operation);
		}
	}	
	
	/**
	 * If an expression is the child of an atom (it is an internal expression
	 * and not a large, overarching expression), the two operands and the operation
	 * are popped off of the stack and formatted such that they form one
	 * string in postfix notation. The string is then pushed back to the stack.
	 */
	public void exitExpr(GrammarParser.ExprContext ctx) {
		String text = "";
		if (ctx.getParent() instanceof GrammarParser.AtomContext)
			text = stack.pop().concat("," + stack.pop()).concat("," + stack.pop());
		
		if (text.length() != 0)
			stack.push(text);
	}	
	
	/**
	 * If the query is formatted incorrectly, throws an exception.
	 * @throws IllegalArgumentException
	 */
	public void visitErrorNode(ErrorNode node) { 
		throw new IllegalArgumentException ();
	}
	
	/**
	 * Reformats the stack into a form such that either :
	 * 		-an operation refers to the two most preceding integers
	 * 			on the stack
	 * 		-an operation refers to the two most preceding sets of
	 * 			operation and operands
	 * 
	 * @return formatted stack of strings, where each entry is either
	 * 			and integer that represents an atoms, or an operation
	 */
	public Stack<String> getStack () {
		Stack<String> tempstack = new Stack<String> ();
	
		while (!stack.isEmpty()) {
			String unformatted = stack.pop();
			String[] array = unformatted.split(",");
			
			if (array.length == 1 && !(array[0].equals("&&") || array[0].equals("||"))) {
				tempstack.push(array[0]);
				tempstack.push(array[0]);
				tempstack.push("&&");
			}
			
			else if (array.length != 1) {
				for (String current: array) 
					tempstack.push(current);
			}
			
			else 
				tempstack.push(unformatted);
		}
		
		while (!tempstack.isEmpty())
			stack.push(tempstack.pop());
		
		return stack;
	}
	
	/**
	 * @return map of all atoms in request, mapped to a unique integer
	 */
	public Map<Integer, String> getAtoms () {
		return atoms;
	}
}
