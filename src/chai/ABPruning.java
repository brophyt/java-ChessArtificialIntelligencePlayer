package chai;

import java.util.Random;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class ABPruning implements ChessAI {
	int maxDepth;
	int maxPlayerNumber;
	int maxDepthReached;
	int numberStatesVisited;
	int maxDepthFinal;
	
	public ABPruning(int d, int i) {
		maxDepth = d; 
		maxDepthFinal = d; 
		maxDepthReached = 0;
		numberStatesVisited = 0;
		maxPlayerNumber = i;
	}
	
	
	public short getMove(Position position) throws IllegalMoveException {		
		short bestMove = abPrune(position, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		return bestMove;
	}
	
	
	public short iterativeABPrune(Position position) throws IllegalMoveException {
		short bestMove = 0; 
		
		for (int i = 0; i < maxDepthFinal; i++) {
			maxDepth =  i; 
			bestMove = abPrune(position, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		}
		
		maxDepth = maxDepthFinal; 
		return bestMove; 
		
	}
	
	public short abPrune(Position position, int low, int high, int depth) throws IllegalMoveException {
		int highestUtility = Integer.MIN_VALUE; 
		short [] moves = position.getAllMoves();
		short bestMove = moves[0];
		
		for (int i = 0; i < moves.length; i++) {
			position.doMove(moves[i]);
			numberStatesVisited++;
			if (cutoffTest(1, position)) {
				if (calculateScore(position) == Integer.MAX_VALUE) {
					bestMove = moves[i];
					position.undoMove();
					break;
				}
			}
			int compare = minValue(2, low, high, position);
			if (highestUtility < compare) {
				highestUtility = compare;
				bestMove = moves[i];
			}
			position.undoMove();
		}
		System.out.println("The maximum depth reached before making the move was " + maxDepthReached);
		System.out.println("The number of states visited was " + numberStatesVisited);	
		numberStatesVisited = 0;
		maxDepthReached = 0;
		return bestMove;
	}
	
	private int maxValue(int depth, int low, int high, Position position) throws IllegalMoveException {
		maxDepthReached = Math.max(maxDepthReached, depth);
		if (cutoffTest(depth, position)) {
			return calculateScore(position);
		}
		
		int utility = Integer.MIN_VALUE;
		short [] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
				numberStatesVisited++;
				utility = Math.max(utility, minValue(depth + 1, low, high, position));
				position.undoMove();
				
				if (utility >= high) {
					return utility;
				}
				low = Math.max(utility, low);
			} catch (IllegalMoveException e) {
				System.out.println("illegal move");
			}
		}
		return utility; 
	}
	
	
	private int minValue(int depth, int low, int high, Position position) throws IllegalMoveException {
		maxDepthReached = Math.max(maxDepthReached, depth);
		if (cutoffTest(depth, position)) {
			return calculateScore(position);
		}
		
		int utility = Integer.MAX_VALUE;
		short [] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
				numberStatesVisited++; 
				utility = Math.min(utility, maxValue(depth + 1, low, high, position));
				position.undoMove();
				
				if (utility <= low) {
					return utility;
				}
				
				high = Math.min(utility, high);
			} catch (IllegalMoveException e) {
				System.out.println("illegal move");
			}
		}
		return utility; 
	}
	
	
	public boolean cutoffTest(int depth, Position position) {
		if (depth >= maxDepth) {
			return true;
		} else if (position.isTerminal()) {
			return true;
		} else {
			return false; 
		}
	}
	
	
	public int calculateScore(Position position) {
		int player = position.getToPlay();
		
		if (position.isStaleMate()) { 
			return 0; 
		} else if (position.isMate()) {
			if (player == maxPlayerNumber) {
				//System.out.println("CHECKMATE!");
				return Integer.MIN_VALUE;
			} else {
				//System.out.println("CHECKMATE!");
				return Integer.MAX_VALUE;
			}
		} else {
			return materialValue(position);
		}
	}
	
	
	private int materialValue(Position position) {
		int player = position.getToPlay();
		int value = 0;
		if (player == maxPlayerNumber) {
			value = position.getMaterial() + (int)position.getDomination();
		} else {
			value = -(position.getMaterial()) - (int)position.getDomination();
		}
		return value; 
	}
}
