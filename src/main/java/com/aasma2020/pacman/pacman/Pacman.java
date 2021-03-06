package com.aasma2020.pacman.pacman;

import com.aasma2020.pacman.board.Messeges;
import com.aasma2020.pacman.board.PacBoard;
import com.aasma2020.pacman.board.moveType;
import com.aasma2020.pacman.communication.Agent;
import com.aasma2020.pacman.communication.MapAreaInfo;
import com.aasma2020.pacman.ghost.Ghost;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Pacman extends Agent implements KeyListener {

	Random rnd = new Random(420);

	// Move Vars
	Timer moveTimer;
	ActionListener moveAL;
	public moveType activeMove;
	moveType todoMove;
	boolean isStuck = true;
	boolean singleplayerGame;

	// Animation Vars
	Timer animTimer;
	ActionListener animAL;
	Image[] pac;
	int activeImage = 0;
	int addFactor = 1;

	public Point pixelPosition;

	private PacBoard parentBoard;

	public Pacman(int x, int y, PacBoard pb, boolean singleplayerGame) {
		this.singleplayerGame = singleplayerGame;
		position = new Point(x, y);
		pixelPosition = new Point(28 * x, 28 * y);

		parentBoard = pb;

		pac = new Image[5];

		activeMove = moveType.NONE;
		todoMove = moveType.NONE;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			pac[0] = ImageIO.read(loader.getResource("images/pac/pac0.png"));
			pac[1] = ImageIO.read(loader.getResource("images/pac/pac1.png"));
			pac[2] = ImageIO.read(loader.getResource("images/pac/pac2.png"));
			pac[3] = ImageIO.read(loader.getResource("images/pac/pac3.png"));
			pac[4] = ImageIO.read(loader.getResource("images/pac/pac4.png"));
		} catch (IOException e) {
			System.err.println("Cannot Read Images !");
		}

		// animation timer
		animAL = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				activeImage = activeImage + addFactor;
				if (activeImage == 4 || activeImage == 0) {
					addFactor *= -1;
				}
			}
		};
		animTimer = new Timer(40, animAL);
		animTimer.start();

		moveAL = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				// update logical position
				if ((pixelPosition.x % 28 == 0) && (pixelPosition.y % 28 == 0)) {
					if (!isStuck) {
						switch (activeMove) {
						case RIGHT:
							position.x++;
							break;
						case LEFT:
							position.x--;
							break;
						case UP:
							position.y--;
							break;
						case DOWN:
							position.y++;
							break;
						}
						// send update message
					}
					isStuck = true;
					animTimer.stop();

					parentBoard.dispatchEvent(new ActionEvent(Pacman.this, Messeges.AREATEST, null));
					parentBoard.dispatchEvent(new ActionEvent(Pacman.this, Messeges.UPDATE, null));
					// ****************************************************************
					// ACRESCENTAR CODIGO AI PACMAN AQUI

					// *****************************************************************
					if (todoMove != moveType.NONE && isPossibleMove(todoMove)) {
						activeMove = todoMove;
						todoMove = moveType.NONE;
					}

				} else {
					isStuck = false;
					animTimer.start();
				}

				switch (activeMove) {
				case RIGHT:
					if ((pixelPosition.x >= (parentBoard.m_x - 1) * 28) && parentBoard.isCustom()) {
						return;
					}
					/*
					 * if((position.x+1 < parentBoard.m_x) &&
					 * (parentBoard.map[position.x+1][position.y]>0)){ return; }
					 */
					if (position.x >= 0 && position.x < parentBoard.m_x - 1 && position.y >= 0
							&& position.y < parentBoard.m_y - 1) {
						if (parentBoard.getMap()[position.x + 1][position.y] > 0) {
							return;
						}
					}
					pixelPosition.x++;
					break;
				case LEFT:
					if ((pixelPosition.x <= 0) && parentBoard.isCustom()) {
						return;
					}
					/*
					 * if((position.x-1 >= 0) && (parentBoard.map[position.x-1][position.y]>0)){
					 * return; }
					 */
					if (position.x > 0 && position.x < parentBoard.m_x - 1 && position.y >= 0
							&& position.y < parentBoard.m_y - 1) {
						if (parentBoard.getMap()[position.x - 1][position.y] > 0) {
							return;
						}
					}
					pixelPosition.x--;
					break;
				case UP:
					if ((pixelPosition.y <= 0) && parentBoard.isCustom()) {
						return;
					}
					/*
					 * if((position.y-1 >= 0) && (parentBoard.map[position.x][position.y-1]>0)){
					 * return; }
					 */
					if (position.x >= 0 && position.x < parentBoard.m_x - 1 && position.y >= 0
							&& position.y < parentBoard.m_y - 1) {
						if (parentBoard.getMap()[position.x][position.y - 1] > 0) {
							return;
						}
					}
					pixelPosition.y--;
					break;
				case DOWN:
					if ((pixelPosition.y >= (parentBoard.m_y - 1) * 28) && parentBoard.isCustom()) {
						return;
					}
					/*
					 * if((position.y+1 < parentBoard.m_y) &&
					 * (parentBoard.map[position.x][position.y+1]>0)){ return; }
					 */
					if (position.x >= 0 && position.x < parentBoard.m_x - 1 && position.y >= 0
							&& position.y < parentBoard.m_y - 1) {
						if (parentBoard.getMap()[position.x][position.y + 1] > 0) {
							return;
						}
					}
					pixelPosition.y++;
					break;
				}

				// send Messege to PacBoard to check collision
				parentBoard.dispatchEvent(new ActionEvent(this, Messeges.COLTEST, null));

			}
		};
		moveTimer = new Timer(9, moveAL);
		moveTimer.start();

	}

	public Timer getMoveTimer() {
		return moveTimer;
	}

	public void setMoveTimer(Timer moveTimer) {
		this.moveTimer = moveTimer;
	}

	public Timer getAnimTimer() {
		return animTimer;
	}

	public void setAnimTimer(Timer animTimer) {
		this.animTimer = animTimer;
	}

	public boolean isPossibleMove(moveType move) {
		if (position.x >= 0 && position.x < parentBoard.m_x - 1 && position.y >= 0
				&& position.y < parentBoard.m_y - 1) {
			switch (move) {
			case RIGHT:
				return !(parentBoard.getMap()[position.x + 1][position.y] > 0);
			case LEFT:
				return !(parentBoard.getMap()[position.x - 1][position.y] > 0);
			case UP:
				return !(parentBoard.getMap()[position.x][position.y - 1] > 0);
			case DOWN:
				return !(parentBoard.getMap()[position.x][position.y + 1] > 0);
			}
		}
		return false;
	}

	public Image getPacmanImage() {
		return pac[activeImage];
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		//
	}

	@Override
	public void keyTyped(KeyEvent ke) {
		//
	}

	// Handle Arrow Keys
	@Override
	public void keyPressed(KeyEvent ke) {
		if (singleplayerGame) {
			switch (ke.getKeyCode()) {
			case 37:
				todoMove = moveType.LEFT;
				break;
			case 38:
				todoMove = moveType.UP;
				break;
			case 39:
				todoMove = moveType.RIGHT;
				break;
			case 40:
				todoMove = moveType.DOWN;
				break;
			}
		}
		// System.out.println(ke.getKeyCode());
	}

	@Override
	public void decideAndAct(MapAreaInfo info) {
		ArrayList<Ghost> ghostsNear = new ArrayList<Ghost>();
		for (Ghost ghost : info.getGhosts().values()) {
			System.out.println(ghost.getClass().getSimpleName());
			ghostsNear.add(ghost);
		}
		
		if (!singleplayerGame) {
			ArrayList<moveType> possibleMoves = new ArrayList<moveType>();
			for (moveType mt : moveType.values()) {
				if (isPossibleMove(mt))
					possibleMoves.add(mt);
			}
			if (!ghostsNear.isEmpty()) {
				chooseMoveGivenGhostPosition(ghostsNear.get(0), possibleMoves);
			} else {
				randomMove(possibleMoves);
			}

		}
		// System.out.println("Found a " + ghost.getClass().getSimpleName() + "
		// ghost...");
	}
	
	public void chooseMoveGivenGhostPosition(Ghost ghost, ArrayList<moveType> possibleMoves) {

		moveType move = moveType.NONE;

		double ghostX = ghost.getPosition().getX();
		double ghostY = ghost.getPosition().getY();

		if(((ghost.isWeak() && position.x <= ghostX) || (!ghost.isWeak() && position.x >= ghostX))
				&& possibleMoves.contains(moveType.RIGHT)) {
			move = moveType.RIGHT;
		}
		if(((ghost.isWeak() && position.x >= ghostX) || (!ghost.isWeak() && position.x <= ghostX))
				&& possibleMoves.contains(moveType.LEFT)) {
			move = moveType.LEFT;
		}
		if(((ghost.isWeak() && position.y >= ghostY) || (!ghost.isWeak() && position.y <= ghostY))
				&& possibleMoves.contains(moveType.UP)) {
			move = moveType.UP;
		}
		if(((ghost.isWeak() && position.y <= ghostY) || (!ghost.isWeak() && position.y >= ghostY))
				&& possibleMoves.contains(moveType.DOWN)) {
			move = moveType.DOWN;
		}

		if(move == moveType.NONE) {
			randomMove(possibleMoves);
		} else {
			todoMove = move;
		}

//		System.out.println("ghost: " + ghostX + " " + ghostY);
//		System.out.println("player: " + position.x + " " + position.y);
		/*if (position.x > ghostX) {
			if (possibleMoves.contains(moveType.RIGHT)) {
				todoMove = moveType.RIGHT;
			} else {
				if (position.y >= ghostY) {
					if (possibleMoves.contains(moveType.DOWN)) {
						todoMove = moveType.DOWN;
					} else {
						randomMove(possibleMoves);
					}
				} else {
					if (possibleMoves.contains(moveType.UP)) {
						todoMove = moveType.UP;
					} else {
						randomMove(possibleMoves);
					}
				}
			}
		} else {
			if (possibleMoves.contains(moveType.LEFT)) {
				todoMove = moveType.LEFT;
			} else {
				if (position.y >= ghostY) {
					if (possibleMoves.contains(moveType.DOWN)) {
						todoMove = moveType.DOWN;
					} else {
						randomMove(possibleMoves);
					}
				} else {
					if (possibleMoves.contains(moveType.UP)) {
						todoMove = moveType.UP;
					} else {
						randomMove(possibleMoves);
					}
				}
			}
		}*/
	}
	
	public void randomMove(ArrayList<moveType> possibleMoves) {
		int moveIndex = rnd.nextInt(possibleMoves.size());
		activeMove = possibleMoves.get(moveIndex);
	}
}
