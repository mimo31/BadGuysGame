package game.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import game.Achievement;
import game.PaintUtils;
import game.StringDraw;

public class AchievementsScreen extends Screen {

	private float positionX = 0;
	private float positionY = 0;
	private int expanded = -1;
	private boolean wasDragging = true;
	private boolean showingHelp;

	private boolean fromStartScreen;

	// Components
	private boolean[] achievementsDrawn = new boolean[Achievement.achievements.length];
	private boolean[] connectionsDrawn = new boolean[Achievement.connections.length];
	private Dimension contentSize = new Dimension();
	private Point mousePosition = new Point();

	public AchievementsScreen() {
		super();
		this.fromStartScreen = true;
	}

	public AchievementsScreen(boolean fromStartScreen) {
		super();
		this.fromStartScreen = fromStartScreen;
	}

	private void updateComponents(Graphics2D g, Dimension contentSize, Point mousePosition) {
		this.contentSize = contentSize;
		this.mousePosition = mousePosition;
		Rectangle viewRectangle = this.toSpaceRectangle(new Rectangle(new Point(0, 0), contentSize));
		for (int i = 0; i < this.achievementsDrawn.length; i++) {
			if (Achievement.achievements[i].shouldShow()) {
				this.achievementsDrawn[i] = Achievement.achievements[i].getSpaceRectangle(i == this.expanded).intersects(viewRectangle);
			}
			else {
				this.achievementsDrawn[i] = false;
			}
		}
		for (int i = 0; i < this.connectionsDrawn.length; i++) {
			if (Achievement.connections[i].shouldShow()) {
				this.connectionsDrawn[i] = Achievement.connections[i].doesIntersect(viewRectangle);
			}
			else {
				this.connectionsDrawn[i] = false;
			}
		}
	}

	private float toSpaceLength(float screenLength) {
		return 2048 * screenLength / this.contentSize.width;
	}

	private float toScreenLength(float spaceLength) {
		return spaceLength * this.contentSize.width / 2048;
	}

	private Point toSpacePoint(Point screenPoint) {
		return new Point((int) (this.positionX + this.toSpaceLength(screenPoint.x - this.contentSize.width / 2)), (int) (this.positionY + this.toSpaceLength(screenPoint.y - this.contentSize.height / 2)));
	}

	private Point toScreenPoint(Point spacePoint) {
		return new Point((int) (this.toScreenLength(spacePoint.x - this.positionX) + this.contentSize.width / 2), (int) (this.toScreenLength(spacePoint.y - this.positionY) + this.contentSize.height / 2));
	}

	private Dimension toSpaceSize(Dimension screenSize) {
		return new Dimension((int) this.toSpaceLength(screenSize.width), (int) this.toSpaceLength(screenSize.height));
	}

	private Dimension toScreenSize(Dimension spaceSize) {
		return new Dimension((int) this.toScreenLength(spaceSize.width), (int) this.toScreenLength(spaceSize.height));
	}

	private Rectangle toSpaceRectangle(Rectangle screenRectangle) {
		return new Rectangle(this.toSpacePoint(screenRectangle.getLocation()), this.toSpaceSize(screenRectangle.getSize()));
	}

	private Rectangle toScreenRectangle(Rectangle spaceRectangle) {
		return new Rectangle(this.toScreenPoint(spaceRectangle.getLocation()), this.toScreenSize(spaceRectangle.getSize()));
	}

	@Override
	public void paint(Graphics2D g, Dimension contentSize, Point mousePosition) throws IOException {
		this.updateComponents(g, contentSize, mousePosition);
		g.setColor(Color.black);
		Rectangle spaceHeadlineBounds = new Rectangle(-512, -512, 1024, 128);
		StringDraw.drawMaxString(g, "Achievements", this.toScreenRectangle(spaceHeadlineBounds));
		g.setStroke(new BasicStroke(this.toScreenLength(5)));
		for (int i = 0; i < Achievement.connections.length; i++) {
			if (this.connectionsDrawn[i]) {
				if (Achievement.connections[i].shouldBeFull()) {
					g.setColor(Color.red);
				}
				else {
					g.setColor(Color.gray);
				}
				Point startPoint = this.toScreenPoint(new Point(Achievement.connections[i].fromX, Achievement.connections[i].fromY));
				Point endPoint = this.toScreenPoint(new Point(Achievement.connections[i].toX, Achievement.connections[i].toY));
				g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
			}
		}
		for (int i = 0; i < Achievement.achievements.length; i++) {
			if (this.achievementsDrawn[i] && i != this.expanded) {
				Rectangle spaceRectangle = Achievement.achievements[i].getSpaceRectangle(false);
				Rectangle screenRectangle = this.toScreenRectangle(spaceRectangle);
				Achievement.achievements[i].paint(g, screenRectangle.getLocation(), screenRectangle.height, false);
			}
		}
		if (this.expanded != -1) {
			Rectangle spaceRectangle = Achievement.achievements[this.expanded].getSpaceRectangle(false);
			Rectangle screenRectangle = this.toScreenRectangle(spaceRectangle);
			Achievement.achievements[this.expanded].paint(g, screenRectangle.getLocation(), screenRectangle.height, true);
		}
		PaintUtils.drawCurrentMoney(g, contentSize);
		if (this.showingHelp) {
			PaintUtils.paintGenericHelpScreen(g, contentSize);
			StringDraw.drawMaxString(g, "These are your achievements.", new Rectangle(contentSize.width / 4, contentSize.height / 4, contentSize.width / 2, contentSize.height / 12));
			StringDraw.drawMaxString(g, "Click on an achievement to get more information about it.", new Rectangle(contentSize.width / 8, contentSize.height / 2, contentSize.width * 3 / 4, contentSize.height / 12));
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if (this.showingHelp) {
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE || event.getKeyCode() == KeyEvent.VK_H) {
				this.showingHelp = false;
				this.wasDragging = false;
			}
		}
		else {
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (this.fromStartScreen) {
					Screen.startNew(new StartScreen());
				}
				else {

				}
			}
			else if (event.getKeyCode() == KeyEvent.VK_H) {
				this.showingHelp = true;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (!this.showingHelp) {
			this.positionX += this.toSpaceLength(this.mousePosition.x - event.getX());
			this.positionY += this.toSpaceLength(this.mousePosition.y - event.getY());
			this.mousePosition = event.getPoint();
			this.wasDragging = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (!this.showingHelp) {
			if (!this.wasDragging) {
				Point spaceClickPosition = this.toSpacePoint(event.getPoint());
				int achievementClicked = -1;
				for (int i = 0; i < this.achievementsDrawn.length; i++) {
					if (this.achievementsDrawn[i]) {
						if (Achievement.achievements[i].getSpaceRectangle(i == this.expanded).contains(spaceClickPosition)) {
							achievementClicked = i;
							break;
						}
					}
				}
				this.expanded = achievementClicked;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (!this.showingHelp) {
			this.wasDragging = false;
		}
	}
}
