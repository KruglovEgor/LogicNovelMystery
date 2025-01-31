import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { LnmHero, LnmPlayerState } from '../frameInterpreter/types';

// Define the shape of your game state
export interface GameState {
	health: number;
	currentChapterId: string;
	currentFrameId: string;
	protagonist: LnmHero;
	playerState: LnmPlayerState;
	intermediateResult: boolean | null;
}

// Define the initial state
const initialState: GameState = {
	health: 100,
	currentChapterId: 'start',
	currentFrameId: 'frame1',
	protagonist: LnmHero.STEVE,
	playerState: LnmPlayerState.CREATED,
	intermediateResult: null,
};

const loadGameState = (): GameState => {
	try {
		const storedState = localStorage.getItem('gameState');
		if (storedState) {
			return JSON.parse(storedState);
		}
		return initialState; // No state found in localStorage
	} catch (error) {
		console.error('Failed to load game state from localStorage:', error);
		return initialState;
	}
};

// Create the slice
const gameStateSlice = createSlice({
	name: 'gameState',
	initialState: loadGameState() as GameState,
	reducers: {
		setHealth(state, action: PayloadAction<number>) {
			state.health = Math.min(100, Math.max(0, action.payload));
		},
		increaseHealth(state, action: PayloadAction<number | 'full'>) {
			if (action.payload === 'full') {
				state.health = 100;
			} else {
				state.health = Math.min(100, state.health + action.payload);
			}
		},
		decreaseHealth(state, action: PayloadAction<number | 'kill'>) {
			if (action.payload === 'kill') {
				state.health = 0;
			} else {
				state.health = Math.max(0, state.health - action.payload);
			}
		},
		setCurrentChapter(state, action: PayloadAction<string>) {
			state.currentChapterId = action.payload;
		},
		setCurrentFrame(state, action: PayloadAction<string>) {
			state.currentFrameId = action.payload;
		},
		setProtagonist(state, action: PayloadAction<LnmHero>) {
			state.protagonist = action.payload;
		},
		setPlayerState(state, action: PayloadAction<LnmPlayerState>) {
			state.playerState = action.payload;
		},
		setIntermediateResult(state, action: PayloadAction<boolean | null>) {
			state.intermediateResult = action.payload;
		},
		resetState(state) {
			Object.assign(state, initialState);
		},
	},
});

// Export actions and reducer
export const {
	setHealth,
	increaseHealth,
	decreaseHealth,
	setCurrentChapter,
	setCurrentFrame,
	setProtagonist,
	setPlayerState,
	setIntermediateResult,
	resetState,
} = gameStateSlice.actions;

export default gameStateSlice.reducer;
