package com.github.mimo31.badguysgame.mechanics;

public class GameReturnData {
	
	public enum GameActionType{
		GAME_OVER, NO_MORE_STAGES, NEXT_STAGE
	}
	
	public GameActionType actionType;
	public int stage;
	
	public GameReturnData(GameActionType actionType, int stage) {
		this.actionType = actionType;
		this.stage = stage;
	}
}
