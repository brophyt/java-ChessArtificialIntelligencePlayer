package chai;

import java.util.Random;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class MiniMaxAI implements ChessAI {
	int maxDepth;
	int maxPlayerNumber;
	int maxDepthReached;
	int numberStatesVisited;
	int maxDepthFinal;
	int bestUtility; 
	
	public MiniMaxAI(int d, int i) {
		bestUtility = 0; 
		maxDepthFinal = d; 
		maxDepth = d; 
		maxDepthReached = 0;
		numberStatesVisited = 0;
		maxPlayerNumber = i;
	}
	
	
	public short getMove(Position position) throws IllegalMoveException {		
		short bestMove = miniMax(position);
		return bestMove;
	}
	
	public short iterativeMiniMax(Position position) throws IllegalMoveException {
		short bestMove = 0; 
		
		for (int i = 0; i < maxDepthFinal; i++) {
			maxDepth =  i; 
			bestMove = miniMax(position);
		}
		
		System.out.println("The maximum depth reached before making the move was " + maxDepthReached);
		System.out.println("The number of states visited was " + numberStatesVisited);	
		numberStatesVisited = 0;
		maxDepthReached = 0;
		maxDepth = maxDepthFinal; 
		return bestMove; 
		
	}
	
	public short miniMax(Position position) throws IllegalMoveException {
		short[] moves = position.getAllMoves();
		short bestMove = moves[0];
		int highestUtility = Integer.MIN_VALUE; 
		
		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
				numberStatesVisited++;
				
				if (cutoffTest(1, position)) {
					if (calculateScore(position) == Integer.MAX_VALUE) {
						bestMove = moves[i];
						bestUtility = Integer.MAX_VALUE;
						position.undoMove();
						System.out.println("Minimax checkmate!");
						break;
					}
				}
				
				int compare = minValue(1, position);
				if (highestUtility < compare) {
					highestUtility = compare;
					bestMove = moves[i];
					bestUtility = highestUtility; 
				}
				position.undoMove();
			} catch (IllegalMoveException e) {
				System.out.println("illegal move");
			}
		}
		System.out.println("The maximum depth reached before making the move was " + maxDepthReached);
		System.out.println("The number of states visited was " + numberStatesVisited);	
		numberStatesVisited = 0;
		maxDepthReached = 0;
		return bestMove;
	}
	
	
	private int maxValue(int depth, Position position) throws IllegalMoveException {
		maxDepthReached = Math.max(maxDepthReached, depth);
		if (cutoffTest(depth, position)) {
			return calculateScore(position);
		}
		
		int highestUtility = Integer.MIN_VALUE;
		short [] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
				numberStatesVisited++;
				highestUtility = Math.max(highestUtility, minValue(depth + 1, position));
				position.undoMove();
			} catch (IllegalMoveException e) {
				System.out.println("illegal move");
			}
		}
		
		return highestUtility;
	}
	
	
	private int minValue(int depth, Position position) throws IllegalMoveException {
		maxDepthReached = Math.max(maxDepthReached, depth);
		if (cutoffTest(depth, position)) {
			return calculateScore(position);
		}
		
		int lowestUtility = Integer.MAX_VALUE;
		short [] moves = position.getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			try {
				position.doMove(moves[i]);
				numberStatesVisited++;
				lowestUtility = Math.min(lowestUtility, maxValue(depth + 1, position));
				position.undoMove();
			} catch (IllegalMoveException e) {
				System.out.println("illegal move");
			}
		}
		
		return lowestUtility; 
	}
	
	
	public boolean cutoffTest(int depth, Position position) {
		if (depth >= maxDepth) {
			return true;
		} else if (position.isTerminal() || position.isMate()) {
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
