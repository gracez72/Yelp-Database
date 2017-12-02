package ca.ece.ubc.cpen221.mp5;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.tree.TerminalNode;

public class GrammarListenerGetNodes extends GrammarBaseListener {
	private List<String> andExpr = new ArrayList<String> ();
	private List<String> orExpr = new ArrayList<String> ();
	
	private List<String> neighbourhoods = new ArrayList<String> ();
	private List<String> categories = new ArrayList<String> ();
	private List<String> names = new ArrayList<String> ();
	private List<String> prices = new ArrayList<String> ();
	private List<String> ratings = new ArrayList<String> ();
	
	public void exitAndexpr(GrammarParser.AndexprContext ctx) { 
		List<TerminalNode> token = ctx.AND();
		String text = "";
		
		for (TerminalNode part: token)
			text = text.concat(part.getText() + " ");
		
		text = text.trim();
		
		andExpr.add(text);
	}
	
	public void exitOrexpr(GrammarParser.OrexprContext ctx) { 
		List<TerminalNode> token = ctx.OR();
		String text = "";
		
		for (TerminalNode part: token)
			text = text.concat(part.getText() + " ");
		
		text = text.trim();
		
		orExpr.add(text);
	}
	
	public void exitIn(GrammarParser.InContext ctx) { 
		List<TerminalNode> token = ctx.WORD();
		String text = "";
		
		for(TerminalNode part: token)
			text = text.concat(part.getText());
		
		token = ctx.NUM();
		for(TerminalNode part: token)
			text = text.concat(part.getText());
		
		neighbourhoods.add(text);
	}
	
	public void exitCategory(GrammarParser.CategoryContext ctx) { 
		List<TerminalNode> token = ctx.WORD();
		String text = "";
		
		for(TerminalNode part: token)
			text = text.concat(part.getText());
		
		categories.add(text);
	}

	public void exitName(GrammarParser.NameContext ctx) { 
		List<TerminalNode> token = ctx.WORD();
		String text = "";
		
		for(TerminalNode part: token)
			text = text.concat(part.getText());
		
		names.add(text);
	}
	
	public void exitPrice(GrammarParser.PriceContext ctx) { 
		TerminalNode token = ctx.NUM();
		GrammarParser.IneqContext ineqCtx = ctx.ineq();
		String text = "";
		
		try {
			text = text.concat(ineqCtx.EQ().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.GT().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.GTE().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.LT().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.LTE().getText());
		} catch (NullPointerException e) {}
		
		text = text.concat(token.getText());
		prices.add(text);
	}
	
	public void exitRating(GrammarParser.RatingContext ctx) { 
		TerminalNode token = ctx.NUM();
		GrammarParser.IneqContext ineqCtx = ctx.ineq();
		String text = "";
		
		try {
			text = text.concat(ineqCtx.EQ().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.GT().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.GTE().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.LT().getText());
		} catch (NullPointerException e) {}
		
		try {
			text = text.concat(ineqCtx.LTE().getText());
		} catch (NullPointerException e) {}
		
		text = text.concat(token.getText());
		ratings.add(text);
	}
	
	//Getter methods
	public List<String> getNeighbourhoods () {
		return neighbourhoods;
	}
	
	public List<String> getCategories () {
		return categories;
	}
	
	public List<String> getNames () {
		return names;
	}
	
	public List<String> getPrices () {
		return prices;
	}
	
	public List<String> getRatings () {
		return prices;
	}
	
	public List<String> getAndExpr () {
		return andExpr;
	}
	
	public List<String> getOrExpr () {
		return orExpr;
	}
}
