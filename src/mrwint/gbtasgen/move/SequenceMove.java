package mrwint.gbtasgen.move;

public class SequenceMove extends DelayableMove {
	
	private Move[] moves;
	
	public SequenceMove(Move... moves) {
		this.moves = moves;
	}

	@Override
	public int prepareMoveInternal(int skips, boolean assumeOnSkip)
			throws Throwable {
		if(moves.length == 0)
			return 0;
		if(!assumeOnSkip) {
			for(int i=0;i<moves.length-1;i++)
				moves[i].execute();
		}
		return ((DelayableMove)moves[moves.length-1]).prepareMoveInternal(skips, assumeOnSkip);
//		return ((DelayableMove)moves[moves.length-1]).prepareMove(skips, assumeOnSkip);
	}

	@Override
	public int doMove() throws Throwable {
		if(moves.length == 0)
			return 0;
		return ((DelayableMove)moves[moves.length-1]).doMove();
	}

	@Override
	public int getInitialKey() {
		if(moves.length == 0)
			return 0;
		return moves[0].getInitialKey();
	}
}