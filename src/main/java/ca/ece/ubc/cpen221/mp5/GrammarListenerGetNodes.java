package ca.ece.ubc.cpen221.mp5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.tree.TerminalNode;

public class GrammarListenerGetNodes extends GrammarBaseListener {
	private Stack<String> stack = new Stack<String> ();

	private Map<Integer, String> atoms = new HashMap<Integer, String> ();
	private int counter = 0;
	
	
	/**
	 * Maps each atom to an integer for easy access.
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
	
	public void enterExpr(GrammarParser.ExprContext ctx) {
		String operation = "";
		List<TerminalNode> token = ctx.AND();
		
		for (TerminalNode part: token) 
			operation = operation.concat(part.getText());
		
		if(operation.equals("")) {
			token = ctx.OR();
			for (TerminalNode part: token) 
				operation = operation.concat(part.getText());
		}
		
		stack.push(operation);
	}	
	
	public void exitExpr(GrammarParser.ExprContext ctx) {
		String text = "";
		if (ctx.getParent() instanceof GrammarParser.AtomContext)
			text = stack.pop().concat("," + stack.pop()).concat("," + stack.pop());
		
		if (text.length() != 0)
			stack.push(text);
	}	
	
	//GETTER METHODS
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
	
	public Map<Integer, String> getAtoms () {
		return atoms;
	}
}
