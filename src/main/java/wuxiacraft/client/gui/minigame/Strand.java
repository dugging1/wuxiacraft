package wuxiacraft.client.gui.minigame;

import com.mojang.blaze3d.matrix.MatrixStack;
import wuxiacraft.client.gui.MeditateScreen;
import wuxiacraft.util.MathUtils;

public class Strand {

	public float x = -1;
	public float y = -1;

	public float movX;
	public float movY;

	public float minX, maxX;
	public float minY, maxY;

	public int ticker;

	public boolean isGrabbed;

	public float red;
	public float green;
	public float blue;

	public Strand() {
		this.movX = -0.3f + 0.6f * (float) Math.random();
		this.movY = -0.3f + 0.6f * (float) Math.random();
		this.ticker = 0;
		this.isGrabbed = false;
		this.red = 0.90f;
		this.green = 0.95f;
		this.blue = 0.20f;
		this.minX = 68;
		this.maxX = 78 + 53;
		this.minY = 25;
		this.maxY = 35 + 110;
	}

	public Strand setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		return this;
	}

	public Strand setBounds(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
	}

	public void tick() {
		if (this.x == -1 && this.y == -1) {
			this.x = minX + (float) Math.random() * (maxX - minX);
			this.y = minY + (float) Math.random() * (maxY - minY);
		}
		if (this.isGrabbed) {
			this.x = (float) MeditateScreen.mousePosX;
			this.y = (float) MeditateScreen.mousePosY;
		} else {
			this.x += movX;
			if (!MathUtils.between(this.x, this.minX, this.maxX)) {
				this.movX *= -1;
			}
			this.y += movY;
			if (!MathUtils.between(this.y, this.minY, this.maxY)) {
				this.movY *= -1;
			}
		}
		ticker++;
		if (this.ticker >= 30) {
			this.ticker = 0;
			this.movX = -0.3f + 0.6f * (float) Math.random();
			this.movY = -0.3f + 0.6f * (float) Math.random();
		}
		this.x = MathUtils.clamp(this.x, minX, maxX);
		this.y = MathUtils.clamp(this.y, minY, maxY);
	}

	public void draw(MatrixStack stack, MeditateScreen screen) {
		this.draw(stack, screen, 43, 0);
	}

	public void draw(MatrixStack stack, MeditateScreen screen, int u, int v) {
		screen.blitColored(stack, (int) this.x - 2, (int) this.y - 2, u, v, 5, 5, this.red, this.green, this.blue);
	}

}
