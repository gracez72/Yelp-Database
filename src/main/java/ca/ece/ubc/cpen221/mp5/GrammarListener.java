// Generated from Grammar.g4 by ANTLR 4.5.1
package ca.ece.ubc.cpen221.mp5;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GrammarParser}.
 */
public interface GrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GrammarParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQuery(GrammarParser.QueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQuery(GrammarParser.QueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(GrammarParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(GrammarParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#andexpr}.
	 * @param ctx the parse tree
	 */
	void enterAndexpr(GrammarParser.AndexprContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#andexpr}.
	 * @param ctx the parse tree
	 */
	void exitAndexpr(GrammarParser.AndexprContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#orexpr}.
	 * @param ctx the parse tree
	 */
	void enterOrexpr(GrammarParser.OrexprContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#orexpr}.
	 * @param ctx the parse tree
	 */
	void exitOrexpr(GrammarParser.OrexprContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#ineq}.
	 * @param ctx the parse tree
	 */
	void enterIneq(GrammarParser.IneqContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#ineq}.
	 * @param ctx the parse tree
	 */
	void exitIneq(GrammarParser.IneqContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#in}.
	 * @param ctx the parse tree
	 */
	void enterIn(GrammarParser.InContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#in}.
	 * @param ctx the parse tree
	 */
	void exitIn(GrammarParser.InContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#category}.
	 * @param ctx the parse tree
	 */
	void enterCategory(GrammarParser.CategoryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#category}.
	 * @param ctx the parse tree
	 */
	void exitCategory(GrammarParser.CategoryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(GrammarParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(GrammarParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#price}.
	 * @param ctx the parse tree
	 */
	void enterPrice(GrammarParser.PriceContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#price}.
	 * @param ctx the parse tree
	 */
	void exitPrice(GrammarParser.PriceContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#rating}.
	 * @param ctx the parse tree
	 */
	void enterRating(GrammarParser.RatingContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#rating}.
	 * @param ctx the parse tree
	 */
	void exitRating(GrammarParser.RatingContext ctx);
}